package io.taaem.vertretungsplan;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.util.Log;
import android.widget.FrameLayout;


import io.taaem.vertretungsplan.R;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FrameLayout layout;
    public static String KEY_PREF_USERKLASSE = "pref_userKlasse";
    public static String KEY_PREF_USERBUCHSTABE = "pref_userKlasseBuchstabe";
    public static String KEY_PREF_USERAUTH = "pref_userAuth";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.d("Vertretungsplan-Settings", "Create");
        // Display the fragment as the main content
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new SettingsFragment())
                .commit();
    }

}
