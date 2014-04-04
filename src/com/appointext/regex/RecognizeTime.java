package com.appointext.regex;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RecognizeTime {
	
	protected static String sms;
	
	public static void main(String[] args) {
		System.out.println(findTime(args[0]));
	}
	
	public static String findTime(String msg) {
		
		sms = msg.toLowerCase(Locale.US).trim().replaceAll("(\\w+)\\p{Punct}(\\s|$)", "$1$2");
		String foundTime = "";
		foundTime += findOClock();
		foundTime += getTimeByRegex();
		
		return foundTime;
		
	}
	
	/* Checks for o'clock and half past */
	
	private static String findOClock () {
	
		String[] words = sms.split(" ");
		String foundTime = "";
		
		for (int i = 0; i < words.length; i++) {

		if (i != 0 && (words[i].equals("o'clock") || words[i].equals("oclock"))) {
				if ((words[i-1].replaceAll("[^0-9]", "")).equals("") == false) { //It's in numerals

					foundTime += words[i-1].replaceAll("[^0-9]", "") + ":00/" + i;
					foundTime += ",";
				}
				else { //convert

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
	
	/* TODO:: Checks for morning, evening, afternoon */
	
	/* Checks for xx:xx  - in all permutations and combination */
	
	private static String getTimeByRegex () {

		Pattern p = Pattern.compile(" ([0-2])?[0-9][: ]([0-5])?[0-9]([^0-9a-zA-Z])?(pm|am)?"); //HH:mm
		String foundTime = "", match;
		sms += ","; //HACK - around the fact that I can't write a good enough RegEx

		Matcher m = p.matcher(sms);
		
		while (m.find()) {
			match=m.group().trim();			
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
