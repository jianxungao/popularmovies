package com.biz.timux.popularmovies1;

import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by gaojianxun on 15/9/13.
 */
public class ChooseToPlayFragment extends DialogFragment {

    private static final String TAG = ChooseToPlayFragment.class.getSimpleName();
    private static final String EXTRA_ID = "movie_id";

    public static ChooseToPlayFragment newInstance(int movieId)
    {
        Bundle args = new Bundle();
        args.putInt(EXTRA_ID, movieId);

        ChooseToPlayFragment fragment = new ChooseToPlayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        /*View v = getActivity().getLayoutInflater()
                    .inflate(R.layout.play_picker, null);*/

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_for_choosing)
                .setPositiveButton(R.string.dialog_txt_browser,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Toast.makeText(getContext(),"Browser -" + id, Toast.LENGTH_SHORT).show();
                                // Open with Browser
                                try {
                                    openBrowserToView(getArguments().getInt(EXTRA_ID));
                                } catch (MalformedURLException e) {
                                    Log.d(TAG, "MalformedURLException", e);
                                }
                            }
                        })
                .setNegativeButton(R.string.dialog_txt_youtube,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Toast.makeText(getContext(), "YouTube -" + id, Toast.LENGTH_SHORT).show();
                                // Open with YouTube
                                openYouTubeToView(getArguments().getInt(EXTRA_ID));
                            }
                        });
        // Create the AlertDialog object and return it
        return builder.create();
    }


    private void openBrowserToView (int movieId) throws MalformedURLException {

        //String url = "http://www.google.com";
        final String BASE_URL = Utility.sBaseMovieUrl;
        final String API_KEY = "api_key";
        final String VIDEOS = "videos";
        final String keyValue = Utility.sAPIKey;


        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(String.valueOf(movieId))
                .appendPath(VIDEOS)
                .appendQueryParameter(API_KEY, keyValue)
                .build();

        //URL url = new URL(builtUri.toString());

        Log.d(TAG, "builtUri is " + builtUri.toString());

        /*Uri.Builder builder = new Uri.Builder();
        builder.scheme(Utility.sScheme)
                .authority(Utility.sAuthority)
                .appendPath(Utility.sKey)
                .appendPath(Utility.sCategory)
                .appendPath(String.valueOf(movieId))
                .appendPath(VIDEOS)
                .appendQueryParameter(API_KEY, keyValue);

        String mUrl = builder.build().toString();
        Log.d(TAG, "mUrl is " + mUrl.toString());*/

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(builtUri.toString()));
        startActivity(i);

    }

    private void openYouTubeToView(int movieId){

        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=cxLG2wtE7TM"));

        startActivity(i);

    }
}
