package com.biz.timux.popularmovies1;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by gaojianxun on 15/8/19.
 */
public class MovieAdapter extends ArrayAdapter<MyMovie> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    /**
     * This is my own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the List is the data we want
     * to populate into the lists
     *
     * @param context        The current context. Used to inflate the layout file.
     * @param movieList  A List of MyMovie objects to display in a list
     */
    public MovieAdapter(Activity context, List<MyMovie> movieList) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, movieList);

    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     *                    (search online for "android view recycling" to learn more)
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
         MyMovie m = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_item, parent, false);
        }


        ImageView iconView = (ImageView)convertView.findViewById(R.id.movie_image);
        Picasso.with(getContext()).load(m.getPath()).into(iconView);
        Log.d(TAG, "m.getPath() is called :" + m.getPath().toString());


        return convertView;
    }

}
