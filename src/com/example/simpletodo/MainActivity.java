package com.example.simpletodo;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
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

import com.example.simpletodo.FontSizeDialog.NoticeDialogListener;
import com.example.simpletodo.RemoveAllTodoConfirmDialog.RemoveAllTodosDialogListener;


public class MainActivity extends Activity implements LoaderCallbacks<Cursor>, NoticeDialogListener, RemoveAllTodosDialogListener {

	public static class Singleton {
		private Singleton() {};
		public static int size;
	}
	
	protected final String PREF_AUTO_KEYB="auto_keyboard";
	protected final String PREF_SIZE_FONT="font_size";

	private static final String logTag = "TodoListMainActivity" ;
		
    CursorLoader cursorLoader;
    LoaderManager loadermanager;		
	SimpleCursorAdapter todoDataAdapter;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        loadermanager=getLoaderManager();

        setContentView(R.layout.activity_main);

        prepareListView();

        loadermanager.initLoader(1, null, this);

		SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
       	boolean isAutoKb    = sharedPref.getBoolean(PREF_AUTO_KEYB, true);
       	Singleton.size = sharedPref.getInt(PREF_SIZE_FONT, 1);
       	setAutoKeyboard(isAutoKb);
       	
        EditText t = (EditText) findViewById(R.id.edit_message);

        Button bn = (Button) findViewById(R.id.edit_button_normal);
        Button bi = (Button) findViewById(R.id.edit_button_important);
        Button bc = (Button) findViewById(R.id.edit_button_critical);
        
        /* Colors defined in res/values/Colors.xml" */
        bn.setBackgroundColor(getResources().getColor(R.color.list_normal));
        bi.setBackgroundColor(getResources().getColor(R.color.list_important));
        bc.setBackgroundColor(getResources().getColor(R.color.list_critical));
        
        t.setOnLongClickListener( new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				EditText t = (EditText)v;
				t.setText("");
				return true;
			}
		});          
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

        addAdapterListeners();
        addListListeners();
		
        ListView v = (ListView) findViewById(R.id.TodoList);
        v.setAdapter(todoDataAdapter);
	}

	protected void addListListeners() {
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
				
		    	int ret = getContentResolver().delete(TodoItemContract.TODO_URI, TodoItemContract.COLUMN_NAME_ID+ "="+id, null);
		    	Log.v(logTag, ret + " elements changes");
		    	assert ret == 1;
		    	return ret == 1;
			}
		});
	}

	protected void addAdapterListeners() {
		todoDataAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

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
            		//SharedPreferences sharedPref = et.getContext().getSharedPreferences.getPreferences(Context.MODE_PRIVATE);
            		switch(Singleton.size){
            			case 0:
            				et.setTextAppearance(et.getContext(), android.R.style.TextAppearance_Small);
            				break;
            			case 1:
            				et.setTextAppearance(et.getContext(), android.R.style.TextAppearance_Medium);
            				break;
            			case 2:
            				et.setTextAppearance(et.getContext(), android.R.style.TextAppearance_Large);
            				break;
            		}
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
	}
	
	
	
	
	
	/***************/
	/** Callbacks **/
	/***************/
	
	public void menu_eraseCheckedTodos(MenuItem item) {
    	int ret = getContentResolver().delete(TodoItemContract.TODO_URI, TodoItemContract.COLUMN_NAME_CHECKED + "=1", null);
		String msg = String.format(getString(R.string.msg_elements_removed), ret);
    	Toast.makeText(this,  msg, Toast.LENGTH_SHORT).show();
	}
	public void menu_eraseAllTodos(MenuItem view) {
        DialogFragment dialog = new RemoveAllTodoConfirmDialog();
        dialog.show(getFragmentManager(), "RemoveAllTodoConfirmDialog");

    	/*int ret = getContentResolver().delete(TodoItemContract.TODO_URI, null, null);
		String msg = String.format(getString(R.string.msg_elements_removed), ret);
    	Toast.makeText(this,  msg, Toast.LENGTH_SHORT).show();*/
	}
	

	
	public void menu_changeFontSize(MenuItem view) {
        DialogFragment dialog = new FontSizeDialog();
        dialog.show(getFragmentManager(), "NoticeDialogFragment");
	}	
	
	public void menu_changeAutoKeyboard(MenuItem view) {
		SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
		Editor e = sharedPref.edit();
		if(view.isChecked()) {
			view.setChecked(false);
			e.putBoolean(PREF_AUTO_KEYB, false);
			setAutoKeyboard(false);
		} else {
			view.setChecked(true);
			e.putBoolean(PREF_AUTO_KEYB, true);
			setAutoKeyboard(true);
		}
		e.commit();
	}
	
	
	public void button_addTodoItemCritical(View view) {
		addTodoItem(view, TodoItemContract.TODO_FLAG_CRITICAL);
	}

	public void button_addTodoItemImportant(View view) {
		addTodoItem(view, TodoItemContract.TODO_FLAG_IMPORTANT);
	}

	public void button_addTodoItemNormal(View view) {
		addTodoItem(view, TodoItemContract.TODO_FLAG_NORMAL);
	}
	
	
	
	private void setAutoKeyboard(boolean isAutoKb) {
		if(isAutoKb)
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		else
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
    @SuppressLint("SimpleDateFormat")
	public void addTodoItem(View view, int flag) {
    	EditText t   = (EditText) findViewById(R.id.edit_message);
/*    	DatePicker dp = (DatePicker) findViewById(R.id.edit_date); 
    	GregorianCalendar c = new GregorianCalendar( dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
    	*/
    	
    	Log.i(logTag, "New ToDo Item created");
    	
		ContentValues initValues = new ContentValues();
		initValues.put(TodoItemContract.COLUMN_NAME_TEXT, t.getText().toString());
		t.setText("");

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
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

    
    
    
    /************/
    /** Loader **/
    /************/
    
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
        getMenuInflater().inflate(R.menu.main, menu);

       	MenuItem mkb = menu.findItem(R.id.action_auto_keyboard);
       	
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
       	boolean isAutoKb    = sharedPref.getBoolean(PREF_AUTO_KEYB, true);
       	mkb.setChecked(isAutoKb);

       	return true;
    }



	@Override
	public void onDialogSelect(int which) {
		SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
		Editor e = sharedPref.edit();
		e.putInt(PREF_SIZE_FONT, which);
		Singleton.size=which;
		e.commit();
		
		todoDataAdapter.notifyDataSetChanged();
		Toast.makeText(this, R.string.msg_font_changed, Toast.LENGTH_SHORT).show();
	}



	@Override
	public void onDialogConfirmRemoveAll() {
		Toast.makeText(this, "Remove all !!!!!", Toast.LENGTH_SHORT).show();
		
	}
    
}
