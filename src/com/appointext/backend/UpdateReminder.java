package com.appointext.backend;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;

import com.appointext.database.CalendarInsertEvent;
import com.appointext.database.DatabaseManager;
import com.appointext.database.GetCalendarEvents;
import com.appointext.database.HandleConflict;
import com.appointext.regex.RecognizeEvent;
import com.appointext.regex.RecognizePeople;

public class UpdateReminder {
	
	static DatabaseManager db;

	/**
	 * This is for direct cancellation of messages. Where one message comes and says 'Meeting cancelled on my side'
	 * No interaction allowed. All query-response must be taken care of in SetReminder itself.
	 * @param con - The context
	 * @param curText - The message that was classified as a cancellation message
	 * @param sender - The phone number of the sender
	 * @param receiver - The phone number of the receiver
	 */
	
	public static void cancelReminder (Context con, String curText, String sender, String receiver) {
	
		Log.e("Appointext Cancellation", "Cancellation request with the message : " + curText);

		//Extract all details so that you can query the t-r-s table appropriately.

		String people = "";
		ArrayList<ArrayList<Object>> rows = new ArrayList<ArrayList<Object>>(); //This is being used multiple times 
		ArrayList<ArrayList<Object>> tempRows = new ArrayList<ArrayList<Object>>();
		
		db = new DatabaseManager(con);
		db.open();
		boolean userCancelled = false; //says whether the person who runs appoinText was the sender of the cancellation message. If he is, then there is no question of updating attendees. The reminder must go.

		people = RecognizePeople.findPeopleNegative(curText); //Okay, I have attendees mentioned here. Special function because I need those people who are NOT coming. Not the guys who are!!
		//Find out if the message is either a sent message or a received one. And add the person who sent the message or is receiving the message also as an attendee. The host himself/ herself is not added yet.
		if(sender.equalsIgnoreCase("123")){
			userCancelled = true; //The owner of the phone sent this message, rather than received it.
			people += HandleConflict.convertNumberToName(con, receiver);
		}		
		else{	
			people += HandleConflict.convertNumberToName(con, sender);
		}
	
		Log.d("AppoinText Cancellation", "The numbers determined are SENDER:: " + sender + " RECEIVER:: " + receiver);

		String event = RecognizeEvent.getEvent(curText); //I know which even we are talking about.
		if (event.length() == 0) //no event was found
			event = "%"; //Any event. Using this for the benefit of the TRS Query. Hoping this works!
		
		Log.d("AppoinText Cancellation", "The event was determined to be " + event);
		
		/*Let's ignore Mr. Date and Time for now!!
		 * String timeExtracted = RecognizeTime.findTime(con, curText); //Probably none of these will be there
		String dateExtracted = RecognizeDate.findDates(curText); //However if they are, it will be an extra head-ache figuring out how to use them *sigh*
		String time = "";
		for ()*/
		
		
		String trs = event + "-" + sender + "-" + receiver; //This takes care that if there is exactly one TRS we shall have only one eventID. If there are more, we shall cross the bridge when we come there			
		rows = db.getMultipleSetReminders("SELECT * FROM setReminders WHERE trs LIKE "+ "'" + trs + "'");
		trs = event + "-" + receiver + "-" + sender;
		tempRows = db.getMultipleSetReminders("SELECT * FROM setReminders WHERE trs LIKE "+ "'" + trs + "'");
		rows.addAll(tempRows);
		
		long toDeleteID = -1;			
		long curTime, nearestTime = Long.MAX_VALUE; //curTime is the time of the current event under consideration, nearestTime is the dtstart time nearest to system date 
		
		for (int i = 0; i < rows.size(); i++) {
			
			long eventId = Integer.parseInt(rows.get(i).get(0).toString()); //get the event ID which matches the TRS
			
			String[] fields = {Events.DTSTART};	 //Get the start times, since they are not stored in the set reminder table
			String result = GetCalendarEvents.getEventByID(con, eventId+"", fields).split("#")[0].split(",")[0]; //Keep only the time 			
			Log.d("AppoinText Cancellation", "The rows fetched form the setReminders db are: " + rows.toString() + 
						"\nThe value of TRS is : " + trs);
			Log.d("AppoinText Cancellation", "The time fetched was " + result);
			//TODO: Use extracted date time! Break if date time match :D
			
			/* Convert the dtstart into long and find the smallest one. That is the nearest to the system date, so unless a special date time has been mentioned, we must use that! */
			try {
				curTime = SetReminder.inMiliseconds(result, null, null);
			}
			catch(Exception e) {
				curTime = Long.MAX_VALUE;
			}
			
			if (curTime < nearestTime) { //This ensures that in absence of other events the nearest event is deleted.
				toDeleteID = eventId;
				nearestTime = curTime;
			}
			
			Log.d("AppoinText Cancellation", "The converted time was " + curTime); //DONE: Use this data to ensure if no event is specified we choose the closest reminder
			
		}
		
		if (toDeleteID == -1) //there is nothing to delete!! Get the hell out of here!
			return;
		
		if (userCancelled) //no need to check attendee information. Just delete
			CalendarInsertEvent.deleteCalendarEntry(con, toDeleteID);
		
		//Now get the attendee information and see if everyone disappeared.
		ContentResolver cr = con.getContentResolver();
		Cursor cursor = CalendarContract.Attendees.query(cr, toDeleteID, new String[] {CalendarContract.Attendees.ATTENDEE_NAME} );
		Log.d("AppoinText Cancellation", "Queried for attendees");
		String aOld = "";
		if (cursor.moveToFirst()) {
			   do {
				  String at = cursor.getString(0);
				  if (at != null && !at.equals("") && !aOld.contains(at))
					  aOld += at;
			   } while (cursor.moveToNext());
			}	
		Log.d("AppoinText Cancellation", "Got attendees as " + aOld);
		
		Log.d("AppoinText Cancellation", "Got current people as " + people);

		String[] arr = people.split(",");
		people = "";
		for (String c : arr)
			if (c != null & c.length() > 0)
				people += c + ","; //to prevent issues with empty names that may creep up
		
		if (people.split(",").length == aOld.split(",").length || curText.toLowerCase().contains("none") || curText.toLowerCase().contains("we")) { //all attendees left. DONE: Take care of empty attendees caused by multiple ,, consequitively - Taken care that null attendees can not be inserted.
			Log.d("AppoinText Cancellation", "Calling delete event");
			CalendarInsertEvent.deleteCalendarEntry(con, toDeleteID); //Delete the calendar entry. TODO: Delete from setReminder as well
			db.deleteRow("setReminders", (int)toDeleteID);
		}
		else {
			String[] oArr = aOld.split(",");
			aOld = "";
			ArrayList<String> peopleWhoLeft = new ArrayList<String>(Arrays.asList(people.split(",")));
			for (String name : oArr) {
				if (name != null && name.length() > 0 && !peopleWhoLeft.contains(name))
					aOld += name + ",";
			}
			Log.d("AppoinText Cancellation", "Calling update of attendees to " + aOld);
			CalendarInsertEvent.updateAttendees(con, toDeleteID, aOld); //DONE: Remove the names we found here.
			
			db.close();
		}
		
	}
	
}
