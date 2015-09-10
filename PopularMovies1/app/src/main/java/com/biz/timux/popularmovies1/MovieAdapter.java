package com.biz.timux.popularmovies1;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CursorAdapter;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;



/**
 * Created by gaojianxun on 15/8/19.
 */
public class MovieAdapter extends CursorAdapter {


    private static final String TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Read weather icon ID from cursor
        String icons = cursor.getString(MainFragment.COL_MOVIE_POSTER_PATH);
        Log.d(TAG, "Cursor.getString is called :" + icons);

        ImageView iconView = (ImageView)view.findViewById(R.id.movie_image);
        Picasso.with(context).load(Utility.sBaseUrl + icons).into(iconView);
        Log.d(TAG, "Picasso load() is called :" + Utility.sBaseUrl+icons);


    }



}
