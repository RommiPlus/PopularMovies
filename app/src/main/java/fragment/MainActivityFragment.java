package fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.popularmovies.BuildConfig;
import com.popularmovies.DetailActivity;
import com.popularmovies.R;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import adapter.MovieAdapter;
import data.Constant;
import model.PopularMovie;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private GridView mMovieGv;
    private MovieAdapter mMovieAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMovieAdapter = new MovieAdapter(getActivity(), new ArrayList<PopularMovie.ResultsBean>());
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mMovieGv = (GridView) view.findViewById(R.id.movie_gv);
        mMovieGv.setAdapter(mMovieAdapter);
        mMovieGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PopularMovie.ResultsBean bean = (PopularMovie.ResultsBean) parent.getItemAtPosition(position);
                DetailActivity.actionStart(getActivity(), bean);

            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (isOnline()){
            updatePopularMovies();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void updatePopularMovies() {
        String orderUnitInfo = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.order), getString(R.string.popular_movie));

        FetchPopularMovieTask popularMovieTask = new FetchPopularMovieTask();
        popularMovieTask.execute(orderUnitInfo);
    }

    public class FetchPopularMovieTask extends AsyncTask<String, Void, List<PopularMovie.ResultsBean>> {

        private final String LOG_TAG = FetchPopularMovieTask.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private List<PopularMovie.ResultsBean> getMovieDataFromJson(String forecastJsonStr)
                throws JSONException {

            Gson data = new GsonBuilder().create();
            PopularMovie movie = data.fromJson(forecastJsonStr, PopularMovie.class);
            List<PopularMovie.ResultsBean> results = movie.getResults();
            return results;
        }

        @Override
        protected List<PopularMovie.ResultsBean> doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String populatMovieJsonStr = null;

            String urlSuffix;
            String page = "1";
            if (params[0].equals(getString(R.string.popular_movie))) {
                urlSuffix = "popular";
            } else {
                urlSuffix = "top_rated";
            }

            try {
                Uri builtUri = Uri.parse(Constant.MOVIE_BASE_URL + urlSuffix).buildUpon()
                        .appendQueryParameter(Constant.API_KEY, BuildConfig.OPEN_POPULAR_MOVIE_API_KEY)
                        .appendQueryParameter(Constant.PAGE, page)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                populatMovieJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(populatMovieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<PopularMovie.ResultsBean> dataList) {
            if (dataList != null && dataList.size() > 0) {
                mMovieAdapter.clear();
                mMovieAdapter.addAll(dataList);
            }
        }
    }
}
