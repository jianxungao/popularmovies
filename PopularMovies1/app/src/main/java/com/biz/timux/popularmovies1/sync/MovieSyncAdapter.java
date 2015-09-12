package com.biz.timux.popularmovies1.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.biz.timux.popularmovies1.R;
import com.biz.timux.popularmovies1.Utility;
import com.biz.timux.popularmovies1.data.MovieContract;

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

/**
 * Created by gaojianxun on 15/9/11.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter{


    public static final String TAG = MovieSyncAdapter.class.getSimpleName();

    // Interval at which to sync with the weather, in seconds
    // 60 seconds (1min) * 180 = 3 hours
    //public static final int SYNC_INTERVAL = 60 * 180;
    //public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "Starting sync");
        // Getting the sort preference to send to the API
        String sort = Utility.getPreferredSortBy(getContext());
        Log.d(TAG, "Starting sync -- sort by " + sort);

        if (sort == null || sort.isEmpty()) {
            return;
        }
        String movieData = getMovieData(sort);

        try {
            getMovieDataFromJson(movieData, sort);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    private void getMovieDataFromJson(String moviesJsonStr, String sort)
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

            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, title);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY, popularity);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE, vote_avg);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, releaseDate);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_DESC, description);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH, backdropPath);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, posterPath);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_HAS_VIDEO, video);
            movieValues.put(MovieContract.MovieEntry.COLUMN_SORT, sort);
                    cVVector.add(movieValues);
        }

        Log.d(TAG, "Sync Complete. " + cVVector.size() + " Inserted");
        /** Insert movie data into database */
        insertMovieIntoDatabase(cVVector);


    }

    private void insertMovieIntoDatabase(Vector<ContentValues> cVVector) {

        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            int rowInserted = getContext().getContentResolver()
                    .bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);

        }

    }


    private String getMovieData(String sort) {

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
                    .appendQueryParameter(SORT_BY, sort)
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

            //Log.d(TAG, "Movies JSON String: " + moviesJsonStr);


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
        return moviesJsonStr;
    }

    /**
     * Helper method to get the fake accounts to be used with SyncAdapter
     */
    public static Account getSyncAccount(Context context) {

        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(context.getString(R.string.app_name),
                context.getString(R.string.sync_account_type));
        // If password doesn't exist, the account doesn't exist
        if (accountManager.getPassword(newAccount) == null) {
            // If not successful
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            // If you don't set android:syncable="true" in your <provider> element in the manifest
            // then call context.setIsSyncable(account, AUTHORITY, 1) here
            //onAccountCreated(newAccount, context);
        }

        return newAccount;
    }

    /*private static void onAccountCreated(Account newAccount, Context context) {
        // Since we've created an account
        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        // Without calling setSyncAutomatically, our periodic sync will not be enabled
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        // Finally, do a sync to get things started
        syncImmediately(context);
    }*/

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context An app context
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        Account account = getSyncAccount(context);
        ContentResolver.requestSync(account,
                context.getString(R.string.content_authority), bundle);
    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    /*public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {

        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(syncInterval, flexTime)
                    .setSyncAdapter(account, authority)
                    .build();
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }*/
}
