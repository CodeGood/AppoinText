package com.appointext.database;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract.Events;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class GetCalendarEvents {
	
	/**
	 * Returns a Comma Seperated String of titles of all reminders within a particular period
	 * @param con - The Context. If you are calling it from  an Activity or Service, pass 'this'
	 * @param sTime - The Start date passed as "dd/MM/yyyy HH:mm"
	 * @param etime - End time, passed in same format
	 * @param fields - A String array mentioning which fields are required. Fields returned will be in order specified here.
	 * 					You can use Events._ID to get the event id, which you require to set reminder, modify event or anything else
	 * 					Events.TITLE or "title" will return the title
	 * 					Similarly other column names can be found at http://developer.android.com/reference/android/provider/CalendarContract.EventsColumns.html
	 * 
	 * COMMON  USAGE - GetCalendarEvents.getEvent(this, start, end, new String[] {Events._ID, Events.TITLE});
	 * 					For Events.something to work, you must import android.provider.CalendarContract.Events;
	 * 					Or you can directly use their values - "_id" and "title"
	 *  
	 * @return - A String where fields are separated by comma and rows are separated by hashes
	 */
	
	public static String getEvent (Context con, String sTime, String eTime, String[] fields) {
		
		String result = "";
		
		if (fields == null) {
			fields = new String[1];
			fields[0] = "title";
		}
		
		try {
			
	        Uri l_eventUri; 
			Calendar calendar = Calendar.getInstance();
			
			if (Build.VERSION.SDK_INT >= 8) {
				l_eventUri = Uri.parse("content://com.android.calendar/events");
			} else {
				l_eventUri = Uri.parse("content://calendar/events");
			}
			ContentResolver contentResolver = con.getContentResolver();

			String dtstart = "dtstart";
			String dtend = "dtend";

			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			Date dateCC = formatter.parse(sTime);
			calendar.setTime(dateCC);
			long after = calendar.getTimeInMillis();

			Calendar endOfDay = Calendar.getInstance();
			Date dateCCC = formatter.parse(eTime);
			endOfDay.setTime(dateCCC);

			Cursor cursor= contentResolver.query(l_eventUri, fields,
					"(" + dtstart + ">" + after + " and "
	            + dtend + "<" + endOfDay.getTimeInMillis() + ")", null,
	            "dtstart ASC");
				
			if (cursor.moveToFirst()) {
			   do {
				  for (int i = 0; i < fields.length; i++)
					  if (fields[i].equals(Events.DTSTART) || fields[i].equals(Events.DTEND)) {
	   					     // Create a calendar object that will convert the date and time value in milliseconds to date. 
							 Calendar calendar1 = Calendar.getInstance();
							 calendar1.setTimeInMillis(cursor.getLong(i));
	Log.d("AppoinTextReminder", "Got date as " + formatter.format(calendar1.getTime()));						 
							 result += formatter.format(calendar1.getTime()) + ",";
						  }
						  else
							  result += cursor.getString(i) + ",";
				  result += "#";
			   } while (cursor.moveToNext());
			}	
			
	    } catch (AssertionError ex) {
	            Log.e("AppoinText", "Assertion Error " + ex);
	    } catch (Exception e) {
	            Log.e("AppoinText", "Non assertion error " + e);
	    }
		
		return result;
	}
	
	/**
	 * 
	 * @param con - The context, from which this method is being called 
	 * @param eventID - The eventID for which you want the details
	 * @param fields - The specific details that you want. Please do not ask for what time the reminder is set. You don't need to know.
	 * 					Reminder time is as +- event time, so it is automatically updated. Or you can manually update if the event type changes. Just don't ask me.
	 * @return - A Comma separated values, in the same order that the fields were passed.
	 * 			Returns the title followed by a comma if no field were passed.
	 */
	
	public static String getEventByID (Context con, String eventID, String[] fields) {
		
		String result = "";
Log.i("AppoinTextReminder", "Got eventID as " + eventID);		
		if (fields == null) {
			fields = new String[1];
			fields[0] = "title";
		}
		
		try {
			
	        Uri l_eventUri; 
			
			if (Build.VERSION.SDK_INT >= 8) {
				l_eventUri = Uri.parse("content://com.android.calendar/events");
			} else {
				l_eventUri = Uri.parse("content://calendar/events");
			}
			ContentResolver contentResolver = con.getContentResolver();

			Cursor cursor= contentResolver.query(l_eventUri, fields,
				"(" + Events._ID + "=" + eventID + ")", null,
	            "dtstart ASC");
Log.i("AppoinTextReminder", "Got cursor as null? " + (cursor == null));				
			if (cursor.moveToFirst()) {
			   do {
				  for (int i = 0; i < fields.length; i++) {
					  if (fields[i].equals(Events.DTSTART) || fields[i].equals(Events.DTEND)) {
						 DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
   					     // Create a calendar object that will convert the date and time value in milliseconds to date. 
						 Calendar calendar = Calendar.getInstance();
						 calendar.setTimeInMillis(cursor.getLong(i));
Log.d("AppoinTextReminder", "Got date as " + formatter.format(calendar.getTime()));						 
						 result += formatter.format(calendar.getTime()) + ",";
					  }
					  else
						  result += cursor.getString(i) + ",";
				  }
			   } while (cursor.moveToNext());
			}	
			
	    } catch (AssertionError ex) {
	            Log.e("AppoinText", "Assertion Error " + ex);
	    } catch (Exception e) {
	            Log.e("AppoinText", "Non assertion error " + e);
	    }
Log.d("AppoinTextReminder", "Got result as " + result);		
		return result;
	}
	
}