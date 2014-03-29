package com.appointext.frontend;

import com.bmsce.appointext.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class SettingsDisplay extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings);
		Log.i("Display", "Created preference activity");
	}
	
}
