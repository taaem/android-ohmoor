package io.taaem.vertretungsplan;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import android.os.AsyncTask;
import android.app.ProgressDialog;

import io.taaem.vertretungsplan.R;
import io.taaem.vertretungsplan.OneFragment;
import  io.taaem.vertretungsplan.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    // Some UI elements that need to be available everywhere in this class
    private ProgressDialog pDialog;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    // Alarm Management
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    // JSON Tags for the main View
    public static final String TAG_DATE = "date";
    public static final String TAG_URL = "href";

    // Final Array needs to be available in the whole class
    private JSONArray dates = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> datesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        // Alarm Setup
        /* Retrieve a PendingIntent that will perform a broadcast */
        // Intent alarmIntent = new Intent("io.taaem.vertretungsplan.RELOAD_ITEMS");
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        mCalendar.set(Calendar.HOUR_OF_DAY, 7);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);

        if(Calendar.getInstance().getTimeInMillis() - mCalendar.getTimeInMillis() > 0){
            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        alarmManager.setInexactRepeating(AlarmManager.RTC, mCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        // UI Setup
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        datesList = new ArrayList<HashMap<String, String>>();

        // Initiate the query to the server
        new GetDates().execute();
    }

    // Everything for the Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            // Show SettingsActivity
            case R.id.settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private final List<String> mFragmentUrlList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String date, String href) {
            // Add tags to the fragment for loading more content
            Bundle args = new Bundle();
            args.putString("href", href);
            fragment.setArguments(args);

            mFragmentList.add(fragment);
            mFragmentTitleList.add(date);
            // Not necessary and Unused
            mFragmentUrlList.add(href);

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public CharSequence getPageUrl(int position){
            return mFragmentUrlList.get(position);
        }
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetDates extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Bitte Warten...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(ApiInfo.getUrl() + "/dates", ApiInfo.getKey());

            if (jsonStr != null) {
                try {
                    JSONArray jsonArr = new JSONArray(jsonStr);

                    // looping through All Items
                    for (int i = 0; i < jsonArr.length(); i++) {
                        JSONObject c = jsonArr.getJSONObject(i);

                        String date = c.getString(TAG_DATE);
                        String url = c.getString(TAG_URL);

                        // tmp hashmap for single dateItem
                        HashMap<String, String> dateMap = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        dateMap.put(TAG_DATE, date);
                        dateMap.put(TAG_URL, url);

                        // adding the tmp Hashmap to the class one
                        datesList.add(dateMap);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into Adapter
             * */
            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            for (int i = 0; i < datesList.size(); i++){
                HashMap<String,String> k = datesList.get(i);
                adapter.addFragment(new OneFragment(), k.get("date"), k.get("href"));
            }
            // Setup the Tabs
            viewPager.setAdapter(adapter);
            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
        }

    }
}
