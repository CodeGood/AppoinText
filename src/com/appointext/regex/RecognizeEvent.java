package com.appointext.regex;

import java.util.HashMap;
import java.util.Map;

public class RecognizeEvent {
	
	private static String[] listOfEvents = {"Anniversary", "Birthday", "Movie", "Trip", "Outing", "Marriage",
								"Date", "Dinner", "Lunch", "Breakfast", "Sleepover", "Drink", 
								"Project Submission", "Submission", "Meeting", "Flight", "Drinks", 
								/* Aparajita added */
								"Training", "Interview", "Wedding", "Party", "Cinema", "catch up", "Theatre", "Mall",
								"Funeral", "Festival", "Carnival", "Sports", "Function", "Doctor", "Test", "Exam", "Examination",
								"Shopping"
								};
	
	public static Map<String, Integer>times;
	
	static {
        times = new HashMap<String, Integer>();
        times.put("anniversary", 0);
        times.put("birthday", 0);
        times.put("movie", 120);
        times.put("trip", 90);
        times.put("outing", 90);
        times.put("marriage", 120);
        times.put("date", 120);
        times.put("dinner", 60);
        times.put("lunch", 60);
        times.put("breakfast", 60);
        times.put("sleepover", 30);
        times.put("drink", 60);
        times.put("project submission", 180);
        times.put("submission", 180);
        times.put("meeting", 30);
        times.put("flight", 160);
        times.put("drinks", 60);
        times.put("training", 45);
        times.put("interview", 60);
        times.put("wedding", 30);
        times.put("party", 30);
        times.put("cinema", 120);
        times.put("catch up", 10);
        times.put("theatre", 120);
        times.put("mall", 60);
        times.put("funeral", 30);
        times.put("festival", 30);
        times.put("carnival", 30);
        times.put("sports", 60);
        times.put("function", 30);
        times.put("doctor", 45);
        times.put("test", 120);
        times.put("exam", 120);
        times.put("examination", 120);
        times.put("shopping", 120);
    }
	
	
	public static String getEvent(String msg){
		
		String eventsList = "";
		
		for(String msgStr :  msg.split("[ ,?.!]"))
		{
			for(String eventStr : listOfEvents){
				
				if(eventStr.equalsIgnoreCase(msgStr)){
					
					eventsList += eventStr + ",";
				}
			}
		}
		
		if(eventsList.equalsIgnoreCase("")){
			
			return "";
		}
			
		return eventsList.substring(0, eventsList.length()-1);
	}

}
