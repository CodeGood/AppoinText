package com.appointext.database;


/** JUSTIFICATION
 * 	I did think of making getCalendarID public. But then I realized each IntentService will serve exactly one sms and die. 
 * So by the time the next reminder has to be set, the previous calendar id is well forgotten.
 * So why make the user work more? May as well as call my API ... 
*/ 

 /*
 * TODO :
 * 		1. Add multiple attendees rather than a CSV
 * 		2. Check whether delete actually deletes the reminder or not. _ DONE
 * 		3. Check whether updating the start time actually updates the reminder time. - DONE.
 * 	I am 99.99% sure of the last two, but then who knows! Always better to check or at least document unchecked  
 */

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.appointext.regex.RecognizeEvent;
import com.bmsce.appointext.R;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.text.format.DateUtils;
import android.util.Log;

@SuppressLint("DefaultLocale")
public class CalendarInsertEvent {
	
	/**
	 * Stores the context CalendarInsertEvent requires to operate
	 */
	private static Context con;
	
	/**
	 * Creates a Calendar for the AppoinText Application
	 * @param con - The context which is inevitably required to do anything at all in this universe!
	 */
	
	private static void createCalendar() {
				  
		  ContentValues values = new ContentValues();
		  values.put(Calendars.ACCOUNT_NAME, "AppoinText");
		  values.put(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
		  values.put(Calendars.NAME, "AppoinText Automated calendar");
		  values.put(Calendars.CALENDAR_DISPLAY_NAME, "AppoinText Calendar");
		  values.put(Calendars.CALENDAR_COLOR,  0xffff00ff);
		  values.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
		  values.put(Calendars.OWNER_ACCOUNT, "appointext@gmail.com");
		  values.put(Calendars.CALENDAR_TIME_ZONE, TimeZone.getDefault().toString());
		  
Log.d("AppoinText", "Got Timezone as " + TimeZone.getDefault().toString());

		  Uri.Builder builder = CalendarContract.Calendars.CONTENT_URI.buildUpon(); 
		  builder.appendQueryParameter(Calendars.ACCOUNT_NAME, "com.appointext");
		  builder.appendQueryParameter(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
		  builder.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true");
		  con.getContentResolver().insert(builder.build(), values);
		
	}

	/**
	 * Returns the ID of the AppoinText Calendar.
	 * @param con - An Application context. If you are calling this method from an Activity or Service class, you can pass 'this', otherwise you need your own context
	 * @return - An ID of the calendar that was found first
	 */
	
	 private static long getCalendarId() { //Just get the first calendar ID the comes up.
		   
		  try {
			  String[] projection = new String[]{
				            Calendars._ID, 
				            Calendars.NAME, 
				            Calendars.ACCOUNT_NAME, 
				            Calendars.ACCOUNT_TYPE};
				Cursor calCursor = con.getContentResolver().query(
								  Calendars.CONTENT_URI, 
				                  projection, 
				                  Calendars.VISIBLE + " = 1", 
				                  null, 
				                  Calendars._ID + " ASC");
				if (calCursor.moveToFirst()) {
				   do {
				      long id = calCursor.getLong(0);
				      //String account_name = calCursor.getString(2); TODO: Check for our own calendar
				      //if (account_name.equals(MY_ACCOUNT_NAME))
				      	return id;
				   } while (calCursor.moveToNext());
				}
				
		  }
		  catch(Exception e) { //If no calendars are found, then add a new calendar		  
			  Log.e("AppoinText - Calendar", "Error in reading the Calendar Value : " + e);
		  }
		  
		return -1;
		
	   }
	 
	   public static long addReminderViaCalendarApp(Context cont, int date, int month, int year, int hour, int minute, int min_before_event, String title, String location, String desc, String attendees) { 
		   
		   /* I don't see any easy way of adding the reminder time and attendees */
		   
		   Intent intent = new Intent(Intent.ACTION_INSERT);
		   intent.setData(Events.CONTENT_URI); 

		   if (date >= 0 && hour >= 0) {
			    Calendar cal = new GregorianCalendar(year, month-1, date);
				cal.setTimeZone(TimeZone.getDefault());
				cal.set(Calendar.HOUR, hour);
				cal.set(Calendar.MINUTE, minute);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				long start = cal.getTimeInMillis();
				intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start); 
				intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, start);
		   }
		   
		   //if (min_before_event > 0 )
			 //  intent.putExtra
			   
		   if (title != null && !title.equals(""))
			   intent.putExtra(Events.TITLE, title);
		   
		   if (location != null && !location.equals(""))
			   intent.putExtra(Events.EVENT_LOCATION, location);
		   
		   if (desc != null && !desc.equals(""))
			   intent.putExtra(Events.DESCRIPTION, desc);
		   
		   intent.putExtra(Events.ALL_DAY, false);
		   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		   cont.startActivity(intent);
		   
		   return -2; //because we have no control on what the user chose to do with this! 
	   }
		
		/**
		 * Adds a Reminder to the calendar
		 * @param cont -Context. If you are calling this method from a class that inherits Activity or Service, you can pass 'this' as context. 
		 * 			Otherwise, you must have been passed a Context from one of the Activity or Service classes. Go look under the curtain for it :P
		 * @param date - The date of the reminder. Mandatory parameter
		 * @param month - Mandatory
		 * @param year - Mandatory
		 * @param hour - Mandatory
		 * @param minute - Mandatory. I assume you do not want milisecond precesion? However that can be provided too!
		 * @param min_before_event - Mandatory. Time before the event that the reminder must be set. Has to be given in minutes
		 * @param title - Optional. If null or empty string, calendar intent shall be raised.
		 * @param location - Optional. Can be null
		 * @param desc - Optional. Can be null. Description of the event. What you are shown when you tap an event
		 * @param attendees - Optional. Comma seperated list of attendees. Can be null
		 * @return eventID. You may want to store it somewhere or just ignore it. 
		 * 						However a value of -1 means reminder was not set, while -2 means it was sent as an Intent
		 */
	
	   @SuppressLint("DefaultLocale")
	public static long addReminder(Context cont, int date, int month, int year, int hour, int minute, int min_before_event, String title, String location, String desc, String attendees) { 

		con = cont; //Set it for use by the whole class
		month = month-1;
		
		title = title.replaceAll(",", "");
		location = location.replaceAll(",", "");
		desc = desc.replaceAll(",", "");
		
		if (location.equals(desc)) //Since you are bad enough to pass the location also as the current text :( This is my work around :D
			location = "";
		
		//Get ahold of prompt settings and act accordingly
		final DatabaseManager dbPrompt = new DatabaseManager(cont);
		String prompt;
		dbPrompt.open();
		ArrayList<Object> row;
		row = dbPrompt.getRowAsArray("settingsTable", "PromptControl");
		dbPrompt.close();
		if(row == null || row.isEmpty())
			prompt = "ambiguity";
		else
			prompt = (String)row.get(1);
		
		//convert the provided reminder time into proper time
		Integer remTime = RecognizeEvent.times.get(title.toLowerCase().trim());
		if (remTime != null)
			min_before_event = remTime;
		
		if (prompt.equalsIgnoreCase("always")) //Not my head ache at ALL. Let's just ignore this :D
			return addReminderViaCalendarApp(cont, date, month, year, hour, minute, min_before_event, title, location, desc, attendees);

		
		try {
			
			long calId = getCalendarId();
			if (calId == -1) { //No calendar
				createCalendar();
				calId = getCalendarId();
			}
			
Log.d("AppointextReminder", "Obtained Calendar ID as " + calId);
			

			/* Now that we know which calendar we are talking about, let's go ahead and check whether any of the required fields are missing. If they are, we go via the intent path. */
			if (date < 1 || hour < 1) { 
				if (!prompt.equalsIgnoreCase("never"))//If date and/or time are unknown, and prompt is not for never
					return addReminderViaCalendarApp(cont, date, month, year, hour, minute, min_before_event, title, location, desc, attendees);
				else
					return -1;
			}

			// This is to create the time in milliseconds as required by the put(Events.DSTART) 

			Calendar cal = new GregorianCalendar(year, month, date);
			cal.setTimeZone(TimeZone.getDefault());
			cal.set(Calendar.HOUR, hour);
			cal.set(Calendar.MINUTE, minute);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			long start = cal.getTimeInMillis();
			
			/*Now that I have a time, first lets check if a reminder will at all be required. */
			
			//First let's figure out what is the mininum time period for which a reminder is required
			
			final DatabaseManager db = new DatabaseManager(cont);
			long timeInMins;
			db.open();
			row = db.getRowAsArray("settingsTable", "MinimumTime");
			db.close();
			if(row == null || row.isEmpty())
				timeInMins = 60;
			else
				timeInMins = (Integer)row.get(1);			
			
			Log.i("AppoinTextReminder", "Okay, time in Mins is " + timeInMins);
			
			long curTime = System.currentTimeMillis(); //get current time
			if (start - curTime < timeInMins* DateUtils.MINUTE_IN_MILLIS) { //if requisite time is not there
				Log.i("AppoinTextReminder", "Too soon");
				return -1; //Reminder not set. But not an error.
			}
			
			/* Now, we move on if a reminder is required. */
			
			/* First check whether it is a duplicate */
			
			if (HandleConflict.duplicateExists(con, title, start, start, desc, location)) //If an exact duplicate exists
				return -1; //Reminder not set
			
			/* Then check if there is a conflict */
			
			if (HandleConflict.findConflicts(con, start, title, attendees)) //Conflict found
				return -2; //reminder not set. Find conflict will raise the notification which shall handle the conflict.
			
			 //Now we start creating the entry to be sent to the calendar database 
			
			if (title == null || title.equals("") || min_before_event < 0) {
					if (!prompt.equalsIgnoreCase("never")) //If title is unknown or you have ambiguity about the time of reminder
						return addReminderViaCalendarApp(cont, date, month, year, hour, minute, min_before_event, title, location, desc, attendees);
					else
						return -1;
			}
			
			ContentValues values = new ContentValues();
			
			values.put(Events.CALENDAR_ID, calId); //set calendar
			
			 //Set the mandatory values now 
			
			values.put(Events.DTSTART, start); 
			values.put(Events.DTEND, start);

Log.d("AppoinTextReminder", "Got the basic values into it.");			

			if ( title != null && !title.equalsIgnoreCase(""))			values.put(Events.TITLE, title);
			else						values.put(Events.TITLE, "Default Event"); //I assume no one will be dumb enough to send title as null? 
Log.d("AppoinTextReminder", "Inserted title");
			// Now let us set the optional values 
			if ( location !=null && !location.equalsIgnoreCase("") )		values.put(Events.EVENT_LOCATION, location);
			Log.d("AppoinText", "Inserted location");
			if ( desc != null && !desc.equalsIgnoreCase(""))		{ Log.d("AppoinText description", "Inserting non null" + desc);	values.put(Events.DESCRIPTION, desc); }
			else  { 	Log.d("AppoinText", "Inserted null description"); }
			
Log.d("AppoinTextReminder", "Got the optional values as well");			
			// Some common sense values, which do not require being parameterized 
			values.put(Events.SELF_ATTENDEE_STATUS, Events.STATUS_CONFIRMED);
			Log.d("AppoinText", "Inserted attendee status");
			values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
			Log.d("AppoinText", "Inserted timezone");
			values.put(Events.GUESTS_CAN_INVITE_OTHERS, 1);
			Log.d("AppoinText", "Inserted gues invite others");
			values.put(Events.GUESTS_CAN_MODIFY, 1);
			Log.d("AppoinText", "Inserted guests can modify");
			values.put(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
Log.d("AppoinTextReminder", "Attempting to add events to calendar");						
			// Now add to calendar 
			Uri uri = 
			     con.getContentResolver().
			            insert(Events.CONTENT_URI, values);
Log.d("AppoinTextReminder", "AppoinTextCalendar");
			long eventId =  Long.valueOf(uri.getLastPathSegment());
			
Log.d("AppoinTextReminder", "Event" + title + " added successfully");

			// Now set a reminder 

			values.clear();
			values.put(Reminders.EVENT_ID, eventId);
			values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
			values.put(Reminders.MINUTES, min_before_event);
			con.getContentResolver().insert(Reminders.CONTENT_URI, values);
			
			// Now add attendees for the reminder. I assume we have nothing to do with anything other than name 
			if (!attendees.equalsIgnoreCase("") && attendees !=  null) {
			
				ContentResolver cr = con.getContentResolver();
Log.i("AppoinText People", "Attendes are " + attendees);				
				String attendeesCSV = "";
				for (String name : attendees.split(",")) {
					if (name != null && name.length() != 0) //ensure no null names pass through by any chance
						attendeesCSV += HandleConflict.convertNumberToName(cont, name) + ",";
				}
				
					values.clear();
					values.put(Attendees.EVENT_ID, eventId);
					values.put(Attendees.ATTENDEE_NAME, attendeesCSV); //TODO: Get attendees to work
					cr.insert(Attendees.CONTENT_URI, values);
				//}
Log.i("AppoinText People", "Attendees inserted are " + attendeesCSV);
				
			}
			
Log.d("Appointext Calendar", "Reminder" + title + "Added Successfully");

			return eventId;			
			
		}
		catch(Exception e) {
			Log.e("AppointextReminder", "Error in Reminder " + title + " Setting : " + e.getMessage() + "\nCause may be : " + e.getCause());
			Log.d("AppoinTextReminder", "Values are " + date+" "+ month+" "+ year+" "+hour+" "+minute+" "+min_before_event+" "+title+" "+ location+" "+ desc +" "+attendees);
		}
		
		return -1;
	
	}
	   
	   /**
	    * Updates calendar entry
	    * @param con - Context
	    * @param entryID - Event to be updated
	    * @param updateValues - each String should be a Key,Value pair, separated by commas. DO NOT add attendees here. This is for location, title, description, start time and end time
	    * @param min_before_event - Number of minutes before the event you want your reminder now. If no change is required, pass a negative value.
	    * @param attendees - New attendee list
	    * @return - number of rows updated
	    */
		   
	   public static int updateCalendarEntry(Context con, long entryID, String[] updateValues, int min_before_event, String attendees) {
		        
		   		int iNumRowsUpdated = 0;

		        ContentValues values = new ContentValues();
		        
		        //Get previous attendees
		        if (attendees == null || attendees.length() == 0)
		        	attendees = ""; //Just get hold of an empty string
		        else if (!attendees.endsWith(","))
		        	attendees += ","; //Add a comma at the end
		        
Log.d("AppoinText People", "Got ID as " + entryID);
Log.d("AppoinText FalseConflict", "Got old attendees as " + attendees);

		        ContentResolver cr = con.getContentResolver();
		        Cursor cursor = CalendarContract.Attendees.query(cr, entryID, new String[] {CalendarContract.Attendees.ATTENDEE_NAME} );
		        if (cursor.moveToFirst()) {
					   do {
							  String at = cursor.getString(0);
							  if (at != null && at.endsWith(",")) //there is a terminal comma
								  at = at.substring(0 , at.length()-1); //get rid of it
Log.d("AppoinText FalseConflict", "Got current attendee as " + at);							  
							  if (at != null && at.length() != 0)
								attendees += cursor.getString(0) + ",";		  
						   } while (cursor.moveToNext());		        	
					  
		        }
		        
Log.d("AppoinText FalseConflict", "Updating attendees to " + attendees);
		        
		        if (updateValues != null) {
			        
			        for (String cur : updateValues) {
			        	String[] parts = cur.split(",");
			        			        	
			        	if (parts[0].equals(Events.TITLE)) {
			        		//convert the provided reminder time into proper time
			        		Integer remTime = RecognizeEvent.times.get(parts[0].toLowerCase().trim());
			        		if (remTime != null)
			        			min_before_event = remTime;
			        	}
			        		
			        	values.put(parts[0], parts[1]);
			        }
			        
	
			        Uri eventUri = ContentUris.withAppendedId(Events.CONTENT_URI, entryID);        
			        iNumRowsUpdated = con.getContentResolver().update(eventUri, values, null,
			                			null);
		        }
		        Log.i("AppoinText", "Updated " + iNumRowsUpdated + " calendar entry.");
		        
		        // adding the reminder, if change is require 
		        if (min_before_event > 0) {
		        	values.clear();
		        	values.put(Reminders.EVENT_ID, entryID);
		        	values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
		        	values.put(Reminders.MINUTES, min_before_event);
		        	con.getContentResolver().insert(Reminders.CONTENT_URI, values);
		        }
		        				
				// adding attendees if any. In a CSV 
				if (attendees != null) {
					values.clear();
					values.put(Attendees.EVENT_ID, entryID);
					values.put(Attendees.ATTENDEE_NAME, attendees); //TODO: Get attendees to work
					con.getContentResolver().insert(Attendees.CONTENT_URI, values);
				}
				
				if (updateValues != null && attendees.split(",").length > 1) { //Multiple attendees and there was a change of plan
					
					NotificationManager notificationManager 
					  = (NotificationManager)con.getSystemService(Context.NOTIFICATION_SERVICE);
					Notification.Builder builder = new Notification.Builder(con);
					builder
					  .setSmallIcon(R.drawable.reminder_hand)
					  .setContentTitle("Group plan changed. Inform?")
					  .setContentText(attendees)
					  .setTicker("Group plan Changed.")
					  .setLights(0xFFFF0000, 500, 500) //setLights (int argb, int onMs, int offMs)
					  .setAutoCancel(true);
					  
					@SuppressWarnings("deprecation")
					Notification notification = builder.getNotification(); //Yes, am NOT updating to API 14. We promised LOW END ANDROID!
					  
					notificationManager.notify(R.drawable.reminder_hand, notification);
				}
				
		        return iNumRowsUpdated;
	   }

	   /**
	    * Deletes the event associated with the eventID passed.
	    * @param con - Context
	    * @param entryID - Event ID
	    * @return - number of rows deleted
	    */
	   
	   public static int deleteCalendarEntry(Context con, long entryID) {
		   
		        int iNumRowsDeleted = 0;
		        Uri eventUri = ContentUris.withAppendedId(Events.CONTENT_URI, entryID);
		        iNumRowsDeleted = con.getContentResolver().delete(eventUri, null, null);
		        Log.i("AppoinText", "Deleted " + iNumRowsDeleted + " calendar entry.");
		        return iNumRowsDeleted;
		}
	   
	   public static int updateAttendees(Context con, long entryID, String attendees) {
	        
	   		int iNumRowsUpdated = 0;
	        ContentValues values = new ContentValues();	        
		
			// adding attendees if any. In a CSV 
			if (attendees != null) {
				values.clear();
				values.put(Attendees.EVENT_ID, entryID);
				values.put(Attendees.ATTENDEE_NAME, attendees); //TODO: Get attendees to work
				con.getContentResolver().insert(Attendees.CONTENT_URI, values);
			}
			
	        return iNumRowsUpdated;
	   }

}


