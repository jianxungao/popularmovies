package com.biz.timux.popularmovies1;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.biz.timux.popularmovies1.data.MovieContract.MyFavMovieEntry;
import com.biz.timux.popularmovies1.data.MovieContract.MovieEntry;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = DetailFragment.class.getSimpleName();
    public static final String MOVIE_KEY = "movie_id";
    //private static final String MOVIE_SORT_KEY = "sort";
    //private String mSort;

    private static final int DETAIL_LOADER = 0;

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

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_TITLE = 1;
    public static final int COL_MOVIE_DESC = 2;
    public static final int COL_MOVIE_RELEASE_DATE = 6;
    public static final int COL_MOVIE_POSTER_PATH = 5;
    public static final int COL_MOVIE_VOTE_AVG = 8;
    public static final int COL_MOVIE_POP = 7;
    private int mMovieId;

    public DetailFragment() {
    }

    public static Fragment newInstance(int movieId) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(MOVIE_KEY, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(MOVIE_KEY, mMovieId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DetailActivity.MOVIE_KEY)) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovieId = arguments.getInt(DetailActivity.MOVIE_KEY);
        }

        /*if (savedInstanceState != null) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }*/
        
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        if (savedInstanceState != null) {
            mMovieId = savedInstanceState.getInt(MOVIE_KEY);
        }
        super.onActivityCreated(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DetailActivity.MOVIE_KEY)) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "\"---- inside DetailFragment Loader ----\"");
        /*Intent intent = getActivity().getIntent();
        if (intent == null || !intent.hasExtra(MOVIE_KEY)) {
            return null;
        }
        int movieId = intent.getIntExtra(MOVIE_KEY, 0);*/
        Log.d(TAG, "-- movie id --" + mMovieId);


        //mSort = Utility.getPreferredSortBy(getActivity());
        Uri movieByIdUri = MovieEntry.buildMovieIdUri(mMovieId);
        Log.d(TAG, movieByIdUri.toString());

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                movieByIdUri,
                MOVIE_COLUMNS,
                null,
                null,
                null
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "In onLoadFinished");

        if (data != null && data.moveToFirst()) {

            while (data.moveToNext()) {
                int moiveId = data.getInt(data.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID));

                if (mMovieId == moiveId) {

                    String movieTitle =
                            data.getString(data.getColumnIndex(MovieEntry.COLUMN_MOVIE_TITLE));
                    ((TextView) getView().findViewById(R.id.movie_title))
                            .setText(movieTitle);

                    String movieDesc =
                            data.getString(data.getColumnIndex(MovieEntry.COLUMN_MOVIE_DESC));
                    ((TextView) getView().findViewById(R.id.movie_overview))
                            .setText(movieDesc);


                    ((TextView) getView().findViewById(R.id.movie_duration))
                            .setText(Utility.getDuration());

                    double movieVote =
                            data.getDouble(data.getColumnIndex(MovieEntry.COLUMN_MOVIE_VOTE));
                    ((TextView) getView().findViewById(R.id.movie_vote))
                            .setText(Utility.getVote(movieVote));


                    String movieYear =
                            data.getString(data.getColumnIndex(MovieEntry.COLUMN_MOVIE_RELEASE_DATE));
                    ((TextView) getView().findViewById(R.id.movie_release_year))
                            .setText(Utility.getYear(movieYear));


                    String icons =
                            data.getString(data.getColumnIndex(MovieEntry.COLUMN_MOVIE_BACKDROP_PATH));

                    ImageView iconView = (ImageView) getView().findViewById(R.id.movie_imgIcon);
                    Picasso.with(getContext()).load(Utility.sBaseUrl + icons).into(iconView);
                    Log.d(TAG, "Picasso load() is called :" + Utility.sBaseUrl + icons);


                    Log.d(TAG, "Movie:  - " + movieTitle + " -  " + movieYear + " - ");
                    break;
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }


}
