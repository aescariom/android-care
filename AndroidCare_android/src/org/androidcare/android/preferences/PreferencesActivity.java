package org.androidcare.android.preferences;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import org.androidcare.android.R;
import org.androidcare.android.service.ServiceManager;

public class PreferencesActivity extends PreferenceActivity {

    private final String TAG = this.getClass().getName();
    protected AccountManager accountManager;
    protected boolean isMock;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isMock = getApplicationContext().getResources().getBoolean(R.bool.mock);
        
        addPreferencesFromResource(R.xml.preferences);
        addGoogleAccounts();
        setDisplayRemindersButton();
        setDisplayAlarmsButton();
        setAdvancedButton();
    }

    private void setDisplayRemindersButton(){
        @SuppressWarnings("deprecation")
        final Preference restart = (Preference) findPreference("viewReminders");
        restart.setOnPreferenceClickListener(new OnPreferenceClickListener(){

            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(PreferencesActivity.this, ReminderList.class);
                startActivity(intent);
                return true;
            }
            
        });
    }

    private void setDisplayAlarmsButton(){
        @SuppressWarnings("deprecation")
        final Preference restart = (Preference) findPreference("viewAlarms");
        restart.setOnPreferenceClickListener(new OnPreferenceClickListener(){

            public boolean onPreferenceClick(Preference preference) {
                Log.v(TAG, "Llegando a alarmas");
                Intent intent = new Intent(PreferencesActivity.this, AlarmList.class);
                startActivity(intent);
                return true;
            }

        });
    }

    private void setAdvancedButton(){
        @SuppressWarnings("deprecation")
        final Preference restart = (Preference) findPreference("showAdvanced");
        restart.setOnPreferenceClickListener(new OnPreferenceClickListener(){

            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(PreferencesActivity.this, AdvancedPreferencesActivity.class);
                startActivity(intent);
                return true;
            }
            
        });
    }

    private void addGoogleAccounts() {

        // 1 - retrieve google accounts
        accountManager = AccountManager.get(getApplicationContext());
        Account[] accounts = accountManager.getAccountsByType("com.google");

        // 2 - listing the accounts
        if (accounts.length > 0) {
            @SuppressWarnings("deprecation")
            final ListPreference accountList = (ListPreference) findPreference("account");
            Context ctx = getApplicationContext();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            String googleAccount = prefs.getString("account", "").trim();
            if (googleAccount.compareToIgnoreCase("") != 0) {
                accountList.setSummary(googleAccount);
            }

            accountList.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Toast.makeText(getApplicationContext(), R.string.restarting_service, Toast.LENGTH_SHORT)
                            .show();
                    ServiceManager.startAllServices(getApplicationContext());
                    accountList.setSummary(newValue.toString());
                    return true;
                }
            });

            int numberOfAccounts = accounts.length;
            String[] accountNames = new String[numberOfAccounts];

            for (int i = 0; i < numberOfAccounts; i++) {
                accountNames[i] = accounts[i].name;
            }

            accountList.setEntries(accountNames);
            accountList.setEntryValues(accountNames);
        } else {
            if(!isMock){
                showNoAccountsDialog();
            }
        }
    }

    private void showNoAccountsDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.zero_accounts);
        alertDialog.setMessage(getResources().getString(R.string.setup_google_account));
//Comentario estos dos warnings pod�amos resolver los
    //    http://stackoverflow.com/questions/13268302/alternative-setbutton
        alertDialog.setButton(getResources().getString(R.string.open_account_settings),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_SYNC_SETTINGS);
                        startActivityForResult(intent, 0);
                        finish();
                    }
                });

        alertDialog.setButton2(getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        alertDialog.setIcon(R.drawable.notification_icon);
        alertDialog.show();
    }
    
    //Comentario la funcionalidad que ten�amos aqu� para parar y arrancar los servicios a veces nos fue �til para depurar
    //�las borrado por alg�n motivo?
}