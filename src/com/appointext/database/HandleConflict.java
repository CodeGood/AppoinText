package com.appointext.database;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.ContactsContract;
import android.text.format.DateUtils;
import android.util.Log;

import com.bmsce.appointext.R;

public class HandleConflict {

	public static String convertNumberToName(Context context, String number) {
		
		Log.v("AppoinTextConflict", "Started retrieving");

		if (number.replaceAll("[^0-9]", "").equals("")) //In case it's not a number at all :P
			return number;
		
		String name = null;

		// define the columns I want the query to return
		String[] projection = new String[] {
		        ContactsContract.PhoneLookup.DISPLAY_NAME};

		// encode the phone number and build the filter URI
		Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number)); 

		// query time
		Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);
		if (cursor.moveToFirst()) {
		    // Get values from contacts database:
		    name =      cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
		    Log.v("AppoinTextConflict", "Started uploadcontactphoto: Contact Found @ " + number);            
		    Log.v("AppoinTextConflict", "Started uploadcontactphoto: Contact name  = " + name);
		    return name;
		    
		} 
		else { //Did not find with the 91, try +91

			contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode("+" + number)); 
			cursor = context.getContentResolver().query(contactUri, projection, null, null, null);
			if (cursor.moveToFirst()) {
				Log.v("AppoinTextConflict", "Started uploadcontactphoto: Contact Not Found @ " + number);
				Log.v("AppoinTextConflict", "Started uploadcontactphoto: Contact name  = " + name);
				return name; // contact not found
			}
			
			else { //Neither 91, nor +91 worked. Let's try without 91
				
				contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number.substring(2, number.length()))); 
				cursor = context.getContentResolver().query(contactUri, projection, null, null, null);
				if (cursor.moveToFirst()) {
					Log.v("AppoinTextConflict", "Started uploadcontactphoto: Contact Not Found @ " + number);
					Log.v("AppoinTextConflict", "Started uploadcontactphoto: Contact name  = " + name);
					return name; // contact not found
				}
				else
					return number;
				
			}

		}
		
	}
	
	public static boolean findConflicts (Context con, long startTime, String title, String attendees) { //find how many reminders exist at this current time
		
		/* An hour before or after the event time is potential conflict zone. If it's a movie, then two hours before and after is a potential conflict zone. */
		/* So get all events in -3 to (+1.5 or +3 based on the title) */
		//Resonable expectation is there won't be many events to iterate over.
		
		long dtstart, dtend;
		
		/**
		 * JUSTIFICATION
		 * 		There are probably many event classes which deserve such special approach. But the special focus on movie is
		 * to create a use case that we can demonstrate. Saying, AppoinText is SO intelligent that it knows movies take around 3
		 * hours to get away from :P
		 */
		Log.d("AppoinTextDuplicate", "Came to find conflicts ");
		if (title.equalsIgnoreCase("movie")) //If it's a movie, it is a reasonable assumption that you can't go anywhere for the next 3 hours
			dtend = startTime + 3*DateUtils.HOUR_IN_MILLIS;
		else
			dtend = startTime + 1*DateUtils.HOUR_IN_MILLIS + 30*DateUtils.MINUTE_IN_MILLIS;
		
		dtstart = startTime - 3*DateUtils.HOUR_IN_MILLIS;
		Log.d("AppoinTextDuplicate", "Querying get Evetns");
		String[] result = GetCalendarEvents.getEvent(con, dtstart, dtend, new String[] {Events.TITLE, Events.DTSTART, Events._ID}).split("#");
		String conflict = null; 
		long conTime = 0;
		String conTitle = null;
		Log.d("AppoinTextDuplicate", "Obtained results");
		
		for (String event : result) {
			
			if (event.equals("")) //Guard against empty events
				continue;
			
			String[] titleDate = event.split(",");
			long eventStart;
			try {
				eventStart = Long.parseLong(titleDate[1]);
			}
			catch (NumberFormatException e) {
				eventStart = 0;
			}
						
			if (eventStart < startTime) { //For movies 3 hours, for others 1.5 hour limit has to be implemented here.
				if (titleDate[0].equalsIgnoreCase("movie")) {
					conflict = titleDate[2];
					conTime = eventStart;
					conTitle = titleDate[0];
				}
				else if ((startTime - eventStart) < DateUtils.HOUR_IN_MILLIS + 30*DateUtils.MINUTE_IN_MILLIS) {  
					conflict = titleDate[2];
					conTime = eventStart;
					conTitle = titleDate[0];
				}
			}
			else  { //Otherwise (the conflicting event is after the one we want to schedule a reminder for), we just need the fact that there is an event. Because we have already filtered accordingly
				conflict = titleDate[2];
				conTime = eventStart;
				conTitle = titleDate[0];
			}
			
			Log.d("AppoinTextDuplicate", "Detecting conflicts");
		}
		
		Log.d("AppoinTextDuplicate", "Going on my own sweet way!!");
		
		if (conflict == null)
			return false;
		else {
			raiseNotification(con, conflict, conTitle, conTime, title, attendees, startTime); //inform the user of a conflict
			return true; //and inform the calendar entry service that a reminder should not be set.
		}
		
	}
	
	public static void raiseNotification(Context context, String eventIDOld, String titleOld, long timeOld, String titleNew, String attendeesNew, long timeNew) {
		
		Log.d("AppoinTextConflict", "Notification Raised");
		
		NotificationManager notificationManager 
			  = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification.Builder builder = new Notification.Builder(context);
			  
		Intent intent = new Intent(context, com.appointext.frontend.ConflictDetected.class);
		intent.putExtra("titleNew", titleNew);
		intent.putExtra("timeNew", timeNew);
		intent.putExtra("attendeesNew", convertNumberToName(context, attendeesNew));
		
		intent.putExtra("eventIDOld", eventIDOld);
		
		//START
		ContentResolver cr = context.getContentResolver();
		long id;
		try {
			id = Long.parseLong(eventIDOld);
		}
		catch(Exception e){
			id = 0;
		}
		Log.d("AppoinTextConflict", "Got ID as " + id);
		Cursor cursor = CalendarContract.Attendees.query(cr, id, new String[] {CalendarContract.Attendees.ATTENDEE_NAME} );
		Log.d("AppoinTextConflict", "Queried");
		String aOld = "";
		if (cursor.moveToFirst()) {
			   do {
				  String at = cursor.getString(0);
				  if (at != null && !at.equals(""))
					  aOld += at;
			   } while (cursor.moveToNext());
			}	
		Log.d("AppoinTextConflict", "Got attendees as " + aOld);
		//END
		
		intent.putExtra("titleOld", titleOld);
		intent.putExtra("timeOld", timeOld);
		intent.putExtra("attendeesOld", aOld);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		PendingIntent pendingIntent 
			  = PendingIntent.getActivity(context, 1004, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			  
		builder
			  .setSmallIcon(R.drawable.reminder_hand)
			  .setContentTitle("Conflict Detected via AppoinText")
			  .setContentText("You seem to have scheduled two meetings at the same time")
			  .setTicker("AppoinText: Conflict Detected.")
			  .setLights(0xFFFF0000, 500, 500) //setLights (int argb, int onMs, int offMs)
			  .setContentIntent(pendingIntent)
			  .setAutoCancel(true);
			  
		@SuppressWarnings("deprecation")
		Notification notification = builder.getNotification(); //Yes, am NOT updating to API 14. We promised LOW END ANDROID!
			  
		notificationManager.notify(R.drawable.reminder_hand, notification);
		
	}
	
	public static boolean duplicateExists(Context con, String title, long dtstart, long dtend, String desc, String location) {
		//This will ensure no duplicate reminders are added.
		Log.d("AppoinTextDuplicate", "Came to duplicate");
		String[] result = GetCalendarEvents.getEvent(con, dtstart, dtend, new String[] {Events.TITLE, Events.DESCRIPTION, Events.EVENT_LOCATION}).split("#");
		Log.d("AppoinTextDuplicate", "Got the event list and started parsing");
		for (String event : result) {
			if (event.equals("")) //For null strings, don't bother processing.
				continue;
			String[] fields = event.split(",");
			if (fields[0].equalsIgnoreCase(title) && fields[1].equalsIgnoreCase(desc) && fields[2].equalsIgnoreCase(location))
				return true;
		}
		Log.d("AppoinTextDuplicate", "Finished processing");
		return false;
	}
	
}
