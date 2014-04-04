package com.appointext.frontend;

import com.bmsce.appointext.R;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.View;

public class SettingsDisplay extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings);
		Log.i("Display", "Created preference activity");
		Preference dateTime = (Preference) findPreference("day_time");
		
		dateTime.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Log.i("AppoinText", "Got the click");
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
}
}
