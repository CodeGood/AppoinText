package com.appointext.regex;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class RecognizePeople {
	
	/**
	 * Rudimentary implementation of NER
	 * @param con - Context, will be required to query the Contacts database
	 * @param msg - The message from which people's names are to be extracted. Please do not mess with capitalisations before sending me the msg.
	 * @return - A Comma seperated list of names, with each name having a /location attached, in case it is required for further processing.
	 */
	
	public static String findPeople (Context con, String msg) {
		String foundNames = "";
		String[] sms = msg.replaceAll("[^a-zA-Z ]", "").split(" ");
		
		for (int i = 0; i < sms.length; i++) {
			
			String pName = sms[i];
			
			if (!Character.isUpperCase(pName.charAt(0)))
				continue; //don't look at non-upper case words!!
			
			//If it does indeed start with an Upper case
			
			//1. Check if it exists in the contacts
			String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" like'%" + pName +"%'";
			String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
			Cursor c = con.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
			        projection, selection, null, null);
			if (c.moveToFirst()) {
			    foundNames += c.getString(0) + ",";
			}
			c.close();
			
			//2. Check if it is preceeded by with
			
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
			
			//3. Check if it is succeeding by coming within in the next three words (is/are coming, will be coming, shall come, will come, will also come etc)
			
			if ((i < sms.length-2 && (sms[i+2].equalsIgnoreCase("coming") || sms[i+2].equalsIgnoreCase("come") )) || (i < sms.length-3 && (sms[i+3].equalsIgnoreCase("coming") || sms[i+3].equalsIgnoreCase("come"))) || (i < sms.length-4 && sms[i+4].equalsIgnoreCase("coming"))) {
				foundNames += pName + ",";
				i += 2; //ignore two words at least. Saves itearations
			}
			
		}
		
		return foundNames;
	}

}
