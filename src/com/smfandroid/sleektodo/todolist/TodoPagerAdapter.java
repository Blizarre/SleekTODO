package com.smfandroid.sleektodo.todolist;


import com.smfandroid.sleektodo.CategoryManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

// Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
public class TodoPagerAdapter extends FragmentPagerAdapter {
    
	public final String TAG = getClass().getSimpleName(); 
	protected CategoryManager mCatManager;
	
	public TodoPagerAdapter(FragmentManager fm, CategoryManager cm) {
        super(fm);
        mCatManager = cm;
    }

    @Override
    public Fragment getItem(int i) {
    	Log.i(TAG, "Item needed for category " + i);

        Fragment fragment = new FragmentTodo();
        Bundle args = new Bundle();
        args.putInt(FragmentTodo.ARG_CATEGORY, i);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return mCatManager.getNbCategory();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mCatManager.getCategoryName(position);
    }
}