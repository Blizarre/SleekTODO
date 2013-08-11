package com.smfandroid.sleektodo;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.smfandroid.sleektodo.MainActivity.Singleton;

public class ListOfTodoItems extends ListView implements LoaderCallbacks<Cursor> {
	
	public ListOfTodoItems(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected String TAG = getClass().getSimpleName();
	
	public void prepareListView() {
		Log.i(TAG, "prepareListView");
		
        String[] from = {
        		TodoItemContract.COLUMN_NAME_CHECKED,
        		TodoItemContract.COLUMN_NAME_TEXT,
        		TodoItemContract.COLUMN_NAME_FLAG
        };
        
        int[] to = {
        		R.id.listview_chkbox,
        		R.id.listview_text,
        		R.id.listview_flag
        };
        
        // NO cursor now, async with the loader
        SimpleCursorAdapter todoDataAdapter = new SimpleCursorAdapter(getContext(), R.layout.listview_todo, null, from, to, 0);

        addAdapterListeners(todoDataAdapter);
        addListListeners();
		
        setAdapter(todoDataAdapter);
	}

	protected void addListListeners() {
		ListView v = (ListView) findViewById(R.id.TodoList);
        
		v.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i(TAG, "id select : " + id);
				ContentValues initValues = new ContentValues();
				Cursor c = getCursorAdapter().getCursor(); 
				c.moveToPosition(position);
				int val = c.getInt(c.getColumnIndex(TodoItemContract.COLUMN_NAME_CHECKED));
				if(val ==0 )
					initValues.put(TodoItemContract.COLUMN_NAME_CHECKED, 1);
				else
					initValues.put(TodoItemContract.COLUMN_NAME_CHECKED, 0);
				
		    	int ret = getContext().getContentResolver().update(TodoItemContract.TODO_URI, initValues, "_ID = "+id, null);
		    	Log.v(TAG, ret + " elements changes");
		    	assert ret == 1;
			}
		});
		
		v.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i(TAG, "id select : " + id);

				Cursor c = getCursorAdapter().getCursor(); 
				c.moveToPosition(position);
				String val = c.getString(c.getColumnIndex(TodoItemContract.COLUMN_NAME_TEXT));

				EditText t = (EditText)findViewById(R.id.edit_message);
				t.setText(val);
				
		    	int ret = getContext().getContentResolver().delete(TodoItemContract.TODO_URI, TodoItemContract.COLUMN_NAME_ID+ "="+id, null);
		    	Log.v(TAG, ret + " elements changes");
		    	assert ret == 1;
		    	return ret == 1;
			}
		});
	}

	protected void addAdapterListeners(SimpleCursorAdapter adapter) {
		adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            	if(columnIndex == cursor.getColumnIndex(TodoItemContract.COLUMN_NAME_CHECKED)) {
            		CheckBox cb = (CheckBox) view;
            		cb.setChecked(cursor.getInt(columnIndex) != 0);
            		cb.setText("");
            		return true;
            	}
            	if(columnIndex == cursor.getColumnIndex(TodoItemContract.COLUMN_NAME_TEXT)) {
            		TextView et = (TextView) view;
            		et.setText(cursor.getString(columnIndex));
            		et.setTextAppearance(et.getContext(), Singleton.size);
            		return true;
            	}            	
            	if(columnIndex == cursor.getColumnIndex(TodoItemContract.COLUMN_NAME_FLAG)) {
            		TextView tv = (TextView) view;

            		tv.setTextAppearance(tv.getContext(), Singleton.size);
            		tv.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
    				tv.setText(" ");

            		switch(cursor.getInt(columnIndex)) {
            		case TodoItemContract.TODO_FLAG_CRITICAL:
            			if(Singleton.isColorBlind) {
            				tv.setText("!!");
            				tv.setBackgroundColor(getResources().getColor(android.R.color.background_light));
            			} else {
            				tv.setBackgroundColor(getResources().getColor(R.color.list_critical));
            			}
            			break;
            		case TodoItemContract.TODO_FLAG_IMPORTANT:
            			if(Singleton.isColorBlind) {
            				tv.setText("++");
            				tv.setBackgroundColor(getResources().getColor(android.R.color.background_light));
            			} else {
            				tv.setBackgroundColor(getResources().getColor(R.color.list_important));
            			}
            			break;
            		case TodoItemContract.TODO_FLAG_NORMAL:
            			if(Singleton.isColorBlind) { 
            				tv.setText("..");
            				tv.setBackgroundColor(getResources().getColor(android.R.color.background_light));
            			} else {
            				tv.setBackgroundColor(getResources().getColor(R.color.list_normal));
            			}
            			break;
            		}
            		return true;
            	}

            	return false;
            }
        });
	}

    
    /************/
    /** Loader **/
    /************/
    
    @Override
    public Loader<Cursor> onCreateLoader(int idCategory, Bundle arg1) {
    	Log.v(TAG, "onCreateLoader");
    	String[] projection = {
    			TodoItemContract._ID,
    			TodoItemContract.COLUMN_NAME_CHECKED,
        		TodoItemContract.COLUMN_NAME_TEXT,
        		TodoItemContract.COLUMN_NAME_FLAG 
        		};
    	
    	CursorLoader cursorLoader = new CursorLoader(getContext(), TodoListDbContentProvider.getUriForCategory(idCategory), projection, null, null, null);
    	Log.v(TAG, "Cursor Loader created");
    	return cursorLoader;

    }    		
    
    @Override
    public void onLoadFinished(Loader<Cursor> loader,Cursor cursor) {
    	Log.v(TAG, "Load finished");
    	if(getAdapter() !=null && cursor!=null)
    		getCursorAdapter().swapCursor(cursor); //swap the new cursor in.
    	else
    		Log.v(TAG,"OnLoadFinished: todoDataAdapter is null");
       }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    	onLoadFinished(arg0, null);
    }	

    protected CursorAdapter getCursorAdapter() {
    	return (CursorAdapter)getAdapter();
    }
}
