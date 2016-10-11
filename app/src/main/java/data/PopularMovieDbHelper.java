package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import data.MovieContract.MovieDetailEntry;
import data.MovieContract.MovieReviewsEntry;
import data.MovieContract.MovieVideoEntry;
/**
 * Created by Administrator on 2016/10/6.
 */

public class PopularMovieDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "movie.db";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public PopularMovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_DETAIL_TABLE = "CREATE TABLE " + MovieDetailEntry.TABLE_NAME + " (" +
                MovieDetailEntry._ID + " INTEGER PRIMARY KEY," +
                MovieDetailEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL , " +
                MovieDetailEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieDetailEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieDetailEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieDetailEntry.COLUMN_RUNTIME + " INTEGER NOT NULL, " +
                MovieDetailEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieDetailEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieDetailEntry.COLUMN_STAR + " INTEGER NOT NULL, " +

                // To assure the application have just one movie id entry for all data
                // it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + MovieDetailEntry.COLUMN_MOVIE_ID  + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_MOVIE_REVIEW_TABLE = "CREATE TABLE " + MovieReviewsEntry.TABLE_NAME + " (" +
                MovieReviewsEntry._ID + " INTEGER PRIMARY KEY," +
                MovieReviewsEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieReviewsEntry.COLUMN_REVIEW_ID + " INTEGER NOT NULL, " +
                MovieReviewsEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                MovieReviewsEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +

                // Set up the movie id as a foreign key to movie detail table.
                " FOREIGN KEY (" + MovieReviewsEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieDetailEntry.TABLE_NAME + " (" + MovieDetailEntry.COLUMN_MOVIE_ID + "), " +

                // To assure the application have just one review id entry for all data
                // it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + MovieReviewsEntry.COLUMN_REVIEW_ID  + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_MOVIE_VIDEO_TABLE = "CREATE TABLE " + MovieVideoEntry.TABLE_NAME + " (" +
                MovieVideoEntry._ID + " INTEGER PRIMARY KEY," +
                MovieVideoEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieVideoEntry.COLUMN_VIDEO_ID + " INTEGER NOT NULL, " +
                MovieVideoEntry.COLUMN_KEY + " TEXT NOT NULL, " +

                // Set up the movie id as a foreign key to movie detail table.
                " FOREIGN KEY (" + MovieVideoEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieDetailEntry.TABLE_NAME + " (" + MovieDetailEntry.COLUMN_MOVIE_ID + "), " +

                // To assure the application have just one video id entry for all data
                // it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + MovieVideoEntry.COLUMN_VIDEO_ID  + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_MOVIE_DETAIL_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_REVIEW_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_VIDEO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        db.execSQL("DROP TABLE IF EXISTS " + MovieDetailEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieReviewsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieVideoEntry.TABLE_NAME);
        onCreate(db);
    }
}
