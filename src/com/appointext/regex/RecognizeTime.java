package com.appointext.regex;

import java.util.Locale;

@SuppressWarnings("unused")
public class RecognizeTime {
	
	protected static String sms;
	
	public static String findTime(String msg) {
		
		sms = msg.toLowerCase(Locale.US).trim();
		return null;
		
	}
	
	/* Checks for the most basic case - whether the string contains am or pm */

	private static String findAMPM() {
		
		String returnString="";
		
		if(sms.contains("am")) {
			
			//Step 1 - Break it off at am and check for I am
			String[] amStrings = sms.split("am");
			
			//Step 2 - Check whether it is I am 
			for (String curam : amStrings) {
				
				if (curam.charAt(curam.length()-1) != 'i') { //It is NOT I am					
					int start = getNumberStartIndex(curam); //check if a number exists in the current string
					if (start != -1) //if it does
						returnString += sms.substring(start, getNumberEndIndex(curam, start)) + ","; //extract and add
				}
				
			}
		}
		
		if (sms.contains("pm ")) {
			
			for (String curpm : sms.split("pm")) { //Get individual pms					
				int start = getNumberStartIndex(curpm); //check if a number exists in the current string
				if (start != -1) //if it does
					returnString += sms.substring(start, getNumberEndIndex(curpm, start)) + ","; //extract and add
			}
			
		}
				
		return returnString;
		
	}
	
	/* Checks for o'clock */
	
	/* Checks for xx:xx  - in all permutations and combination */
	
	/* Checks for xx xx - in all permutations and combinations */
	
	/* Methods to find numbers in the given string. Since we are looking for time, we take : and " " as digit over here. */
	/**
	 * Remember to trim after splitting at , ... The value returned may contain stray spaces
	 * @param str - the String in which we are looking for a time
	 * @return - the index where the first digit was found
	 */
	
	private static int getNumberStartIndex(String str) { //returns index of first digit it finds in the string
		
		for (int i = 0; i < str.length(); i++)
			if (Character.isDigit(str.charAt(i)))
					return i;
			
		return -1;
		
	}
	
	private static int getNumberEndIndex(String str, int i) { //returns index of first digit it finds in the string
		
		for ( ; i < str.length(); i++)
			if (!Character.isDigit(str.charAt(i)) && str.charAt(i) != ':' && str.charAt(i) != ' ')
					return i; //returns the position of the first non-digit. So basically end of number, exclusive
			
		return -1;
		
	}

}
