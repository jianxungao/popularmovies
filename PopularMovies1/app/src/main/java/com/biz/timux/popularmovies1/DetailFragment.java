package com.biz.timux.popularmovies1;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {
    private final String TAG = DetailFragment.class.getSimpleName();
    private int movieId;
    private MyMovie mMyMovie;

    public DetailFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Log.v(TAG, "in onCreate");

        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            movieId = (int)intent.getIntExtra(Intent.EXTRA_TEXT, 0);
            mMyMovie = MyMovieList.get(getActivity()).getMyMovie(movieId);
        }

        TextView movieTitle = (TextView)rootView.findViewById(R.id.movie_title);
        movieTitle.setText(mMyMovie.getTitle());

        ImageView movieIcon = (ImageView)rootView.findViewById(R.id.movie_imgIcon);
        Picasso.with(getContext()).load(mMyMovie.getIconPath()).into(movieIcon);
        Log.d(TAG, "m.getIconPath() is called :" + mMyMovie.getIconPath().toString());

        TextView movieYear = (TextView)rootView.findViewById(R.id.movie_release_year);
        movieYear.setText(Utility.getYear(mMyMovie.getReleaseDate()));

        TextView movieVote = (TextView)rootView.findViewById(R.id.movie_vote);
        movieVote.setText(Utility.getVote(mMyMovie.getVote_avg()));

        TextView movieDuration = (TextView)rootView.findViewById(R.id.movie_duration);
        movieDuration.setText(Utility.getDuration());

        TextView movieDesc = (TextView)rootView.findViewById(R.id.movie_overview);
        movieDesc.setText(mMyMovie.getDescription());

        return rootView;
    }
}
