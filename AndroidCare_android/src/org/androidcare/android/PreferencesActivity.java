package org.androidcare.android;

import org.androidcare.android.R;
import org.androidcare.android.service.ReminderService;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Main activity
 * @author Alejandro Escario MŽndez
 *
 */
public class PreferencesActivity extends PreferenceActivity {

	private static final String TAG = "PreferencesActivity";
	
    protected AccountManager accountManager;
    
	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    addPreferencesFromResource(R.xml.preferences);

	    addGoogleAccounts();
	}

	private void addGoogleAccounts() {
	    
	    //1 - retrieve google accounts
        accountManager = AccountManager.get(getApplicationContext());
	    Account[] accounts = accountManager.getAccountsByType("com.google");
	    
	    //2 - listing the accounts
	    if(accounts.length > 0){

		    final ListPreference accountList = (ListPreference) findPreference("account");
		    
		    Context ctx = getApplicationContext();
		    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		    
		    String googleAccount = prefs.getString("account", "").trim();
		    
		    if(googleAccount.compareToIgnoreCase("") != 0){
		    	accountList.setSummary(googleAccount);
		    }
		    
		    accountList.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

				@Override
				public boolean onPreferenceChange(Preference preference,
						Object newValue) {
					Toast.makeText(getApplicationContext(), R.string.restarting_service, Toast.LENGTH_SHORT).show();
					Intent serviceIntent = new Intent(getApplicationContext(), ReminderService.class);
				    startService(serviceIntent);
				    accountList.setSummary(newValue.toString());
					return true;
				}});

		    int num = accounts.length;
		    String[] text = new String[num];
		    
		    for(int i = 0; i < num; i++){
		    	text[i] = accounts[i].name;
		    }
		    
		    accountList.setEntries(text);
		    accountList.setEntryValues(text);
	    }else{
	    	showNoAccountsDialog();
	    }
	}
	


	private void showNoAccountsDialog() {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(R.string.zero_accounts);
		alertDialog.setMessage(getResources().getString(R.string.setup_google_account));

		alertDialog.setButton(getResources().getString(R.string.open_account_settings), new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		    	  	Intent intent = new Intent(Settings.ACTION_SYNC_SETTINGS);
		    	  	startActivityForResult(intent, 0);
			    	  try {
			    		  finalizeActivity();
			    	  } catch (Throwable e) {
			    		  Log.e(TAG, "AccountSelectorActiviy couldn't be finished");
			    		  e.printStackTrace();
			    	  }
		    } });
		
		alertDialog.setButton2(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		    	  try {
		    		  finalizeActivity();
		    	  } catch (Throwable e) {
		    		  Log.e(TAG, "AccountSelectorActiviy couldn't be finished");
		    		  e.printStackTrace();
		    	  }
		    } });
		
		alertDialog.setIcon(R.drawable.notification_icon);
		alertDialog.show();
	}
	
	private void finalizeActivity() throws Throwable{
		this.finish();
	}
}