package com.smfandroid.sleektodo;

import android.database.Cursor;

public class TodoItem {
	public String mText;
	public String mDate;
	public int mCategory;
	public boolean mIsChecked;
	public String mLongText;
	public int mFlag;

	protected static String getIfExistString(Cursor c, String colName) {
		int ind;		
		ind = c.getColumnIndex(colName);
		if(ind != -1)
			return c.getString(ind);
		else
			return "";
	}
	
	protected static int getIfExistInt(Cursor c, String colName) {
		int ind;		
		ind = c.getColumnIndex(colName);
		if(ind != -1)
			return c.getInt(ind);
		else
			return 0;

	}

	/**
	 * Generate an new TodoItem from the data of the cursor. Any non-imported column
	 * will have a default value
	 * 
	 * @param c The cursor holding the data
	 * @return an new TodoItem
	 * 
	 */
	public static TodoItem fromCursor(Cursor c) {
		TodoItem todoI = new TodoItem();
		
		todoI.mText = getIfExistString(c, TodoItemContract.COLUMN_NAME_TEXT);
		todoI.mFlag = getIfExistInt(c, TodoItemContract.COLUMN_NAME_FLAG);
		todoI.mDate = getIfExistString(c, TodoItemContract.COLUMN_NAME_DATE);
		todoI.mCategory = getIfExistInt(c, TodoItemContract.COLUMN_NAME_CATEGORY);
		todoI.mLongText = getIfExistString(c, TodoItemContract.COLUMN_NAME_LONGTEXT);
		todoI.mIsChecked = (getIfExistInt(c, TodoItemContract.COLUMN_NAME_CHECKED) != 0);
		return todoI;
	}
}
