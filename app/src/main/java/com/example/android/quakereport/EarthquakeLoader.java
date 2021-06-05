package com.example.android.quakereport;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

public class EarthquakeLoader extends AsyncTaskLoader<List<EarthQuake>> {

    //Tag for log messages
    private static final String LOG_TAG = EarthquakeLoader.class.getName();

    //Query url
    private String mUrl;

    /**
     * Constructs a new {@link EarthquakeLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public EarthquakeLoader(Context context, String url){
        super(context);
        mUrl = url;
    }


    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "TEST :onStartLoading() called");
        forceLoad();
    }

    //This is on  a background thread
    @Nullable
    @Override
    public List<EarthQuake> loadInBackground() {
        Log.i(LOG_TAG, "TEST :loadInBackground() called");
        if (mUrl == null){
            return null;
        }

        //Perform the network request .parse the response and extract a list of earthquakes

        List<EarthQuake> earthquakes = QueryUtils.fetchEarthquakeData(mUrl);
        return earthquakes;
    }
}
