package com.biz.timux.popularmovies1;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceFragment;

/**
 * A simple {@link PreferenceFragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment{

    private SharedPreferences.OnSharedPreferenceChangeListener mListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if (key.equals(getString(R.string.pref_sort_by_key))) {
                        Preference tempUnitsPref = findPreference(key);
                        tempUnitsPref.setSummary(sharedPreferences
                                .getString(key, getString(R.string.pref_sort_by_popularity)));
                    }
                }
            };


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Load the preference from an XML resource
        addPreferencesFromResource(R.xml.pref_general);

        //update the sort by preferences
        findPreference(getString(R.string.pref_sort_by_key))
                .setSummary(PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_sort_by_key),getString(R.string.pref_sort_by_popularity)));


    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(mListener);
    }


}
