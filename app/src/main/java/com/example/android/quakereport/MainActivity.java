package com.example.android.quakereport;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
//import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
import android.os.UserHandle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.loader.app.LoaderManager;

import java.net.NetworkInterface;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<EarthQuake>> {

    public static final String LOG_TAG = MainActivity.class.getName();

    /*Adapter for the list of earthquakes*/
    private EarthQuakeAdapter mAdapter;

    /**
     * Constant value for the earthquake loader ID, we can choose any integer,
     * This really only comes into play if you,re using multiple loaders.
     */
    private static final int EARTHQUAKE_LOADER_ID = 1;

    //TextView that is displayed when the list is empty
    private TextView mEmptyStateTextView;

    //progressBarView that is displayed for the delay in the network request
    private ProgressBar mProgressBar;

    //URL for earthQuake data from the USGS dataSet
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "TEST : Earthquake activity onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        // Create a fake list of earthquake locations.
//        ArrayList<EarthQuake> earthquakes = new ArrayList<>();
//        ArrayList<EarthQuake> earthquake = QueryUtils.extractFeatureFromJson();
//        earthquakes.add(new EarthQuake("4.5", "San Francisco",
//                "Feb 7, 2015"));
//        earthquakes.add(new EarthQuake("7.1", "London",
//                "July 20, 2015"));
//        earthquakes.add(new EarthQuake("3.5", "Tokyo",
//                "Aug 4, 2014"));
//        earthquakes.add(new EarthQuake("7.1", "Mexico City",
//                "March 23, 2016"));
//        earthquakes.add(new EarthQuake("2.8", "Moscow",
//                "May 28, 2013"));
//        earthquakes.add(new EarthQuake("3.7", "Rio de Janeiro",
//                "April 22, 2012"));
//        earthquakes.add(new EarthQuake("6.7", "Paris",
//                "Jan 1, 2015"));


        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);


        // Create a new {@link ArrayAdapter} of earthquakes
        mAdapter = new EarthQuakeAdapter(
                this, new ArrayList<EarthQuake>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);

        //Setting up the empty state value for the listView.
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(mEmptyStateTextView);

        mProgressBar = (ProgressBar) findViewById(R.id.loading_spinner);




        //get a reference to the connectivity manager to get the state of the network connection
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        //get the details of the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //if there is a network connection fetch data
        if (networkInfo != null && networkInfo.isConnected()){
            //get a reference to the LoaderManager, in order to interact with loaders
            LoaderManager lm = LoaderManager.getInstance(this);

            //Initialize the Loader. pass in the int ID constant defined above and pass in null for
            //the bundle. Pass in this activity for the LoaderCallbacksParameter (which is valid
            //because this activity implements the LoaderCallbacksInterface).
            Log.i(LOG_TAG, "TEST :initLoader() called");
            lm.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        } else {
            //otherwise display Error
            //hide loading indicator so error message will be visible
            mProgressBar.setVisibility(View.GONE);

            //update emptyState with  no connection error
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }



        //start the executorService to fetch the earthquake data.
//        processInBg(true, USGS_REQUEST_URL);

        //set an item click listener on the listView which sends an intent to a web browser
        //to open a website with more information about the selected earthquake
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //find the current earthquake that was clicked on
                EarthQuake currentEarthquake = mAdapter.getItem(position);

                //convert the string url into a uri object (to pass into the intent constructor).
                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

                //create a new intent to view the earthquake uri
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                //send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });


    }

    @Override
    public Loader<List<EarthQuake>> onCreateLoader(int i, Bundle bundle){
        Log.i(LOG_TAG, "TEST :onCreateLoader() called");

        //fetching the user preference and adding it to the query part if the url.
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPref.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPref.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        //Create a new loader for the given URL
        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished (Loader < List < EarthQuake >> loader, List < EarthQuake > earthquakes){
        Log.i(LOG_TAG, "TEST :onLoadFinished() called");

        mProgressBar.setVisibility(View.GONE);

        //set empty state text to display no earthquakes found.
        mEmptyStateTextView.setText(R.string.no_earthquakes);

        //clear the adapter of previous earthquake data
        mAdapter.clear();

        //If there is a valid list of {@link Earthquake}s then add them to the adapter,s
        // dataSet. This will trigger the listView wto update.

        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
        }
    }

    @Override
    public void onLoaderReset (Loader < List < EarthQuake >> loader) {
        Log.i(LOG_TAG, "TEST :onLoaderReset() called");
        //Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


//    ExecutorService mExecutor = Executors.newSingleThreadExecutor();
//    Handler mHandler = new Handler(Looper.getMainLooper());
//    ArrayList<EarthQuake> result;
//
//    private void processInBg(final boolean finished, final String... url){
//        // An interface is probably a better method for handling the result
//        // but I think this should still work too.
////        Event result;
//
//        final Runnable updateUIRunnable = new Runnable(){
//            @Override
//            public void run(){
////                // Update the UI here
////                updateUi(result);
//
//                //clear the adapter of previous earthquake data
//                mAdapter.clear();
//
//                //If there is a valid list of {@link Earthquake}s then add them to the adapter,s
//                // data set. This will trigger the listview wto update.
//
//                if (result != null && !result.isEmpty()){
//                    mAdapter.addAll(result);
//                }
//                // If we're done with the ExecutorService, shut it down.
//                // (If you want to re-use the ExecutorService,
//                // make sure to shut it down whenever everything's completed
//                // and you don't need it any more.)
//                if(finished){
//                    mExecutor.shutdown();
//                }
//            }
//        };
//
//        Runnable backgroundRunnable = new Runnable(){
//            @Override
//            public void run(){
//
//                if (url.length < 1 || url[0] == null){
//                    mHandler.post(null);
//                }
//                // Perform your background operation(s) and set the result(s)
//                result = QueryUtils.fetchEarthquakeData(url[0]);
//
//                // ...
//
//                // Use the handler so we're not trying to update the UI from the bg thread
//                mHandler.post(updateUIRunnable);
//            }
//        };
//
//        mExecutor.execute(backgroundRunnable);
//    }
