package com.appointext.widget;

import com.appointext.frontend.AppoinTextActivity;
import com.appointext.frontend.ReminderPeriodPicker;
//import com.appointext.frontend.RemindersDisplay;
import com.appointext.frontend.RemindersHour;
import com.appointext.frontend.RemindersPeriodDisplay;

import com.appointext.frontend.RemindersToday;
import com.bmsce.appointext.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

//import android.text.format.DateFormat;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends AppWidgetProvider {
	  SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
	 
	  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
	    final int N = appWidgetIds.length;
	    //View view = null;
	  
	    
	    
	   // Log.i("ExampleWidget",  "Updating widgets " + Arrays.asList(appWidgetIds));
	 
	    // Perform this loop procedure for each App Widget that belongs to this
	    // provider
	    for (int i = 0; i < N; i++) {
	    	int appWidgetId = appWidgetIds[i];

	    	// Get the layout for the App Widget and attach an on-click listener 
	    	// to the button 
	    	RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.activity_main_widget);
	    	
	    	//Linear Layout 
	    	
	    	//LinearLayout linear = (LinearLayout)view.findViewById(R.id.layout1);
	    	
	    	
	    	// Create an Intent to launch AppoinTextActivityClass from frontend 
	    	Intent intent = new Intent(context, AppoinTextActivity.class);
	    	PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
	    	views.setOnClickPendingIntent(R.id.buttonAT, pendingIntent);

	    	// Create an Intent to launch Day from frontend
	    	Intent intent1 = new Intent(context, RemindersToday.class);
	    	PendingIntent pending = PendingIntent.getActivity(context, 0, intent1, 0);
	    	views.setOnClickPendingIntent(R.id.button3, pending); 
	    	
	    	// Create an Intent to launch Hours from frontend 
	       	Intent intent2 = new Intent(context, RemindersHour.class);
	    	PendingIntent pending1 = PendingIntent.getActivity(context, 0, intent2, 0);
	    	views.setOnClickPendingIntent(R.id.button1, pending1); 
	    	
	    	// Create an Intent to launch Customize from frontend
	       	Intent intent3 = new Intent(context, ReminderPeriodPicker.class);
	    	PendingIntent pending2 = PendingIntent.getActivity(context, 0, intent3, 0);
	    	views.setOnClickPendingIntent(R.id.button2, pending2); 
	    	
	    	appWidgetManager.updateAppWidget(appWidgetId, views);
	    } 
	  } 
	}