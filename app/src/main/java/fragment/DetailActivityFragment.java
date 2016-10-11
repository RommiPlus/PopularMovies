package fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.Vector;

import adapter.DetailAdapter;
import data.Constant;
import data.MovieContract.MovieDetailEntry;
import data.MovieContract.MovieReviewsEntry;
import data.MovieContract.MovieVideoEntry;
import model.MovieDetailInfo;
import model.Reviews;
import model.Videos;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private RecyclerView mRecyclerView;
    private DetailAdapter mDetailAdapter;

    private MovieDetailInfo mMovieDetailInfo;
    private List<Videos.ResultsBean> mVideoData;
    private List<Reviews.ResultsBean> mReviews;

    List<Object> mAdapterData;

    private static final int ERROR_MOVIE_ID = -1;

    private final int DETAIL_ID = 0;

    private int mMovieId;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        mAdapterData = new ArrayList<>();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mDetailAdapter = new DetailAdapter(mAdapterData, getContext());
        mRecyclerView.setAdapter(mDetailAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Intent intent = getActivity().getIntent();
        mMovieId = intent.getIntExtra(DetailActivity.DETAIL_MOVIE_INFO, ERROR_MOVIE_ID);

        if (mMovieId != ERROR_MOVIE_ID) {
            queryMovieInfo(mMovieId);
        }

        return view;
    }

    private void queryMovieInfo(int movieId) {
        FetchMovieDetailInfo fetchMovieDetailInfoTask = new FetchMovieDetailInfo();
        fetchMovieDetailInfoTask.execute(movieId);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), MovieDetailEntry.buildMovieDetailUri(mMovieId),
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public class FetchMovieDetailInfo extends AsyncTask<Integer, Void, Integer> {

        private final String LOG_TAG = FetchMovieDetailInfo.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private int getMovieDetailDataFromJson(String forecastJsonStr)
                throws JSONException {
            Gson data = new GsonBuilder().create();
            MovieDetailInfo info = data.fromJson(forecastJsonStr, MovieDetailInfo.class);

            ContentValues values = new ContentValues();
            values.put(MovieDetailEntry.COLUMN_RUNTIME, info.getRuntime());
            getContext().getContentResolver().update(MovieDetailEntry.CONTENT_URI,
                    values,
                    MovieDetailEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[] {String.valueOf(info.getId())});
            return info.getId();
        }

        @Override
        protected Integer doInBackground(Integer... params) {

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
                Uri builtUri = Uri.parse(Constant.MOVIE_BASE_URL + params[0]).buildUpon()
                        .appendQueryParameter(Constant.API_KEY, BuildConfig.OPEN_POPULAR_MOVIE_API_KEY)
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
                return getMovieDetailDataFromJson(populatMovieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return 0;
        }

        @Override
        protected void onPostExecute(Integer movieId) {
            super.onPostExecute(movieId);

            if (movieId != 0) {
                FetchVideos fetchVideos = new FetchVideos();
                fetchVideos.execute(movieId);
            }
        }
    }

    public class FetchVideos extends AsyncTask<Integer, Void, Integer> {

        private final String LOG_TAG = FetchVideos.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private int getVideoDataFromJson(String forecastJsonStr)
                throws JSONException {
            Gson data = new GsonBuilder().create();
            Videos videos = data.fromJson(forecastJsonStr, Videos.class);
            List<Videos.ResultsBean> results = videos.getResults();

            Vector<ContentValues> info = new Vector<>();
            for (Videos.ResultsBean movieData : results) {
                ContentValues values = new ContentValues();
                values.put(MovieVideoEntry.COLUMN_VIDEO_ID, movieData.getId());
                values.put(MovieVideoEntry.COLUMN_MOVIE_ID, videos.getId());
                values.put(MovieVideoEntry.COLUMN_KEY, movieData.getKey());

                info.add(values);
            }

            int inserted = 0;
            if (results.size() > 0 ) {
                ContentValues[] values = new ContentValues[results.size()];
                info.toArray(values);
                inserted = getContext().getContentResolver().bulkInsert(MovieVideoEntry.CONTENT_URI, values);
            }

            Log.d(LOG_TAG, "FetchPopularMovieTask Complete. " + inserted + " Inserted");
            return videos.getId();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
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
                Uri builtUri = Uri.parse(Constant.MOVIE_BASE_URL + params[0] + "/videos")
                        .buildUpon()
                        .appendQueryParameter(Constant.API_KEY, BuildConfig.OPEN_POPULAR_MOVIE_API_KEY)
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
                return getVideoDataFromJson(populatMovieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return 0;
        }

        @Override
        protected void onPostExecute(Integer movieId) {
            super.onPostExecute(movieId);

            if (movieId != 0) {
                FetchReviews fetchReviews = new FetchReviews();
                fetchReviews.execute(movieId);
            }
        }
    }

    public class FetchReviews extends AsyncTask<Integer, Void, Void> {

        private final String LOG_TAG = FetchReviews.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private void getReviewsDataFromJson(String forecastJsonStr)
                throws JSONException {
            Gson data = new GsonBuilder().create();
            Reviews reviews = data.fromJson(forecastJsonStr, Reviews.class);
            List<Reviews.ResultsBean> results = reviews.getResults();

            Vector<ContentValues> info = new Vector<>();
            for (Reviews.ResultsBean movieData : results) {
                ContentValues values = new ContentValues();
                values.put(MovieReviewsEntry.COLUMN_REVIEW_ID, movieData.getId());
                values.put(MovieReviewsEntry.COLUMN_MOVIE_ID, reviews.getId());
                values.put(MovieReviewsEntry.COLUMN_CONTENT, movieData.getContent());
                values.put(MovieReviewsEntry.COLUMN_AUTHOR, movieData.getAuthor());

                info.add(values);
            }

            int inserted = 0;
            if (results.size() > 0 ) {
                ContentValues[] values = new ContentValues[results.size()];
                info.toArray(values);
                inserted = getContext().getContentResolver().bulkInsert(MovieReviewsEntry.CONTENT_URI, values);
            }

            Log.d(LOG_TAG, "FetchPopularMovieTask Complete. " + inserted + " Inserted");
        }

        @Override
        protected Void doInBackground(Integer... params) {

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
                Uri builtUri = Uri.parse(Constant.MOVIE_BASE_URL + params[0] + "/reviews")
                        .buildUpon()
                        .appendQueryParameter(Constant.API_KEY, BuildConfig.OPEN_POPULAR_MOVIE_API_KEY)
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
                getReviewsDataFromJson(populatMovieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

    }

}
