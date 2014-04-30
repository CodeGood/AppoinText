package com.appointext.backend;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
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

@SuppressLint("SimpleDateFormat")
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
		
		Log.d("appointext", "the time and date : " + timeExtracted + " " + dateExtracted );	
		
		boolean val = FindPostponement.findPostponement(curText);
		
		if(val){
			
			
			String trs = event + "-" + senderNumber + "-" + recieverNumber;			
			rows = db.getMultipleSetReminders("SELECT * FROM setReminders WHERE trs="+ "'" + trs + "'");
			
			
			trs = event + "-" + recieverNumber + "-" + senderNumber;
			tempRows = db.getMultipleSetReminders("SELECT * FROM setReminders WHERE trs="+ "'" + trs + "'"); 
			
			rows.addAll(tempRows);
			
			if(rows.isEmpty()){
				
				return -1;
			}
			
			int eventId = Integer.parseInt(rows.get(0).get(0).toString());
			String[] fields = {Events.DTSTART};
						
			String result = GetCalendarEvents.getEventByID(con, eventId+"", fields);
			
			Log.d("Postpone: Appointext", "the rows fetched form the setReminders db are: " + rows.toString() + " The value of trs is : " + trs);
					
			
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
				Log.i("Postpone: Appointext", "I got an exception");
			}
			Log.d("Postpone: Appointext", "the values extracted : hour: " + hour + "minute: " + minute + "dd: " + dd + "mm: " + mm + "yy: " + yy );
			
			return -1;
		}
		
		//After extracting date and time, get it in the format HH:MM,dd/mm/yyyy and store it in the pending reminders list
			
		if(!timeExtracted.equalsIgnoreCase("") && !dateExtracted.equalsIgnoreCase("")){
		
			when = timeExtracted.split("[/,]")[0] + "," + dateExtracted.split("[/,]")[0]+"/"+dateExtracted.split("[/,]")[1]+"/"+dateExtracted.split("[/,]")[2];
		}
		
		else if(!timeExtracted.equalsIgnoreCase("")){
			
			rows = db.getMultiplePendingReminders("SELECT * FROM pendingReminders WHERE senderNumber=" + "'" + senderNumber + "'" + " and receiverNumber=" + "'" + recieverNumber+"'");
			
			Log.d("AppoinText", "the value of rows is : " + rows.toString());

			tempRows = db.getMultiplePendingReminders("SELECT * FROM pendingReminders WHERE senderNumber=" + "'" + recieverNumber + "'" + " and receiverNumber=" + "'" + senderNumber +"'");
			
			Log.d("AppoinText", "the value of tempRows : " + tempRows.toString());
			
			rows.addAll(tempRows);
			
			if(!rows.isEmpty()){
			
				String whenStamp = rows.get(0).get(6).toString();
				String finalTime = "";
			
				if(whenStamp.startsWith(",")){
					
					finalTime = timeExtracted.split("[/,]")[0] + whenStamp;
					
					if(rows.get(0).get(3).toString().equals("1")){
						
						String[] extractedData = finalTime.split(",");	         	    		
						int date=0, month=0, year=0, hour=0, minute=0;
						String[] dateExtract, timeExtract;
	
						timeExtract = extractedData[0].split(":");
						dateExtract = extractedData[1].split("/");
	
						hour = Integer.parseInt(timeExtract[0]);
						minute = Integer.parseInt(timeExtract[1]);
	
						date = Integer.parseInt(dateExtract[0]);
						month = Integer.parseInt(dateExtract[1]);
						year = Integer.parseInt(dateExtract[2]);
	
						//	   public static long addReminder(              Context, int date, int month, int year, int hour, int minute, int min_before_event, String title,                  String location,               String desc,  String attendees) 
						int eventId = (int) CalendarInsertEvent.addReminder(con,      date,      month,     year,     hour,     minute,        30,             rows.get(0).get(5).toString(), rows.get(0).get(7).toString(),    curText,       rows.get(0).get(4).toString());
	
						Log.d("AppoinText", "the database insert statement :" + date + "'" + month +"'"+year+"'" + "'" + hour + "'" + minute + "'" );
						// after the reminder set, then put the entry to the set reminders table and add all the details to extractedData field in the form of Location:xxxx-Attendees:xxxx-Event:xxxx- all of them being a CSV 
	
						int isComplete = 1, isGroup = 0;
	
						String people1;
						String extractedInfo = "";
	
						people1 = rows.get(0).get(4).toString();
	
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
	
						if(!rows.get(0).get(7).toString().equalsIgnoreCase("")){
							Log.i("blah",  rows.get(0).get(7).toString());
							extractedInfo += ("Location:" + rows.get(0).get(7).toString() + "-");
						}
	
						else{
							isComplete = 0;
						}
	
						if(!rows.get(0).get(5).toString().equalsIgnoreCase("")){	
							Log.i("blah",  rows.get(0).get(5).toString());	
							extractedInfo += ("Occasion:" + rows.get(0).get(5).toString() + "-");
						}
	
						else{	
							isComplete = 0;
						}
	
						// the event-sender-receiver string to retrieve the data from the set reminders table later 
						// create trs and store the entry in the set reminder table
	
						String trs = rows.get(0).get(5).toString() + "-" + senderNumber + "-" + recieverNumber;
						
						db.deleteRow("pendingReminders", (Integer)rows.get(0).get(0));
	
						db.addRow("setReminders", eventId, isComplete, isGroup, trs, extractedInfo);
						
						rows = db.getMultipleSetReminders("SELECT * FROM setReminders");
						
						Log.d("Appointext", " The set reminder database is like this : " + rows.toString());
						
						rows = db.getMultiplePendingReminders("SELECT * FROM pendingReminders");
						
						Log.d("Appointext", " The pending reminder database is like this : " + rows.toString());
	
						return 1;
					}
					
					else{
						
						db.updateRow("pendingReminders", (Integer)rows.get(0).get(0), rows.get(0).get(1).toString(), rows.get(0).get(2).toString(), (Integer)rows.get(0).get(3), rows.get(0).get(4).toString(), rows.get(0).get(5).toString(), finalTime, rows.get(0).get(7).toString());	
						return 2;
					}		
				}	
			}//
		}
		
		else if(!dateExtracted.equalsIgnoreCase("")){
			
			//when =  "," + dateExtracted.split("[/,]")[0]+"/"+dateExtracted.split("[/,]")[1]+"/"+dateExtracted.split("[/,]")[2];
			
			rows = db.getMultiplePendingReminders("SELECT * FROM pendingReminders WHERE senderNumber=" + "'" + senderNumber + "'" + " and receiverNumber=" + "'" + recieverNumber+"'");
			
			Log.d("AppoinText", "the value of rows is : " + rows.toString());

			tempRows = db.getMultiplePendingReminders("SELECT * FROM pendingReminders WHERE senderNumber=" + "'" + recieverNumber + "'" + " and receiverNumber=" + "'" + senderNumber +"'");
			
			Log.d("AppoinText", "the value of tempRows : " + tempRows.toString());
			
			rows.addAll(tempRows);
			
			if(!rows.isEmpty()){
			
				String whenStamp = rows.get(0).get(6).toString();
				String finalTime = "";
			
				if(whenStamp.endsWith(",")){
					
					finalTime = whenStamp + dateExtracted.split("[/,]")[0]+"/"+dateExtracted.split("[/,]")[1]+"/"+dateExtracted.split("[/,]")[2];
					
					if(rows.get(0).get(3).toString().equals("1")){
						
						String[] extractedData = finalTime.split(",");	         	    		
						int date=0, month=0, year=0, hour=0, minute=0;
						String[] dateExtract, timeExtract;
	
						timeExtract = extractedData[0].split(":");
						dateExtract = extractedData[1].split("/");
	
						hour = Integer.parseInt(timeExtract[0]);
						minute = Integer.parseInt(timeExtract[1]);
	
						date = Integer.parseInt(dateExtract[0]);
						month = Integer.parseInt(dateExtract[1]);
						year = Integer.parseInt(dateExtract[2]);
	
						//	   public static long addReminder(              Context, int date, int month, int year, int hour, int minute, int min_before_event, String title,                  String location,               String desc,  String attendees) 
						int eventId = (int) CalendarInsertEvent.addReminder(con,      date,      month,     year,     hour,     minute,        30,             rows.get(0).get(5).toString(), rows.get(0).get(7).toString(),    curText,       rows.get(0).get(4).toString());
	
						Log.d("AppoinText", "the database insert statement :" + date + "'" + month +"'"+year+"'" + "'" + hour + "'" + minute + "'" );
						// after the reminder set, then put the entry to the set reminders table and add all the details to extractedData field in the form of Location:xxxx-Attendees:xxxx-Event:xxxx- all of them being a CSV 
	
						int isComplete = 1, isGroup = 0;
	
						String people1;
						String extractedInfo = "";
	
						people1 = rows.get(0).get(4).toString();
	
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
	
						if(!rows.get(0).get(7).toString().equalsIgnoreCase("")){
							Log.i("blah",  rows.get(0).get(7).toString());
							extractedInfo += ("Location:" + rows.get(0).get(7).toString() + "-");
						}
	
						else{
							isComplete = 0;
						}
	
						if(!rows.get(0).get(5).toString().equalsIgnoreCase("")){	
							Log.i("blah",  rows.get(0).get(5).toString());	
							extractedInfo += ("Occasion:" + rows.get(0).get(5).toString() + "-");
						}
	
						else{	
							isComplete = 0;
						}
	
						// the event-sender-receiver string to retrieve the data from the set reminders table later 
						// create trs and store the entry in the set reminder table
	
						String trs = rows.get(0).get(5).toString() + "-" + senderNumber + "-" + recieverNumber;
						
						db.deleteRow("pendingReminders", (Integer)rows.get(0).get(0));
	
						db.addRow("setReminders", eventId, isComplete, isGroup, trs, extractedInfo);
						
						rows = db.getMultipleSetReminders("SELECT * FROM setReminders");
						
						Log.d("Appointext", " The set reminder database is like this : " + rows.toString());
						
						rows = db.getMultiplePendingReminders("SELECT * FROM pendingReminders");
						
						Log.d("Appointext", " The pending reminder database is like this : " + rows.toString());
	
						return 1;
					}
					
					else{
						
						db.updateRow("pendingReminders", (Integer)rows.get(0).get(0), rows.get(0).get(1).toString(), rows.get(0).get(2).toString(), (Integer)rows.get(0).get(3), rows.get(0).get(4).toString(), rows.get(0).get(5).toString(), finalTime, rows.get(0).get(7).toString());	
						return 2;
					}		
				}	
			}//
		}

		rows = db.getMultiplePendingReminders("SELECT * FROM pendingReminders WHERE senderNumber=" + "'" + senderNumber + "'" + " and receiverNumber=" + "'" + recieverNumber+"'" + " and whenIsIt=" + "'" + when + "'");
		
		Log.d("Appointext", "I am trying to figure out duplicate rows : " + rows.toString());
		
		if(rows.isEmpty()){
			
			db.addRow("pendingReminders", senderNumber, recieverNumber, 0, people, event, when, curText); 
			Log.e("Appointext", "db.addRow  : " + " " + senderNumber+ " " +recieverNumber+ " " +0+ " " + people+ " " + event+ " " + when+ " " +location+ " "  );

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
		
		
		if(rows.isEmpty()){
			
			Log.e("AppoinText", "the rows are empty");
			//call cancel
			return -1;  //-1 value used will indicate that there were no reminders to confirm
		}

		if(reply.equalsIgnoreCase("yes")){
			
			Log.i("AppoinText","no the rows are not empty and the 7th element : " + rows.get(0).get(6).toString() + " And the rows are " + rows.toString());

			// if the reply is affirmative, then check if the time was found. If not, then just change the entry in the db to indicate that the meeting is confirmed

			if(rows.get(0).get(6).toString()!= null && (rows.get(0).get(6).toString().equalsIgnoreCase("") || rows.get(0).get(6).toString().startsWith(",") || rows.get(0).get(6).toString().endsWith(","))){         	    		

				Log.d("AppoinText", "Inside the if condition. Not enough information to add pending reminder. :P");
				db.updateRow("pendingReminders", (Integer)rows.get(0).get(0), rows.get(0).get(1).toString(), rows.get(0).get(2).toString(), 1, rows.get(0).get(4).toString(), rows.get(0).get(5).toString(), rows.get(0).get(6).toString(), rows.get(0).get(7).toString());	
				return 2; //return value of 2 means that there was not enough information to set the reminder 
			}

			else{

				//else extract the details required for the add reminder function and add the reminder

				String whenStamp = rows.get(0).get(6).toString();	
				String desc = rows.get(0).get(7).toString();
				String[] extractedData = whenStamp.split(",");	         	    		
				int date=0, month=0, year=0, hour=0, minute=0;
				String[] dateExtract, timeExtract;

				timeExtract = extractedData[0].split(":");
				dateExtract = extractedData[1].split("/");

				hour = Integer.parseInt(timeExtract[0]);
				minute = Integer.parseInt(timeExtract[1]);

				date = Integer.parseInt(dateExtract[0]);
				month = Integer.parseInt(dateExtract[1]);
				year = Integer.parseInt(dateExtract[2]);

				//	   public static long addReminder(              Context, int date, int month, int year, int hour, int minute, int min_before_event, String title,                  String location,               String desc,  String attendees) 
				int eventId = (int) CalendarInsertEvent.addReminder(con,      date,      month,     year,     hour,     minute,        30,             rows.get(0).get(5).toString(), rows.get(0).get(7).toString(),    desc,       rows.get(0).get(4).toString());

				Log.d("AppoinText", "the database insert statement :" + date + "'" + month +"'"+year+"'" + "'" + hour + "'" + minute + "'" );
				// after the reminder set, then put the entry to the set reminders table and add all the details to extractedData field in the form of Location:xxxx-Attendees:xxxx-Event:xxxx- all of them being a CSV 

				int isComplete = 1, isGroup = 0;

				String people;
				String extractedInfo = "";

				people = rows.get(0).get(4).toString();

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

				if(!rows.get(0).get(7).toString().equalsIgnoreCase("")){

					Log.i("blah",  rows.get(0).get(7).toString());

					extractedInfo += ("Location:" + rows.get(0).get(7).toString() + "-");
				}

				else{

					isComplete = 0;
				}

				if(!rows.get(0).get(5).toString().equalsIgnoreCase("")){

					Log.i("blah",  rows.get(0).get(5).toString());

					extractedInfo += ("Occasion:" + rows.get(0).get(5).toString() + "-");
				}

				else{

					isComplete = 0;
				}

				// the event-sender-receiver string to retrieve the data from the set reminders table later

				String trs = rows.get(0).get(5).toString() + "-" + senderNumber + "-" + recieverNumber;
				
				db.deleteRow("pendingReminders", (Integer)rows.get(0).get(0));

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
				
				if(event.equalsIgnoreCase(rows.get(0).get(5).toString())){
					
					db.deleteRow("pendingReminders", (Integer)rows.get(0).get(0));
				}
				
				else{
					
					// call cancel
				}
			}

			db.deleteRow("pendingReminders", (Integer)rows.get(0).get(0));
			db.close();
			return 1;
		}
		
		db.close();
		
		return 0;
	}
	
	public static long inMiliseconds(String oldDT, String newDate, String newTime)throws Exception {

		String[] dt = oldDT.split(" ");
	
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
