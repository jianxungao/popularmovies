package com.biz.timux.popularmovies1;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.GridView;


import com.biz.timux.popularmovies1.data.MovieContract.MovieEntry;




/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static boolean DEBUG = true;
    private static final String TAG = MainFragment.class.getSimpleName();

    private MovieAdapter mAdapter;
    private GridView mGridView;

    private static String sSortBy;

    private static final int MOVIE_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {

            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.COLUMN_MOVIE_ID,
            MovieEntry.COLUMN_MOVIE_TITLE,
            MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
            MovieEntry.COLUMN_MOVIE_DESC,
            MovieEntry.COLUMN_MOVIE_POPULARITY,
            MovieEntry.COLUMN_MOVIE_VOTE,
            MovieEntry.COLUMN_MOVIE_BACKDROP_PATH,
            MovieEntry.COLUMN_MOVIE_POSTER_PATH,
            MovieEntry.COLUMN_MOVIE_VOTE
    };


    // These indices are tied to MOVIE_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_MOVIE_ID = 1;
    //public static final int COL_MOVIE_TITLE = 1;
    public static final int COL_MOVIE_DESC = 2;
    public static final int COL_MOVIE_RELEASE_DATE = 6;
    public static final int COL_MOVIE_POSTER_PATH = 5;
    public static final int COL_MOVIE_VOTE_AVG = 8;
    public static final int COL_MOVIE_POP = 7;

    public MainFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {;
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d(TAG, "onCreate() called");
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //updateMovieList();
        mAdapter = new MovieAdapter(getActivity(), null, 0);


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        mGridView = (GridView) rootView.findViewById(R.id.movie_grid);
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = mAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)){

                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra(DetailActivity.MOVIE_KEY, cursor.getInt(COL_MOVIE_ID));
                    Log.d(TAG, "onCreateView() called" + cursor.getInt(COL_MOVIE_ID));
                    startActivity(intent);
                }

            }
        });

        Log.d(TAG, "onCreateView() called");
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateMovieList() {
        String sortBy = Utility.getPreferredSortBy(getActivity());
        new FetchMovieTask(getActivity()).execute(sortBy);
    }


    // To get the movie sorting preference
    public String getSortBy() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_by = prefs.getString(
                getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_popularity));
        if (sort_by.equals("popularity")) {
            sSortBy = "popularity.desc";
            Log.v(TAG, "Sort by - - -" + sSortBy);

        } else if (sort_by.equals("highest_rated")) {
            sSortBy = "vote_average.desc";
            Log.v(TAG, "Sort by - - - " + sSortBy);
        }
        return sSortBy;
    }

    @Override
    public void onResume() {
        super.onResume();
        sSortBy = getSortBy();
        //mAdapter.setNotifyOnChange(true);
        Log.d(TAG, "onResume called and Sorting by -  " + sSortBy);
    }

    @Override
    public void onStart(){
        super.onStart();
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.d(TAG, "inside Loader ----");
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.

        if (DEBUG) {
            Cursor movieCursor = getActivity().getContentResolver().query(
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
        }

        return new CursorLoader(
                getActivity(),
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
