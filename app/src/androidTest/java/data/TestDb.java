package data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by 123 on 2016/10/10.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TestDb {

    public static final String LOG_TAG = TestDb.class.getSimpleName();
    private Context mContext;

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(PopularMovieDbHelper.DATABASE_NAME);
    }


    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
        deleteTheDatabase();
    }

    @Test
    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(MovieContract.MovieDetailEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.MovieReviewsEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.MovieVideoEntry.TABLE_NAME);

        mContext.deleteDatabase(PopularMovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new PopularMovieDbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor cursor = db.rawQuery("SELECT name from sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly",
                cursor.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(cursor.getString(0));
        } while(cursor.moveToNext());

        assertTrue("Error: Your database was created without both the movie detail entry " +
                "and movie reviews and movie video entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        cursor = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieDetailEntry.TABLE_NAME +
                ");", null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                cursor.moveToFirst());

        final HashSet<String> detailColumnHashSet = new HashSet<>();
        detailColumnHashSet.add(MovieContract.MovieDetailEntry._ID);
        detailColumnHashSet.add(MovieContract.MovieDetailEntry.COLUMN_MOVIE_ID);
        detailColumnHashSet.add(MovieContract.MovieDetailEntry.COLUMN_TITLE);
        detailColumnHashSet.add(MovieContract.MovieDetailEntry.COLUMN_OVERVIEW);
        detailColumnHashSet.add(MovieContract.MovieDetailEntry.COLUMN_RELEASE_DATE);
        detailColumnHashSet.add(MovieContract.MovieDetailEntry.COLUMN_RUNTIME);
        detailColumnHashSet.add(MovieContract.MovieDetailEntry.COLUMN_VOTE_AVERAGE);
        detailColumnHashSet.add(MovieContract.MovieDetailEntry.COLUMN_STAR);
        detailColumnHashSet.add(MovieContract.MovieDetailEntry.COLUMN_POSTER_PATH);

        int columnIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnIndex);
            detailColumnHashSet.remove(columnName);
        } while (cursor.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                detailColumnHashSet.isEmpty());
        db.close();
    }

    @Test
    public void testMovieDetailTable() {
        insertMovieDetailInfo();
    }

    @Test
    public void testMovieReviesTable() {
        long locationRowId = insertMovieDetailInfo();

        // Make sure we have a valid row ID.
        assertFalse("Error: Location Not Inserted Correctly", locationRowId == -1L);

        PopularMovieDbHelper dbHelper = new PopularMovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = TestUtilities.createMovieReviesInfo(locationRowId);
        long insertedRowId = db.insert(MovieContract.MovieReviewsEntry.TABLE_NAME, null, values);

        assertTrue(insertedRowId != -1);

        Cursor cursor = db.query(MovieContract.MovieReviewsEntry.TABLE_NAME, null, null, null,
                null, null, null);

        assertTrue("Error: No Records returned from location query", cursor.moveToFirst());

        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, values);

        assertFalse("Error: More than one record returned from location query",
                cursor.moveToNext());

        cursor.close();
        db.close();
    }

    @Test
    public void testMovieVideoTable() {
        long locationRowId = insertMovieDetailInfo();

        // Make sure we have a valid row ID.
        assertFalse("Error: Location Not Inserted Correctly", locationRowId == -1L);

        PopularMovieDbHelper dbHelper = new PopularMovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = TestUtilities.createMovieVedioInfo(locationRowId);
        long insertedRowId = db.insert(MovieContract.MovieVideoEntry.TABLE_NAME, null, values);

        assertTrue(insertedRowId != -1);

        Cursor cursor = db.query(MovieContract.MovieVideoEntry.TABLE_NAME, null, null, null,
                null, null, null);

        assertTrue("Error: No Records returned from location query", cursor.moveToFirst());

        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, values);

        assertFalse("Error: More than one record returned from location query",
                cursor.moveToNext());

        cursor.close();
        db.close();
    }

    public long insertMovieDetailInfo() {
        PopularMovieDbHelper dbHelper = new PopularMovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = TestUtilities.createMovieDetailInfo();
        long insertedRowId = db.insert(MovieContract.MovieDetailEntry.TABLE_NAME, null, values);

        // Verify we got a row back.
        assertTrue(insertedRowId != -1);

        Cursor cursor = db.query(MovieContract.MovieDetailEntry.TABLE_NAME, null, null, null,
                null, null, null);

        assertTrue("Error: No Records returned from location query", cursor.moveToFirst());

        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, values);

        assertFalse("Error: More than one record returned from location query",
                cursor.moveToNext());

        cursor.close();
        db.close();
        return insertedRowId;
    }

}
