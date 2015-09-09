package com.biz.timux.popularmovies1;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;

/**
 * Created by gaojianxun on 15/8/22.
 * Help functions
 */
public class Utility {
    public static String getYear(String releaseDate){
        String year = "2015";
        int i = releaseDate.indexOf('-');
        year = releaseDate.substring(0,i);
        return year;
    }

    public static String getVote(double vote){
        String moveVote = Double.toString(vote);
        return moveVote+"/10";
    }

    // As a placeholder
    public static String getDuration(){
        return "120min";
    }

    public static String getPreferredSortBy(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_by_key),
                context.getString(R.string.pref_sort_by_popularity));
    }
}
