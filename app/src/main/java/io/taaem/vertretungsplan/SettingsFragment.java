package io.taaem.vertretungsplan;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.content.SharedPreferences;
import android.preference.Preference;

import java.util.Objects;

import io.taaem.vertretungsplan.SettingsActivity;

import io.taaem.vertretungsplan.R;
/**
 * Created by taaem on 05.11.15.
 */
public class SettingsFragment extends PreferenceFragment {
    private ListPreference klasse;
    private ListPreference sign;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_user);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_user, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        klasse =  (ListPreference)findPreference(SettingsActivity.KEY_PREF_USERKLASSE);
        klasse.setOnPreferenceChangeListener(changeListener);

        sign = (ListPreference)findPreference(SettingsActivity.KEY_PREF_USERBUCHSTABE);
        sign.setOnPreferenceChangeListener(changeListener);

        checkOberStufe(sharedPreferences.getString(SettingsActivity.KEY_PREF_USERKLASSE, ""));

        Log.d("Vertretungsplan", "Created Fragment");

    }

    Preference.OnPreferenceChangeListener changeListener = new Preference.OnPreferenceChangeListener() {
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Log.d("Preference", preference.getKey());
            String key = preference.getKey().toString();
            if (key.equals(SettingsActivity.KEY_PREF_USERKLASSE)){
                Log.d("Preference", newValue.toString());
                checkOberStufe(newValue.toString());
            }
            return true;
        }
    };
    private void checkOberStufe(String number){
        if(number.equals("11") || number.equals("12")){
            sign.setEnabled(false);
            sign.setValue(null);
        }else{
            sign.setEnabled(true);
        }
    }
}
