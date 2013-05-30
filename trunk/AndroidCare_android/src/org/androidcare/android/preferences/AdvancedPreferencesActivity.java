package org.androidcare.android.preferences;

import org.androidcare.android.R;
import org.androidcare.android.service.ServiceManager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.provider.Settings;
import android.text.InputType;
import android.widget.Toast;

public class AdvancedPreferencesActivity extends PreferenceActivity {

    protected AccountManager accountManager;
    protected boolean isMock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isMock = getApplicationContext().getResources().getBoolean(R.bool.mock);
        
        addPreferencesFromResource(R.xml.advanced_preferences);
        setLocationUpdates();
        setSynchronizationInterval();
        setReminderUpdates();
    }
    
    private void setSynchronizationInterval() {
        EditTextPreference pref = (EditTextPreference)findPreference("synchronizationInterval");
        pref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        pref.setSummary(pref.getText() + " " + getApplicationContext().getResources().getString(R.string.minute_s));
        pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            public boolean onPreferenceChange(Preference pref, Object newValue) {
                pref.setSummary(newValue.toString() + " " + getApplicationContext().getResources().getString(R.string.minute_s));
                return true;
            }
        });
    }

    private void setLocationUpdates() {
        EditTextPreference pref = (EditTextPreference)findPreference("locationUpdatesInterval");
        pref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        pref.setSummary(pref.getText() + " " + getApplicationContext().getResources().getString(R.string.minute_s));
        pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            public boolean onPreferenceChange(Preference pref, Object newValue) {
                pref.setSummary(newValue.toString() + " " + getApplicationContext().getResources().getString(R.string.minute_s));
                return true;
            }
        });
    }
    
    private void setReminderUpdates() {
        EditTextPreference pref = (EditTextPreference)findPreference("reminderResquestInterval");
        pref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        pref.setSummary(pref.getText());
        pref.setSummary(pref.getText() + " " + getApplicationContext().getResources().getString(R.string.hour_s));
        pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            public boolean onPreferenceChange(Preference pref, Object newValue) {
                pref.setSummary(newValue.toString() + " " + getApplicationContext().getResources().getString(R.string.hour_s));
                return true;
            }
        });
    }
}