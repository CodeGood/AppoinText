package com.appointext.regex;

import java.util.Arrays;

public class RecognizeDay {

	/**
	 * Returns a String of Days (of the week) found, and AN EMPTY STRING (NOT equal to null) if none are found
	 * @param msg - Input:: SMS Body (Text Only) to process 
	 * @return - A Comma Seperated List of Days that were found in the SMS Body. All days are represented by their 3 letter abbreviation
	 */

	 public static String findDay(String msg) {
	 
		String[] dotw = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday", "Mon", "Tue", "Wed", "Thu", "Thurs", "Fri", "Sat", "Sun"};
		String foundDays = "";
	
		for (String curWord : msg.split(" ")) //This ensures we avoid recognizing works like wedding as days! But 'wed' and 'sat' can present REAL problems without a PoS tagger
			if (Arrays.asList(dotw).contains(curWord))
				foundDays += (curWord.substring(0,3) + ",");
				
		return foundDays.substring(0, foundDays.length()-1);
	 }
}

