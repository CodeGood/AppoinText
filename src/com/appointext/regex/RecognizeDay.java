package com.appointext.regex;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@SuppressLint("SimpleDateFormat")
public class RecognizeDay {

	private static Calendar c = Calendar.getInstance(); //Got the current date

	/**
	 * Returns a String of Days (of the week) found, and AN EMPTY STRING (NOT equal to null) if none are found
	 * @param msg - Input:: SMS Body (Text Only) to process 
	 * @return - A Comma Seperated List of Days that were found in the SMS Body. All days are represented by their 3 letter abbreviation
	 */

	 public static String findDay(String msg) {
	 
		String[] dotw = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sun", "Mon", "Tue", "Wed", "Thu", "Thurs", "Fri", "Sat"};
		String foundDates = "";
		String[] words = msg.split(" ");
	
		for (int i = 0; i < words.length; i++)	{	//This ensures we avoid recognizing works like wedding as days! But 'wed' and 'sat' can present REAL problems without a PoS tagger
		
			for (int j = 0; j < dotw.length; j++) {			
			
				if (dotw[j].equals(words[i]) && !(words[i-1].equalsIgnoreCase("last"))) //am not interested in 'past' dates!
					foundDates += convertToDate((j > 6 ? j-7 : j), words[i-1]);
					foundDates += ",";
			}
			
		}

		return foundDates.substring(0, foundDates.length()-1);
	 }
	 
	 public static String convertToDate(int day, String prevWord) {

		int today = c.get(Calendar.DAY_OF_WEEK); //Got the day out of it.
		
		int toAdd = 0;
		
		/*Now figure out how many days to add to current date */
		
		/* LET ME KNOW IF SOMEONE SEES AN ERROR IN THIS LOGIC. */
		
		if (prevWord.equalsIgnoreCase("next")) { //Today is Thursday (4). If I say next Sunday, I need to add 3 + 7. 
		
			toAdd = day - today; //But I will get 0 - 4 = -4, which in module 7 is 3
			
			if (toAdd < 0) //Perform the modulo 7 thing
				toAdd = 7 + toAdd;
				
			if (day + toAdd <= 7) //and generally next week means, AFTER sunday, so add 7 to it.
					toAdd += 7;
		
		}
		else { //Today is Sunday. I say Wednesday. 3 - 0 = 3. Today is Sunday, I say Thursday. 4 - 0 = 4
			
			toAdd = day - today;
			if (toAdd < 0)
				toAdd = 7 + toAdd;
		}
		
		c.add(Calendar.DATE, toAdd+1); //Okay looks like it needs one extra
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.format(c.getTime());
	 }
}
