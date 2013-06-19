package com.example.simpletodo;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements LoaderCallbacks<Cursor>{

	private static final String logTag = "TodoListMainActivity" ;
    CursorLoader cursorLoader;

//	TodoListDbContentProvider mDbProvider;
    LoaderManager loadermanager;		

	SimpleCursorAdapter todoDataAdapter;
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        loadermanager=getLoaderManager();

        setContentView(R.layout.activity_main);

        prepareListView();

        loadermanager.initLoader(1, null, this);

        EditText t = (EditText) findViewById(R.id.edit_message);

        t.setOnLongClickListener( new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				EditText t = (EditText)v;
				t.setText("");
				return true;
			}
		});        
        
        Button bn = (Button) findViewById(R.id.edit_button_normal);
        Button bi = (Button) findViewById(R.id.edit_button_important);
        Button bc = (Button) findViewById(R.id.edit_button_critical);
        
        /* Colors defined in res/values/Colors.xml" */
        bn.setBackgroundColor(getResources().getColor(R.color.list_normal));
        bi.setBackgroundColor(getResources().getColor(R.color.list_important));
        bc.setBackgroundColor(getResources().getColor(R.color.list_critical));
        
    }
    
	public void prepareListView() {
		Log.i(logTag, "prepareListView");
		
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
        todoDataAdapter = new SimpleCursorAdapter(this, R.layout.listview_todo, null, from, to, 0);

        todoDataAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            	if(columnIndex == cursor.getColumnIndex(TodoItemContract.COLUMN_NAME_CHECKED)) {
            		CheckBox cb = (CheckBox) view;
            		cb.setChecked(cursor.getInt(columnIndex) != 0);
            		cb.setText("");
            		return true;
            	}
            	if(columnIndex == cursor.getColumnIndex(TodoItemContract.COLUMN_NAME_FLAG)) {
            		TextView tv = (TextView) view;
            		switch(cursor.getInt(columnIndex)) {
            		case TodoItemContract.TODO_FLAG_CRITICAL:
            			tv.setBackgroundColor(getResources().getColor(R.color.list_critical));
            			break;
            		case TodoItemContract.TODO_FLAG_IMPORTANT:
            			tv.setBackgroundColor(getResources().getColor(R.color.list_important));
            			break;
            		case TodoItemContract.TODO_FLAG_NORMAL:
            			tv.setBackgroundColor(getResources().getColor(R.color.list_normal));
            			break;
            		}
            		return true;
            	}

            	return false;
            }
        });
        
        ListView v = (ListView) findViewById(R.id.TodoList);
        
		v.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i(logTag, "id select : " + id);
				ContentValues initValues = new ContentValues();
				Cursor c =todoDataAdapter.getCursor(); 
				c.moveToPosition(position);
				int val = c.getInt(c.getColumnIndex(TodoItemContract.COLUMN_NAME_CHECKED));
				if(val ==0 )
					initValues.put(TodoItemContract.COLUMN_NAME_CHECKED, 1);
				else
					initValues.put(TodoItemContract.COLUMN_NAME_CHECKED, 0);
				
		    	int ret = getContentResolver().update(TodoItemContract.TODO_URI, initValues, "_ID = "+id, null);
		    	Log.v(logTag, ret + " elements changes");
		    	assert ret == 1;
			}
		});
		
		v.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i(logTag, "id select : " + id);

				Cursor c =todoDataAdapter.getCursor(); 
				c.moveToPosition(position);
				String val = c.getString(c.getColumnIndex(TodoItemContract.COLUMN_NAME_TEXT));

				EditText t = (EditText)findViewById(R.id.edit_message);
				t.setText(val);
				
		    	int ret = getContentResolver().delete(TodoItemContract.TODO_URI, "_ID = "+id, null);
		    	Log.v(logTag, ret + " elements changes");
		    	assert ret == 1;
		    	return ret == 1;
			}
		});
		
		
        v.setAdapter(todoDataAdapter);
        Log.v(logTag, "prepareListView done");
	}
	
	
	public void addTodoItemCritical(View view) {
		addTodoItem(view, TodoItemContract.TODO_FLAG_CRITICAL);
	}

	public void addTodoItemImportant(View view) {
		addTodoItem(view, TodoItemContract.TODO_FLAG_IMPORTANT);
	}

	public void addTodoItemNormal(View view) {
		addTodoItem(view, TodoItemContract.TODO_FLAG_NORMAL);
	}
	
    public void addTodoItem(View view, int flag) {
    	EditText t   = (EditText) findViewById(R.id.edit_message);
/*    	DatePicker dp = (DatePicker) findViewById(R.id.edit_date); 
    	GregorianCalendar c = new GregorianCalendar( dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
    	*/
    	
    	Log.i(logTag, "New ToDo Item created");
    	
		ContentValues initValues = new ContentValues();
		initValues.put(TodoItemContract.COLUMN_NAME_TEXT, t.getText().toString());
		t.setText("");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		initValues.put(TodoItemContract.COLUMN_NAME_DATE, df.format(new Date()));
		
		initValues.put(TodoItemContract.COLUMN_NAME_FLAG, flag);
		initValues.put(TodoItemContract.COLUMN_NAME_CHECKED, 0); // Always unchecked by default
    	
    	Uri ret_URI = getContentResolver().insert(TodoItemContract.TODO_URI, initValues);
    	
    	if(ret_URI != null) {
    		Toast.makeText(this,  R.string.msg_todo_added,  Toast.LENGTH_SHORT).show();
    	} else {
    		Toast.makeText(this,  R.string.msg_todo_added_nok,  Toast.LENGTH_SHORT).show();
    	}
    	todoDataAdapter.notifyDataSetChanged();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
    	Log.v(logTag, "onCreateLoader");
    	String[] projection = {
    			TodoItemContract._ID,
    			TodoItemContract.COLUMN_NAME_CHECKED,
        		TodoItemContract.COLUMN_NAME_TEXT,
        		TodoItemContract.COLUMN_NAME_FLAG 
        		};
    	
    	cursorLoader = new CursorLoader(this, TodoItemContract.TODO_URI, projection, null, null, null);
    	Log.v(logTag, "Cursor Loader created");
    	return cursorLoader;

    }    		
    
    @Override
    public void onLoadFinished(Loader<Cursor> loader,Cursor cursor) {
    	Log.v(logTag, "Load finished");
    	if(todoDataAdapter!=null && cursor!=null)
    		todoDataAdapter.swapCursor(cursor); //swap the new cursor in.
    	else
    		Log.v(logTag,"OnLoadFinished: todoDataAdapter is null");
       }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    	onLoadFinished(arg0, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
