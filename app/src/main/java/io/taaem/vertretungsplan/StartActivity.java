package io.taaem.vertretungsplan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class StartActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        startMainActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();

        startMainActivity();
    }

    private void startMainActivity() {
        // Get Preferences and show LoginActivity if not authorized
        Log.d("MainActivity", Boolean.toString(sharedPreferences.getBoolean("pref_userAuth", false)));
        Intent intent;
        if (!sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_USERAUTH, false)){
            intent = new Intent(this, LoginActivity.class);
        }else{
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
//        finish();
    }
}
