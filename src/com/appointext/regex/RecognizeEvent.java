package com.appointext.regex;

public class RecognizeEvent {
	
	private static String[] listOfEvents = {"Anniversary", "Birthday", "Movie", "Trip", "Outing", "Marriage", "Date", "Dinner", "Lunch", "Breakfast", "Sleepover", "Drink", "Project Submission", "Submission", "Meeting", "Flight", "Drinks"};
	
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
