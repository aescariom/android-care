package org.androidcare.android;

import org.androidcare.android.service.LocalService;
import org.androidcare.android.R;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Main activity
 * @author Alejandro Escario MŽndez
 *
 */
public class MainActivity extends ListActivity {
    protected AccountManager accountManager;
    protected Intent intent;
    
	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    //1 - retrieving the google accounts
	    accountManager = AccountManager.get(getApplicationContext());
	    Account[] accounts = accountManager.getAccountsByType("com.google");
	    
	    //2 - listing all the accounts
	    this.setListAdapter(new ArrayAdapter<Object>(this, R.layout.simple_item_layout, accounts)); 
	    
	    //3 - Starting the service that will manage everything
		Intent i = new Intent(this.getApplicationContext(),
				   LocalService.class);
		this.startService(i);  
		
		//4 - Closing the activity
		//this.finish();
	}

	/**
	 * 
	 */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    }
}