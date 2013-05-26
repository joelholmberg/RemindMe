/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package se.joelholmberg.android.remindme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class NotesDbAdapter {

    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_ID = "_id";
    public static final String KEY_NOTEID = "note_id";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_RADIUS = "radius";
    
    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    
//    private static final String DATABASE_DROP_TABLES =
//    		""

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE_NOTES = "notes";
    private static final String DATABASE_TABLE_POSITIONS = "positions";
    private static final int DATABASE_VERSION = 2;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE_NOTES =
        "create table " + DATABASE_TABLE_NOTES + "(_id integer primary key autoincrement, "
        + "title text not null, body text not null)";
    
    private static final String DATABASE_CREATE_POSITIONS =
            "create table " + DATABASE_TABLE_POSITIONS + "(_id integer primary key autoincrement, "
            + "note_id integer not null, "+ "latitude text not null, longitude text not null, radius text not null)";
    
    
    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE_NOTES);
            db.execSQL(DATABASE_CREATE_POSITIONS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_NOTES +"; DROP TABLE IF EXISTS " + DATABASE_TABLE_POSITIONS);
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public NotesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public NotesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the note
     * @param body the body of the note
     * @return rowId or -1 if failed
     */
    public long createNote(String title, String body) {
        ContentValues initialValuesNote = new ContentValues();
        initialValuesNote.put(KEY_TITLE, title);
        initialValuesNote.put(KEY_BODY, body);
        
        
        return mDb.insert(DATABASE_TABLE_NOTES, null, initialValuesNote);
    }
    
    /**
     * Create a new position with specified radius for a note
     * 
     * @param id the id of the note
     * @param latitude the latitude of the point
     * @param longitude the longitude of the point
     * @param radius the radius of the circle around the point
     * @return rowId or -1 if failed
     */
    public void createPosition(long noteId, String latitude, String longitude, String radius) {
        if(latitude!=null && longitude!=null && radius!=null 
        		&& latitude!="" && longitude!="" && radius!=""){ 
            ContentValues initialValuesPosition = new ContentValues();
            initialValuesPosition.put(KEY_NOTEID, noteId);
            initialValuesPosition.put(KEY_LATITUDE, latitude);
            initialValuesPosition.put(KEY_LONGITUDE, longitude);
            initialValuesPosition.put(KEY_RADIUS, radius);
            
            mDb.insert(DATABASE_TABLE_POSITIONS, null, initialValuesPosition); 
        }
        
    }
    


    /**
     * Delete the note with the given rowId
     * 
     * @param noteId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNote(long noteId) {
    	
        mDb.delete(DATABASE_TABLE_POSITIONS, KEY_NOTEID + "=" + noteId, null);
        return mDb.delete(DATABASE_TABLE_NOTES, KEY_ID + "=" + noteId, null) > 0;
        
    }
    
    public void clearTables(){
    	dropTables();
    	mDbHelper.onCreate(mDb);
    }
    
    /**
     * Drop tables
     * 
     */
    public void dropTables() {
    	
        dropNotesTable();
        dropPositionsTable();
        
    }
    
    /**
     * Drop notes tables
     * 
     */
    public void dropNotesTable() {
    	
        mDb.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_NOTES);
        
    }
    
    /**
     * Drop positions table
     * 
     */
    public void dropPositionsTable() {
    	
    	mDb.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_POSITIONS);
        
    }
    
    

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllNotes() {

        return mDb.query(DATABASE_TABLE_NOTES, new String[] {KEY_ID, KEY_TITLE,
                KEY_BODY}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchNote(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE_NOTES, new String[] {KEY_ID,
                    KEY_TITLE, KEY_BODY}, KEY_ID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    /**
     * Return a Cursor positioned at the first position that matches the given rowId
     * 
     * @param rowId id of note from which positions will be fetched
     * @return Cursor positioned to the first of the matching positions, if found. Each returned row holds 4 columns: _id, latitude, longitude, radius.
     * @throws SQLException if positions could not be found/retrieved
     */
    public Cursor fetchPositions(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(false, DATABASE_TABLE_POSITIONS, new String[] {KEY_ID,
                    KEY_LATITUDE, KEY_LONGITUDE, KEY_RADIUS}, KEY_NOTEID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    /**
     * Return a Cursor positioned at the first position of all positions in DB
     * 
     * @return Cursor positioned to the first of the positions, if found. Each returned row holds 4 columns: _id, latitude, longitude, radius.
     * @throws SQLException if positions could not be found/retrieved
     */
	public Cursor fetchAllPositions() {
		
		Cursor mCursor =

	            mDb.query(false, DATABASE_TABLE_POSITIONS, new String[] {KEY_ID,
	                    KEY_LATITUDE, KEY_LONGITUDE, KEY_RADIUS}, null, null,
	                    null, null, null, null);
	        if (mCursor != null) {
	            mCursor.moveToFirst();
	        }
	        return mCursor;
	}

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateNote(long rowId, String title, String body) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_BODY, body);

        return mDb.update(DATABASE_TABLE_NOTES, args, KEY_ID + "=" + rowId, null) > 0;
    }


}
