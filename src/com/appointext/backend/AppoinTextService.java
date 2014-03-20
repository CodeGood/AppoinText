package com.appointext.backend;

import java.util.Map;
import com.appointext.naivebayes.Classifier;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/*
 * JUSTIFICATION -->
 * 
 * IntentService is a light weight way of implementing a Service class
 * Why we used this instead of just Service is, IntentService cleans up after itself
 * An ordinary service would continue running in the Backgroun unless the Android System murders it.
 * An IntentService decently terminates, once it's work is done.
 * 
 * Calls are queued and passed on to onHandleIntent (Reference here http://stackoverflow.com/questions/14833555/android-how-to-queue-multiple-intents-on-an-intentservice)
 * All data must be passed through Intent which starts/calls this Service.
 * Downside is NO UI Activity permitted. NONE. Howeever, I assume that shoud never be required in the actual AppoinText app?
 */

public class AppoinTextService extends IntentService {
	
	public AppoinTextService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
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

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		
	}


}
