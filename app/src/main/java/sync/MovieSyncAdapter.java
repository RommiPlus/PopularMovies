package sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.SQLException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.popularmovies.BuildConfig;
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

import data.Constant;
import data.MovieContract;
import model.PopularMovie;

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the weather, in seconds.
    public static final int SYNC_INTERVAL = 60 * 60 * 3;  // 3 hours
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;


    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        String orderInfo = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString(getContext().getString(R.string.order),
                        getContext().getString(R.string.popular_movie));

        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (orderInfo.isEmpty()) {
            return;
        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String populatMovieJsonStr = null;

        String urlSuffix;
        String page = "1";
        if (orderInfo.equals(getContext().getString(R.string.popular_movie))) {
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
                return;
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
                return;
            }
            populatMovieJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return;
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
        return;
    }

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

        String orderInfo = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString(getContext().getString(R.string.order),
                        getContext().getString(R.string.popular_movie));

        Vector<ContentValues> info;
        if (orderInfo.equals(getContext().getString(R.string.popular_movie))) {
            info = createMovieDetailVectors(results, MovieContract.MovieDetailEntry.COLUMN_POP_MOVIES,
                    Constant.IS_POP_MOVIE);
        } else {
            info = createMovieDetailVectors(results, MovieContract.MovieDetailEntry.COLUMN_TOP_RANKED,
                    Constant.IS_TOP_RANKED);
        }

        for (ContentValues value : info) {
            try {
                getContext().getContentResolver().insert(MovieContract.MovieDetailEntry.CONTENT_URI, value);
            } catch (SQLException e) {
                int movieId = value.getAsInteger(MovieContract.MovieDetailEntry.COLUMN_MOVIE_ID);
                value.remove(MovieContract.MovieDetailEntry.COLUMN_MOVIE_ID);
                getContext().getContentResolver().update(MovieContract.MovieDetailEntry.CONTENT_URI, value,
                        MovieContract.MovieDetailEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(movieId)});
            }
        }
    }

    private Vector<ContentValues> createMovieDetailVectors(
            List<PopularMovie.ResultsBean> results, String column, int flag) {
        Vector<ContentValues> info = new Vector<>();
        for (PopularMovie.ResultsBean movieData : results) {
            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieDetailEntry.COLUMN_MOVIE_ID, movieData.getId());
            values.put(MovieContract.MovieDetailEntry.COLUMN_TITLE, movieData.getTitle());
            values.put(MovieContract.MovieDetailEntry.COLUMN_OVERVIEW, movieData.getOverview());
            values.put(MovieContract.MovieDetailEntry.COLUMN_RELEASE_DATE, movieData.getReleaseDate());
            values.put(MovieContract.MovieDetailEntry.COLUMN_VOTE_AVERAGE, movieData.getVoteAverage());
            values.put(MovieContract.MovieDetailEntry.COLUMN_POSTER_PATH, movieData.getPosterPath());
            values.put(column, flag);

            info.add(values);
        }
        return info;
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.app_name));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

}