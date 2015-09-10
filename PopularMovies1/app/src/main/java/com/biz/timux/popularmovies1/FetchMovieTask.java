/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.biz.timux.popularmovies1;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.biz.timux.popularmovies1.data.MovieContract.MovieEntry;
import com.biz.timux.popularmovies1.data.MovieContract.MyFavMovieEntry;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class FetchMovieTask extends AsyncTask<String, Void, Void> {

    private static boolean DEBUG = true;
    private final String TAG = FetchMovieTask.class.getSimpleName();
    private final Context mContext;

    public FetchMovieTask(Context context) {
        mContext = context;
    }

    /**
     * Helper method to handle insertion of a new favorite movie in the movie database.
     *
     * @param movieId The movie id
     * @return the row ID of the added movie.
     */
    private long addMyFavMoive(int movieId) {

        // First, check if the location with this city name exists in the db
        Cursor cursor = mContext.getContentResolver().query(
                MyFavMovieEntry.CONTENT_URI,
                new String[]{MyFavMovieEntry._ID},
                MyFavMovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[] {String.valueOf(movieId)},
                null);

        if (cursor.moveToFirst()) {
            int locationIdIndex = cursor.getColumnIndex(MyFavMovieEntry._ID);
            return cursor.getLong(locationIdIndex);
        } else {
            ContentValues myFavMovieValues = new ContentValues();
            myFavMovieValues.put(MyFavMovieEntry.COLUMN_MOVIE_ID, movieId);


            Uri myFavMovieInsertUri = mContext.getContentResolver()
                    .insert(MyFavMovieEntry.CONTENT_URI, myFavMovieValues);

            return ContentUris.parseId(myFavMovieInsertUri);
        }
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getMovieDataFromJson(String moviesJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String MV_RESULTS = "results";
        final String MV_ID = "id";
        final String MV_TITLE = "title";
        final String MV_OVERVIEW = "overview";
        final String MV_BACKDROP_PATH = "backdrop_path";
        final String MV_POSTER_PATH = "poster_path";
        final String MV_RELEASE_DATE = "release_date";
        final String MV_POPULARITY = "popularity";
        final String MV_VOTE_AVG = "vote_average";
        final String MV_VIDEO = "video";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(MV_RESULTS);



        // Get and insert the new weather information into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());

        for(int i = 0; i < moviesArray.length(); i++) {
            // These are the values that will be collected.

            JSONObject moviesPopulated = moviesArray.getJSONObject(i);

            int id = moviesPopulated.getInt(MV_ID);
            String title = moviesPopulated.getString(MV_TITLE);
            double popularity = moviesPopulated.getDouble(MV_POPULARITY);
            double vote_avg = moviesPopulated.getDouble(MV_VOTE_AVG);
            String releaseDate = moviesPopulated.getString(MV_RELEASE_DATE);
            String description = moviesPopulated.getString(MV_OVERVIEW);
            String posterPath = moviesPopulated.getString(MV_POSTER_PATH);
            String backdropPath = moviesPopulated.getString(MV_BACKDROP_PATH);
            boolean video = moviesPopulated.getBoolean(MV_VIDEO);

            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieEntry.COLUMN_MOVIE_ID, id);
            movieValues.put(MovieEntry.COLUMN_MOVIE_TITLE, title);
            movieValues.put(MovieEntry.COLUMN_MOVIE_POPULARITY, popularity);
            movieValues.put(MovieEntry.COLUMN_MOVIE_VOTE, vote_avg);
            movieValues.put(MovieEntry.COLUMN_MOVIE_RELEASE_DATE, releaseDate);
            movieValues.put(MovieEntry.COLUMN_MOVIE_DESC, description);
            movieValues.put(MovieEntry.COLUMN_MOVIE_BACKDROP_PATH, backdropPath);
            movieValues.put(MovieEntry.COLUMN_MOVIE_POSTER_PATH, posterPath);
            movieValues.put(MovieEntry.COLUMN_MOVIE_HAS_VIDEO, video);

            cVVector.add(movieValues);
        }
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
        }

        /*if (DEBUG) {
            Cursor movieCursor = mContext.getContentResolver().query(
                    MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );

            if (movieCursor.moveToFirst()) {
                ContentValues resultValues = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(movieCursor, resultValues);
                Log.v(TAG, "Query succeeded! **********");
                for (String key : resultValues.keySet()) {
                    Log.v(TAG, key + ": " + resultValues.getAsString(key));
                }
            } else {
                Log.v(TAG, "Query failed! :( **********");
            }

            movieCursor.close();
        }*/
    }

    @Override
    protected Void doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        String sortBy = params[0];

        //String sortBy = "popularity.desc";
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;

        String format = "json";

        String keyValue = "5f781f14a22dd8dc12423a79603e3e1f";

        try {

            final String BASE_URL =
                    "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_BY = "sort_by";
            final String API_KEY = "api_key";


            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_BY, sortBy)
                    .appendQueryParameter(API_KEY, keyValue)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.d(TAG, "Built URI " + builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
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
                return null;
            }


            moviesJsonStr = buffer.toString();

            Log.d(TAG, "Movies JSON String: " + moviesJsonStr);

        } catch (IOException e) {
            Log.e(TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }

        try {
            getMovieDataFromJson(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }
}
