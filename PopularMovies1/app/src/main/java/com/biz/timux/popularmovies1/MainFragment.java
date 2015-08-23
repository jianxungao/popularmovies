package com.biz.timux.popularmovies1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.GridView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private ArrayList<MyMovie> mMoivesList;
    private static final String TAG = MainFragment.class.getSimpleName();

    private MovieAdapter mAdapter;
    //default sort by value
    private String mSortBy = "popularity.desc";

    public MainFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putParcelableArrayList("MyMovies", MyMovieList.getMyMovies());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.movies_title);
        if(savedInstanceState == null || !savedInstanceState.containsKey("MyMovies")){
            mMoivesList = new ArrayList<MyMovie>();
        }else {
            //mMoivesList = MyMovieList.getMyMovies();
            mMoivesList = savedInstanceState.getParcelableArrayList("MyMovies");
        }
        Log.d(TAG, "onCreate() called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAdapter = new MovieAdapter(getActivity(), mMoivesList);

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        GridView gridView = (GridView) rootView.findViewById(R.id.movie_grid);
        gridView.setAdapter(mAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyMovie myMovie = mAdapter.getItem(position);
                Intent intent = new Intent(getActivity(),DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, myMovie.getId());
                startActivity(intent);
            }
        });

        Log.d(TAG, "onCreateView() called");
        return rootView;
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager
        = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        
    }
    
    private void updateMovie() {

        FetchMoviesTask uTask = new FetchMoviesTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_by = prefs.getString(
                getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_popularity));
        if (sort_by.equals("popularity")){
            mSortBy = "popularity.desc";
            Log.v(TAG, "Sort by - - -" + mSortBy.toString());

        }else if (sort_by.equals("highest_rated")){
            mSortBy = "vote_average.desc";
            Log.v(TAG, "Sort by - - - " + mSortBy.toString());
        }
        if (isNetworkAvailable()){
            uTask.execute();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }


    public class FetchMoviesTask extends AsyncTask<MyMovie, Void, ArrayList<MyMovie>> {

        private final String TAG = FetchMoviesTask.class.getSimpleName();

        //populate movie data to an array list from json source.
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

            ArrayList<MyMovie> movieList = new ArrayList<MyMovie>();

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
                movieList.add(m);

            }

            for (MyMovie s : movieList) {
                Log.v(TAG, "Movie entry: " + s.getPath());
            }

            //set the MyMovieList to hold the movie data for sharing between activities
            MyMovieList myMovieList = MyMovieList.get(getActivity());
            myMovieList.setMyMovies(movieList);

            return movieList;

        }


        @Override
        protected ArrayList<MyMovie> doInBackground(MyMovie... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            String format = "json";
            //please replace the keyValue with your key!
            String keyValue = "";

            try {

                final String BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY = "sort_by";
                final String API_KEY = "api_key";


                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY, mSortBy)
                        .appendQueryParameter(API_KEY, keyValue)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(TAG, "Built URI " + builtUri.toString());

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

                Log.v(TAG, "Movies JSON String: " + moviesJsonStr);

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
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MyMovie> result) {

            if (result != null) {
                mAdapter.clear();
                for (MyMovie moviesStr : result) {
                    mAdapter.add(moviesStr);

                }
            }
        }
    }


}
