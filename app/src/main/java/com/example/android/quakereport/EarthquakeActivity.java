/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<EarthQuake>> {
    /**
     * URL for earthquake data from the USGS dataset
     */
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private EarthQuakeAdapter mEarthQuakeAdapter;
    private TextView mEmptyStateTextView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        mProgressBar = findViewById(R.id.progressBar);
        mEmptyStateTextView = findViewById(R.id.emptyTextView);

        /*  new EarthquakeAsyncTask().execute(USGS_REQUEST_URL);*/
        if (QueryUtils.isConnected(this)) {
            Log.i(LOG_TAG, "initLoader");
            getSupportLoaderManager().initLoader(0, null, this);
        } else {
            mEmptyStateTextView.setText(R.string.no_internet_connection);
            mProgressBar.setVisibility(View.GONE);
        }
        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Create a new {@link ArrayAdapter} of earthquakes
        mEarthQuakeAdapter = new EarthQuakeAdapter(this, new ArrayList<EarthQuake>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mEarthQuakeAdapter);


        //Set empty state textView to list view
        earthquakeListView.setEmptyView(mEmptyStateTextView);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EarthQuake earthQuake = mEarthQuakeAdapter.getItem(position);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(earthQuake.getEarthQuakeUrl()));

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });


    }

    @NonNull
    @Override
    public Loader<List<EarthQuake>> onCreateLoader(int i, @Nullable Bundle bundle) {
        Log.i(LOG_TAG, "onCreateLoader");
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        String magnitude=sharedPreferences.getString(getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        Uri baseUri=Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder=baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format","geojson");
        uriBuilder.appendQueryParameter("orderby","time");
        uriBuilder.appendQueryParameter("minmag",magnitude);
        uriBuilder.appendQueryParameter("limit","10");


        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<EarthQuake>> loader, List<EarthQuake> earthQuakes) {
        Log.i(LOG_TAG, "onLoadFinished");

        // Clear the adapter of previous earthquake data
        mEarthQuakeAdapter.clear();

        if (QueryUtils.isConnected(this)) {
            mEmptyStateTextView.setText(R.string.no_earthquake_found);
        } else {
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
        mProgressBar.setVisibility(View.GONE);

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthQuakes != null && !earthQuakes.isEmpty()) {
            mEarthQuakeAdapter.addAll(earthQuakes);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<EarthQuake>> loader) {
        Log.i(LOG_TAG, "onLoaderReset");
        // Loader reset, so we can clear out our existing data.
        mEarthQuakeAdapter.clear();
    }


    private class EarthquakeAsyncTask extends AsyncTask<String, Void, ArrayList<EarthQuake>> {

        @Override
        protected ArrayList<EarthQuake> doInBackground(String... urls) {
            return QueryUtils.fetchEarthquakeData(urls[0]);
        }

        @Override
        protected void onPostExecute(final ArrayList<EarthQuake> earthquakes) {

            // Clear the adapter of previous earthquake data
            mEarthQuakeAdapter.clear();

            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (earthquakes != null && !earthquakes.isEmpty()) {
                mEarthQuakeAdapter.addAll(earthquakes);
            }


        }
    }

    private static class EarthquakeLoader extends AsyncTaskLoader<List<EarthQuake>> {

        String mUrls;

        public EarthquakeLoader(Context context, String urls) {
            super(context);
            mUrls = urls;
        }

        @Override
        public List<EarthQuake> loadInBackground() {
            Log.i(LOG_TAG, "loadInBackground");
            return QueryUtils.fetchEarthquakeData(mUrls);
        }

        @Override
        protected void onStartLoading() {
            Log.i(LOG_TAG, "onStartLoading");
            forceLoad();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
