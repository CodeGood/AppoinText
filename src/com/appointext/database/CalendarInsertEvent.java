package com.appointext.database;

/* JUSTIFICATION
 * 	I did think of making getCalendarID public. But then I realized each IntentService will serve exactly one sms and die. 
 * So by the time the next reminder has to be set, the previous calendar id is well forgotten.
 * So why make the user work more? May as well as call my API ... 
 */

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.util.Log;

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
		 * @param title - Mandatory. Title of the reminder. What you are shown when you open a calendar page
		 * @param location - Optional. Can be null
		 * @param desc - Optional. Can be null. Description of the event. What you are shown when you tap an event
		 * @param attendees - Optional. Comma seperated list of attendees. Can be null
		 * @return eventID. You may want to store it somewhere or just ignore it.
		 */
	
	   public static long addReminder(Context cont, int date, int month, int year, int hour, int minute, int min_before_event, String title, String location, String desc, String attendees) { 
		   
		con = cont; //Set it for use by the whole class
		   
		try {
			
			long calId = getCalendarId();
			if (calId == -1) { //No calendar
				createCalendar();
				calId = getCalendarId();
			}
			
Log.d("Appointext Calendar", "Obtained Calendar ID as " + calId);
			
			/* This is to create the time in milliseconds as required by the put(Events.DSTART) */

			Calendar cal = new GregorianCalendar(year, month, date);
			cal.setTimeZone(TimeZone.getDefault());
			cal.set(Calendar.HOUR, hour);
			cal.set(Calendar.MINUTE, minute);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			long start = cal.getTimeInMillis();
			
			/* Now we start creating the entry to be sent to the calendar database */
			
			ContentValues values = new ContentValues();
			
			values.put(Events.CALENDAR_ID, calId); //set calendar
			
			/* Set the mandatory values now */
			
			values.put(Events.DTSTART, start); 
			values.put(Events.DTEND, start);
			
			if (title != null)			values.put(Events.TITLE, title);
			else						values.put(Events.TITLE, "Default Event"); //I assume no one will be dumb enough to send title as null? 

			/* Now let us set the optional values */
			if (location != null)		values.put(Events.EVENT_LOCATION, location);
			if (desc != null)			values.put(Events.DESCRIPTION, desc);
			
			
			/* Some common sense values, which do not require being parameterized */
			values.put(Events.SELF_ATTENDEE_STATUS, Events.STATUS_CONFIRMED);
			values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
			values.put(Events.GUESTS_CAN_INVITE_OTHERS, 1);
			values.put(Events.GUESTS_CAN_MODIFY, 1);
			values.put(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
						
			/* Now add to calendar */
			Uri uri = 
			     con.getContentResolver().
			            insert(Events.CONTENT_URI, values);
			long eventId =  Long.valueOf(uri.getLastPathSegment());
			
Log.d("Appointext Calendar", "Event added successfully");

			/* Now set a reminder */

			values.clear();
			values.put(Reminders.EVENT_ID, eventId);
			values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
			values.put(Reminders.MINUTES, min_before_event);
			con.getContentResolver().insert(Reminders.CONTENT_URI, values);
			
			/* Now add attendees for the reminder. I assume we have nothing to do with anything other than name */
			if (attendees != null) {

				values.clear();
				for (String name : attendees.split(","))
					values.put(Attendees.ATTENDEE_NAME, name);
				con.getContentResolver().insert(Attendees.CONTENT_URI, values);
				
			}
			
Log.d("Appointext Calendar", "Reminder Added Successfully");

			return eventId;			
			
		}
		catch(Exception e) {
			Log.e("Appointext Calendar", "Error in Reminder Setting : " + e.getMessage() + "\nCause may be : " + e.getCause());
		}
		
		return -1;
	
   }
	   
}
