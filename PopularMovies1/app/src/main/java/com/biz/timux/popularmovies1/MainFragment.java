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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.GridView;


import com.biz.timux.popularmovies1.data.MovieContract;
import com.biz.timux.popularmovies1.data.MovieContract.MovieEntry;
import com.biz.timux.popularmovies1.sync.MovieSyncAdapter;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static boolean DEBUG = true;
    private static final String TAG = MainFragment.class.getSimpleName();
    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private MovieAdapter mAdapter;
    private GridView mGridView;

    private String mSort;

    private static final int MOVIE_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {

            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.COLUMN_MOVIE_ID,
            //MovieEntry.COLUMN_MOVIE_TITLE,
            //MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
            //MovieEntry.COLUMN_MOVIE_DESC,
            //MovieEntry.COLUMN_MOVIE_POPULARITY,
            //MovieEntry.COLUMN_MOVIE_VOTE,
            //MovieEntry.COLUMN_MOVIE_BACKDROP_PATH,
            MovieEntry.COLUMN_MOVIE_POSTER_PATH,
            //MovieEntry.COLUMN_MOVIE_VOTE
    };


    // These indices are tied to MOVIE_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_MOVIE_ID = 1;
    //public static final int COL_MOVIE_TITLE = 1;
    //public static final int COL_MOVIE_DESC = 2;
    //public static final int COL_MOVIE_RELEASE_DATE = 6;
    //public static final int COL_MOVIE_POSTER_PATH = 5;
    public static final int COL_MOVIE_POSTER_PATH = 2;
    //public static final int COL_MOVIE_VOTE_AVG = 8;
    //public static final int COL_MOVIE_POP = 7;



    public MainFragment() {
    }

    public interface Callback {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(Uri movieUri);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != GridView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d(TAG, "onCreate() called");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mainfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateMovieList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAdapter = new MovieAdapter(getActivity(), null, 0);


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        mGridView = (GridView) rootView.findViewById(R.id.movie_grid);
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = mAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    ((Callback) getActivity())
                            .onItemSelected(MovieContract.MovieEntry.buildMovieIdUri(
                                    cursor.getInt(COL_MOVIE_ID)
                            ));
                }
                mPosition = position;

            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        Log.d(TAG, "onCreateView() called");
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateMovieList() {
        MovieSyncAdapter.syncImmediately(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSort != null && !mSort.equals(Utility.getPreferredSortBy(getActivity()))) {
            getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        }
        Log.d(TAG, "onResume called and Sorting by -  " + mSort);
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    // since we read the sort pref when we create the loader, all we need to do is restart things
    void onSortPreferenceChanged() {
        updateMovieList();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.d(TAG, "---- inside MainFragment Loader ----");
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        mSort = Utility.getPreferredSortBy(getActivity());
        Uri movieSortedByUri = MovieEntry.buildMovieListSortedByPreferenceUri(mSort);
        return new CursorLoader(
                getActivity(),
                //MovieEntry.CONTENT_URI,
                movieSortedByUri,
                MOVIE_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

        if (mPosition != GridView.INVALID_POSITION){
            mGridView.setSelection(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
