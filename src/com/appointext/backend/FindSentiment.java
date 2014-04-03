package com.appointext.backend;

public class FindSentiment {

	private static String[] positiveWords = {};
	private static String[] negativeWords = {};
	
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
