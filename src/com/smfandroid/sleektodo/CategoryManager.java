package com.smfandroid.sleektodo;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.util.Log;

public class CategoryManager {
	
	private static final String PREF_CATEGORY_NB = "categoryNb";
	protected Context mContext;
	protected List<String> mListCategoryArrays;
	protected final String TAG = getClass().getSimpleName();
	
	public CategoryManager(Context c) {
		mContext = c;
		mListCategoryArrays = new ArrayList<String>();
		for(int i=0; i< getNbCategory(); i++)
			mListCategoryArrays.add(getCategoryName(i));
	}
	
	public String getCategoryName(int num) {
		String numToString = Integer.toString(num);
		SharedPreferences pref = mContext.getSharedPreferences(getClass().getCanonicalName(), Context.MODE_PRIVATE);
		return pref.getString("category" + numToString, "<" + numToString + ">");
	}
	
	
	public void setCategoryName(int num, String name) {
		Log.i(TAG, "change category " + num + " name to : " + name);
		if(num >= getNbCategory() || num < 0) 
			throw new IllegalArgumentException("Category number doesn't exist");
		
		String catNumInString = Integer.toString(num);
		SharedPreferences pref = mContext.getSharedPreferences(getClass().getCanonicalName(), Context.MODE_PRIVATE);
		Editor prefEdit = pref.edit();
		prefEdit.putString("category" + catNumInString, name);
		prefEdit.commit();
		mListCategoryArrays.set(num, name);
	}


	public int getNbCategory() {
		Log.i(TAG, "get Number of categories");
		SharedPreferences pref = mContext.getSharedPreferences(getClass().getCanonicalName(), Context.MODE_PRIVATE);
		return pref.getInt(PREF_CATEGORY_NB, 1);
	}


	/***
	 * Get the number of todo items that will need to be moved if a call to setNbCategory is made
	 */
	public int getNbElementsToRelocate(int newNbCategory) {
		int nbElts;
		String where;
		String[] arg = new String[1];
		
		String[] projection = { TodoItemContract.COLUMN_NAME_ID };
		where = TodoItemContract.COLUMN_NAME_CATEGORY + ">=?";
		arg[0] = Integer.toString(newNbCategory);
		Cursor ret = mContext.getContentResolver().query(TodoItemContract.TODO_URI, projection, where, arg, null);
		nbElts = ret.getCount();
		ret.close();
		return nbElts;
	}
	
	public void setNbCategory(int newNbCategory) {
		String[] arg = new String[1];
		String where;
		ContentValues cv = new ContentValues();
		
		Log.i(TAG, "set Number of categories to : " + newNbCategory);
		if(newNbCategory <= 0) 
			throw new IllegalArgumentException("The number of category must be > 0");

		if(newNbCategory < getNbCategory()) {
			// 5 category (0 to 4) => new max category number = 5-1 = 4
			cv.put("category", newNbCategory - 1);
			// remove all category >= nbCategory
			where = TodoItemContract.COLUMN_NAME_CATEGORY + ">=?";
			arg[0] = Integer.toString(newNbCategory);
	    	
			mContext.getContentResolver().update(TodoItemContract.TODO_URI, cv, where, arg);
		}
		
		SharedPreferences pref = mContext.getSharedPreferences(getClass().getCanonicalName(), Context.MODE_PRIVATE);
		Editor prefEdit = pref.edit();
		prefEdit.putInt(PREF_CATEGORY_NB, newNbCategory);
		prefEdit.commit();

		// Set right names
		mListCategoryArrays.clear();
		for(int i = 0; i < getNbCategory(); i++) {
			mListCategoryArrays.add(getCategoryName(i));
		}
	}

	public List<String> getCategoryArray() {
		return mListCategoryArrays;
	}

}
