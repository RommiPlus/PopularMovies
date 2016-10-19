package fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import java.util.List;
import java.util.Vector;

import adapter.MoviePreviewAdapter;
import data.Constant;
import data.MovieContract.MovieDetailEntry;
import model.PopularMovie;
import util.NetworkUtil;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private GridView mMovieGv;
    private MoviePreviewAdapter mMovieAdapter;

    private final int UNIQUE_LOADER = 0;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMovieAdapter = new MoviePreviewAdapter(getContext(), null, 0);
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mMovieGv = (GridView) view.findViewById(R.id.movie_gv);
        mMovieGv.setAdapter(mMovieAdapter);
        mMovieGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getItemAtPosition(position);
                int index = c.getColumnIndex(MovieDetailEntry.COLUMN_MOVIE_ID);
                DetailActivity.actionStart(getActivity(), c.getInt(index));
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(UNIQUE_LOADER, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (NetworkUtil.isOnline(getActivity())) {
            updatePopularMovies();
        }
    }

    private void updatePopularMovies() {
        String orderUnitInfo = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.order), getString(R.string.popular_movie));

        FetchPopularMovieTask popularMovieTask = new FetchPopularMovieTask();
        popularMovieTask.execute(orderUnitInfo);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                MovieDetailEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }


    public class FetchPopularMovieTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchPopularMovieTask.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private void getMovieDataFromJson(String forecastJsonStr)
                throws JSONException {
            Gson data = new GsonBuilder().create();
            PopularMovie movie = data.fromJson(forecastJsonStr, PopularMovie.class);
            List<PopularMovie.ResultsBean> results = movie.getResults();

            Vector<ContentValues> info = new Vector<>();
            for (PopularMovie.ResultsBean movieData : results) {
                ContentValues values = new ContentValues();
                values.put(MovieDetailEntry.COLUMN_MOVIE_ID, movieData.getId());
                values.put(MovieDetailEntry.COLUMN_TITLE, movieData.getTitle());
                values.put(MovieDetailEntry.COLUMN_OVERVIEW, movieData.getOverview());
                values.put(MovieDetailEntry.COLUMN_RELEASE_DATE, movieData.getReleaseDate());
                values.put(MovieDetailEntry.COLUMN_VOTE_AVERAGE, movieData.getVoteAverage());
                values.put(MovieDetailEntry.COLUMN_POSTER_PATH, movieData.getPosterPath());
                values.put(MovieDetailEntry.COLUMN_RUNTIME, 0);
                values.put(MovieDetailEntry.COLUMN_STAR, Constant.MOVIE_UNSTAR);

                info.add(values);
            }

            int inserted = 0;
            if (results.size() > 0) {
                ContentValues[] values = new ContentValues[results.size()];
                info.toArray(values);
                inserted = getContext().getContentResolver().bulkInsert(MovieDetailEntry.CONTENT_URI, values);
            }

            Log.d(LOG_TAG, "FetchPopularMovieTask Complete. " + inserted + " Inserted");
        }

        @Override
        protected Void doInBackground(String... params) {

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
                getMovieDataFromJson(populatMovieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }
    }
}
