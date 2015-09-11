package com.biz.timux.popularmovies1.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by gaojianxun on 15/9/6.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.biz.timux.popularmovies1";

    // Use CONTENT AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_MOVIE = "movie";

    public static final String PATH_MOVIE_FAV = "my_fav_movie";

    //public static final String DATE_FORMAT = "yyyyMMdd";


    public static final class MovieEntry implements BaseColumns {

        // Base location to search for the Movie with a Content Provider
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        // Return multiple rows
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        // Return a single row
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";
        // movie id (int)
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        // movie popularity and vote_avg (double)
        public static final String COLUMN_MOVIE_POPULARITY = "movie_pop";
        public static final String COLUMN_MOVIE_VOTE = "movie_vote";

        public static final String COLUMN_MOVIE_RELEASE_DATE = "movie_rel_date";
        public static final String COLUMN_MOVIE_DESC = "movie_desc";
        public static final String COLUMN_MOVIE_POSTER_PATH = "movie_ppath";
        public static final String COLUMN_MOVIE_BACKDROP_PATH = "movie_dpath";
        // video (boolean)
        public static final String COLUMN_MOVIE_HAS_VIDEO = "movie_video";

        public static final String COLUMN_SORT = "sort_by";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieIdUri(int movieId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build();
        }

        public static Uri buildMovieListSortedByPreferenceUri(String sort) {
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter(COLUMN_SORT, sort).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }



    }

    public static final class MyFavMovieEntry implements BaseColumns {

        // Base location to search for the My Favorite Movie with a Content Provider
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_FAV).build();

        // Return multiple rows
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_FAV;

        // Return a single row
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE_FAV;

        public static final String TABLE_NAME = "my_fav_movie";
        //movie id (int)
        public static final String COLUMN_MOVIE_ID = "movie_id";

        // Column with the foreign key into the  Movie table
        //public static final String COLUMN_MOVIE_KEY = "movie_id";


        public static Uri buildMyFavMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


}
