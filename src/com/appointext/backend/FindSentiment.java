package com.appointext.backend;

public class FindSentiment {

	private static String[] positiveWords = {"Yes","Sure","Yeah","Yup","Okay","love to","will come","I'm in","let's go", "accept", "fine"};
	private static String[] negativeWords = {"No","Nope","Na", "can not", "I cannot","can't","sorry","will not be able","won't", "cancelled", "cancel", "not"};
	
	static String findSentiment(String msg){
		
		String val="probably";
		
		for(String msgStr : msg.split("[ ,.?!]")){
			
			for(String yesStr : positiveWords){
				
				if(msgStr.equalsIgnoreCase(yesStr)){
					
					val = "yes";
					
					return val;
					
				}
				
			}
			
			for(String noStr : negativeWords){
				
				if(msgStr.equalsIgnoreCase(noStr)){
					
					val = "no";
					
					return val;
				}
				
			}
		}
		
		
		return val;
		
	}
}
