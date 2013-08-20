package com.smfandroid.sleektodo.todolist;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

// Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
public class TodoPagerAdapter extends FragmentPagerAdapter {
    
	public final String TAG = getClass().getSimpleName(); 

	public TodoPagerAdapter(FragmentManager fm) {
        super(fm);
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
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "" + (position + 1);
    }
}