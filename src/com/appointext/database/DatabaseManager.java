package com.appointext.database;
 
import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
 
public class DatabaseManager {

       //private static final String TAG = "AppoinText";       
       private static final String DATABASE_NAME = "AppoinTextDB";    
       private static final int DATABASE_VERSION = 1;

       private static final String DATABASE_CREATE_SETTINGSTABLE =
    		   			"create table " +
						"settingsTable" +
						" (" +
						"name" + " text primary key not null," +
						"value" + " text" +
						");";

       private static final String DATABASE_CREATE_SETREMINDERS =
    		   			"create table " +
						"setReminders" +
						" (" +
						"eventId" + " integer primary key not null," +
						"isComplete" + " integer," +
						"isGroup" + " integer," +
						"trs" + " text," +
						"extractedInfo" + " text" +
						");";
       
       private static final String DATABASE_CREATE_PENDINGREMINDERS =
    		   			"create table " +
						"pendingReminders" +
						" (" +
						"eventId" + " integer primary key autoincrement not null," +
						"senderNumber" + " text," +
						"receiverNumber" + " text," +
						"isConfirmed" + " integer," +
						"attendees" + " text," +
						"what" + " text," +
						"whenIsIt" + " text," +
						"whereIsIt" + " text," +
						"lastAccessed" + " text" +
						");";

       
       public DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
       public Calendar cal = Calendar.getInstance();
  		
       private Context context;
       private DatabaseHelper DBHelper;
       private SQLiteDatabase db;

       public DatabaseManager(Context ctx) 
       {
           this.context = ctx;
           DBHelper = new DatabaseHelper(context);
       }

       private static class DatabaseHelper extends SQLiteOpenHelper 
       {
           DatabaseHelper(Context context) 
           {
               super(context, DATABASE_NAME, null, DATABASE_VERSION);
           }

           @Override
           public void onCreate(SQLiteDatabase db) 
           { 
               db.execSQL(DATABASE_CREATE_SETTINGSTABLE);
               db.execSQL(DATABASE_CREATE_SETREMINDERS);
               db.execSQL(DATABASE_CREATE_PENDINGREMINDERS);
           }

           @Override
           public void onUpgrade(SQLiteDatabase db, int oldVersion, 
                                 int newVersion) 
           {
               Log.w("Upgrading", "Upgrading database from version " + oldVersion 
                     + " to "
                     + newVersion + ", which will destroy all old data");
               db.execSQL("DROP TABLE IF EXISTS settingsTable");
               db.execSQL("DROP TABLE IF EXISTS pendingReminders");
               db.execSQL("DROP TABLE IF EXISTS setReminders");

               onCreate(db);
           }
       }


       public DatabaseManager open() throws SQLException 
       {
           db = DBHelper.getWritableDatabase();
           return this;
       }

       //---closes the database---    
       public void close() 
       {
           DBHelper.close();
       }


       //For set reminders table
       public void addRow(String dbName,int evntId, int isComp, int isGrp, String trs, String extractedData)
       {
   		// this is a key value pair holder used by android's SQLite functions

	   		ContentValues values = new ContentValues();
	   		values.put("eventId", evntId);
	   		values.put("isComplete", isComp);
	   		values.put("isGroup", isGrp);
	   		values.put("trs", trs);
	   		values.put("extractedInfo", extractedData);
	    
	   		// ask the database object to insert the new data 
	   		try{
	   			db.insert(dbName, null, values);
	   			Log.i("setReminders","I am in addrow");
	   		}
	   		catch(Exception e)
	   		{
	   			Log.e("DB ERROR", e.toString());
	   			e.printStackTrace();
	   		}
   		}

	       //this is for pendingReminders
	   public void addRow(String dbName, String sender, String receiver, int isConfirmed, String attendees, String what, String whenIsIt, String whereIsIt)
	   {
	   			// this is a key value pair holder used by android's SQLite functions
		  
		   		String timeStamp = dateFormat.format(cal.getTime());
	
	   			ContentValues values = new ContentValues();
	   			//values.put("eventId", evntId);
	   			values.put("senderNumber", sender);
	   			values.put("receiverNumber", receiver);
	   			values.put("isConfirmed", isConfirmed);
	   			values.put("attendees", attendees);
	   			values.put("what", what);
	   			values.put("whenIsIt", whenIsIt);
	   			values.put("whereIsIt", whereIsIt);
	   			values.put("lastAccessed", timeStamp);
	   	 
	   			// ask the database object to insert the new data 
	   			try{
	   				db.insert(dbName, null, values);
	   				Log.i("pendingReminders","I am in addrow");
	   			}
	   			catch(Exception e)
	   			{
	   				Log.e("DB ERROR", e.toString());
	   				e.printStackTrace();
	   			}
	   }
       
       //this is settingsTable
	    public void addRow(String dbName, String name, String value)
	    {
	   			// this is a key value pair holder used by android's SQLite functions
	
	   			ContentValues values = new ContentValues();
	   			//values.put("settingId", settingId);
	   			values.put("name", name);
	   			values.put("value", value);
	   		
	   			// ask the database object to insert the new data 
	   			try{
	   				db.insert(dbName, null, values);
	   				Log.i("setting","I am in addrow");
	   			}
	   			catch(Exception e)
	   			{
	   				Log.e("DB ERROR", e.toString());
	   				e.printStackTrace();
	   			}
	    }
       
	    public int getId(String query){
	    	
	   		Cursor cursor;
	   		int rowId = 0;
	  		
	   		cursor = db.rawQuery(query, null);
	   		   		
	   		if(cursor!=null){
	  			cursor.moveToFirst();
	   			rowId= cursor.getInt(0);
	   		}
	   		
	   		return rowId;
	    	
	    }
	    
	    public ArrayList<ArrayList<Object>> getMultipleSetReminders(String query){
	    	
	    	ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();
	    	
	    	Cursor cursor;	    	
	    	cursor = db.rawQuery(query, null);    	
	    	cursor.moveToFirst();
	    	
	    	if (!cursor.isAfterLast())
			{
				do
				{
					ArrayList<Object> dataList = new ArrayList<Object>();
 
					dataList.add(cursor.getInt(0));
					dataList.add(cursor.getInt(1));
					dataList.add(cursor.getInt(2));
					dataList.add(cursor.getString(3));
					dataList.add(cursor.getString(4));
 
					dataArrays.add(dataList);
				}
				// move the cursor's pointer up one position.
				while (cursor.moveToNext());
			}
	    	
	    	cursor.close();
	    	
	    	return dataArrays;
	    }
	    
	    public ArrayList<ArrayList<Object>> getMultiplePendingReminders(String query){
	    	
	    	ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();
	    	
	    	Cursor cursor;	    	
	    	cursor = db.rawQuery(query, null);    	
	    	cursor.moveToFirst();
	    	
	    	if (!cursor.isAfterLast())
			{
				do
				{
					ArrayList<Object> dataList = new ArrayList<Object>();
 
					dataList.add(cursor.getInt(0)); //eventId
					dataList.add(cursor.getString(1)); //senderNumber
					dataList.add(cursor.getString(2)); //recieverNumber
					dataList.add(cursor.getInt(3));	//isConfirmed
					dataList.add(cursor.getString(4)); //attendees
					dataList.add(cursor.getString(5)); //what(event)
					dataList.add(cursor.getString(6)); //when 
					dataList.add(cursor.getString(7)); //where
					dataList.add(cursor.getString(8)); //lastAcceessed
 
					dataArrays.add(dataList);
				}
				// move the cursor's pointer up one position.
				while (cursor.moveToNext());
			}
	    	
	    	cursor.close();
	    	return dataArrays;
	    }
	    
	   	public ArrayList<Object> getRowAsArray(String dbName, int rowID)
	   	{
	   		// create an array list to store data from the database row.
	   		// I would recommend creating a JavaBean compliant object 
	   		// to store this data instead.  That way you can ensure
	   		// data types are correct.
	   		ArrayList<Object> rowArray = new ArrayList<Object>();
	   		Cursor cursor;
	    
	   		try
	   		{
	   			// this is a database call that creates a "cursor" object.
	   			// the cursor object store the information collected from the
	   			// database and is used to iterate through the data.
	   	
	   			if(dbName.equalsIgnoreCase("setReminders")){
	   				cursor = db.query
	   				(
	   						dbName,
	   						new String[] { "eventId", "isComplete", "isGroup", "trs", "extractedInfo" },
	   						"eventId" + "=" + rowID,
	   						null, null, null, null, null
	   				);
	   				
	   				// move the pointer to position zero in the cursor.
	   				cursor.moveToFirst();
	   	 
	   				// if there is data available after the cursor's pointer, add
	   				// it to the ArrayList that will be returned by the method.
	   				if (!cursor.isAfterLast())
	   				{
	   					do
	   					{
	   						rowArray.add(cursor.getInt(0));
	   						rowArray.add(cursor.getInt(1));
	   						rowArray.add(cursor.getInt(2));
	   						rowArray.add(cursor.getString(3));
	   						rowArray.add(cursor.getString(4));
	   					}
	   					while (cursor.moveToNext());
	   				}
	   	 
	   				// let java know that you are through with the cursor.
	   				cursor.close();
	   				
	   				Log.i("setReminders","I am in getrow");
	
	   				
	   			}
	   			
	   			if(dbName.equalsIgnoreCase("pendingReminders")){
	   				cursor = db.query
	   				(
	   						dbName,
	   						new String[] { "eventId", "senderNumber", "receiverNumber", "isConfirmed", "attendees", "what", "whenIsIt", "whereIsIt", "lastAccessed" },
	   						"eventId" + "=" + rowID,
	   						null, null, null, null, null
	   				);
	   				
	   				// move the pointer to position zero in the cursor.
	   				cursor.moveToFirst();
	   	 
	   				// if there is data available after the cursor's pointer, add
	   				// it to the ArrayList that will be returned by the method.
	   				if (!cursor.isAfterLast())
	   				{
	   					do
	   					{
	   						rowArray.add(cursor.getInt(0));
	   						rowArray.add(cursor.getString(1));
	   						rowArray.add(cursor.getString(2));
	   						rowArray.add(cursor.getInt(3));
	   						rowArray.add(cursor.getString(4));
	   						rowArray.add(cursor.getString(5));
	   						rowArray.add(cursor.getString(6));
	   						rowArray.add(cursor.getString(7));
	   						rowArray.add(cursor.getString(8));					}
	   					while (cursor.moveToNext());
	   				}
	   	 
	   				// let java know that you are through with the cursor.
	   				cursor.close();
	   				
	   				Log.i("pendingReminders","I am in getrow");	
	   			}
	   			
	   						
	   		}
	   		catch (SQLException e) 
	   		{
	   			Log.e("DB ERROR", e.toString());
	   			e.printStackTrace();
	   		}
	   
	   		// return the ArrayList containing the given row from the database.
	   		return rowArray;
	   	}
	   	
	   	
	   	public ArrayList<Object> getRowAsArray(String dbName, String name){
	   		
	   		ArrayList<Object> rowArray = new ArrayList<Object>();
	   		Cursor cursor;
	   		
	   		if(dbName.equalsIgnoreCase("settingsTable")){
   				cursor = db.query
   				(
   						dbName,
   						new String[] { "name", "value" },
   						"name" + "=" + "'" + name + "'",
   						null, null, null, null, null
   				);
   				
   				// move the pointer to position zero in the cursor.
   				cursor.moveToFirst();
   	 
   				// if there is data available after the cursor's pointer, add
   				// it to the ArrayList that will be returned by the method.
   				if (!cursor.isAfterLast())
   				{
   					do
   					{
   						rowArray.add(cursor.getString(0));
   						rowArray.add(cursor.getString(1));

   					}
   					while (cursor.moveToNext());
   				}
   	 
   				// let java know that you are through with the cursor.
   				cursor.close();
   				
   				Log.i("setting","I am in getrow");
   			}   
	   		
	   		return rowArray;
	   	}
   	
   	
	   	public void deleteRow(String dbName, int rowID)
		{
			// ask the database manager to delete the row of given id
			try {
				if(dbName.equalsIgnoreCase("setReminders")){				
					db.delete(dbName, "eventId" + "=" + rowID, null);
					Log.i("setReminders","I am in deleterow");
				}
				
				if(dbName.equalsIgnoreCase("pendingReminders")){				
					db.delete(dbName, "eventId" + "=" + rowID, null);
					Log.i("pendingReminders","I am in deleterow");
				}
			
			}
			catch (Exception e)
			{
				Log.e("DB ERROR", e.toString());
				e.printStackTrace();
			}
		}
	   	
	   	public void deleteRow(String dbName, String name){
	   		
	   		if(dbName.equalsIgnoreCase("settingsTable")){				
				db.delete(dbName, "name" + "=" + "'" + name + "'", null);
				Log.i("sharedPreferences","I am in deleterow");
			}
	   		
	   	}
   	
	   	//for the setReminders table
	   	public void updateRow(String dbName, int evntId, int isComp, int isGrp, String trs, String extractedData)
		{
			// this is a key value pair holder used by android's SQLite functions
			ContentValues values = new ContentValues();
			values.put("isComplete", isComp);
			values.put("isGroup", isGrp);
			values.put("trs", trs);
			values.put("extractedInfo", extractedData);
	 
			// ask the database object to update the database row of given rowID
			try {
					db.update(dbName, values, "eventId" + "=" + evntId, null);
					Log.i("setReminders","I am in updaterow");
				}
			catch (Exception e)
			{
				Log.e("DB Error", e.toString());
				e.printStackTrace();
			}
		}
	
		// this is for the pendingReminders table
		public void updateRow(String dbName, int evntId, String sender, String receiver, int isConfirmed, String attendees, String what, String whenIsIt, String whereIsIt)
		{
					// this is a key value pair holder used by android's SQLite functions
	
					String timeStamp = dateFormat.format(cal.getTime());
					
					ContentValues values = new ContentValues();
					values.put("senderNumber", sender);
					values.put("receiverNumber", receiver);
					values.put("isConfirmed", isConfirmed);
					values.put("attendees", attendees);
					values.put("what", what);
					values.put("whenIsIt", whenIsIt);
					values.put("whereIsIt", whereIsIt);
					values.put("lastAccessed", timeStamp);
			 
					// ask the database object to insert the new data 
					try{
						db.update(dbName, values, "eventId" + "=" + evntId, null);
						Log.i("pendingReminders","I am in updaterow");
					}
					catch(Exception e)
					{
						Log.e("DB ERROR", e.toString());
						e.printStackTrace();
					}
		}
		
		//this is for the setReminders table
		public void updateRow(String dbName, String name, String value)
		{
				// this is a key value pair holder used by android's SQLite functions
				ContentValues values = new ContentValues();
				values.put("value", value);
		 
				// ask the database object to update the database row of given rowID
				try {
						db.update(dbName, values, "name" + "=" + "'" + name + "'", null);
						Log.i("sharedPreferences","I am in updaterow");
					}
				catch (Exception e)
				{
					Log.e("DB Error", e.toString());
					e.printStackTrace();
				}
		}
}