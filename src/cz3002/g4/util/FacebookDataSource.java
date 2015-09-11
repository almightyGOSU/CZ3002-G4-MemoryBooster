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

	public ArrayList<String> getAllProfileID() {
		ArrayList<String> profileIDs = new ArrayList<String>();

		Cursor cursor = _database.query(
				AlmightySQLiteHelper.FB_TABLE_NAME,
				new String[] {AlmightySQLiteHelper.FB_COLUMN_PROF_ID},
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			
			profileIDs.add(cursor.getString(0));
			cursor.moveToNext();
		}
		
		// Make sure to close the cursor
		cursor.close();
		return profileIDs;
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
		
		return bm;
	}
}
