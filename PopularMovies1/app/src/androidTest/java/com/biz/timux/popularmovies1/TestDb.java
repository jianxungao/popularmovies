package com.biz.timux.popularmovies1;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.biz.timux.popularmovies1.data.MovieContract.MyFavMovieEntry;
import com.biz.timux.popularmovies1.data.MovieContract.MovieEntry;
import com.biz.timux.popularmovies1.data.MovieDbHelper;


import java.util.Map;
import java.util.Set;

/**
 * Test runner will execute all the functions in this class that start with "test"
 * and in the order by which they are declared here.
 * Each function should have at least one assert function
 */
public class TestDb extends AndroidTestCase {

    public static final String TAG = TestDb.class.getSimpleName();

    public static String TEST_BACKDROP_PATH = "/tbhdm8UJAb4ViCTsulYFL3lxMCd.jpg";

    public static int  TEST_MOVIE_ID = 76341;
    public static String  TEST_OVERVIEW = "An apocalyptic story set in the furthest reaches of our " +
            "planet, in a stark desert landscape where humanity is broken, and most everyone is " +
            "crazed fighting for the necessities of life. Within this world exist two rebels " +
            "on the run who just might be able to restore order. There's Max, a man of action and " +
            "a man of few words, who seeks peace of mind following the loss of his wife and child" +
            " in the aftermath of the chaos. And Furiosa, a woman of action and a woman who believes" +
            " her path to survival may be achieved if she can make it across the desert back to " +
            "her childhood homeland.";
    public static String  TEST_RELEASE_DATE = "2015-05-15";
    public static String  TEST_POSTER_PATH =  "/kqjL17yufvn9OVLyXYpvtyrFfak.jpg";
    public static double  TEST_POPULARITY =  48.6504;
    public static String  TEST_TITLE =  "Mad Max: Fury Road";
    public static int  TEST_VIDEO =   0;
    public static double  TEST_VOTE =    7.7;



    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry.COLUMN_MOVIE_ID, TEST_MOVIE_ID);
        movieValues.put(MovieEntry.COLUMN_MOVIE_TITLE, TEST_TITLE);
        movieValues.put(MovieEntry.COLUMN_MOVIE_DESC, TEST_OVERVIEW);
        movieValues.put(MovieEntry.COLUMN_MOVIE_BACKDROP_PATH, TEST_BACKDROP_PATH);
        movieValues.put(MovieEntry.COLUMN_MOVIE_POSTER_PATH, TEST_POSTER_PATH);
        movieValues.put(MovieEntry.COLUMN_MOVIE_RELEASE_DATE, TEST_RELEASE_DATE);
        movieValues.put(MovieEntry.COLUMN_MOVIE_POPULARITY, TEST_POPULARITY);
        movieValues.put(MovieEntry.COLUMN_MOVIE_VOTE, TEST_VOTE);
        movieValues.put(MovieEntry.COLUMN_MOVIE_HAS_VIDEO, TEST_VIDEO);

        return movieValues;
    }

    static ContentValues createMyFavMovieValues() {
        ContentValues values = new ContentValues();
        values.put(MyFavMovieEntry.COLUMN_MOVIE_ID, TEST_MOVIE_ID);

        return values;
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {
        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int index = valueCursor.getColumnIndex(columnName);
            assertFalse(index == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(index));
        }
    }

    public void testInsertReadDb() {
        // If there's an error in those massive SQL table creation Strings,
        // errors will be throw here when you try to get a writable database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Create a new map of values, where columns names are the keys
        ContentValues values = createMyFavMovieValues();

        long myFavMovieRowId;
        myFavMovieRowId = db.insert(MyFavMovieEntry.TABLE_NAME, null, values);

        // Verify we got a row back
        assertTrue(myFavMovieRowId != -1);
        Log.d(TAG, "New row id: " + myFavMovieRowId);

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                MyFavMovieEntry.TABLE_NAME,   // Table to query
                null,       // Columns for the query to get
                null,       // Columns for the "where" clause
                null,       // Values for the "where" clause
                null,       // Columns to group by
                null,       // Columns to filter by row groups
                null        // sort order
        );

        validateCursor(cursor, values);
        cursor.close();

        ContentValues movieValues = createMovieValues();

        long movieRowId = db.insert(MovieEntry.TABLE_NAME, null, movieValues);

        assertTrue(movieRowId != -1);
        Log.d(TAG, "Movie Row Id is " + movieRowId);

        Cursor movieTableCursor = db.query(
                MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        validateCursor(movieTableCursor, movieValues);
        cursor.close();

        dbHelper.close();
    }
}
