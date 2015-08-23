package com.biz.timux.popularmovies1;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by gaojianxun on 15/8/21.
 * A singleton class used as central place to share movies info between different activities
 */
public class MyMovieList {

    private static ArrayList<MyMovie> mMyMovies;

    private static MyMovieList sMyMovieList;
    private Context mAppContext;

    private MyMovieList(Context appContext){
        mAppContext = appContext;
        mMyMovies = new ArrayList<MyMovie>();

    }

    public static MyMovieList get(Context c){
        if (sMyMovieList == null){
            sMyMovieList = new MyMovieList(c.getApplicationContext());
        }
        return sMyMovieList;
    }

    public ArrayList<MyMovie> getMyMovies() {
        return mMyMovies;
    }


    public MyMovie getMyMovie(int id){
        for (MyMovie m : mMyMovies){
            if (m.getId() == id)
                return m;
        }
        return null;
    }
    
    public void  setMyMovies (ArrayList<MyMovie> myMovies){
        for (MyMovie m : myMovies) {
            mMyMovies.add(m);
        }
    }
}
