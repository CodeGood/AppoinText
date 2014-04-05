package com.appointext.frontend;

import java.util.ArrayList;

import com.appointext.database.DatabaseManager;
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
import android.widget.Toast;

public class SettingsDisplay extends PreferenceActivity {
	static final int PICK_CONTACT_REQUEST = 1;

	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings);

		//Preferences to select the times for various times of the day
		Preference dateTime = (Preference) findPreference("day_time");
		dateTime.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(SettingsDisplay.this, TimeSelector.class);
				startActivity(intent);
				return false;
			}
		});

		//Preferences for No Nag Mode
		Preference noNag = (Preference) findPreference("no_nag");
		noNag.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(SettingsDisplay.this, NoNagMode.class);
				startActivity(intent);
				return false;
			}

		});

		//Preferences for Choosing a Contact To Be Excluded from the contacts
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

		//To show numbers that are excluded from AppoinText's purview. :P
		Preference displayBlocked = (Preference) findPreference("ShowBlockedNumbers");
		displayBlocked.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(SettingsDisplay.this, BlockedNumberDisplay.class);
				startActivity(intent);
				return false;
			}
		});

		//Preference to try and get the user to enter manual numbers to block! :P
		Preference manualNumber = (Preference) findPreference("numberManual");
		manualNumber.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(SettingsDisplay.this, ManualBlockedNumber.class);
				startActivity(intent);
				return false;
			}
		});

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String finalData;
		if(requestCode == PICK_CONTACT_REQUEST){
			if(resultCode == RESULT_OK){
				Uri contactData = data.getData();
				Cursor cursor =  managedQuery(contactData, null, null, null, null);
				cursor.moveToFirst();
				String number = (cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))).replaceAll("[^0-9]", "");
				String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));
				Log.i("BlockedName",name);
				DatabaseManager db = new DatabaseManager(SettingsDisplay.this);
				db.open();
				ArrayList<Object> row;
				row = db.getRowAsArray("settingsTable", "BlockedNumbers");
				if(row.isEmpty())	{
					finalData = number + " - " + name;
					db.addRow("settingsTable", "BlockedNumbers", finalData);
					Toast.makeText(getApplicationContext(), number + " added to excluded list!", Toast.LENGTH_SHORT).show();
				}
				else	{
					String numbersExisting = row.get(1).toString();
					if(!numbersExisting.contains(number))	{
						finalData = number + " - " + name;
						String toBeAdded = numbersExisting + "," + finalData;
						db.updateRow("settingsTable", "BlockedNumbers", toBeAdded);
						Toast.makeText(getApplicationContext(), number + " added to excluded list!", Toast.LENGTH_SHORT).show();
					}
					else
						Toast.makeText(getApplicationContext(), number + " already exists in excluded list!", Toast.LENGTH_SHORT).show();
				}
			}

		}

	}
}

