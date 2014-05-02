package com.appointext.regex;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.Log;

import com.appointext.database.DatabaseManager;


public class RecognizeTime {
	
	protected static String sms;
		
	public static String findTime(Context con, String msg) {
		
		//sms = msg.toLowerCase(Locale.US).trim().replaceAll("(\\w+)\\p{Punct}(\\s|$)", "$1$2");
		sms = msg.toLowerCase(Locale.US).trim().replaceAll("[^a-zA-Z0-9\\' ]", ""); 
		String foundTime = "";
		foundTime += findOClock();
		foundTime += getTimeByRegex();
		
		if (foundTime.length() == 0)
			foundTime += getMEN(con); //get Morning, Evening, Night - kindly ignore the unintended puny function name
		
		return foundTime;
		
	}

	private static String getMEN(Context con) {
		
		String foundTime = "";		
		final DatabaseManager db = new DatabaseManager(con);
Log.w("AppoinTextReminder", "In GetMEN");		
		/* Get the values or set it to defaults ...
		 * May save time if I get a value only if the string includes the world morning!!
		 * Chose to do it this way, because multiple occurences of afternoon or morning is unlikely IMO 
		 */
		
		db.open();
		ArrayList<Object> row;
		
		String[] words = sms.toLowerCase().split(" ");
		
		for (int i = 0; i < words.length; i++) {
		
			if (words[i].equals("morning")) {
			
				row = db.getRowAsArray("settingsTable", "DayTimeMorning");
				if(row == null || row.isEmpty())
					foundTime += "09:00";
				else {
					String[] fromDB = ((String) row.get(1)).split(":");
					if (fromDB[0].length() < 2) fromDB[0] = "0" + fromDB[0];
					if (fromDB[1].length() < 2) fromDB[1] = "0" + fromDB[1];
					foundTime += fromDB[0] + ":" + fromDB[1];
				}
				foundTime += ",";
			
			}
			if (words[i].equals("afternoon")) {
				
				row = db.getRowAsArray("settingsTable", "DayTimeAfternoon");
				if(row == null || row.isEmpty())
					foundTime += "13:00";
				else {
					String[] fromDB = ((String) row.get(1)).split(":");
					if (fromDB[0].length() < 2) fromDB[0] = "0" + fromDB[0];
					if (fromDB[1].length() < 2) fromDB[1] = "0" + fromDB[1];
					foundTime += fromDB[0] + ":" + fromDB[1];
				}
				foundTime += ",";			
			
			}
			if (words[i].equals("evening")) {
				
				row = db.getRowAsArray("settingsTable", "DayTimeEvening");
				if(row == null || row.isEmpty())
					foundTime += "18:00";
				else {
					String[] fromDB = ((String) row.get(1)).split(":");
					if (fromDB[0].length() < 2) fromDB[0] = "0" + fromDB[0];
					if (fromDB[1].length() < 2) fromDB[1] = "0" + fromDB[1];
					foundTime += fromDB[0] + ":" + fromDB[1];
				}
				foundTime += ",";			
			
			}
			if (words[i].equals("night") || words[i].equals("tonight")) {
				
				row = db.getRowAsArray("settingsTable", "DayTimeNight");
				if(row == null || row.isEmpty())
					foundTime += "21:00";
				else {
					String[] fromDB = ((String) row.get(1)).split(":");
					if (fromDB[0].length() < 2) fromDB[0] = "0" + fromDB[0];
					if (fromDB[1].length() < 2) fromDB[1] = "0" + fromDB[1];
					foundTime += fromDB[0] + ":" + fromDB[1];
				}
				foundTime += ",";		
			
			}
			if (words[i].equals("noon")) {
				foundTime += "12:00";						
			}
			
		}
		db.close();
		return foundTime;
	}
	
	/* Checks for o'clock and half past */
	
	private static String findOClock () {
		
		String[] words = sms.split(" ");
		String foundTime = "";

		for (int i = 0; i < words.length; i++) {
Log.e("AppoinTextChange", "The word of the moment is " + words[i]);
			if (i != 0 && (words[i].equalsIgnoreCase("o'clock") || words[i].equalsIgnoreCase("oclock"))) {

				if ((words[i-1].replaceAll("[^0-9]", "")).equals("") == false) { //It's in numerals

					int time;
					try {
						time = Integer.parseInt(words[i-1].replaceAll("[^0-9]", ""));
					}
					catch (NumberFormatException ne) {
						time = 00;
					}
Log.e("AppoinTextChange", "The time is " + time);					
					if (time < 10)
						time = time + 12;
					
					foundTime += time + ":00/" + i;
					foundTime += ",";
				}
				else { //convert
Log.d("AppoinTextChange", "Somehow ended up here at word times :(");
					if (words[i-1].equals("one")) foundTime += "13:00/" + i + ",";
					else if (words[i-1].equals("two")) foundTime += "14:00/"+i+ ",";
					else if (words[i-1].equals("three")) foundTime += "15:00/"+i+ ",";
					else if (words[i-1].equals("four")) foundTime += "16:00/"+i+ ",";
					else if (words[i-1].equals("five")) foundTime += "17:00/"+i+ ",";
					else if (words[i-1].equals("six")) foundTime += "18:00/"+i+ ",";
					else if (words[i-1].equals("seven")) foundTime += "19:00/"+i+ ",";
					else if (words[i-1].equals("eight")) foundTime += "20:00/"+i+ ",";
					else if (words[i-1].equals("nine")) foundTime += "09:00/"+i+ ",";
					else if (words[i-1].equals("ten")) foundTime += "10:00/"+i+ ",";
					else if (words[i-1].equals("eleven")) foundTime += "11:00/"+i+ ",";
					else if (words[i-1].equals("twelve")) foundTime += "12:00/"+i+ ",";
				}				
			}
			else if (words[i].equals("half") && i != words.length-2 && (i == words.length -1 || words[i+1].equals("past"))) {
				if(words[i+2].replaceAll("[^0-9]", "").equals("") == false) {
					foundTime += words[i+2].replaceAll("[^0-9]", "");
					foundTime += ":30/" + i+ ",";					
				}
								
				else { //convert
					if (words[i].equals("one")) foundTime += "13:30/"+i+ ",";
					else if (words[i+2].equals("two")) foundTime += "14:30/"+i+ ",";
					else if (words[i+2].equals("three")) foundTime += "15:30/"+i+ ",";
					else if (words[i+2].equals("four")) foundTime += "16:30/"+i+ ",";
					else if (words[i+2].equals("five")) foundTime += "17:30/"+i+ ",";
					else if (words[i+2].equals("six")) foundTime += "18:30/"+i+ ",";
					else if (words[i+2].equals("seven")) foundTime += "19:30/"+i+ ",";
					else if (words[i+2].equals("eight")) foundTime += "20:30/"+i+ ",";
					else if (words[i+2].equals("nine")) foundTime += "09:30/"+i+ ",";
					else if (words[i+2].equals("ten")) foundTime += "10:30/"+i+ ",";
					else if (words[i+2].equals("eleven")) foundTime += "11:30/"+i+ ",";
					else if (words[i+2].equals("twelve")) foundTime += "12:30/"+i+ ",";
				}
				i++;
			}
			
		}
				
		return foundTime;
	}
		
	/* Checks for xx:xx  - in all permutations and combination */
	
	private static String getTimeByRegex () {

		Pattern p = Pattern.compile(" ([0-2])?[0-9][: ]([0-5])?[0-9]([^0-9a-zA-Z])?(pm|am)?"); //HH:mm
		String foundTime = "", match;
		sms += ","; //HACK - around the fact that I can't write a good enough RegEx

		Matcher m = p.matcher(sms);
		
		while (m.find()) {
			match=m.group().trim();			
System.out.println("Match in HH:mm = " + match);
			if (match.endsWith("pm")) {
				try {
					int time = Integer.parseInt(match.replaceAll("[^0-9]", ""));
					if (time < 12) { time = time + 12; match = time + ":00"; }
					else if (time < 119) { time = time + 120; match = time/10 + ":0" + time%10; }
					else if (time > 119 && time <= 129 ) { match = "00:0" + time%10; }
					else if (time < 1200) { time = time + 1200; match = time/100 + ":" + time%100; } //boundary case of 12 not screwed
					else { match = match.replaceAll("[^0-9:]", ""); }//1200 left as it is.
				}
				catch (NumberFormatException e) {
					continue;
				}
			}
			else if (match.endsWith("am")){
				try {
					int time = Integer.parseInt(match.replaceAll("[^0-9]", ""));
					if (time == 12) { match = "00:00"; }
					else	match = match.replaceAll("[^0-9:]", "");
					
					if (match.length() !=  5) { //It's not in hh:mm form
						
						if (match.length() == 1) //It was something like 9 am
							match = "0" + match + ":00";
						else if (match.length() == 2) //It's something like 10am
							match = match + ":00";
						else if (match.length() == 4) //It's of form 9:30 am
							match = "0" + match;
						
					}
					
				}
				catch (NumberFormatException e) {
					continue;
				}
			}
			
			if(match.length() != 5) {
				
				if (match.length() == 1)
					match = "0" + match + ":00";
				else if (match.length() == 2)
					match = match + "00";
				else if (match.length() == 4)
					match = "0" + match;
				
			}
				
			foundTime +=(match + "/c" + sms.indexOf(m.group()) + ","); //I can't return word count here, and returning indexOf everywhere else would be too time consuming. So c107 means I am returning character 107. 
		
		}
		
		p = Pattern.compile(" ([0-2])?[0-9]([^0-9a-zA-Z\\:])?(pm|am)"); //HH
		m = p.matcher(sms);
		
		while (m.find()) {
			match=m.group().trim();			
System.out.println("Match is HH = " + match);
			if (match.endsWith("pm")) {
			
				try {
					int time = Integer.parseInt(match.replaceAll("[^0-9]", ""));
					if (time < 12) { time = time + 12; match = time + ":00"; }
					else if (time < 119) { time = time + 120; match = time/10 + ":0" + time%10; }
					else if (time > 119 && time <= 129 ) { match = "00:0" + time%10; }
					else if (time < 1200) { time = time + 1200; match = time/100 + ":" + time%100; } //boundary case of 12 not screwed
					else { match = match.replaceAll("[^0-9:]", ""); }//1200 left as it is.
				}
				catch (NumberFormatException e) {
					continue;
				}
			}
			else if (match.endsWith("am")){
				try {
					int time = Integer.parseInt(match.replaceAll("[^0-9]", ""));
					if (time == 12) { match = "00:00"; }
					else	match = match.replaceAll("[^0-9:]", "");
				}
				catch (NumberFormatException e) {
					continue;
				}
			}
				
			foundTime +=(match + "/c" + sms.indexOf(m.group()) + ","); //I can't return word count here, and returning indexOf everywhere else would be too time consuming. So c107 means I am returning character 107. 
		
		}		

		return foundTime;
	}		
	
}
