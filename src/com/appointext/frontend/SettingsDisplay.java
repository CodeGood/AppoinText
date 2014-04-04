package com.appointext.frontend;

import com.bmsce.appointext.R;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

public class SettingsDisplay extends PreferenceActivity {
	static final int PICK_CONTACT_REQUEST = 1;

	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings);
		Preference dateTime = (Preference) findPreference("day_time");
		
		dateTime.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(SettingsDisplay.this, TimeSelector.class);
				startActivity(intent);
				return false;
			}
		});


		Preference noNag = (Preference) findPreference("no_nag");
		noNag.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(SettingsDisplay.this, NoNagMode.class);
				startActivity(intent);
				return false;
				}
		
	});
			
		Preference contactBlocker = (Preference) findPreference("contactChosen");

	contactBlocker.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
			    pickContactIntent.setType(Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
			    startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
			    return false;
			}
		});
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == PICK_CONTACT_REQUEST){
			   if(resultCode == RESULT_OK){
			    Uri contactData = data.getData();
			    Cursor cursor =  managedQuery(contactData, null, null, null, null);
			    cursor.moveToFirst();
			      String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
			      Log.i("BlockedNumber",number);
	        }
		}
	}
}
