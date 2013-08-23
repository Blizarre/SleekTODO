package com.smfandroid.sleektodo;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
		return pref.getString("category" + numToString, numToString);
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


	public void setNbCategory(int nbCategory) {
		Log.i(TAG, "set Number of categories to : " + nbCategory);
		if(nbCategory <= 0) 
			throw new IllegalArgumentException("The number of category must be > 0");
		SharedPreferences pref = mContext.getSharedPreferences(getClass().getCanonicalName(), Context.MODE_PRIVATE);
		Editor prefEdit = pref.edit();
		prefEdit.putInt(PREF_CATEGORY_NB, nbCategory);
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
