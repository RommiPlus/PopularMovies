package data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import data.MovieContract.MovieDetailEntry;
import data.MovieContract.MovieReviewsEntry;
import data.MovieContract.MovieVideoEntry;

import static data.TestDb.LOG_TAG;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by 123 on 2016/10/10.
 */

@RunWith(AndroidJUnit4.class)
public class TestProvider {

    private Context mContext;

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieDetailEntry.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(
                MovieReviewsEntry.CONTENT_URI,
                null,
                null
        );


        mContext.getContentResolver().delete(
                MovieVideoEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MovieDetailEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals("Error: Records not deleted from Weather table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MovieReviewsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Location table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MovieVideoEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Location table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
        deleteAllRecords();
    }

    @Test
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());

        try {
            ProviderInfo info = pm.getProviderInfo(componentName, 0);
            assertEquals("Error: WeatherProvider registered with authority: " + info.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                     MovieContract.CONTENT_AUTHORITY, info.authority);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            // I guess the provider isn't registered correctly.
            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    @Test
    public void testBasicMovieDetailQuery() {
        // insert our test records into the database
        PopularMovieDbHelper dbHelper = new PopularMovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMovieDetailInfo();
        TestUtilities.insertMovieDetailValues(mContext);

        db.close();

        // Test the basic content provider query
        Cursor detailCursor = mContext.getContentResolver().query(
                MovieDetailEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicWeatherQuery", detailCursor, testValues);
    }

    @Test
    public void testReaderProviderInsert() {
        ContentValues values = TestUtilities.createMovieDetailInfo();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieDetailEntry.CONTENT_URI, true, tco);
        Uri movieDetailUri = mContext.getContentResolver().insert(MovieDetailEntry.CONTENT_URI,
                TestUtilities.createMovieDetailInfo());

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long locationRowId = ContentUris.parseId(movieDetailUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieDetailEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating LocationEntry.",
                cursor, values);


        // Fantastic.  Now that we have a location, add some weather!
        ContentValues ReviewValues = TestUtilities.createMovieReviesInfo(locationRowId);
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(MovieReviewsEntry.CONTENT_URI, true, tco);

        Uri reviewInsertUri = mContext.getContentResolver()
                .insert(MovieReviewsEntry.CONTENT_URI, ReviewValues);
        assertTrue(reviewInsertUri != null);

        // Did our content observer get called?  Students:  If this fails, your insert weather
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor reviewCursor = mContext.getContentResolver().query(
                MovieReviewsEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating WeatherEntry insert.",
                reviewCursor, ReviewValues);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues videoValues = TestUtilities.createMovieVedioInfo(locationRowId);
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(MovieVideoEntry.CONTENT_URI, true, tco);

        Uri videoInsertUri = mContext.getContentResolver()
                .insert( MovieVideoEntry.CONTENT_URI, videoValues);
        assertTrue(videoInsertUri != null);

        // Did our content observer get called?  Students:  If this fails, your insert weather
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor videoCursor = mContext.getContentResolver().query(
                MovieVideoEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating WeatherEntry insert.",
                videoCursor, videoValues);

        videoValues.putAll(values);
        videoValues.putAll(ReviewValues);

        Cursor joinedCursor = mContext.getContentResolver().query(
                MovieDetailEntry.buildMovieDetailUri(TestUtilities.TEST_MOVIE_ID),
                null, null, null, null);
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Weather and Location Data.",
                joinedCursor, videoValues);
    }

    @Test
    public void testMovieVideoUpdate() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createMovieDetailInfo();

        Uri locationUri = mContext.getContentResolver().
                insert(MovieDetailEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(MovieDetailEntry.COLUMN_TITLE, "Santa's Village");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor locationCursor = mContext.getContentResolver().query(MovieDetailEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MovieDetailEntry.CONTENT_URI, updatedValues, MovieDetailEntry._ID + "= ?",
                new String[] { Long.toString(locationRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieDetailEntry.CONTENT_URI,
                null,   // projection
                MovieDetailEntry._ID + " = " + locationRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateLocation.  Error validating location entry update.",
                cursor, updatedValues);

        cursor.close();
    }

    static ContentValues[] createBulkInsertMovieDetailValues(long locationRowId) {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues movieDetailValues = new ContentValues();
            movieDetailValues.put(MovieDetailEntry.COLUMN_MOVIE_ID, locationRowId + i);
            movieDetailValues.put(MovieDetailEntry.COLUMN_TITLE, "item: " + i);
            movieDetailValues.put(MovieDetailEntry.COLUMN_VOTE_AVERAGE, 6.6);
            movieDetailValues.put(MovieDetailEntry.COLUMN_STAR, Constant.MOVIE_UNSTAR);
            movieDetailValues.put(MovieDetailEntry.COLUMN_OVERVIEW, "This is item: " + i);
            movieDetailValues.put(MovieDetailEntry.COLUMN_RELEASE_DATE, "2016-12-06");
            movieDetailValues.put(MovieDetailEntry.COLUMN_RUNTIME, 122);
            movieDetailValues.put(MovieDetailEntry.COLUMN_POSTER_PATH, "SKIJDKJOOJ");
            returnContentValues[i] = movieDetailValues;
        }
        return returnContentValues;
    }

    @Test
    public void testMovieDetailBulkInsert() {
        // first, let's create a location value
        ContentValues testValues = TestUtilities.createMovieDetailInfo();
        Uri locationUri = mContext.getContentResolver().insert(MovieDetailEntry.CONTENT_URI, testValues);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieDetailEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testBulkInsert. Error validating LocationEntry.",
                cursor, testValues);

        // Now we can bulkInsert some weather.  In fact, we only implement BulkInsert for weather
        // entries.  With ContentProviders, you really only have to implement the features you
        // use, after all.
        ContentValues[] bulkInsertContentValues = createBulkInsertMovieDetailValues(locationRowId);

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver weatherObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieDetailEntry.CONTENT_URI, true, weatherObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(MovieDetailEntry.CONTENT_URI, bulkInsertContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        weatherObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(weatherObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        cursor = mContext.getContentResolver().query(
                MovieDetailEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating WeatherEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }

    @Test
    public void testMovieDetailInfoUpdate() {
        // first, let's create a location value
        ContentValues testValues = TestUtilities.createMovieDetailInfo();
        Uri locationUri = mContext.getContentResolver().insert(MovieDetailEntry.CONTENT_URI, testValues);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieDetailEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testBulkInsert. Error validating LocationEntry.",
                cursor, testValues);

        ContentValues updateValues = createUpdateMovieDetailValues();
        long updateRowId =  mContext.getContentResolver().update(MovieDetailEntry.CONTENT_URI,
                updateValues, MovieDetailEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(TestUtilities.TEST_MOVIE_ID)});

        assertTrue("update rowId is not same with insert row Id", updateRowId == locationRowId);

        cursor = mContext.getContentResolver().query(
                MovieDetailEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                MovieDetailEntry.COLUMN_MOVIE_ID + " = ?", // cols for "where" clause
                new String[]{String.valueOf(TestUtilities.TEST_MOVIE_ID)}, // values for "where" clause
                null  // sort order
        );

        // and let's make sure they match the ones we created
        assertTrue(cursor.moveToFirst());
        TestUtilities.validateCursor("testBulkInsert. Error validating LocationEntry.",
                    cursor, TestUtilities.createMovieDetailInfo(TestUtilities.TEST_NEW_RUNTIME));

    }

    public ContentValues createUpdateMovieDetailValues() {
        ContentValues values = new ContentValues();
        values.put(MovieDetailEntry.COLUMN_RUNTIME, String.valueOf(TestUtilities.TEST_NEW_RUNTIME));
        return values;
    }

    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertMovieReviewValues(long locationRowId) {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues movieReviewsValues = new ContentValues();
            movieReviewsValues.put(MovieReviewsEntry.COLUMN_REVIEW_ID, i);
            movieReviewsValues.put(MovieReviewsEntry.COLUMN_MOVIE_ID, TestUtilities.TEST_MOVIE_ID);
            movieReviewsValues.put(MovieReviewsEntry.COLUMN_CONTENT, "item: " + i);
            movieReviewsValues.put(MovieReviewsEntry.COLUMN_AUTHOR, "item: " + i);
            returnContentValues[i] = movieReviewsValues;
        }
        return returnContentValues;
    }

    @Test
    public void testMovieReviewsBulkInsert() {
        ContentValues testValues = TestUtilities.createMovieDetailInfo();
        Uri locationUri = mContext.getContentResolver().insert(MovieDetailEntry.CONTENT_URI, testValues);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieDetailEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testBulkInsert. Error validating LocationEntry.",
                cursor, testValues);

        // Now we can bulkInsert some weather.  In fact, we only implement BulkInsert for weather
        // entries.  With ContentProviders, you really only have to implement the features you
        // use, after all.
        ContentValues[] bulkInsertContentValues = createBulkInsertMovieReviewValues(locationRowId);

        TestUtilities.TestContentObserver weatherObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieReviewsEntry.CONTENT_URI, true, weatherObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(MovieReviewsEntry.CONTENT_URI, bulkInsertContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        weatherObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(weatherObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        cursor = mContext.getContentResolver().query(
                MovieReviewsEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating WeatherEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }

    static ContentValues[] createBulkInsertMovieVideoValues(long locationRowId) {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues movieDetailValues = new ContentValues();
            movieDetailValues.put(MovieVideoEntry.COLUMN_VIDEO_ID, i);
            movieDetailValues.put(MovieVideoEntry.COLUMN_MOVIE_ID, TestUtilities.TEST_MOVIE_ID);
            movieDetailValues.put(MovieVideoEntry.COLUMN_KEY, "MNSINS" + i);
            movieDetailValues.put(MovieVideoEntry.COLUMN_NAME, "MNSINS" + i);
            returnContentValues[i] = movieDetailValues;
        }
        return returnContentValues;
    }

    @Test
    public void testMovieVideoBulkInsert() {
        ContentValues testValues = TestUtilities.createMovieDetailInfo();
        Uri locationUri = mContext.getContentResolver().insert(MovieDetailEntry.CONTENT_URI, testValues);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieDetailEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testBulkInsert. Error validating LocationEntry.",
                cursor, testValues);

        // Now we can bulkInsert some weather.  In fact, we only implement BulkInsert for weather
        // entries.  With ContentProviders, you really only have to implement the features you
        // use, after all.
        ContentValues[] bulkInsertContentValues = createBulkInsertMovieVideoValues(locationRowId);

        TestUtilities.TestContentObserver weatherObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieVideoEntry.CONTENT_URI, true, weatherObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(MovieVideoEntry.CONTENT_URI, bulkInsertContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        weatherObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(weatherObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        cursor = mContext.getContentResolver().query(
                MovieVideoEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating WeatherEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }



}
