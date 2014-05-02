package com.appointext.backend;


import java.util.Arrays;
import java.util.Map;


import com.appointext.database.DatabaseManager;
import com.appointext.naivebayes.Classifier;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import java.util.ArrayList;

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
	private static final int INBOX = 1;
	private static final int SENT = 2;
	private static int origin;

	public AppoinTextService() {
		super("AppoinText"); //Apparently the name is necessary only for tracking purposes
		//It is easier to give a default name than bothering to figure out how to raise an Intent to call a parameterized constructor
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		//For an IntentService, all work must be in onHandleIntent. onCreate and onCommand should never be overridden.

		String originS = intent.getStringExtra("origin");
		Long timeStamp = intent.getLongExtra("timestamp", 0);
		//Log.i("AppoinText", "The vlaue of the time stamp when the messge was detected this time is : " + timeStamp);

		String curText = null;

		if (originS.equals("inbox"))
			origin = INBOX;
		else
			origin = SENT;

		SmsMessage[] msgs = null;

		if (origin == INBOX) {
			//Should have received the incoming sms as part of the Intent
			Bundle smsBundle = intent.getExtras();
            Object[] pdus = (Object[]) smsBundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            msgs[0] = SmsMessage.createFromPdu((byte[])pdus[0]);             
            curText = msgs[0].getMessageBody().toString();			
		}
		else 
			curText = intent.getStringExtra("body");

        Log.i("AppoinText", "Got origin as " + origin);
        Log.d("AppoinTextCurText", "curText is " + curText);        
        
        if(curText == null || curText.length() == 0 || curText.equals(""))
		{
			Log.e("appointext", "curText is null or empty sorry :(");
			return;
		}

		db = new DatabaseManager(this);

		db.open();

		ArrayList<Object> row = new ArrayList<Object>();
		row = db.getRowAsArray("timeStampTable", "lastTimeStamp");

		Log.d("AppoinText","I am trying to get the value of the last time stamp : " + row.toString() );

		if(row.isEmpty()){

		Log.d("appointext", "I am in the empty clause of timeStamp");

			db.addRow("timeStampTable", "lastTimeStamp", timeStamp.toString());
		}


		else{

		Log.d("appointext", "I am in the same clause of timeStamp");

			if(Long.parseLong(row.get(1).toString()) == timeStamp){
		Log.d("appointext", "I am duplicate");	
				db.close();
				return;
			}

			else{

		Log.d("AppoinText", "I am updating the last acessed to " + timeStamp +"and the last message was : " + curText);

				if(timeStamp !=  0){

					db.updateRow("timeStampTable", "lastTimeStamp", timeStamp.toString());
					row = db.getRowAsArray("timeStampTable", "lastTimeStamp");
					Log.d("appointext", "updated timestamp " + row.toString());
				}
				db.close();
			}
		}


		Map<String, Double> res;
		String category = null;
		Double minimumConfidence = 0 - Double.MAX_VALUE;

		res = this.classify(curText);

		for (Map.Entry<String, Double> entry : res.entrySet()) {
			String key = entry.getKey();
			Double value = entry.getValue();

			if(value > minimumConfidence){

				minimumConfidence = value;
				category = key;

				//Log.d("Confidence Value", "The values are minimumConfidence :" + minimumConfidence + " category :" + category);
			}

		}// get the category and the confidence in case it is required

		Log.d("AppoinText", "The final values are minimumConfidence :" + minimumConfidence + " category :" + category);

		if(category.equalsIgnoreCase("irrelevant")){
			// this is not important to us, so stop the service by returning.
			return;
		}

		String senderNumber =null, recieverNumber = null;

		if(origin == INBOX){                		
			senderNumber = (msgs[0].getOriginatingAddress().replaceAll("[^0-9]", ""));
			recieverNumber = "123";
			if (senderNumber.length() == 10) {
				//Log.d("NumberConversion", "Original sender number " + senderNumber);
				senderNumber = "91" + senderNumber;
				//Log.d("NumberConversion", "New number as " + senderNumber);
			}

			db.open();

			ArrayList<Object> blockedNumbers;			
			blockedNumbers = db.getRowAsArray("settingsTable", "BlockedNumbers");

			String[] numbers  = blockedNumbers.toString().split(",");

			
			//Log.i("Appointext block numbers", "The strings are " + Arrays.toString(numbers));
			if (numbers.length == 0)
				Log.i("AppoinText Block Numbers", "There are no blocked numbers.");
			
			for(int i=0; i<numbers.length; i++){
				
				numbers[i] = numbers[i].replaceAll("[^0-9]", "");
				
				if(numbers[i].equalsIgnoreCase(senderNumber)){
					
					//Log.d("Appointext block numbers", "Entered if");
					return;
				}
			}
			db.close();


			for(int i=0; i<numbers.length; i++){

			}

		}

		else{               		
			senderNumber = "123";
			recieverNumber = (intent.getStringExtra("receiver").replaceAll("[^0-9]", "")); 
			if (recieverNumber.length() == 10) {
				//Log.d("NumberConversion", "Original sender number " + recieverNumber);
				recieverNumber = "91" + recieverNumber;
				//Log.d("NumberConversion", "New number as " + recieverNumber);
			}
			
			db.open();
			
			ArrayList<Object> blockedNumbers;			
			blockedNumbers = db.getRowAsArray("settingsTable", "BlockedNumbers");
			
			String[] numbers  = blockedNumbers.toString().split(",");
			//Log.i("Appointext block numbers", "The strings are " + numbers.toString());
			
			for(int i=0; i<numbers.length; i++){
				
				if(numbers[i].equalsIgnoreCase(recieverNumber)){
					
					//Log.d("Appointext block numbers", "Entered if");
					return;
				}
			}
			db.close();
			

		}

		if(category.equalsIgnoreCase("query")){

			SetReminder.addToPendingTable(this, curText, senderNumber, recieverNumber);

		}

		if(category.equalsIgnoreCase("reply")){

			SetReminder.setReminderBasedOnReply(this, curText, senderNumber, recieverNumber);
		}

		if(category.equalsIgnoreCase("meeting")){
			
			Log.i("Appointext meeting", "I am in meeting ");
			SetReminder.addToPendingTable(this, curText, senderNumber, recieverNumber);
		}

		/*
		 * The following values will be utilized some day or the other
		 * .getTimestampMillis(); - Will give you the time stamp of receipt in milliseconds from epoch
		 * .getOriginatingAddress(); - Will give the sender number
		 * ReceiverNumber should be known, or can be replaced by an x in all databases.
		 */

		Log.i("AppoinText", "Result of" + curText + " was " + classify(curText));

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
