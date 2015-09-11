package cz3002.g4.util;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

public class FacebookDataSource {

	// Database fields
	private SQLiteDatabase _database = null;
	private AlmightySQLiteHelper _dbHelper = null;
	
	@SuppressWarnings("unused")
	private static final String[] ALL_FB_COLUMNS = {
			AlmightySQLiteHelper.FB_COLUMN_ID,
			AlmightySQLiteHelper.FB_COLUMN_PROF_ID,
			AlmightySQLiteHelper.FB_COLUMN_PROF_NAME,
			AlmightySQLiteHelper.FB_COLUMN_PROF_PIC};

	public FacebookDataSource(Context context) {
		
		_dbHelper = new AlmightySQLiteHelper(context);
	}

	public void open() throws SQLException {
		
		_database = _dbHelper.getWritableDatabase();
	}

	public void close() {
		
		_dbHelper.close();
	}

	public boolean insertFbFriend(String profileID, String profileName, byte[] profiePic) {
		
		if(_database == null)
			return false;
		
		ContentValues values = new ContentValues();
		values.put(AlmightySQLiteHelper.FB_COLUMN_PROF_ID, profileID);
		values.put(AlmightySQLiteHelper.FB_COLUMN_PROF_NAME, profileName);
		values.put(AlmightySQLiteHelper.FB_COLUMN_PROF_PIC, profiePic);
		
		long insertID = _database.insert(
				AlmightySQLiteHelper.FB_TABLE_NAME, null, values);
		
		return (insertID != -1);
	}

	/** Retrieve all profile IDs stored in database **/
	public ArrayList<String> getAllProfileID() {
		
		ArrayList<String> profileIDList = new ArrayList<String>();

		Cursor cursor = _database.query(
				AlmightySQLiteHelper.FB_TABLE_NAME,
				new String[] {AlmightySQLiteHelper.FB_COLUMN_PROF_ID},
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			
			profileIDList.add(cursor.getString(0));
			cursor.moveToNext();
		}
		
		// Make sure to close the cursor
		cursor.close();
		return profileIDList;
	}
	
	public ArrayList<String> getAllProfileName() {
		
		ArrayList<String> profileNameList = new ArrayList<String>();
		
		Cursor cursor = _database.query(
				AlmightySQLiteHelper.FB_TABLE_NAME,
				new String[] {AlmightySQLiteHelper.FB_COLUMN_PROF_NAME},
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			
			profileNameList.add(cursor.getString(0));
			cursor.moveToNext();
		}
		
		// Make sure to close the cursor
		cursor.close();
		return profileNameList;
	}
	
	public String getProfileName(String profileID) {
		
		String profileName;
		
		Cursor cursor = _database.query(
				AlmightySQLiteHelper.FB_TABLE_NAME,
				new String[] {AlmightySQLiteHelper.FB_COLUMN_PROF_NAME},
				AlmightySQLiteHelper.FB_COLUMN_PROF_ID + " = " + profileID,
				null, null, null, null);
		
		cursor.moveToFirst();
		profileName = cursor.getString(0);
		
		// Make sure to close the cursor
		cursor.close();
		return profileName;
	}
	
	public Bitmap getProfilePic(String profileID) {
		
		Bitmap bm = null;
		
		Cursor cursor = _database.query(
				AlmightySQLiteHelper.FB_TABLE_NAME,
				new String[] {AlmightySQLiteHelper.FB_COLUMN_PROF_PIC},
				AlmightySQLiteHelper.FB_COLUMN_PROF_ID + " = " + profileID,
				null, null, null, null);
		
		cursor.moveToFirst();
		bm = BitmapUtil.getImage(cursor.getBlob(0));
		
		// Make sure to close the cursor
		cursor.close();
		return bm;
	}
	
	/** Get random list of profile IDs **/
	public ArrayList<String> getRandomProfileIDs(int count) {
		
		ArrayList<String> profileIDList = new ArrayList<String>();
		
		String rawQueryStr = "SELECT " + AlmightySQLiteHelper.FB_COLUMN_PROF_NAME
				+ " FROM " + AlmightySQLiteHelper.FB_TABLE_NAME
				+ " ORDER BY RANDOM() LIMIT "
				+ String.valueOf(count);
		
		Cursor cursor = _database.rawQuery(rawQueryStr, new String []{});
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			
			profileIDList.add(cursor.getString(0));
			cursor.moveToNext();
		}
		
		// Make sure to close the cursor
		cursor.close();
		return profileIDList;
	}
	
	public ArrayList<String> getRandomNameOptions(String profileID, int count) {
		
		ArrayList<String> profileNameOptionsList = new ArrayList<String>();
		
		String rawQueryStr = "SELECT ? FROM ? WHERE ? <> ? "
				+ "ORDER BY RANDOM() LIMIT ?;";
		Cursor cursor = _database.rawQuery(rawQueryStr, new String[] {
				AlmightySQLiteHelper.FB_COLUMN_PROF_NAME,
				AlmightySQLiteHelper.FB_TABLE_NAME,
				AlmightySQLiteHelper.FB_COLUMN_PROF_ID,
				profileID, String.valueOf(count) });
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			
			profileNameOptionsList.add(cursor.getString(0));
			cursor.moveToNext();
		}
		
		// Make sure to close the cursor
		cursor.close();
		return profileNameOptionsList;
	}
}
