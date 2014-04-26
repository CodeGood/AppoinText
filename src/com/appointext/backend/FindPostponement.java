package com.appointext.backend;

public class FindPostponement {
	
	private static String[] postponeWords = {"Instead","Changed","Postponed"};
	
	static boolean findPostponement(String msg){
		
		boolean val=false;
		
		for(String msgStr : msg.split("[ ,.?!]")){
			
			for(String yesStr : postponeWords){
				
				if(msgStr.equalsIgnoreCase(yesStr)){
					
					val = true;
					
					return val;
					
				}
				
			}
			
		}
		
		return val;
	}
}
