package com.appointext.backend;

import java.util.Map;

import com.appointext.database.DatabaseManager;
import com.appointext.naivebayes.Classifier;
import com.appointext.nertagger.*;
import com.appointext.regex.RecognizeDay;
import com.appointext.regex.RecognizeEvent;
import com.appointext.regex.RecognizeTime;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import java.util.ArrayList;
//import com.appointext.database.CalendarInsertEvent;

/*
 * JUSTIFICATION -->
 * 
 * IntentService is a light weight way of implementing a Service class
 * Why we used this instead of just Service is, IntentService cleans up after itself
 * An ordinary service would continue running in the Background unless the Android System murders it.
 * An IntentService decently terminates, once it's work is done.
 * 
 * Calls are queued and passed on to onHandleIntent (Reference here http://stackoverflow.com/questions/14833555/android-how-to-queue-multiple-intents-on-an-intentservice)
 * All data must be passed through Intent which starts/calls this Service.
 * Down side is NO UI Activity permitted. NONE. However, I assume that should never be required in the actual AppoinText app?
 */

public class AppoinTextService extends IntentService {
	
	DatabaseManager db;
	
	public AppoinTextService() {
		super("AppoinText"); //Apparently the name is necessary only for tracking purposes
		//It is easier to give a default name than bothering to figure out how to raise an Intent to call a parameterised constructor
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		//For an IntentService, all work must be in onHandleIntent. onCreate and onCommand should never be overridden.
	Log.d("AppoinText", "Service successfully called.");
		
		//Should have received the incoming sms as part of the Intent
		//Bundle smsBundle = intent.getExtras();
		String origin = intent.getStringExtra("origin");
		Log.i("AppoinText", "Got origin as " + origin);
		
/*		SmsMessage[] msgs = null;
		String curText = null;
		
        if (smsBundle != null) {
            
        	//---retrieve the SMS message received. Using the array just for the sake of safety ---
        	
            Object[] pdus = (Object[]) smsBundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            Map<String, Double> res;
            String category = null;
            Double minimumConfidence = Double.MIN_VALUE;
            
            //loop through the messages, if multiple are received in the same time
            for (int i=0; i<msgs.length; i++){
            	
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);             
                curText = msgs[i].getMessageBody().toString();
                
                String taggedCurText = null;
                
                res = this.classify(curText);
                
                for (Map.Entry<String, Double> entry : res.entrySet()) {
                    String key = entry.getKey();
                    Double value = entry.getValue();
                    
                    if(value > minimumConfidence){
                    	
                    	minimumConfidence = value;
                    	category = key;
                    }
                   
                }
                
                if(category.equalsIgnoreCase("irrelevant")){
                	// this is not important to us, so stop the service by returning.
                	return;
                }
                
                if(category.equalsIgnoreCase("query")){
                	
                	//since this is a query, extract all the details and then store it in the pending table.
                	
                	String[] taggedWords; 
                	String people = "";
                	String location = "";
 
                	try{
                		taggedCurText = NERecognizer.NERTagger(curText);
                	}
                	catch(Exception e){	
                		Log.e("NER Tagger", "Died while tagging :"+e);
                	}
                	
                	if(taggedCurText!=null){                		
                		taggedWords = taggedCurText.split(" ");
                
	                	for(String word : taggedWords){
	                		if(word.contains("PERSON")){  
	                			people += word.split("/")[0] + ",";
	                		}
	                		
	                		if(word.contains("LOCATION")){   			
	                			location += word.split("/")[0] + ",";
	                		}
	                		
	                		if(word.contains("ORGANISATION")){   			
	                			location += word.split("/")[0] + ",";
	   	                	}
	                	}
                	}
                	
                	int senderNumber =0, recieverNumber=0;
                	
                	if(origin.equalsIgnoreCase("inbox")){                		
                		senderNumber = Integer.parseInt(msgs[i].getOriginatingAddress());
                		recieverNumber = 123;
                	}
                	
                	else{               		
                		senderNumber = 123;
                		recieverNumber = Integer.parseInt(msgs[i].getOriginatingAddress());;              		
                	}
                	
                	String event = RecognizeEvent.getEvent(curText);
                	String when;
                	
                	when = RecognizeTime.findTime(curText) + "," + RecognizeDay.findDay(curText);
                	
                	db = new DatabaseManager(this);
                	
                	db.open();
                	db.addRow("pendingReminders", senderNumber, recieverNumber, 0, people, event, when, location);  
                	db.close();
                	
                	return;
                	
                }
                
                if(category.equalsIgnoreCase("reply")){
                	
                	ArrayList<ArrayList<Object>> rows;
                	
                	db = new DatabaseManager(this);
         	        db.open();
         	        
         	       int senderNumber =0, recieverNumber=0;
         	        
         	       if(origin.equalsIgnoreCase("inbox")){                		
	               		senderNumber = Integer.parseInt(msgs[i].getOriginatingAddress());
	               		recieverNumber = 123;
	               	}
	               	
	               	else{               		
	               		senderNumber = 123;
	               		recieverNumber = Integer.parseInt(msgs[i].getOriginatingAddress());;              		
	               	}
         	        
         	        rows = db.getMultiplePendingReminders("SELECT * FROM pendingReminders WHERE senderNumber=" + senderNumber + " and receiverNumber=" + recieverNumber);
         	        
         	        String reply = FindSentiment.findSentiment(curText);
         	        
         	        //TODO : Figure out if only date and only day is give, what to do then. And take care of the situation
         	        
	         	    if(reply.equalsIgnoreCase("yes")){
	         	    	
	         	    	if(rows.get(0).get(6).toString().equalsIgnoreCase("") || rows.get(0).get(6).toString().startsWith(",") || rows.get(0).get(6).toString().endsWith(",")){         	    		
	         	    		
	         	    		db.updateRow("pendingReminders", (Integer)rows.get(0).get(0), (Integer)rows.get(0).get(1), (Integer)rows.get(0).get(2), 1, rows.get(0).get(4).toString(), rows.get(0).get(5).toString(), rows.get(0).get(6).toString(), rows.get(0).get(7).toString());	
	         	    		return;
	         	    	}
	         	    	
	         	    	else{
	         	    		
	         	    		String whenStamp = rows.get(0).get(6).toString();	         	    		
	         	    		String[] extractedData = whenStamp.split(",");	         	    		
	         	    		int date, month, year, hour, minute;
	         	    		String[] dateExtract, timeExtract;
	         	    		
	         	    		timeExtract = extractedData[0].split(":");
	         	    		dateExtract = extractedData[1].split("/");
	         	    		
	         	    		hour = Integer.parseInt(timeExtract[0]);
	         	    		minute = Integer.parseInt(timeExtract[1]);
	         	    		
	         	    		date = Integer.parseInt(dateExtract[0]);
	         	    		month = Integer.parseInt(dateExtract[1]);
	         	    		year = Integer.parseInt(dateExtract[2]);
	         	    		
	         	    		int eventId = (int) CalendarInsertEvent.addReminder(this, date, month, year, hour, minute, 30, rows.get(0).get(5).toString(), rows.get(0).get(7).toString(), null, rows.get(0).get(4).toString());
	         	    		
	         	    		//TODO : to check the values and add it to set reminders tables
	         	    		
	         	    		return;
	         	    	}
	         	    }
	         	    
	         	   if(reply.equalsIgnoreCase("no")){
	         		   
	         		   db.deleteRow("pendingReminders", (Integer)rows.get(0).get(0));
	         		   return;
	         	   }
                }*/
             
                /*
                 * The following values will be utilized some day or the other
                 * .getTimestampMillis(); - Will give you the time stamp of receipt in milliseconds from epoch
                 * .getOriginatingAddress(); - Will give the sender number
                 * ReceiverNumber should be known, or can be replaced by an x in all databases.
                 
                
                Log.i("AppoinText", "Result of" + curText + " was " + classify(curText));
                
			}

		} */
		
	}

	/**
	 * Classifies the parameter passed and returns a Map of <Category, Confidence> values
	 * Category may be Meeting, Query, Reply, Irrelevant
	 * Confidence gives the Naive Probability that Category is applicable to the parameter passed
	 * @param message : The incoming text message that has to be categories
	 * @return Map of <Category, Confidence> values for the recently received sms. Null is returned in case of an error.
	 */
	
	Map<String, Double> classify (String message) { //May choose to make this guy private. 
		
		try {			
			Classifier cs = new Classifier(getApplicationContext(), "classifier.ser" );
			return cs.getConfidence(message);
		
		}
		catch (Exception e) {
			//TODO: Handle exceptions appropriately. Consider throwing some exceptions onwards. Or to the exception class.
			Log.e("AppoinText Service", "classify failed for " + message + " with error " + e);
		}
		
		return null;
	}

}
