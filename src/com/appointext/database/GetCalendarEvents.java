package com.appointext.database;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

public class GetCalendarEvents {
	
	/**
	 * Returns a Comma Seperated String of titles of all reminders within a particular period
	 * @param con - The Context. If you are calling it from  an Activity or Service, pass 'this'
	 * @param sTime - The Start date passed as "dd/MM/yyyy HH:mm"
	 * @param etime - End time, passed in same format
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
	
}
