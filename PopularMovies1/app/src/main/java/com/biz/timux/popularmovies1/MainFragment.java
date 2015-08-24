package com.biz.timux.popularmovies1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.GridView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;



/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private ArrayList<MyMovie> mMoiveList;
    private static final String TAG = MainFragment.class.getSimpleName();

    private MovieAdapter mAdapter;

    private static String mSortBy;

    public MainFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("MyMovies", mMoiveList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.movies_title);
        if (savedInstanceState == null || !savedInstanceState.containsKey("MyMovies")) {
            mMoiveList = new ArrayList<MyMovie>();
        } else {
            mMoiveList = savedInstanceState.getParcelableArrayList("MyMovies");
        }

        if (isNetworkAvailable()){
            getOnlineResource();
        }else {
            Toast.makeText(getActivity(), "Please check your network connections!",
                    Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Network problems!");
        }
        Log.d(TAG, "onCreate() called");
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    private void getOnlineResource(){


        RequestQueue queue = Volley.newRequestQueue(getActivity());

        String keyValue = "";
        final String BASE_URL =
                "http://api.themoviedb.org/3/discover/movie?";
        final String SORT_BY = "sort_by";
        final String API_KEY = "api_key";
        mSortBy = getSortBy();
        if (mSortBy == null){
            mSortBy = "popularity.desc";
            Log.d(TAG, "Sorting value :  " + mSortBy);
        }

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(SORT_BY, mSortBy)
                .appendQueryParameter(API_KEY, keyValue)
                .build();

        String url = builtUri.toString();
        Log.v(TAG, "Built URI " + builtUri.toString());

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                //null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            mMoiveList = getMovieDataFromJson(response.toString());
                            if (mMoiveList != null) {
                                mAdapter.clear();
                                for (MyMovie m : mMoiveList) {
                                    mAdapter.add(m);
                                    Log.d(TAG, "Movie entry: " + m.getTitle() + " - " + m.getId());
                                }
                            } else {
                                mAdapter = new MovieAdapter(getActivity(), mMoiveList);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("Error", error.toString());
                    }
                });

        queue.add(jsObjRequest);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAdapter = new MovieAdapter(getActivity(), mMoiveList);

        if (mMoiveList != null) {
            for (MyMovie s : mMoiveList) {
                Log.d(TAG, "Movie entry: onCreateView Method " + s.getTitle());
            }
        }

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        GridView gridView = (GridView) rootView.findViewById(R.id.movie_grid);
        gridView.setAdapter(mAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyMovie myMovie = mAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, myMovie);
                startActivity(intent);
            }
        });

        Log.d(TAG, "onCreateView() called");
        return rootView;
    }


    // To get the movie sorting preference
    public String getSortBy() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_by = prefs.getString(
                getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_popularity));
        if (sort_by.equals("popularity")) {
            mSortBy = "popularity.desc";
            Log.v(TAG, "Sort by - - -" + mSortBy);

        } else if (sort_by.equals("highest_rated")) {
            mSortBy = "vote_average.desc";
            Log.v(TAG, "Sort by - - - " + mSortBy);
        }
        return mSortBy;
    }

    @Override
    public void onResume() {
        super.onResume();
        mSortBy = getSortBy();
        //mAdapter.setNotifyOnChange(true);
        Log.d(TAG, "onResume called and Sorting by -  " + mSortBy);
    }

    @Override
    public void onStart(){
        super.onStart();
    }


    private ArrayList<MyMovie> getMovieDataFromJson(String moviesJsonStr)
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

        mMoiveList = new ArrayList<MyMovie>();

        for (int i = 0; i < moviesArray.length(); i++) {

            // Get the JSON object reference
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

            //create a MyMovie object each time and put to an array list
            MyMovie m = new MyMovie(id, title, popularity, vote_avg, releaseDate, description,
                    posterPath, backdropPath, video);
            mMoiveList.add(m);
        }

        for (MyMovie s : mMoiveList) {
            Log.v(TAG, "Movie entry: " + s.getPath());
        }

        return mMoiveList;
    }


}
