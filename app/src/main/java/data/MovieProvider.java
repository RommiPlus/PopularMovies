package data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import data.MovieContract.MovieDetailEntry;
import data.MovieContract.MovieReviewsEntry;
import data.MovieContract.MovieVideoEntry;


/**
 * Created by 123 on 2016/10/9.
 */

public class MovieProvider extends ContentProvider {
    // TODO: Movie detail and movie review and movie video three tables inner join

    public static final UriMatcher sUriMatcher = buildUriMatcher();
    private PopularMovieDbHelper mMovieDbHelper;

    public static final int MOVIE_DETAIL = 100;
    public static final int MOVIE_WHOLE_INFO = 101;
    public static final int MOVIE_REVIEWS = 200;
    public static final int MOVIE_VIDEO = 300;

    private static final SQLiteQueryBuilder sMovieWholeInfoQueryBuilder;

    static {
        sMovieWholeInfoQueryBuilder = new SQLiteQueryBuilder();

        sMovieWholeInfoQueryBuilder.setTables(
                MovieDetailEntry.TABLE_NAME + " INNER JOIN " +
                    MovieReviewsEntry.TABLE_NAME +
                    " ON " + MovieDetailEntry.TABLE_NAME +
                    "." + MovieDetailEntry.COLUMN_MOVIE_ID +
                    " = " + MovieReviewsEntry.TABLE_NAME +
                    "." + MovieReviewsEntry.COLUMN_MOVIE_ID +
                    " INNER JOIN " + MovieVideoEntry.TABLE_NAME +
                        " ON " + MovieDetailEntry.TABLE_NAME +
                        "." + MovieDetailEntry.COLUMN_MOVIE_ID +
                        " = " + MovieVideoEntry.TABLE_NAME +
                        "." + MovieVideoEntry.COLUMN_MOVIE_ID);
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE_DETAIL, MOVIE_DETAIL);
        // * represent number, # represent string.
        matcher.addURI(authority, MovieContract.PATH_MOVIE_DETAIL + "/*", MOVIE_WHOLE_INFO);
        matcher.addURI(authority, MovieContract.PATH_MOVIE_REVIEW, MOVIE_REVIEWS);
        matcher.addURI(authority, MovieContract.PATH_MOVIE_VIDEO, MOVIE_VIDEO);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mMovieDbHelper = new PopularMovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();
        Cursor queryCursor;
        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE_WHOLE_INFO:
                String movieId = MovieDetailEntry.getMovieIdInfoFromUri(uri);
                selection = MovieDetailEntry.TABLE_NAME + "." + MovieDetailEntry.COLUMN_MOVIE_ID + " = ? ";
                selectionArgs = new String[] { movieId };
                queryCursor = sMovieWholeInfoQueryBuilder.query(
                        db, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case MOVIE_DETAIL:
                queryCursor = db.query(MovieDetailEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case MOVIE_REVIEWS:
                queryCursor = db.query(MovieReviewsEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case MOVIE_VIDEO:
                queryCursor = db.query(MovieVideoEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        queryCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return queryCursor;
    }

    /**
     * This is used when user call ContentResolver.getType() and return type of given uri.
     *
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE_DETAIL:
                return MovieDetailEntry.CONTENT_TYPE;

            case MOVIE_REVIEWS:
                return MovieReviewsEntry.CONTENT_TYPE;

            case MOVIE_VIDEO:
                return MovieVideoEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE_DETAIL: {
                long _id = db.insert(MovieDetailEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieDetailEntry.buildMovieDetailUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case MOVIE_REVIEWS: {
                long _id = db.insert(MovieReviewsEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieReviewsEntry.buildMovieReviewsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case MOVIE_VIDEO: {
                long _id = db.insert(MovieVideoEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieVideoEntry.buildMovieVideoUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(returnUri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int deletedNumbers;
        if (selection == null) selection = "1";
        switch (match) {
            case MOVIE_DETAIL:
                deletedNumbers = db.delete(
                        MovieDetailEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case MOVIE_REVIEWS:
                deletedNumbers = db.delete(
                        MovieReviewsEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case MOVIE_VIDEO:
                deletedNumbers = db.delete(
                        MovieVideoEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (deletedNumbers != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deletedNumbers;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int updateNumbers;

        switch (match) {
            case MOVIE_DETAIL:
                updateNumbers = db.update(MovieDetailEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;

            case MOVIE_REVIEWS:
                updateNumbers = db.update(MovieReviewsEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;

            case MOVIE_VIDEO:
                updateNumbers = db.update(MovieVideoEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (updateNumbers != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updateNumbers;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE_DETAIL: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues contentValues : values) {
                        long _id = db.insert(MovieDetailEntry.TABLE_NAME, null, contentValues);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            default:
                return super.bulkInsert(uri, values);
        }
    }
}
