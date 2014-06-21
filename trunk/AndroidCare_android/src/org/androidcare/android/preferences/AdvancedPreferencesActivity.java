package org.androidcare.android.preferences;

import org.androidcare.android.R;
import org.androidcare.android.service.ServiceManager;

import android.accounts.AccountManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.InputType;
import android.widget.Toast;

public class AdvancedPreferencesActivity extends PreferenceActivity {

    protected AccountManager accountManager;
    protected boolean isMock;
//Comentario es que me fastidiaba en estos warnings y no tengo intenci�n de ponernos a usar fragmentos
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isMock = getApplicationContext().getResources().getBoolean(R.bool.mock);
        
        addPreferencesFromResource(R.xml.advanced_preferences);
        setLocationUpdates();
        setSynchronizationInterval();
        setReminderUpdates();
        setAlarmUpdates();
        setCacheTime();
        setStopButton();
        setResetButton();
    }

    private void setSynchronizationInterval() {
        @SuppressWarnings("deprecation")
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
        @SuppressWarnings("deprecation")
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
    
    private void setAlarmUpdates() {
        @SuppressWarnings("deprecation")
        EditTextPreference pref = (EditTextPreference)findPreference("alarmResquestInterval");
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

    private void setReminderUpdates() {
        @SuppressWarnings("deprecation")
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
    
    private void setCacheTime() {
        @SuppressWarnings("deprecation")
        EditTextPreference pref = (EditTextPreference)findPreference("reminderCacheTime");
        pref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        pref.setSummary(pref.getText());
        pref.setSummary(pref.getText() + " " + getApplicationContext().getResources().getString(R.string.day_s));
        pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            public boolean onPreferenceChange(Preference pref, Object newValue) {
                pref.setSummary(newValue.toString() + " " + getApplicationContext().getResources().getString(R.string.day_s));
                return true;
            }
        });
    }
    
    private void setResetButton(){
        @SuppressWarnings("deprecation")
        final Preference restart = (Preference) findPreference("restartServices");
        restart.setOnPreferenceClickListener(new OnPreferenceClickListener(){

            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getApplicationContext(), R.string.restarting_service, Toast.LENGTH_SHORT)
                .show();
                ServiceManager.startAllServices(getApplicationContext());
                return true;
            }
            
        });
    }
    
    private void setStopButton(){
        @SuppressWarnings("deprecation")
        final Preference restart = (Preference) findPreference("stopServices");
        restart.setOnPreferenceClickListener(new OnPreferenceClickListener(){

            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getApplicationContext(), R.string.stopping_service, Toast.LENGTH_SHORT)
                .show();
                ServiceManager.stopSecondaryServices(getApplicationContext());
                return true;
            }
            
        });
    }
}