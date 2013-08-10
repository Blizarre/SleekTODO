package com.smfandroid.sleektodo;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


public class TodoListDbContentProvider extends ContentProvider {

	private static final String logTag = "TodoListDbContentProvider" ;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

    private static UriMatcher sUriMatcher;

    
	private static class DatabaseHelper extends SQLiteOpenHelper {


	    // If you change the database schema, you must increment the database version.
	    public static final int DATABASE_VERSION = 5;
	    public static final String DATABASE_NAME = "todoList.db";
	
	    private static final String TEXT_TYPE = " TEXT ";
	    private static final String INT_TYPE = " INTEGER ";
	    private static final String COMMA_SEP = ",";
	    private static final String ADD_KEYWORD = " ADD ";
	    
	    public static final String SQL_CREATE_ENTRIES =
	        "CREATE TABLE " + TodoItemContract.TABLE_TODO_NAME + " (" +
	        TodoItemContract._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
	        TodoItemContract.COLUMN_NAME_TEXT + TEXT_TYPE + COMMA_SEP +
	        TodoItemContract.COLUMN_NAME_LONGTEXT + TEXT_TYPE + COMMA_SEP +
	        TodoItemContract.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
	        TodoItemContract.COLUMN_NAME_CHECKED + INT_TYPE + COMMA_SEP +
	        TodoItemContract.COLUMN_NAME_FLAG + INT_TYPE + 	        
	        " );";

	    
	    // V4 -> V5
	    // Create the column "category" in the todo table
	    public static final String SQL_UPGRADE_CREATE_CATEGORY_COLUMN =
		        "ALTER TABLE " + TodoItemContract.TABLE_TODO_NAME + ADD_KEYWORD +
		        TodoItemContract.COLUMN_NAME_CATEGORY + TEXT_TYPE;

	    // Create the "category" table with the names (not used right now)
	    public static final String SQL_UPGRADE_CREATE_CATEGORY_TABLE =
		        "CREATE TABLE " + TodoItemContract.TABLE_CATEGORY_NAME + " (" +
		        TodoItemContract._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
		        TodoItemContract.COLUMN_NAME_TEXT + TEXT_TYPE + COMMA_SEP +
		        " );";
	    
	    
	    public static final String SQL_DELETE_ENTRIES =
	        "DROP TABLE IF EXISTS " + TodoItemContract.TABLE_TODO_NAME;
	
	    
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(logTag, SQL_CREATE_ENTRIES);
			db.execSQL(SQL_CREATE_ENTRIES);
		}

		/* Both methods should NOT be used for now. */

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(logTag, SQL_DELETE_ENTRIES);
			if(oldVersion < 5) { 
				db.execSQL(SQL_UPGRADE_CREATE_CATEGORY_COLUMN);
				db.execSQL(SQL_UPGRADE_CREATE_CATEGORY_TABLE);
			}
			onCreate(db);
		}
/*		
		@Override
		public void onDOwngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
			onCreate(db);			
		}
*/
		
	}

	@Override
	public boolean onCreate() {
		mDbHelper = new DatabaseHelper(getContext());
	    sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(TodoItemContract.AUTHORITY, TodoItemContract.CONTENT_TODO, 1);
		mDb = mDbHelper.getWritableDatabase();
		return true;
	}

	public void close() {
		if (mDbHelper != null) {
			mDbHelper.close();
		}
	}


	public boolean deleteTodo(int id) {
		int doneDelete = 0;
		doneDelete = mDb.delete(TodoItemContract.TABLE_TODO_NAME, "_ID=" + id, null);
		return doneDelete == 1;
	}

	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		String category = null;
		
	    switch (sUriMatcher.match(uri)) {

	    	case 1:
	        	if (TextUtils.isEmpty(sortOrder))
	        		sortOrder = TodoItemContract.COLUMN_NAME_CHECKED + " ASC, " + TodoItemContract.COLUMN_NAME_FLAG + " DESC";

	        	category = uri.getQueryParameter(TodoItemContract.CONTENT_PARAM_CATEGORY);
	        	if(category != null) {
	        		selectionArgs = new String[1];
	        		selectionArgs[0] = category;
	        	}
	        	
	            break;
	            
	    }

		Cursor cursor = mDb.query(
				TodoItemContract.TABLE_TODO_NAME,
				projection,
				selection,
				selectionArgs,
				null,
				null,
				sortOrder
				);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		
		cursor.setNotificationUri(getContext().getContentResolver(), TodoItemContract.TODO_URI);
		return cursor;
	}

	
	@Override
	public String getType(Uri uri) {
		return "vnd.android.cursor.item.todoList";
	}
	
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		Log.i(logTag, "Demande de changement");
		int ret = mDb.update(TodoItemContract.TABLE_TODO_NAME, values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(TodoItemContract.TODO_URI, null);
		return ret;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long id = mDb.insert(TodoItemContract.TABLE_TODO_NAME, null, values);
		if (id !=  -1) {
			getContext().getContentResolver().notifyChange(TodoItemContract.TODO_URI, null);
			
			return ContentUris.withAppendedId(TodoItemContract.TODO_URI,id);
		} else
			return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int ret = mDb.delete(TodoItemContract.TABLE_TODO_NAME, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(TodoItemContract.TODO_URI, null);
		return ret;
	}
}