package com.lucasfreegames.castlewars.persistency;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.lucasfreegames.castlewars.manager.ResourcesManager;

public final class LevelContract {
	public LevelContract() {
	}

	/* Inner class that defines the table contents */
	public static abstract class LevelEntry implements BaseColumns {
		public static final String TABLE_NAME = "levels";
		public static final String COLUMN_NAME_LEVEL_ID = "levelid";
		public static final String COLUMN_NAME_LEVEL_PROGRESS = "progress";// completed
																			// notcompleted
		public static final String COLUMN_NAME_LEVEL_STARS = "stars";
		public static final String COLUMN_NAME_LEVEL_LOCK = "status";// Unlocked
																		// Locked

		private static final String TEXT_TYPE = " TEXT";
		private static final String COMMA_SEP = ",";
		public static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS"
				+ LevelEntry.TABLE_NAME + " (" + LevelEntry._ID
				+ " INTEGER PRIMARY KEY," + LevelEntry.COLUMN_NAME_LEVEL_ID
				+ TEXT_TYPE + COMMA_SEP + LevelEntry.COLUMN_NAME_LEVEL_PROGRESS
				+ TEXT_TYPE + COMMA_SEP + LevelEntry.COLUMN_NAME_LEVEL_STARS
				+ TEXT_TYPE + COMMA_SEP + LevelEntry.COLUMN_NAME_LEVEL_LOCK
				+ TEXT_TYPE + COMMA_SEP + " )";
		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ LevelEntry.TABLE_NAME;
	}
	
    public static void initLevelsDatabase() {
    	LevelHelper dbHelper = new LevelHelper(ResourcesManager.getInstance().activity);
    	SQLiteDatabase db = dbHelper.getWritableDatabase();

    	for (int i=1; i>=ResourcesManager.getInstance().getNumberOfLevels();i++ )
    	{// Create a new map of values, where column names are the keys
    	ContentValues values = new ContentValues();
    	values.put(LevelEntry.COLUMN_NAME_LEVEL_ID, i+"");
    	values.put(LevelEntry.COLUMN_NAME_LEVEL_PROGRESS, "none");
    	values.put(LevelEntry.COLUMN_NAME_LEVEL_STARS, "0");
    	values.put(LevelEntry.COLUMN_NAME_LEVEL_LOCK, "locked");
    	// Insert the new row, returning the primary key value of the new row, not captured as of now
    	db.insert(
    			LevelEntry.TABLE_NAME,
    			null,
    	         values);
    	}
	}
	public static  Cursor loadLevelsOnCursor() {
		LevelHelper dbHelper = new LevelHelper(ResourcesManager.getInstance().activity);
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = {
				LevelEntry.COLUMN_NAME_LEVEL_ID,
				LevelEntry.COLUMN_NAME_LEVEL_PROGRESS,
				LevelEntry.COLUMN_NAME_LEVEL_STARS,
				LevelEntry.COLUMN_NAME_LEVEL_LOCK,
		    };

		// How you want the results sorted in the resulting Cursor
		String sortOrder =
				LevelEntry.COLUMN_NAME_LEVEL_ID + " ASC";

		Cursor c = db.query(
				LevelEntry.TABLE_NAME,  // The table to query
		    projection,                               // The columns to return
		    null,                                // The columns for the WHERE clause
		    null,                            // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    sortOrder                                 // The sort order
		    );
		
		return c;
		
	}
}