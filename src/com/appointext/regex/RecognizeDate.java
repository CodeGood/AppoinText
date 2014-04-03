package com.appointext.regex;

/** ASSUMPTIONS
day after = day after tomorrow
24th = 24th of this month
*/

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

public class RecognizeDate {
	
	private static int thisDay, thisMonth, thisYear;
	private static SimpleDateFormat dateFormat; 
	private static Calendar cal;

	static {
		dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		cal = Calendar.getInstance();
		String[] today = dateFormat.format(cal.getTime()).split("/");
		thisYear = Integer.parseInt(today[2]);
		thisMonth = Integer.parseInt(today[1]);
		thisDay = Integer.parseInt(today[0]);
	}

	public static void main(String[] args) {
//System.out.println("abc12bd3".replaceAll("[^0-9]", ""));
		System.out.println(findDates(args[0]));
		
	}
	
	/**
	* Returns a Comma Separated list of dates found - including keywords like today. Returns an EMPTY String - NOT null - if no date found.
	*@param msg - The SMS to analyze
	*@return - A comma separated list of dates that were found
	*/
	
	public static String findDates(String msg) {
			
		String foundDates = "";		
		msg = msg.replaceAll("(\\w+)\\p{Punct}(\\s|$)", "$1$2"); //I hope there is no pain lurking around the corner :-|

		foundDates += findByKeywords(msg);
		foundDates += findByMonthName(msg);
		foundDates += findByDDMMYY(msg);
		
		return deDup(foundDates); //Gets rid of the last comma and returns
	
	}
	
	/** 
	* Searches for keywords
	* @param msg - The incoming SMS
	* @return - CSV list of dates
	*/
	
	public static String findByKeywords(String msg) {
	
		msg = msg.toLowerCase().trim(); //Since I do not see how preserving capitalisation will help
		String foundDates = "";	
		String[] words = msg.split(" ");
		
		for (int i = 0; i < words.length; i++) {
		
			if (words[i].equals("today")) {
				foundDates += (thisDay + "/" + thisMonth + "/" + thisYear + "/" + i + ",");
			}
			else if (words[i].equals("tomorrow")) {
				cal.add(Calendar.DATE, 1); //so tomorrow happened
				foundDates += (dateFormat.format(cal.getTime()) + "/" + i + ",");
				cal = Calendar.getInstance(); //reset it back to tomorrow so that no confusions happen
			}
			else if (words[i].equals("day") && i != words.length-1 && words[i+1].equals("after")) {
				cal.add(Calendar.DATE, 2); //so tomorrow happened
				foundDates += (dateFormat.format(cal.getTime()) + "/" + i + ",");
				cal = Calendar.getInstance();
				i++; //Ignore after
				if (i != words.length && words[i].equals("tomorrow"))
					i++; //Ignore tomorrow, since day after means the same thing as day after tomorrow
			}
			else if (words[i].equals("week")) {
				/* Getting starting day of week */
				int toAdd = Calendar.MONDAY - cal.get(Calendar.DAY_OF_WEEK);
				cal.add(Calendar.DATE, toAdd);
				foundDates += dateFormat.format(cal.getTime()).split("/")[0];
				cal.add(Calendar.DATE, 6);
				foundDates += "-" + dateFormat.format(cal.getTime()).split("/")[0];
				cal = Calendar.getInstance();
				foundDates += ("/" + thisMonth + "/" + thisYear + "/" + i + ",");
			}		
			else if (words[i].equals("month") && (i == words.length-1 || !words[i+1].equals("of"))) { //the second part ignores stuff like 'month of July'
				foundDates += ("xx/" + thisMonth + "/" + thisYear + "/" + i + ",");
			}	
			else if (words[i].equals("year")) {
				foundDates += ("xx/xx/" + thisYear + "/" + i + ",");
			}				
			
			//If you think of anything else, feel free to add else ifs
		}
		return foundDates;
	
	}
	
	/** 
	* Searches for month names
	* @param msg - The incoming SMS
	* @return - CSV list of dates
	*/
	
	public static String findByMonthName (String msg) {

		String[] words = msg.split(" ");
		String foundDates = "";
		String date = "xx";
		int index;
		
		for (int i = 0; i < words.length; i++) {
		
			if ((index = words[i].indexOf("Jan")) != -1) {

				if (i != 0) { //Check if the month is preceeded by a date
					if((date = words[i-1].replaceAll("[^0-9]", "")).equals("")) //if there are no numbers in the previous string
						date = "xx"; //We did not get a date. Otherwise the date was already assigned					
				}
				if (i > 1 && words[i-1].equals("of") && (date = words[i-1].replaceAll("[^0-9]", "")).equals("")) //DANGER!!!
					date = "xx"; //How this works is, if the previous word is of, it cheks the one before that. If that is contains a number, it is assigned to date
					//Otherwise, we found no date. So assign xx.
				foundDates += (date + "/01/" + thisYear + "/" + i + ",");
				
			}
			else if ((index = words[i].indexOf("Feb")) != -1){

				if (i != 0) { 
					if((date = words[i-1].replaceAll("[^0-9]", "")).equals("")) 
						date = "xx"; 
				}
				if (i > 1 && words[i-1].equals("of") && (date = words[i-1].replaceAll("[^0-9]", "")).equals(""))
					date = "xx"; 
				foundDates += (date + "/02/" + thisYear + "/" + i + ",");
				
			}
			else if ((index = words[i].indexOf("Mar")) != -1) {
				if (i != 0) { 
					if((date = words[i-1].replaceAll("[^0-9]", "")).equals("")) 
						date = "xx"; 
				}
				if (i > 1 && words[i-1].equals("of") && (date = words[i-1].replaceAll("[^0-9]", "")).equals(""))
					date = "xx"; 
				foundDates += (date + "/01/" + thisYear + "/" + i + ",");
			}
			else if ((index = words[i].indexOf("Apr")) != -1) { 

				if (i != 0) {
					if((date = words[i-1].replaceAll("[^0-9]", "")).equals("")) 
						date = "xx"; 
				}
				if (i > 1 && words[i-1].equals("of") && (date = words[i-1].replaceAll("[^0-9]", "")).equals("")) 
					date = "xx"; 
				foundDates += (date + "/04/" + thisYear + "/" + i + ",");
				
			}
			else if ((index = words[i].indexOf("May")) != -1) { 

				if (i != 0) { 
					if((date = words[i-1].replaceAll("[^0-9]", "")).equals("")) 
						date = "xx";
				}
				if (i > 1 && words[i-1].equals("of") && (date = words[i-1].replaceAll("[^0-9]", "")).equals("")) //DANGER!!!
					date = "xx"; 
				foundDates += (date + "/05/" + thisYear + "/" + i + ",");
				
			}
			else if ((index = words[i].indexOf("Jun")) != -1) { 

				if (i != 0) { 
					if((date = words[i-1].replaceAll("[^0-9]", "")).equals("")) 
						date = "xx"; 
				}
				if (i > 1 && words[i-1].equals("of") && (date = words[i-1].replaceAll("[^0-9]", "")).equals("")) 
					date = "xx";
				foundDates += (date + "/06/" + thisYear + "/" + i + ",");
				
			}
			else if ((index = words[i].indexOf("Jul")) != -1) { 

				if (i != 0) { 
					if((date = words[i-1].replaceAll("[^0-9]", "")).equals("")) 
						date = "xx"; 
				}
				if (i > 1 && words[i-1].equals("of") && (date = words[i-1].replaceAll("[^0-9]", "")).equals("")) 
					date = "xx"; 
				foundDates += (date + "/07/" + thisYear + "/" + i + ",");
				
			}
			else if ((index = words[i].indexOf("Aug")) != -1) { 

				if (i != 0) {
					if((date = words[i-1].replaceAll("[^0-9]", "")).equals("")) 
						date = "xx";
				}
				if (i > 1 && words[i-1].equals("of") && (date = words[i-1].replaceAll("[^0-9]", "")).equals("")) 
					date = "xx"; 
				foundDates += (date + "/08/" + thisYear + "/" + i + ",");
				
			}
			else if ((index = words[i].indexOf("Sep")) != -1) { 

				if (i != 0) {
					if((date = words[i-1].replaceAll("[^0-9]", "")).equals(""))
						date = "xx"; 
				}
				if (i > 1 && words[i-1].equals("of") && (date = words[i-1].replaceAll("[^0-9]", "")).equals("")) 
					date = "xx"; 
				foundDates += (date + "/09/" + thisYear + "/" + i + ",");
				
			}
			else if ((index = words[i].indexOf("Oct")) != -1) { 

				if (i != 0) { 
					if((date = words[i-1].replaceAll("[^0-9]", "")).equals("")) 
						date = "xx";
				}
				if (i > 1 && words[i-1].equals("of") && (date = words[i-1].replaceAll("[^0-9]", "")).equals("")) 
					date = "xx"; 
				foundDates += (date + "/10/" + thisYear + "/" + i + ",");
				
			}
			else if ((index = words[i].indexOf("Nov")) != -1) { 

				if (i != 0) {
					if((date = words[i-1].replaceAll("[^0-9]", "")).equals(""))
						date = "xx"; 
				}
				if (i > 1 && words[i-1].equals("of") && (date = words[i-1].replaceAll("[^0-9]", "")).equals(""))
					date = "xx";
				foundDates += (date + "/11/" + thisYear + "/" + i + ",");
				
			}
			else if ((index = words[i].indexOf("Dec")) != -1) { 

				if (i != 0) { 
					if((date = words[i-1].replaceAll("[^0-9]", "")).equals(""))
						date = "xx"; 
				}
				if (i > 1 && words[i-1].equals("of") && (date = words[i-1].replaceAll("[^0-9]", "")).equals("")) 
					date = "xx"; 
				foundDates += (date + "/12/" + thisYear + "/" + i + ",");
				
			}
			else if (!((date = words[i].replaceAll("[^0-9]", "")).equals("")) && words[i].endsWith("th")) //Just a date, without any month would mean this month.
				foundDates += (date + "/" + thisMonth + "/" + thisYear + "/" + i + ",");
			
		}
		
		return foundDates;
			
	}
	
	/** 
	* Searches for regexes of the form dd/mm/yyyy or dd/mm/yy or d/m/yy or something similar
	* @param msg - The incoming SMS
	* @return - CSV list of dates
	*/
	
	public static String findByDDMMYY (String msg) {

		Pattern p = Pattern.compile(" ([0-3])?[0-9][/\\.\\-]([0-1])?[0-9][/\\.\\-]20[1-9][0-9]([^0-9a-zA-Z/\\-\\.])");
		String foundDates = "", match;
		msg += ","; //HACK - around the fact that I can't write a good enough RegEx

		Matcher m = p.matcher(msg);
		while (m.find()) {
			match=m.group().trim().replaceAll("[\\.\\-]", "/");
			
			if (match.indexOf("/") == 1) //first slash should be at 2 dd/mm/yyyy
				match = "0" + match;
			if (match.lastIndexOf("/") == 4) //last slash should be at 5, dd/mm/yyyy
				match = match.substring(0,3) + "0" + match.substring(3);
				
			foundDates +=(match + "/c" + msg.indexOf(m.group()) + ","); //I can't return word count here, and returning indexOf everywhere else would be too time consuming. So c107 means I am returning character 107. 
		}		

		p = Pattern.compile(" ([0-3])?[0-9][/\\.\\-]([0-1])?[0-9][/\\.\\-][1-9][0-9]([^0-9a-zA-Z/\\-\\.])");
		m = p.matcher(msg);
		while (m.find()) {
			match = m.group().trim().replaceAll("[\\.\\-]", "/");
			
			if (match.indexOf("/") == 1) //first slash should be at 2 dd/mm/yyyy
				match = "0" + match;
			if (match.lastIndexOf("/") == 4) //last slash should be at 5, dd/mm/yyyy
				match = match.substring(0,3) + "0" + match.substring(3);
			
			match = match.substring(0,6) + "20" + match.substring(6,8);	
			
			foundDates +=(match+ "/c" + msg.indexOf(m.group()) + ","); //I can't return word count here, and returning indexOf everywhere else would be too time consuming. So c107 means I am returning character 107. 
		}		

		p = Pattern.compile(" ([0-3])?[0-9][/\\.\\-]([0-1])?[0-9]([^0-9a-zA-Z/\\-\\.])");//Can not be a legitimate full stop, since they would have already been taken out.
		m = p.matcher(msg);
		while (m.find()) {
			match = m.group().trim().replaceAll("[\\.\\-]", "/");;

			if (match.indexOf("/") == 1) //first slash should be at 2 dd/mm
				match = "0" + match;
			if (match.length() != 5) //length should be 5
				match = match.substring(0,3) + "0" + match.substring(3);

			foundDates +=(match + "/" + thisYear + "/c" + msg.indexOf(m.group()) + ","); //I can't return word count here, and returning indexOf everywhere else would be too time consuming. So c107 means I am returning character 107. 
		}	

		return foundDates;
	}
	
	
	/**
	* Removes duplicates from the string of dates created. Post processing turned out to be far easier to write than ensure none of the functions overlap
	* WARNING - MODIFIES it's input. Not that it will matter, but this is just good practice!
	* @param answer - The List of dates found by all the different functions in here
	* @return - a de-duplicated answer, which does not have the trailing comma either.
	*/
	
	public static String deDup (String answer) { //Removes duplicate dates in case they were added by any one
	
		HashSet<String> set = new HashSet<String>(Arrays.asList(answer.split(","))); //Get individual dates and convert them into a 'set' ... Automatically removes duplicates :D
		answer = "";
		
		for (String val : set)
			answer += val + ",";
		
		return answer.substring(0, answer.length()-1);
	}
}
