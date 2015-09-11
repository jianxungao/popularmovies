package com.biz.timux.popularmovies1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.biz.timux.popularmovies1.data.MovieContract.MovieEntry;
import com.biz.timux.popularmovies1.data.MovieContract.MyFavMovieEntry;

/**
 * Created by gaojianxun on 15/9/6.
 */
public class MovieDbHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 2;

    public static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table to hold locations. A location consists of the string supplied in
        // the location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY, " +

                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +

                MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_DESC + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_BACKDROP_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +

                MovieEntry.COLUMN_MOVIE_POPULARITY + " REAL NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_VOTE + " REAL NOT NULL, " +

                MovieEntry.COLUMN_MOVIE_HAS_VIDEO + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_SORT + " TEXT NOT NULL);";



        // Create a table to hold weather
        final String SQL_CREATE_MY_FAV_TABLE = "CREATE TABLE " + MyFavMovieEntry.TABLE_NAME + " (" +
                // Store the favorite movie
                MyFavMovieEntry._ID + " INTEGER PRIMARY KEY, " +

                MyFavMovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_MY_FAV_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        // persistent the user data
        db.execSQL("DROP TABLE IF EXISTS " + MyFavMovieEntry.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

