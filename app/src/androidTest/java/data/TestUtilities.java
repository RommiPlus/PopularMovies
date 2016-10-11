package data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;

import java.util.Map;
import java.util.Set;

import data.MovieContract.MovieDetailEntry;
import data.MovieContract.MovieReviewsEntry;
import data.MovieContract.MovieVideoEntry;
import utils.PollingCheck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by 123 on 2016/10/10.
 */

public class TestUtilities {

    static final int TEST_MOVIE_ID = 1;

    static ContentValues createMovieDetailInfo() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieDetailEntry.COLUMN_MOVIE_ID, TEST_MOVIE_ID);
        contentValues.put(MovieDetailEntry.COLUMN_OVERVIEW, "This is a test overview");
        contentValues.put(MovieDetailEntry.COLUMN_RELEASE_DATE, "2016-09-03");
        contentValues.put(MovieDetailEntry.COLUMN_RUNTIME, "123");
        contentValues.put(MovieDetailEntry.COLUMN_STAR, Constant.MOVIE_STAR);
        contentValues.put(MovieDetailEntry.COLUMN_TITLE, "Wolf coming");
        contentValues.put(MovieDetailEntry.COLUMN_VOTE_AVERAGE, 6.4);
        contentValues.put(MovieDetailEntry.COLUMN_POSTER_PATH, "JUHSIDHOO");
        return contentValues;
    }

    static ContentValues createMovieReviesInfo(long rowId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieReviewsEntry.COLUMN_MOVIE_ID, TEST_MOVIE_ID);
        contentValues.put(MovieReviewsEntry.COLUMN_AUTHOR, "This is a test overview");
        contentValues.put(MovieReviewsEntry.COLUMN_CONTENT, "2016-09-03");
        contentValues.put(MovieReviewsEntry.COLUMN_REVIEW_ID, "123");
        return contentValues;
    }

    static ContentValues createMovieVedioInfo(long rowId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieVideoEntry.COLUMN_MOVIE_ID, TEST_MOVIE_ID);
        contentValues.put(MovieVideoEntry.COLUMN_KEY, "WAEDJI");
        contentValues.put(MovieVideoEntry.COLUMN_VIDEO_ID, "455");
        return contentValues;
    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor cursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> value : valueSet) {
            String columnName = value.getKey();
            int idx = cursor.getColumnIndex(columnName);
            assertTrue("Column '" + columnName + "' not found. " + error, idx != -1);
            String expectedValue = value.getValue().toString();
            assertEquals("Value '" + value.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, cursor.getString(idx));
        }
    }

    public static long insertMovieDetailValues(Context context) {
        PopularMovieDbHelper dbHelper = new PopularMovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = TestUtilities.createMovieDetailInfo();
        long InsertedRowId = db.insert(MovieContract.MovieDetailEntry.TABLE_NAME, null, values);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", InsertedRowId != -1);

        return InsertedRowId;
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
