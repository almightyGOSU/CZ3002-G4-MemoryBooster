package cz3002.g4.util;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

public class GeneralDataSource {

	// Database fields
	private SQLiteDatabase _database = null;
	private AlmightySQLiteHelper _dbHelper = null;

	public GeneralDataSource(Context context) {
		
		_dbHelper = new AlmightySQLiteHelper(context);
	}

	public void open() throws SQLException {
		
		_database = _dbHelper.getWritableDatabase();
	}

	public void close() {
		
		_dbHelper.close();
	}

	public boolean insertGenData(String answer, String categoryTag, byte[] image) {
		
		if(_database == null)
			return false;
		
		ContentValues values = new ContentValues();
		values.put(AlmightySQLiteHelper.GEN_COLUMN_ANSWER, answer);
		values.put(AlmightySQLiteHelper.GEN_COLUMN_TAG, categoryTag);
		values.put(AlmightySQLiteHelper.GEN_COLUMN_IMAGE, image);
		
		long insertID = _database.insert(
				AlmightySQLiteHelper.GEN_TABLE_NAME, null, values);
		
		return (insertID != -1);
	}

	/** Retrieve all (answer, tag) pairs stored in database **/
	public ArrayList<AnswerTagPair> getAllAnswerTag() {
		
		ArrayList<AnswerTagPair> answerTagPairs = new ArrayList<AnswerTagPair>();

		Cursor cursor = _database.query(
				AlmightySQLiteHelper.GEN_TABLE_NAME,
				new String[] {AlmightySQLiteHelper.GEN_COLUMN_ANSWER,
						AlmightySQLiteHelper.GEN_COLUMN_TAG},
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			
			answerTagPairs.add(new AnswerTagPair(
					cursor.getString(0), cursor.getString(1)));
			cursor.moveToNext();
		}
		
		// Make sure to close the cursor
		cursor.close();
		return answerTagPairs;
	}
	
	public Bitmap getImage(String answer, String categoryTag) {
		
		Bitmap bm = null;
		
		Cursor cursor = _database.query(
				AlmightySQLiteHelper.GEN_TABLE_NAME,
				new String[] {AlmightySQLiteHelper.GEN_COLUMN_IMAGE},
				AlmightySQLiteHelper.GEN_COLUMN_ANSWER + " = '" + answer +
				"' AND " + AlmightySQLiteHelper.GEN_COLUMN_TAG + " = '" +
				categoryTag + "'",
				null, null, null, null);
		
		cursor.moveToFirst();
		bm = BitmapUtil.getImage(cursor.getBlob(0));
		
		// Make sure to close the cursor
		cursor.close();
		return bm;
	}
	
	/** Gets a random category tag */
	public String getRandomTag() {
		
		String rawQueryStr = "SELECT " + AlmightySQLiteHelper.GEN_COLUMN_TAG
				+ " FROM " + AlmightySQLiteHelper.GEN_TABLE_NAME
				+ " ORDER BY RANDOM() LIMIT 1";
		
		Cursor cursor = _database.rawQuery(rawQueryStr, new String []{});
		cursor.moveToFirst();
		
		String randomTag = cursor.getString(0);
		
		// Make sure to close the cursor
		cursor.close();
		return randomTag;
	}
	
	/** Get random list of answers with the given category tag **/
	public ArrayList<String> getRandomAnswers(String categoryTag, int count) {
		
		ArrayList<String> answersList = new ArrayList<String>();
		
		String rawQueryStr = "SELECT " + AlmightySQLiteHelper.GEN_COLUMN_ANSWER
				+ " FROM " + AlmightySQLiteHelper.GEN_TABLE_NAME
				+ " WHERE " + AlmightySQLiteHelper.GEN_COLUMN_TAG
				+ " = '" + categoryTag
				+ "' ORDER BY RANDOM() LIMIT "
				+ String.valueOf(count);
		
		Cursor cursor = _database.rawQuery(rawQueryStr, new String []{});
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			
			answersList.add(cursor.getString(0));
			cursor.moveToNext();
		}
		
		// Make sure to close the cursor
		cursor.close();
		return answersList;
	}
	
	/** Get random list of name options with the given category tag */
	public ArrayList<String> getRandomNameOptions(String answer,
			String categoryTag, int count) {
		
		ArrayList<String> profileNameOptionsList = new ArrayList<String>();
		
		String rawQueryStr = "SELECT " + AlmightySQLiteHelper.GEN_COLUMN_ANSWER
				+ " FROM " + AlmightySQLiteHelper.GEN_TABLE_NAME
				+ " WHERE " + AlmightySQLiteHelper.GEN_COLUMN_ANSWER
				+ " <> '" + answer + "' AND " + AlmightySQLiteHelper.GEN_COLUMN_TAG
				+ " = '" + categoryTag
				+ "' ORDER BY RANDOM() LIMIT "
				+ String.valueOf(count);
		
		Cursor cursor = _database.rawQuery(rawQueryStr, new String []{});
		
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
