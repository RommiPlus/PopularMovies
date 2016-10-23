package service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.popularmovies.BuildConfig;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import data.Constant;
import data.MovieContract;
import model.MovieDetailInfo;
import model.Reviews;
import model.Videos;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class DataSyncIntentService extends IntentService {
    public static String MOVIE_ID = "movie_id";

    private final String LOG_TAG = DataSyncIntentService.class.getSimpleName();

    public DataSyncIntentService() {
        super("DataSyncIntentService");
    }

    public static void startService(Context context, int movieId) {
        Intent intent = new Intent(context, DataSyncIntentService.class);
        intent.putExtra(MOVIE_ID, movieId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int movieId = intent.getIntExtra(MOVIE_ID, -1);
        if (movieId == -1) {
            return;
        }

        String movieDetailInfo = FetchMovieDetailInfo(movieId);
        String videoInfo = fetchVideos(movieId);
        String reviewsInfo = fetchReviews(movieId);

        try {
            if (movieDetailInfo != null) {
                insertMovieDetailDataToDb(movieDetailInfo);
            }

            if (reviewsInfo != null) {
                insertReviewsDataToDb(reviewsInfo);
            }

            if (videoInfo != null) {
                insertVideoDataToDb(videoInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p/>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private int insertMovieDetailDataToDb(String forecastJsonStr)
            throws JSONException {
        Gson data = new GsonBuilder().create();
        MovieDetailInfo info = data.fromJson(forecastJsonStr, MovieDetailInfo.class);

        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieDetailEntry.COLUMN_RUNTIME, info.getRuntime());
        getContentResolver().update(MovieContract.MovieDetailEntry.CONTENT_URI,
                values,
                MovieContract.MovieDetailEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(info.getId())});
        return info.getId();
    }

    public String FetchMovieDetailInfo(int movieId) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String populatMovieJsonStr = null;

        try {
            Uri builtUri = Uri.parse(Constant.MOVIE_BASE_URL + movieId).buildUpon()
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

        return populatMovieJsonStr;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p/>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private int insertVideoDataToDb(String forecastJsonStr)
            throws JSONException {
        Gson data = new GsonBuilder().create();
        Videos videos = data.fromJson(forecastJsonStr, Videos.class);
        List<Videos.ResultsBean> results = videos.getResults();

        Vector<ContentValues> info = new Vector<>();
        for (Videos.ResultsBean movieData : results) {
            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieVideoEntry.COLUMN_VIDEO_ID, movieData.getId());
            values.put(MovieContract.MovieVideoEntry.COLUMN_MOVIE_ID, videos.getId());
            values.put(MovieContract.MovieVideoEntry.COLUMN_KEY, movieData.getKey());
            values.put(MovieContract.MovieVideoEntry.COLUMN_NAME, movieData.getName());

            info.add(values);
        }

        int inserted = 0;
        if (results.size() > 0) {
            ContentValues[] values = new ContentValues[results.size()];
            info.toArray(values);
            inserted = getContentResolver().bulkInsert(MovieContract.MovieVideoEntry.CONTENT_URI, values);
        }

        Log.d(LOG_TAG, "FetchPopularMovieTask Complete. " + inserted + " Inserted");
        return videos.getId();
    }

    public String fetchVideos(int movieId) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String populatMovieJsonStr = null;

        try {
            Uri builtUri = Uri.parse(Constant.MOVIE_BASE_URL + movieId + "/videos")
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

        return populatMovieJsonStr;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p/>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void insertReviewsDataToDb(String forecastJsonStr)
            throws JSONException {
        Gson data = new GsonBuilder().create();
        Reviews reviews = data.fromJson(forecastJsonStr, Reviews.class);
        List<Reviews.ResultsBean> results = reviews.getResults();

        Vector<ContentValues> info = new Vector<>();
        for (Reviews.ResultsBean movieData : results) {
            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieReviewsEntry.COLUMN_REVIEW_ID, movieData.getId());
            values.put(MovieContract.MovieReviewsEntry.COLUMN_MOVIE_ID, reviews.getId());
            values.put(MovieContract.MovieReviewsEntry.COLUMN_CONTENT, movieData.getContent());
            values.put(MovieContract.MovieReviewsEntry.COLUMN_AUTHOR, movieData.getAuthor());

            info.add(values);
        }

        int inserted = 0;
        if (results.size() > 0) {
            ContentValues[] values = new ContentValues[results.size()];
            info.toArray(values);
            inserted = getContentResolver().bulkInsert(MovieContract.MovieReviewsEntry.CONTENT_URI, values);
        }

        Log.d(LOG_TAG, "FetchPopularMovieTask Complete. " + inserted + " Inserted");
    }

    public String fetchReviews(int movieId) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String populatMovieJsonStr = null;

        try {
            Uri builtUri = Uri.parse(Constant.MOVIE_BASE_URL + movieId + "/reviews")
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

        return populatMovieJsonStr;
    }
}
