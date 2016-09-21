package fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.popularmovies.DetailActivity;
import com.popularmovies.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import adapter.MovieAdapter;
import data.Constant;
import model.MovieDetailInfo;
import model.Reviews;
import model.Videos;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private MovieDetailInfo mMovieDetailInfo;
    private List<Videos.ResultsBean> mVideoData;
    private List<Reviews.ResultsBean> mReviews;

    private static final int ERROR_MOVIE_ID = -1;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        int movieId = intent.getIntExtra(DetailActivity.EXTRA_BUNDLE, ERROR_MOVIE_ID);

        if (movieId != ERROR_MOVIE_ID) {
            queryMovieInfo(movieId);
        }

//        if (mMovieData != null) {
//            ((TextView)view.findViewById(R.id.movie_title)).setText(mMovieData.getTitle());
//            Picasso.with(getContext()).load(
//                    MovieAdapter.POPULAR_MOVIE + mMovieData.getPosterPath())
//                    .into((ImageView)view.findViewById(R.id.movie_img));
//            ((TextView)view.findViewById(R.id.movie_date_time)).setText(mMovieData.getReleaseDate());
//            ((TextView)view.findViewById(R.id.movie_vote_average)).setText(String.valueOf(mMovieData.getVoteAverage()));
//            ((TextView)view.findViewById(R.id.movie_overview)).setText(mMovieData.getOverview());
//        }
        return view;
    }

    private void queryMovieInfo(int movieId) {
        FetchVideos fetchVideoTask = new FetchVideos();
        fetchVideoTask.execute(movieId);
    }

    public class FetchMovieDetailInfo extends AsyncTask<Integer, Void, MovieDetailInfo> {

        private final String LOG_TAG = FetchMovieDetailInfo.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private MovieDetailInfo getMovieDetailDataFromJson(String forecastJsonStr)
                throws JSONException {
            Gson data = new GsonBuilder().create();
            return data.fromJson(forecastJsonStr, MovieDetailInfo.class);
        }

        @Override
        protected MovieDetailInfo doInBackground(Integer... params) {

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

            try {
                Uri builtUri = Uri.parse(Constant.MOVIE_BASE_URL + params[0]);

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
                return getMovieDetailDataFromJson(populatMovieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(MovieDetailInfo info) {
            mMovieDetailInfo = info;

            FetchReviews fetchReviewsTask = new FetchReviews();
            fetchReviewsTask.execute(info.getId());
        }
    }

    public class FetchVideos extends AsyncTask<Integer, Void, List<Videos.ResultsBean>> {

        private final String LOG_TAG = FetchVideos.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private List<Videos.ResultsBean> getVideoDataFromJson(String forecastJsonStr)
                throws JSONException {
            Gson data = new GsonBuilder().create();
            Videos videos = data.fromJson(forecastJsonStr, Videos.class);
            List<Videos.ResultsBean> results = videos.getResults();
            return results;
        }

        @Override
        protected List<Videos.ResultsBean> doInBackground(Integer... params) {

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

            try {
                Uri builtUri = Uri.parse(Constant.MOVIE_BASE_URL + params[0] + "/videos");

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
                return getVideoDataFromJson(populatMovieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<Videos.ResultsBean> dataList) {
            if (dataList != null && dataList.size() > 0) {
                mVideoData = dataList;
            }

            FetchReviews fetchReviewsTask = new FetchReviews();
            fetchReviewsTask.execute(mMovieDetailInfo.getId());
        }
    }

    public class FetchReviews extends AsyncTask<Integer, Void, List<Reviews.ResultsBean>> {

        private final String LOG_TAG = FetchReviews.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private List<Reviews.ResultsBean> getReviewsDataFromJson(String forecastJsonStr)
                throws JSONException {

            Gson data = new GsonBuilder().create();
            Reviews reviews = data.fromJson(forecastJsonStr, Reviews.class);
            List<Reviews.ResultsBean> results = reviews.getResults();
            return results;
        }

        @Override
        protected List<Reviews.ResultsBean> doInBackground(Integer... params) {

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

            try {
                Uri builtUri = Uri.parse(Constant.MOVIE_BASE_URL + params[0] + "/reviews");

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
                return getReviewsDataFromJson(populatMovieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<Reviews.ResultsBean> dataList) {
            if (dataList != null && dataList.size() > 0) {
                mReviews = dataList;
            }

            updateUI();
        }
    }

    private void updateUI() {
        ((TextView) getView().findViewById(R.id.movie_title)).setText(mMovieDetailInfo.getTitle());
        Picasso.with(getContext()).load(
                MovieAdapter.POPULAR_MOVIE + mMovieDetailInfo.getPosterPath())
                .into((ImageView) getView().findViewById(R.id.movie_img));
        ((TextView) getView().findViewById(R.id.movie_date_time)).setText(mMovieDetailInfo.getReleaseDate());
        ((TextView) getView().findViewById(R.id.movie_vote_average)).setText(
                String.valueOf(mMovieDetailInfo.getVoteAverage()) + "/10");
        ((TextView) getView().findViewById(R.id.movie_overview)).setText(mMovieDetailInfo.getOverview());
    }
}
