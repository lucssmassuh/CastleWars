package com.lucasfreegames.castlewars.persistency;

import org.andengine.util.debug.Debug;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.lucasfreegames.castlewars.manager.ResourcesManager;

public final class LevelContract {
		public static final int LEVEL_COMPLETED_3STARS=0,LEVEL_COMPLETED_2STARS=1, LEVEL_COMPLETED_1STAR=2, LEVEL_UNLOCKED=3, LEVEL_LOCKED=4 ;  

	public LevelContract() {
	}

	/* Inner class that defines the table contents */
	public static abstract class LevelEntry implements BaseColumns {
		
		public static final String TABLE_NAME = "levels";
		public static final String COLUMN_NAME_LEVEL_ID = "levelid";
		public static final String COLUMN_NAME_LEVEL_PROGRESS = "progress";
			public static final String VALUE_LEVEL_PROGRESS_COMPLETED = "completed";
			public static final String VALUE_LEVEL_PROGRESS_NOTCOMPLETED = "notcompleted";
		public static final String COLUMN_NAME_LEVEL_STARS = "stars";
		public static final String COLUMN_NAME_LEVEL_LOCK = "status";
		public static final String VALUE_LEVEL_LOCK_LOCKED = "locked";
		public static final String VALUE_LEVEL_LOCK_UNLOCKED = "unlocked";

		private static final String TEXT_TYPE = " TEXT";
		private static final String COMMA_SEP = ",";
		public static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS "
				+ LevelEntry.TABLE_NAME + " (" + LevelEntry._ID
				+ " INTEGER PRIMARY KEY," + LevelEntry.COLUMN_NAME_LEVEL_ID
				+ TEXT_TYPE + COMMA_SEP + LevelEntry.COLUMN_NAME_LEVEL_PROGRESS
				+ TEXT_TYPE + COMMA_SEP + LevelEntry.COLUMN_NAME_LEVEL_STARS
				+ TEXT_TYPE + COMMA_SEP + LevelEntry.COLUMN_NAME_LEVEL_LOCK + TEXT_TYPE
				+ " )";
		public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
				+ LevelEntry.TABLE_NAME;
	}

	
	public static void createLevelsDatabase(SQLiteDatabase db){
		db.execSQL(LevelEntry.SQL_CREATE_ENTRIES);
	}
	
    public static void initLevelsDatabase(SQLiteDatabase db) {
    	Debug.e("Levels: "+ ResourcesManager.getInstance().getNumberOfLevels());
    	for (int i=1; i<=ResourcesManager.getInstance().getNumberOfLevels();i++ )
    	{// Create a new map of values, where column names are the keys
       	ContentValues values = new ContentValues();
       	values.put(LevelEntry._ID, i);
    	values.put(LevelEntry.COLUMN_NAME_LEVEL_ID, i+"");
    	values.put(LevelEntry.COLUMN_NAME_LEVEL_PROGRESS, LevelEntry.VALUE_LEVEL_PROGRESS_NOTCOMPLETED);
    	values.put(LevelEntry.COLUMN_NAME_LEVEL_STARS, "0");
    	if( i==1){
    		values.put(LevelEntry.COLUMN_NAME_LEVEL_LOCK, LevelEntry.VALUE_LEVEL_LOCK_UNLOCKED);
    	}else{
    		values.put(LevelEntry.COLUMN_NAME_LEVEL_LOCK, LevelEntry.VALUE_LEVEL_LOCK_LOCKED);
    	}
    	// Insert the new row, returning the primary key value of the new row, not captured as of now
    	db.insert(
    			LevelEntry.TABLE_NAME,
    			LevelEntry.COLUMN_NAME_LEVEL_ID,
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
	
	public static  void updateStarsRecord(int levelId, int newStarsRecord) {
		LevelHelper dbHelper = new LevelHelper(ResourcesManager.getInstance().activity);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		// New value for one column
		ContentValues values = new ContentValues();
		values.put(LevelEntry.COLUMN_NAME_LEVEL_STARS, newStarsRecord);
		values.put(LevelEntry.COLUMN_NAME_LEVEL_PROGRESS, LevelEntry.VALUE_LEVEL_PROGRESS_COMPLETED);

		// Which row to update, based on the ID
		String selection = LevelEntry.COLUMN_NAME_LEVEL_ID+ " = ";
		String[] selectionArgs = { String.valueOf(levelId) };

		db.update(
		    LevelEntry.TABLE_NAME,
		    values,
		    selection,
		    selectionArgs);		
	}
	public static  void unlockLevel(int levelId) {
		LevelHelper dbHelper = new LevelHelper(ResourcesManager.getInstance().activity);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		// New value for one column
		ContentValues values = new ContentValues();
		values.put(LevelEntry.COLUMN_NAME_LEVEL_LOCK, LevelEntry.VALUE_LEVEL_LOCK_UNLOCKED);

		// Which row to update, based on the ID
		String selection = LevelEntry.COLUMN_NAME_LEVEL_ID+ " = ";
		String[] selectionArgs = { String.valueOf(levelId) };

		db.update(
		    LevelEntry.TABLE_NAME,
		    values,
		    selection,
		    selectionArgs);		
	}

}