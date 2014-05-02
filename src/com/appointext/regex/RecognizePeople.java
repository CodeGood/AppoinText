package com.appointext.regex;

import android.util.Log;

public class RecognizePeople {
	
	/**
	 * Rudimentary implementation of NER.
	 * Just do not say someone "will also be coming". "is also coming", "will be coming", "will come", "will also come" are all okay!
	 * "will be coming too", "will come as well" everything :P As long as name and coming are not more than three words apart :D
	 * @param con - Context, will be required to query the Contacts database
	 * @param msg - The message from which people's names are to be extracted. Please do not mess with capitalisations before sending me the msg.
	 * @return - A Comma seperated list of names, with each name having a /location attached, in case it is required for further processing.
	 */

	public static String findPeople (String msg) {
		
		if (msg == null || msg.length() == 0) {
			return "";
		}
		
		String[] sentences = msg.split("[.?!]");
		String foundNames = "";
		
		for (String sentence : sentences) {
			
			if (sentence.length() == 0)
				continue;
			
			//0. Check if the sentence is negated. That is whether it contains the word not or the phrase n't
			
			if (sentence.toLowerCase().contains(" not ") || sentence.toLowerCase().contains("n't"))
				continue;
			
			String[] sms = sentence.replaceAll("[^a-zA-Z ]", "").split(" ");
			
			for (int i = 0; i < sms.length; i++) {
				
				if (sms[i].length() == 0)
					continue;
				
				String pName = sms[i];
	
				//If it does indeed start with an Upper case
				if (pName.length() == 0 || !Character.isUpperCase(pName.charAt(0)))
					continue; //don't look at non-upper case words!!
				
				//1. Check if it is preceeded by with
				
				if (i != 0 && sms[i-1].equalsIgnoreCase("with")) {
					foundNames += sms[i] + ",";
					if (i < sms.length-1 && sms[i+1].equalsIgnoreCase("and")) { //more people to come
						i+= 2; //i was the name, so +2 puts it at the word after and
						do {
							if (i < sms.length && Character.isUpperCase(sms[i].charAt(0)))
								foundNames += sms[i] + ",";
							i++;
						}while(i < sms.length && Character.isUpperCase(sms[i].charAt(0)));
					}
					
					continue;
				}
				
				//2. Check if it is succeeding by coming within in the next three words (is/are coming, will be coming, shall come, will come, will also come etc)
				
				if ((i < sms.length-2 && (sms[i+2].equalsIgnoreCase("coming") || sms[i+2].equalsIgnoreCase("come") )) || (i < sms.length-3 && (sms[i+3].equalsIgnoreCase("coming") || sms[i+3].equalsIgnoreCase("come")))) { // || (i < sms.length-4 && sms[i+4].equalsIgnoreCase("coming") && !sms[i+1].equalsIgnoreCase("and"))) {
					//System.out.println("Coming detected for " + pName);				
					foundNames += pName + ",";
					int prevI = i; 
					if (i > 0 && sms[i-1].equalsIgnoreCase("and")) { //If it is a list of anded stuff, then just go on as long as you find capitalised stuff
						while (true) {
							i--;
							if (i < 0)
								break;
							if (!sms[i].equals("and")) {
								if (sms[i].length() > 0 && !Character.isUpperCase(sms[i].charAt(0)))
									break; //except names, I want only ands
								else
									foundNames += sms[i] + ",";
							}
						}
					}
					i = prevI + 2; //ignore two words at least. Saves itearations
				}
							
			}
			
		}
Log.i("AppoinText People", "Found names " + foundNames);

		return purgeTroubleWords(foundNames);
	}

	public static String findPeopleNegative (String msg) { //FIXME - Recepie for disaster
		
		if (msg == null || msg.length() == 0) {
			return "";
		}
		
		String[] sentences = msg.split("[.?!]");
		String foundNames = "";
		
		for (String sentence : sentences) {
			
			if (sentence.length() == 0)
				continue;
			
			String[] sms = sentence.replaceAll("[^a-zA-Z ]", "").split(" ");
			
			for (int i = 0; i < sms.length; i++) {
				
				if (sms[i].length() == 0)
					continue;
				
				String pName = sms[i];
	
				//If it does indeed start with an Upper case
				if (pName.length() == 0 || !Character.isUpperCase(pName.charAt(0)))
					continue; //don't look at non-upper case words!!
				
				//1. Check if it is preceeded by with
				
				if (i != 0 && sms[i-1].equalsIgnoreCase("with")) {
					foundNames += sms[i] + ",";
					if (i < sms.length-1 && sms[i+1].equalsIgnoreCase("and")) { //more people to come
						i+= 2; //i was the name, so +2 puts it at the word after and
						do {
							if (i < sms.length && Character.isUpperCase(sms[i].charAt(0)))
								foundNames += sms[i] + ",";
							i++;
						}while(i < sms.length && Character.isUpperCase(sms[i].charAt(0)));
					}
					
					continue;
				}
				
				//2. Check if it is succeeding by coming within in the next three words (is/are coming, will be coming, shall come, will come, will also come etc)
				
				if ((i < sms.length-2 && (sms[i+2].equalsIgnoreCase("coming") || sms[i+2].equalsIgnoreCase("come") )) || (i < sms.length-3 && (sms[i+3].equalsIgnoreCase("coming") || sms[i+3].equalsIgnoreCase("come")))) { // || (i < sms.length-4 && sms[i+4].equalsIgnoreCase("coming") && !sms[i+1].equalsIgnoreCase("and"))) {
					//System.out.println("Coming detected for " + pName);				
					foundNames += pName + ",";
					int prevI = i; 
					if (i > 0 && sms[i-1].equalsIgnoreCase("and")) { //If it is a list of anded stuff, then just go on as long as you find capitalised stuff
						while (true) {
							i--;
							if (i < 0)
								break;
							if (!sms[i].equals("and")) {
								if (sms[i].length() > 0 && !Character.isUpperCase(sms[i].charAt(0)))
									break; //except names, I want only ands
								else
									foundNames += sms[i] + ",";
							}
						}
					}
					i = prevI + 2; //ignore two words at least. Saves itearations
				}
							
			}
			
		}
Log.i("AppoinText People", "Found names " + foundNames);

		return purgeTroubleWords(foundNames);
	}

	
	private static String purgeTroubleWords(String foundNames) {
		
		if (foundNames == null || foundNames.length() == 0)
			return foundNames;
		
		String[] names = foundNames.split(",");
		String returnVal = "";
		for (String name : names)
			if (!(name.equals("Can") || name.equals("However") || name.equals("You") || name.equals("I") || name.equals("Will") || name.equals("Have") ))
				returnVal += name + ",";
		
		//if (returnVal.length() > 0)
			return returnVal;//.substring(0, returnVal.length()-1);
		//else
			//return "";
	}
}
