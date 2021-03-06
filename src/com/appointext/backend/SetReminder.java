package com.appointext.backend;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.provider.CalendarContract.Events;
import android.util.Log;

import com.appointext.database.CalendarInsertEvent;
import com.appointext.database.DatabaseManager;
import com.appointext.database.GetCalendarEvents;
import com.appointext.regex.RecognizeDate;
import com.appointext.regex.RecognizeEvent;
import com.appointext.regex.RecognizePeople;
import com.appointext.regex.RecognizeTime;

public class SetReminder {

	static DatabaseManager db;
	public static int addToPendingTable(Context con, String curText, String senderNumber, String recieverNumber){

		Log.e("Appointext", "I am a query and the message is : " + curText);

		//since this is a query, extract all the details and then store it in the pending table.

		String people = "";
		String location = "";
		ArrayList<ArrayList<Object>> rows = new ArrayList<ArrayList<Object>>(); //This is being used multiple times 
		ArrayList<ArrayList<Object>> tempRows = new ArrayList<ArrayList<Object>>();

		db = new DatabaseManager(con);

		db.open();

		//From the tagged text find out the list of persons and the place if any mentioned. Organizations are also classified as location here

		people = RecognizePeople.findPeople(curText);

		//Find out if the message is either a sent message or a received one. And add the person who sent the message or is receiving the message also as an attendee. The host himself/ herself is not added yet.

		if(senderNumber.equalsIgnoreCase("123")){

			people += recieverNumber;
		}

		else{

			people += senderNumber;
		}

		Log.d("appointext", "the numbers determined are" + senderNumber + " " + recieverNumber);

		String event = RecognizeEvent.getEvent(curText);
		String when = "";
		String timeExtracted = "", dateExtracted = "";

		timeExtracted = RecognizeTime.findTime(con, curText);
		dateExtracted = RecognizeDate.findDates(curText);

		Log.d("appointext", "the time :" + timeExtracted + " date : " + dateExtracted );	

		boolean val = FindPostponement.findPostponement(curText);

		String returnVal = FindSentiment.findSentiment(curText);

		if(returnVal.equalsIgnoreCase("no")){

			UpdateReminder.cancelReminder(con, curText, senderNumber, recieverNumber);
		}

		if(val){

			int index = 0;
			long latestTime = Long.MAX_VALUE;

			Log.i("Appointext:Postpone", "Entered postpone, event is : " + event);

			if(event.equalsIgnoreCase("")){

				Log.i("Appointext:PostponeWithoutEvent", "Entered the postpone case.No event specified");

				event = "%";

				String trs = event + "-" + senderNumber + "-" + recieverNumber;	

				rows = db.getMultipleSetReminders("SELECT * FROM setReminders WHERE trs LIKE "+ "'" + trs + "'");

				trs = event + "-" + recieverNumber + "-" + senderNumber;	

				tempRows = db.getMultipleSetReminders("SELECT * FROM setReminders WHERE trs LIKE "+ "'" + trs + "'");

				rows.addAll(tempRows);

				Log.i("Appointext:PostponeWithoutEvent", "The rows I fetched are : " + rows.toString() );

				if(rows.isEmpty()){

					Log.i("Appointext:PostponeWithoutEvent", "Entered the postpone case. but the rows are empty, no reminders for this sender reciever numbers");
					return -1;
				}

				index = 0;
			}

			else{

				Log.i("Appointext:Postpone", "Event is specified" + event);

				String trs = event + "-" + senderNumber + "-" + recieverNumber;			

				rows = db.getMultipleSetReminders("SELECT * FROM setReminders WHERE trs="+ "'" + trs + "'");


				trs = event + "-" + recieverNumber + "-" + senderNumber;
				tempRows = db.getMultipleSetReminders("SELECT * FROM setReminders WHERE trs="+ "'" + trs + "'"); 

				rows.addAll(tempRows);

				Log.i("Appointext:Postpone", "Entered the postpone case : " + rows.toString());

				if(rows.isEmpty()){

					return -1;
				}

				if(!rows.isEmpty()){
					try{

						for(int count = 0; count < rows.size(); count++){

							int eventId = Integer.parseInt(rows.get(count).get(0).toString());
							String[] fields = {Events.DTSTART};

							String result = GetCalendarEvents.getEventByID(con, eventId+"", fields);

							long tim = inMiliseconds(result.split(",")[0], "", "");

							if(tim < latestTime){
								latestTime = tim;
								index = count;
							}

						}
					}

					catch(Exception e){

						Log.i("Appointext:Postpone", "Gave error when I tried to get time in milli seconds :P");
					}
				}
			}

			Log.i("Appointext:Postpone", "The index is : " + index);

			int eventId = Integer.parseInt(rows.get(index).get(0).toString());
			String[] fields = {Events.DTSTART};

			String result = GetCalendarEvents.getEventByID(con, eventId+"", fields);

			//Log.d("Postpone: Appointext", "the rows fetched form the setReminders db are: " + rows.toString() + " The value of trs is : " + trs);


			String[] dateExtract = {},timeExtract={};
			int hour=0 , minute=0, dd=0, mm=0, yy=0;
			String finalTime=null, finalDate=null;

			if(!timeExtracted.equalsIgnoreCase("")){

				timeExtract = timeExtracted.split(",")[0].split(":");

				hour = Integer.parseInt(timeExtract[0]);
				minute = Integer.parseInt(timeExtract[1].split("/")[0]);

				finalTime = hour + ":" + minute;
			}

			if(!dateExtracted.equalsIgnoreCase("")){

				dateExtract = dateExtracted.split(",")[0].split("/");

				dd = Integer.parseInt(dateExtract[0]);
				mm = Integer.parseInt(dateExtract[1]);
				yy = Integer.parseInt(dateExtract[2]);

				finalDate = dd +"/" + mm + "/" + yy;				
			}

			try{
				long tim = inMiliseconds(result, finalDate, finalTime);

				Log.i("Postpone: Appointext", "No exception" + tim);
				CalendarInsertEvent.updateCalendarEntry(con, eventId, new String[] {Events.DTSTART + "," + tim,  Events.DTEND + "," + tim}, -1, "");
				return 0;

			}
			catch(Exception e){
				Log.i("Postpone: Appointext", "I got an exception" + e);
			}
			//Log.d("Postpone: Appointext", "the values extracted : hour: " + hour + "minute: " + minute + "dd: " + dd + "mm: " + mm + "yy: " + yy );

			return -1;
		}

		//After extracting date and time, get it in the format HH:MM,dd/mm/yyyy and store it in the pending reminders list

		if(!timeExtracted.equalsIgnoreCase("") && !dateExtracted.equalsIgnoreCase("")){

			when = timeExtracted.split("[/,]")[0] + "," + dateExtracted.split("[/,]")[0]+"/"+dateExtracted.split("[/,]")[1]+"/"+dateExtracted.split("[/,]")[2];
		}

		else if(!timeExtracted.equalsIgnoreCase("")){

			when = timeExtracted.split("[/,]")[0] + ",";

			rows = db.getMultiplePendingReminders("SELECT * FROM pendingReminders WHERE senderNumber=" + "'" + senderNumber + "'" + " and receiverNumber=" + "'" + recieverNumber+"'");

			Log.d("AppoinText", "the value of rows is : " + rows.toString());

			tempRows = db.getMultiplePendingReminders("SELECT * FROM pendingReminders WHERE senderNumber=" + "'" + recieverNumber + "'" + " and receiverNumber=" + "'" + senderNumber +"'");

			Log.d("AppoinText", "the value of tempRows : " + tempRows.toString());

			rows.addAll(tempRows);

			if(!rows.isEmpty()){

				int index = 0;
				long latestTime = 0;

				for(int count = 0; count < rows.size(); count++){
					if(rows.get(count).get(7).toString() == null || rows.get(count).get(7).toString().length() == 0 ||rows.get(count).get(7).toString().equals(""))
						continue;
					if(Long.parseLong(rows.get(count).get(8).toString()) > latestTime){
						latestTime = Long.parseLong(rows.get(count).get(8).toString());
						index = count;
					}
				}

				Log.i("Appointext : in complete time ", "The rows fetched are : " + rows.toString());

				String whenStamp = rows.get(index).get(6).toString();
				String finalTime = "";

				if(whenStamp.startsWith(",")){

					Log.i("Appointext : in complete time ", "In the right clause");

					finalTime = timeExtracted.split("[/,]")[0] + whenStamp;

					if(rows.get(index).get(3).toString().equals("1")){

						Log.i("appointext : in complete time ", "In the confirmed case");

						String[] extractedData = finalTime.split(",");	         	    		
						int date=0, month=0, year=0, hour=0, minute=0;
						String[] dateExtract, timeExtract;

						timeExtract = extractedData[0].split(":");
						dateExtract = extractedData[1].split("/");

						if(timeExtract.length >= 2){
							hour = Integer.parseInt(timeExtract[0]);
							minute = Integer.parseInt(timeExtract[1]);
						}

						if(dateExtract.length >= 3){			
							date = Integer.parseInt(dateExtract[0]);
							month = Integer.parseInt(dateExtract[1]);
							year = Integer.parseInt(dateExtract[2]);
						}

						//	   public static long addReminder(              Context, int date, int month, int year, int hour, int minute, int min_before_event, String title,                  String location,               String desc,  String attendees) 
						int eventId = (int) CalendarInsertEvent.addReminder(con,      date,      month,     year,     hour,     minute,        30,             rows.get(index).get(5).toString(), "",    curText,       rows.get(index).get(4).toString());

						Log.d("AppoinText", "the database insert statement :" + date + "'" + month +"'"+year+"'" + "'" + hour + "'" + minute + "'" );
						// after the reminder set, then put the entry to the set reminders table and add all the details to extractedData field in the form of Location:xxxx-Attendees:xxxx-Event:xxxx- all of them being a CSV 

						int isComplete = 1, isGroup = 0;

						String people1;
						String extractedInfo = "";

						people1 = rows.get(index).get(4).toString();

						int number = people1.split(",").length;

						if(number > 1){
							isGroup = 1;
						}

						if(!people1.equalsIgnoreCase("")){

							Log.i("blah", people1);
							extractedInfo += ("Attendees:" + people1+"-");
						}

						else{	
							isComplete = 0;
						}

						if(!rows.get(index).get(7).toString().equalsIgnoreCase("")){
							Log.i("blah",  rows.get(index).get(7).toString());
							extractedInfo += ("Location:" + rows.get(index).get(7).toString() + "-");
						}

						else{
							isComplete = 0;
						}

						if(!rows.get(index).get(5).toString().equalsIgnoreCase("")){	
							Log.i("blah",  rows.get(index).get(5).toString());	
							extractedInfo += ("Occasion:" + rows.get(index).get(5).toString() + "-");
						}

						else{	
							isComplete = 0;
						}

						// the event-sender-receiver string to retrieve the data from the set reminders table later 
						// create trs and store the entry in the set reminder table

						String trs = rows.get(index).get(5).toString() + "-" + senderNumber + "-" + recieverNumber;

						db.deleteRow("pendingReminders", (Integer)rows.get(index).get(0));

						db.addRow("setReminders", eventId, isComplete, isGroup, trs, extractedInfo);

						rows = db.getMultipleSetReminders("SELECT * FROM setReminders");

						Log.d("Appointext", " The set reminder database is like this : " + rows.toString());

						rows = db.getMultiplePendingReminders("SELECT * FROM pendingReminders");

						Log.d("Appointext", " The pending reminder database is like this : " + rows.toString());

						return 1;
					}

					else{

						Log.i("appointext : in complete time", "In the update case");

						db.updateRow("pendingReminders", (Integer)rows.get(index).get(0), rows.get(index).get(1).toString(), rows.get(index).get(2).toString(), (Integer)rows.get(index).get(3), rows.get(index).get(4).toString(), rows.get(index).get(5).toString(), finalTime, rows.get(index).get(7).toString());	
						return 2;
					}		
				}	
			}//
		}

		else if(!dateExtracted.equalsIgnoreCase("")){

			when =  "," + dateExtracted.split("[/,]")[0]+"/"+dateExtracted.split("[/,]")[1]+"/"+dateExtracted.split("[/,]")[2];

			//Log.i("Appointext : update date", "Im trying to just update date " + when);

			rows = db.getMultiplePendingReminders("SELECT * FROM pendingReminders WHERE senderNumber=" + "'" + senderNumber + "'" + " and receiverNumber=" + "'" + recieverNumber+"'");

			Log.d("AppoinText", "the value of rows is : " + rows.toString());

			tempRows = db.getMultiplePendingReminders("SELECT * FROM pendingReminders WHERE senderNumber=" + "'" + recieverNumber + "'" + " and receiverNumber=" + "'" + senderNumber +"'");

			Log.d("AppoinText", "the value of tempRows : " + tempRows.toString());

			rows.addAll(tempRows);

			if(!rows.isEmpty()){

				int index = 0;
				long latestTime = 0;

				for(int count = 0; count < rows.size(); count++){
					if(rows.get(count).get(7).toString() == null || rows.get(count).get(7).toString().length() == 0 ||rows.get(count).get(7).toString().equals(""))
						continue;
					if(Long.parseLong(rows.get(count).get(8).toString()) > latestTime){
						latestTime = Long.parseLong(rows.get(count).get(8).toString());
						index = count;
					}
				}

				Log.i("Appointext : in complete date", "The rows are not empty in date");

				String whenStamp = rows.get(index).get(6).toString();
				String finalTime = "";

				if(whenStamp.endsWith(",")){

					Log.i("Appointext : in complete date", "In the right path");

					finalTime = whenStamp + dateExtracted.split("[/,]")[0]+"/"+dateExtracted.split("[/,]")[1]+"/"+dateExtracted.split("[/,]")[2];

					if(rows.get(index).get(3).toString().equals("1")){

						Log.i("Appointext : in complete date", "Updating and setting reminder + final time is : " + finalTime);

						String[] extractedData = finalTime.split(",");	         	    		
						int date=0, month=0, year=0, hour=0, minute=0;
						String[] dateExtract, timeExtract;

						timeExtract = extractedData[0].split(":");
						dateExtract = extractedData[1].split("/");

						if(timeExtract.length >= 2){
							hour = Integer.parseInt(timeExtract[0]);
							minute = Integer.parseInt(timeExtract[1]);
						}

						if(dateExtract.length >= 3){			
							date = Integer.parseInt(dateExtract[0]);
							month = Integer.parseInt(dateExtract[1]);
							year = Integer.parseInt(dateExtract[2]);
						}

						//	   public static long addReminder(              Context, int date, int month, int year, int hour, int minute, int min_before_event, String title,                  String location,               String desc,  String attendees) 
						int eventId = (int) CalendarInsertEvent.addReminder(con,      date,      month,     year,     hour,     minute,        30,             rows.get(index).get(5).toString(), "",    curText,       rows.get(index).get(4).toString());

						Log.d("AppoinText", "the database insert statement :" + date + "'" + month +"'"+year+"'" + "'" + hour + "'" + minute + "'" );
						// after the reminder set, then put the entry to the set reminders table and add all the details to extractedData field in the form of Location:xxxx-Attendees:xxxx-Event:xxxx- all of them being a CSV 

						int isComplete = 1, isGroup = 0;

						String people1;
						String extractedInfo = "";

						people1 = rows.get(index).get(4).toString();

						int number = people1.split(",").length;

						if(number > 1){
							isGroup = 1;
						}

						if(!people1.equalsIgnoreCase("")){

							Log.i("blah", people1);
							extractedInfo += ("Attendees:" + people1+"-");
						}

						else{	
							isComplete = 0;
						}

						if(!rows.get(index).get(7).toString().equalsIgnoreCase("")){
							Log.i("blah",  rows.get(index).get(7).toString());
							extractedInfo += ("Location:" + rows.get(index).get(7).toString() + "-");
						}

						else{
							isComplete = 0;
						}

						if(!rows.get(index).get(5).toString().equalsIgnoreCase("")){	
							Log.i("blah",  rows.get(index).get(5).toString());	
							extractedInfo += ("Occasion:" + rows.get(index).get(5).toString() + "-");
						}

						else{	
							isComplete = 0;
						}

						// the event-sender-receiver string to retrieve the data from the set reminders table later 
						// create trs and store the entry in the set reminder table

						String trs = rows.get(index).get(5).toString() + "-" + senderNumber + "-" + recieverNumber;

						db.deleteRow("pendingReminders", (Integer)rows.get(index).get(0));

						db.addRow("setReminders", eventId, isComplete, isGroup, trs, extractedInfo);

						rows = db.getMultipleSetReminders("SELECT * FROM setReminders");

						Log.d("Appointext", " The set reminder database is like this : " + rows.toString());

						rows = db.getMultiplePendingReminders("SELECT * FROM pendingReminders");

						Log.d("Appointext", " The pending reminder database is like this : " + rows.toString());

						return 1;
					}

					else{

						Log.i("Appointext : in complete date", "Updating reminder");

						db.updateRow("pendingReminders", (Integer)rows.get(index).get(0), rows.get(index).get(1).toString(), rows.get(index).get(2).toString(), (Integer)rows.get(index).get(3), rows.get(index).get(4).toString(), rows.get(index).get(5).toString(), finalTime, rows.get(index).get(7).toString());	
						return 2;
					}		
				}	
			}//
		}

		rows = db.getMultiplePendingReminders("SELECT * FROM pendingReminders WHERE senderNumber=" + "'" + senderNumber + "'" + " and receiverNumber=" + "'" + recieverNumber+"'" + " and whenIsIt=" + "'" + when + "'");

		Log.d("Appointext", "I am trying to figure out duplicate rows : " + rows.toString());

		if(rows.isEmpty()){

			if (event.length() != 0 || when.length() != 0) {
				db.addRow("pendingReminders", senderNumber, recieverNumber, 0, people, event, when, curText); 
				Log.e("Appointext", "db.addRow  : " + " " + senderNumber+ " " +recieverNumber+ " " +0+ " " + people+ " " + event+ " " + when+ " " +location+ " "  );
			}

		}
		db.close();

		return 1;

	}



	/* Start of the reply based function :P */

	static public int setReminderBasedOnReply(Context con, String curText, String senderNumber, String recieverNumber){
		Log.i("AppoinText", "Inside SetReminder. I am a reply : "+ curText);
		//for the category reply, we first extract all the pending list messages based on the sender and the receiver number

		ArrayList<ArrayList<Object>> rows = new ArrayList<ArrayList<Object>>();
		ArrayList<ArrayList<Object>> tempRows = new ArrayList<ArrayList<Object>>();

		db = new DatabaseManager(con);
		db.open();

		rows = db.getMultiplePendingReminders("SELECT * FROM pendingReminders");

		Log.d("AppoinText", "The rows stored are : "+ rows.toString());

		Log.d("AppoinText", "Sender number "+ senderNumber + "Reciever Number" + recieverNumber);

		rows = db.getMultiplePendingReminders("SELECT * FROM pendingReminders WHERE senderNumber=" + "'" + senderNumber + "'" + " and receiverNumber=" + "'" + recieverNumber+"'");

		Log.d("AppoinText", "the value of rows is : " + rows.toString());

		tempRows = db.getMultiplePendingReminders("SELECT * FROM pendingReminders WHERE senderNumber=" + "'" + recieverNumber + "'" + " and receiverNumber=" + "'" + senderNumber +"'");

		Log.d("AppoinText", "the value of tempRows : " + tempRows.toString());

		rows.addAll(tempRows);

		String reply = FindSentiment.findSentiment(curText);

		Log.d("AppoinText", "The sentiment is : "+ reply);
		
		if(rows.isEmpty() && reply.equals("yes")){

			return -1;  //-1 value used will indicate that there were no reminders to confirm
		}


		if(rows.isEmpty() && !reply.equals("yes")){

			Log.e("AppoinText", "the rows are empty");
			UpdateReminder.cancelReminder(con, curText, senderNumber, recieverNumber);
			return -1;  //-1 value used will indicate that there were no reminders to confirm
		}

		int index = 0;
		long latestTime = 0;

		for(int count = 0; count < rows.size(); count++){
			if(rows.get(count).get(7).toString() == null || rows.get(count).get(7).toString().length() == 0 ||rows.get(count).get(7).toString().equals(""))
				continue;
			
			if(Long.parseLong(rows.get(count).get(8).toString()) > latestTime){
				latestTime = Long.parseLong(rows.get(count).get(8).toString());
				index = count;
			}
		}

		if(reply.equalsIgnoreCase("yes")){

			Log.i("AppoinText","no the rows are not empty and the 7th element : " + rows.get(index).get(6).toString() + " And the rows are " + rows.toString());

			// if the reply is affirmative, then check if the time was found. If not, then just change the entry in the db to indicate that the meeting is confirmed

			if(rows.get(index).get(6).toString()!= null && (rows.get(index).get(6).toString().equalsIgnoreCase("") || rows.get(index).get(6).toString().startsWith(",") || rows.get(index).get(6).toString().endsWith(","))){         	    		

				Log.d("AppoinText", "Inside the if condition. Not enough information to add pending reminder. :P");
				db.updateRow("pendingReminders", (Integer)rows.get(index).get(0), rows.get(index).get(1).toString(), rows.get(index).get(2).toString(), 1, rows.get(index).get(4).toString(), rows.get(index).get(5).toString(), rows.get(index).get(6).toString(), rows.get(index).get(index).toString());	
				return 2; //return value of 2 means that there was not enough information to set the reminder 
			}

			else{

				//else extract the details required for the add reminder function and add the reminder

				String whenStamp = rows.get(index).get(6).toString();	
				String desc = rows.get(index).get(7).toString();
				String[] extractedData = whenStamp.split(",");	         	    		
				int date=0, month=0, year=0, hour=0, minute=0;
				String[] dateExtract, timeExtract;

				timeExtract = extractedData[0].split(":");
				dateExtract = extractedData[1].split("/");

				if(timeExtract.length >= 2){
					hour = Integer.parseInt(timeExtract[0]);
					minute = Integer.parseInt(timeExtract[1]);
				}

				if(dateExtract.length >= 3){			
					date = Integer.parseInt(dateExtract[0]);
					month = Integer.parseInt(dateExtract[1]);
					year = Integer.parseInt(dateExtract[2]);
				}

				//	   public static long addReminder(              Context, int date, int month, int year, int hour, int minute, int min_before_event, String title,                  String location,               String desc,  String attendees) 
				int eventId = (int) CalendarInsertEvent.addReminder(con,      date,      month,     year,     hour,     minute,        30,             rows.get(index).get(5).toString(), "",    desc,       rows.get(index).get(4).toString());

				Log.d("AppoinText", "the database insert statement :" + date + "'" + month +"'"+year+"'" + "'" + hour + "'" + minute + "'" );
				// after the reminder set, then put the entry to the set reminders table and add all the details to extractedData field in the form of Location:xxxx-Attendees:xxxx-Event:xxxx- all of them being a CSV 

				int isComplete = 1, isGroup = 0;

				String people;
				String extractedInfo = "";

				people = rows.get(index).get(4).toString();

				int number = people.split(",").length;

				if(number > 1){
					isGroup = 1;
				}

				if(!people.equalsIgnoreCase("")){

					Log.i("blah", people);

					extractedInfo += ("Attendees:" + people+"-");
				}

				else{

					isComplete = 0;
				}

				if(!rows.get(index).get(7).toString().equalsIgnoreCase("")){

					Log.i("blah",  rows.get(index).get(7).toString());

					extractedInfo += ("Location:" + rows.get(index).get(7).toString() + "-");
				}

				else{

					isComplete = 0;
				}

				if(!rows.get(index).get(5).toString().equalsIgnoreCase("")){

					Log.i("blah",  rows.get(index).get(5).toString());

					extractedInfo += ("Occasion:" + rows.get(index).get(5).toString() + "-");
				}

				else{

					isComplete = 0;
				}

				// the event-sender-receiver string to retrieve the data from the set reminders table later

				String trs = rows.get(index).get(5).toString() + "-" + senderNumber + "-" + recieverNumber;

				db.deleteRow("pendingReminders", (Integer)rows.get(index).get(0));

				db.addRow("setReminders", eventId, isComplete, isGroup, trs, extractedInfo);

				rows = db.getMultipleSetReminders("SELECT * FROM setReminders");

				Log.d("Appointext", " The set reminder database is like this : " + rows.toString());

				rows = db.getMultiplePendingReminders("SELECT * FROM pendingReminders");

				Log.d("Appointext", " The pending reminder database is like this : " + rows.toString());

				return 1;
			}
		}

		if(reply.equalsIgnoreCase("no")){

			//if the reply is a no, then delete the entry form the pending reminders table

			String event = RecognizeEvent.getEvent(curText);

			if(event.equalsIgnoreCase("")){

				if(event.equalsIgnoreCase(rows.get(index).get(5).toString())){

					db.deleteRow("pendingReminders", (Integer)rows.get(index).get(0));
				}

				else{
					UpdateReminder.cancelReminder(con, curText, senderNumber, recieverNumber);
				}
			}

			db.deleteRow("pendingReminders", (Integer)rows.get(index).get(0));
			db.close();
			return 1;
		}

		db.close();

		return 0;
	}

	public static long inMiliseconds(String oldDT, String newDate, String newTime)throws Exception {

		String[] dt = oldDT.split(" ");
		oldDT = oldDT.replaceAll("[^0-9/: ]", "");

		if (newDate != null && !newDate.equals(""))
			dt[0] = newDate;

		if (newTime != null && !newTime.equals(""))
			dt[1] = newTime;

		oldDT = dt[0] + " " + dt[1];
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date finalDate = formatter.parse(oldDT);
		return finalDate.getTime();

	}
}