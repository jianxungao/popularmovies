package com.biz.timux.popularmovies1;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;


import com.biz.timux.popularmovies1.data.MovieContract;
import com.biz.timux.popularmovies1.data.MovieContract.MovieEntry;
import com.biz.timux.popularmovies1.data.MovieContract.MyFavMovieEntry;
import com.biz.timux.popularmovies1.data.MovieDbHelper;

/**
 * Created by gaojianxun on 15/9/8.
 */
public class TestProvider extends AndroidTestCase {

    public static final String TAG = TestProvider.class.getSimpleName();

    // The target api annotation is needed for the call to keySet -- we wouldn't want
    // to use this in our app, but in a test it's fine to assume a higher target
    void addAllContentValues(ContentValues destination, ContentValues source) {
        for (String key : source.keySet())
            destination.put(key, source.getAsString(key));
    }

    public void testDeleteDb() throws Throwable {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }



    public void testInsertReadProvider() {
        // If there's an error in those massive SQL table creation Strings,
        // errors will be throw here when you try to get a writable database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Create a new map of values, where columns names are the keys
        ContentValues testMyFavMovieValues = TestDb.createMyFavMovieValues();

        long aRowId;
        aRowId = db.insert(MyFavMovieEntry.TABLE_NAME, null, testMyFavMovieValues);

        // Verify we got a row back
        assertTrue(aRowId != -1);
        Log.d(TAG, "New row id: " + aRowId);

        // A cursor is your primary interface to the query results.
        Cursor myFavMovieCursor = mContext.getContentResolver().query(
                MyFavMovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestDb.validateCursor(myFavMovieCursor, testMyFavMovieValues);
        myFavMovieCursor.close();


        ContentValues testMoiveValues = TestDb.createMovieValues();

        long mRowId = db.insert(MovieEntry.TABLE_NAME, null,  testMoiveValues);

        assertTrue(mRowId != -1);
        Log.d(TAG, "Movie Row Id is " + mRowId);

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,       // leaving "columns" null just returns all the columns
                null,       // columns for "where" clause
                null,       // values for "where" clause
                null        // columns to group by
        );

        TestDb.validateCursor(movieCursor, testMoiveValues);
        movieCursor.close();

        // Add the location values in with the weather data so that we can make
        // sure that the join worked and we actually get all the values back
        addAllContentValues(testMoiveValues, testMyFavMovieValues);

        // Get the joined Movie and My Fav movie data
        Cursor myFavoritMovieCursor = mContext.getContentResolver().query(
                MovieEntry.buildMovieIdUri(TestDb.TEST_MOVIE_ID),
                null,
                null,
                null,
                null
        );

        TestDb.validateCursor(myFavoritMovieCursor, testMoiveValues);
        myFavoritMovieCursor.close();



        dbHelper.close();
    }

    public void testGetType() {
        // content://com.biz.timux.popularmovies1/movie/
        String type = mContext.getContentResolver().getType(MovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.biz.timux.popularmovies1/movie
        assertEquals(MovieEntry.CONTENT_TYPE, type);

        int testMovieId = 76341;
        // content://com.biz.timux.popularmovies1/movie
        type = mContext.getContentResolver().getType(
                MovieEntry.buildMovieIdUri(testMovieId));
        // vnd.android.cursor.item/com.biz.timux.popularmovies1/movie
        assertEquals(MovieEntry.CONTENT_ITEM_TYPE, type);


        // content://com.biz.timux.popularmovies1/my_fav_movie
        type = mContext.getContentResolver().getType(MyFavMovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.biz.timux.popularmovies1/my_fav_movie
        assertEquals(MyFavMovieEntry.CONTENT_TYPE, type);


    }
}