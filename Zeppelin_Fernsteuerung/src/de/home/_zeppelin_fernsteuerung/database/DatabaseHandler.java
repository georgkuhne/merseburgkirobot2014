package de.home._zeppelin_fernsteuerung.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
	
	// All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "Zeppelin-Fernsteuerung";
 
    // Contacts table name
    private static final String TABLE_HANDYDATA = "handydata";
 
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_PRESSURE = "pressure";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_ALTITUDE = "altitude";
    private static final String KEY_SPEED = "speed";
    private static final String KEY_ACCURACY = "accuracy";
    private static final String KEY_ROLL = "roll";
    private static final String KEY_PITCH = "pitch";
    private static final String KEY_AZIMUTH = "azimuth";
    private static final String KEY_BATTERY = "battery";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_HANDYDATA_TABLE = "CREATE TABLE " + TABLE_HANDYDATA + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_PRESSURE + " TEXT,"
                + KEY_LATITUDE + " TEXT" + KEY_LONGITUDE+" TEXT,"+KEY_ALTITUDE+" TEXT,"+KEY_SPEED+" TEXT," +
                KEY_ACCURACY + " TEXT, "+ KEY_ROLL + " TEXT,"+ KEY_PITCH + " TEXT,"+KEY_AZIMUTH+ " TEXT,"+
                KEY_BATTERY+" TEXT"+" )";
        db.execSQL(CREATE_HANDYDATA_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HANDYDATA);
 
        // Create tables again
        onCreate(db);
		
	}
	
	public void addDATA(HandyDaten hd) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(KEY_PRESSURE, hd.getpressure()); 
	    values.put(KEY_LATITUDE, hd.getlatitude());
	    values.put(KEY_LONGITUDE, hd.getlongitude()); 
	    values.put(KEY_ALTITUDE, hd.getaltitude());
	    values.put(KEY_SPEED, hd.getspeed()); 
	    values.put(KEY_ACCURACY, hd.getaccuracy());
	    values.put(KEY_ROLL, hd.getroll()); 
	    values.put(KEY_PITCH, hd.getpitch());
	    values.put(KEY_AZIMUTH, hd.getazimuth()); 
	    values.put(KEY_BATTERY, hd.getbattery());
	    // Inserting Row
	    db.insert(TABLE_HANDYDATA, null, values);
	    db.close(); // Closing database connection
	}

	public HandyDaten getDATA(int id) {
	    SQLiteDatabase db = this.getReadableDatabase();
	 
	    Cursor cursor = db.query(TABLE_HANDYDATA, new String[] { KEY_ID,
	    		KEY_PRESSURE, KEY_LATITUDE,  KEY_LONGITUDE,KEY_ALTITUDE, KEY_SPEED,KEY_ACCURACY,KEY_ROLL,KEY_PITCH,
	    		KEY_AZIMUTH, KEY_BATTERY}, KEY_ID + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);
	    if (cursor != null)
	        cursor.moveToFirst();
	 
	    HandyDaten hdata = new HandyDaten(Integer.parseInt(cursor.getString(0)),
	            cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), 
	            cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), 
	            cursor.getString(9), cursor.getString(10), cursor.getString(11));
	    // return contact
	    return hdata;
	}
	
	public List<HandyDaten> getAllhandydata() {
	    List<HandyDaten> dataList = new ArrayList<HandyDaten>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_HANDYDATA;
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	HandyDaten allhandy = new HandyDaten();
	        	allhandy.setID(Integer.parseInt(cursor.getString(0)));
	        	allhandy.setpressure(cursor.getString(1));
	        	allhandy.setlatitude(cursor.getString(2));
	        	allhandy.setlongitude(cursor.getString(3));
	        	allhandy.setaltitude(cursor.getString(4));
	        	allhandy.setspeed(cursor.getString(5));
	        	allhandy.setaccuracy(cursor.getString(6));
	        	allhandy.setroll(cursor.getString(7));
	        	allhandy.setpitch(cursor.getString(8));
	        	allhandy.setazimuth(cursor.getString(9));
	        	allhandy.setbattery(cursor.getString(10));
	            // Adding contact to list
	            dataList.add(allhandy);
	        } while (cursor.moveToNext());
	    }
		return dataList;
	}
}
