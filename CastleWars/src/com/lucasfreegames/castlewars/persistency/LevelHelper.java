package com.lucasfreegames.castlewars.persistency;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LevelHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "castlewars.db";

    public LevelHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
		LevelContract.createLevelsDatabase(db);
		LevelContract.initLevelsDatabase(db);

/*    	if (!isDatabaseInitialized(db, LevelContract.LevelEntry.TABLE_NAME ))
            {
    			LevelContract.createLevelsDatabase();
    			LevelContract.initLevelsDatabase();
            }*/
    }
    
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //db.execSQL(levelContract.LevelEntry.SQL_DELETE_ENTRIES);
        //onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //onUpgrade(db, oldVersion, newVersion);
    }
    
    public Cursor getLevels(){
    	return LevelContract.loadLevelsOnCursor();
    }
       
    public boolean isDatabaseInitialized(SQLiteDatabase db, String tableName){
    	{
    	    if (tableName == null || db == null || !db.isOpen())
    	    {
    	        return false;
    	    }
    	    Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
    	    if (!cursor.moveToFirst())
    	    {
    	        return false;
    	    }
    	    int count = cursor.getInt(0);
    	    cursor.close();
        	db.close();
    	    return count > 0;
    	}    
    }
    
    
}