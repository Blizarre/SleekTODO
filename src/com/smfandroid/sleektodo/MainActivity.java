package com.smfandroid.sleektodo;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.smfandroid.sleektodo.FontSizeDialog.NoticeDialogListener;
import com.smfandroid.sleektodo.ImportExportDialog.ImportExportDialogListener;
import com.smfandroid.sleektodo.RemoveAllTodoConfirmDialog.RemoveAllTodosDialogListener;
import com.smfandroid.sleektodo.todolist.FragmentTodo;
import com.smfandroid.sleektodo.todolist.TodoPagerAdapter;


public class MainActivity extends FragmentActivity implements NoticeDialogListener, RemoveAllTodosDialogListener, ImportExportDialogListener {

	public static class Singleton {
		private Singleton() {};
		public static int size;
		public static boolean isColorBlind;
	}
	
	protected final String PREF_AUTO_KEYB="auto_keyboard";
	protected final String PREF_IS_COLORBLIND="is_colorblind";
	protected final String PREF_SIZE_FONT="font_size";
	protected final String PREF_FIRST_START="first_start";
	
   	protected CategoryManager mCatManager;

	private final String TAG = getClass().getSimpleName();
		
	/**
	 * List of fragments used to show the categories. As soon as they are created by
	 * the viewPager adapter, they register themselves to their MainActivity. That
	 * way the activity can notify them when data has changed. 
	 */
	SparseArray<FragmentTodo> mListOfTodoFragments;
		
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mListOfTodoFragments = new SparseArray<FragmentTodo>();

		SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
       	boolean isAutoKb    = sharedPref.getBoolean(PREF_AUTO_KEYB, true);
       	Singleton.size = sharedPref.getInt(PREF_SIZE_FONT, 1);
       	Singleton.isColorBlind = sharedPref.getBoolean(PREF_IS_COLORBLIND, false);
       	
       	mCatManager = new CategoryManager(this);
       	
       	setAutoKeyboard(isAutoKb);
       	
        if(sharedPref.getBoolean(PREF_FIRST_START, true)) {
        	Editor e = sharedPref.edit();
        	e.putBoolean(PREF_FIRST_START, false);
        	e.commit();
        	populateTodoList();
        	menu_showHelp(null);
        }
       	
       	ViewPager v = (ViewPager)findViewById(R.id.pager_todo);
       	v.setAdapter(new TodoPagerAdapter(getSupportFragmentManager(), mCatManager));
       	
        Button bn = (Button) findViewById(R.id.edit_button_normal);
        Button bi = (Button) findViewById(R.id.edit_button_important);
        Button bc = (Button) findViewById(R.id.edit_button_critical);
        
        bn.setText(Html.fromHtml(getString(R.string.button_add_normal)));
        bi.setText(Html.fromHtml(getString(R.string.button_add_important)));
        bc.setText(Html.fromHtml(getString(R.string.button_add_critical)));
        
        // No need to show the app title on the action bar. Take too much space on small devices.
        getActionBar().setDisplayShowTitleEnabled(false);
        
        TextView t = (TextView) findViewById(R.id.edit_message);

        t.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				View v = findViewById(R.id.buttons_group);
				if(s.length() != 0)
					v.setVisibility(View.VISIBLE);
				else
					v.setVisibility(View.GONE);
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
        

    }
    
	/**
	 * This method is called by the fragments created by the viewPager adapter to
	 * register themselves to the activity.
	 * @param category : the category ID of the fragment
	 * @param fragmentTodo : the fragment registering
	 */
	public void addTodoFragment(int category, FragmentTodo fragmentTodo) {
		mListOfTodoFragments.put(category, fragmentTodo);
	}


	/**
	 * Initial todo items shown to the users during first startup (tutorial) 
	 */
	public void populateTodoList() {
		mCatManager.setNbCategory(2);
		mCatManager.setCategoryName(0, getString(R.string.cat_name_tutorial));
		mCatManager.setCategoryName(1, getString(R.string.cat_name_another));

		ContentValues initValues = new ContentValues();
		initValues.put(TodoItemContract.COLUMN_NAME_CHECKED, 0); // Always unchecked by default
		initValues.put(TodoItemContract.COLUMN_NAME_CATEGORY, 0); // Category 0 by default 
     	initValues.put(TodoItemContract.COLUMN_NAME_FLAG, TodoItemContract.TODO_FLAG_CRITICAL);
		initValues.put(TodoItemContract.COLUMN_NAME_TEXT, getString(R.string.todo_text_1));
		getContentResolver().insert(TodoItemContract.TODO_URI, initValues);		
		initValues.put(TodoItemContract.COLUMN_NAME_TEXT, getString(R.string.todo_text_2));
		getContentResolver().insert(TodoItemContract.TODO_URI, initValues);		
		initValues.put(TodoItemContract.COLUMN_NAME_FLAG, TodoItemContract.TODO_FLAG_IMPORTANT);
		initValues.put(TodoItemContract.COLUMN_NAME_TEXT, getString(R.string.todo_text_3)); 		
		getContentResolver().insert(TodoItemContract.TODO_URI, initValues);		
		initValues.put(TodoItemContract.COLUMN_NAME_TEXT, getString(R.string.todo_text_4)); 
		getContentResolver().insert(TodoItemContract.TODO_URI, initValues);		
		initValues.put(TodoItemContract.COLUMN_NAME_FLAG, TodoItemContract.TODO_FLAG_NORMAL);
		initValues.put(TodoItemContract.COLUMN_NAME_TEXT, getString(R.string.todo_text_5));
		getContentResolver().insert(TodoItemContract.TODO_URI, initValues);				
		initValues.put(TodoItemContract.COLUMN_NAME_TEXT, getString(R.string.todo_text_6)); 
		getContentResolver().insert(TodoItemContract.TODO_URI, initValues);

		initValues.put(TodoItemContract.COLUMN_NAME_CATEGORY, 1); 
		initValues.put(TodoItemContract.COLUMN_NAME_TEXT, getString(R.string.todo_text_7)); 
		getContentResolver().insert(TodoItemContract.TODO_URI, initValues);
	}

	
	
	
	
	/***************/
	/** Callbacks **/
	/***************/
	
	/**
	 *  Android 4.0.3 doesn't like AT ALL custom themes and onClick.
	 *  That's why I have to use onOptionsItemSelected
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem mi) {
		switch (mi.getItemId()) {
			case R.id.action_auto_keyboard:
				return menu_changeAutoKeyboard(mi);
			case R.id.action_change_font:
				return menu_changeFontSize(mi);
			case R.id.action_colorblind_mode:
				return menu_changeColorblind(mi);
			case R.id.action_erase_all:
				return menu_eraseAllTodos(mi);
			case R.id.action_erase_checked:
				return menu_eraseCheckedTodos(mi);
			case R.id.action_erase_text:
				return menu_erase_text(mi);
			case R.id.action_help:
				return menu_showHelp(mi);
			case R.id.action_import_export:
				return menu_showImportExport(mi);
			case R.id.action_cat_edit:
				return menu_showCatEditor(mi);				
			default:
				return false;
		}
		
	}
	
	public boolean menu_showHelp(MenuItem mi) {
		HelpDialog hd = new HelpDialog();
		hd.show(getSupportFragmentManager(), "NoticeDialogFragment");
		return true;
	}

	
	public boolean menu_changeAutoKeyboard(MenuItem mi) {
		SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
		Editor e = sharedPref.edit();
		boolean newVal = ! mi.isChecked();
		
		mi.setChecked(newVal);
		e.putBoolean(PREF_AUTO_KEYB, newVal);
		setAutoKeyboard(newVal);
		e.commit();
		return true;
	}
		
	public boolean menu_eraseCheckedTodos(MenuItem item) {
    	int ret = getContentResolver().delete(TodoItemContract.TODO_URI, TodoItemContract.COLUMN_NAME_CHECKED + "=1", null);
		String msg = String.format(getString(R.string.msg_elements_removed), ret);
    	Toast.makeText(this,  msg, Toast.LENGTH_SHORT).show();
    	return true;
	}
	public boolean menu_eraseAllTodos(MenuItem view) {
        DialogFragment dialog = new RemoveAllTodoConfirmDialog();
        dialog.show(getSupportFragmentManager(), "RemoveAllTodoConfirmDialog");
        return true;
	}
	
	public boolean menu_changeFontSize(MenuItem view) {
        DialogFragment dialog = new FontSizeDialog();
        dialog.show(getSupportFragmentManager(), "ChangeFontSizeDialogFragment");
        return true;
	}	

	public boolean menu_showImportExport(MenuItem mi) {
		DialogFragment hd = new ImportExportDialog();
		hd.show(getSupportFragmentManager(), "ImprotExportDialogFragment");
		return true;
	}
	
	public boolean menu_erase_text(MenuItem view) {
		EditText t = (EditText)findViewById(R.id.edit_message);
		t.setText("");
		return true;
	}

	public boolean menu_changeColorblind(MenuItem mi) {
		SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
		Editor e = sharedPref.edit();
		boolean newVal = ! mi.isChecked();
		
		mi.setChecked(newVal);
		e.putBoolean(PREF_IS_COLORBLIND, newVal);
		Singleton.isColorBlind = newVal;
		e.commit();
		notifyCurrentListDataChanged();
		return true;
	}


	public boolean menu_showCatEditor(MenuItem mi) {
		Intent intent = new Intent(this, CategoriesEditorActivity.class);
		startActivityForResult(intent, 1);
		return true;
	}	

	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		
		if(resultCode == Activity.RESULT_OK) {
			Log.i(TAG, "result of activity is OK : categories have changed");
			ViewPager v = (ViewPager)findViewById(R.id.pager_todo);
			v.getAdapter().notifyDataSetChanged();
		} else {
			Log.i(TAG, "result of activity is NOK : categories have NOT changed");
		}
	}
	
	
	public void notifyCurrentListDataChanged() {
		int cat = getCurrentCategory();
		mListOfTodoFragments.get(cat).notifyTodoDataChanged();
	}

	public void setEditMessage(String s) {
		EditText t = (EditText)findViewById(R.id.edit_message);
		t.setText(s);
	}
    
	/**
	 * Get the category currently displayer on screen by the ViewPager
	 * @return the category ID
	 */
    private int getCurrentCategory() {
		ViewPager vPag = (ViewPager)findViewById(R.id.pager_todo);
		return vPag.getCurrentItem();
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
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		else
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
    @SuppressLint("SimpleDateFormat")
	public void addTodoItem(View view, int flag) {
    	EditText t   = (EditText) findViewById(R.id.edit_message);
    	
    	Log.i(TAG, "New ToDo Item created");
    	
		ContentValues initValues = new ContentValues();
		initValues.put(TodoItemContract.COLUMN_NAME_TEXT, t.getText().toString());
		t.setText("");

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		initValues.put(TodoItemContract.COLUMN_NAME_DATE, df.format(new Date()));
		
		initValues.put(TodoItemContract.COLUMN_NAME_FLAG, flag);
		initValues.put(TodoItemContract.COLUMN_NAME_CHECKED, 0); // Always unchecked by default
		
		initValues.put(TodoItemContract.COLUMN_NAME_CATEGORY, getCurrentCategory()); // Always unchecked by default
    	
    	Uri ret_URI = getContentResolver().insert(TodoItemContract.TODO_URI, initValues);
    	
    	if(ret_URI != null) {
    		Toast.makeText(this,  R.string.msg_todo_added,  Toast.LENGTH_SHORT).show();
    	} else {
    		Toast.makeText(this,  R.string.msg_todo_added_nok,  Toast.LENGTH_SHORT).show();
    	}
    	notifyCurrentListDataChanged();
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

       	MenuItem mkb = menu.findItem(R.id.action_auto_keyboard);
       	MenuItem mkcb = menu.findItem(R.id.action_colorblind_mode);
       	
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
       	boolean isAutoKb    = sharedPref.getBoolean(PREF_AUTO_KEYB, true);
       	mkb.setChecked(isAutoKb);
       	mkcb.setChecked(Singleton.isColorBlind);
       	return true;
    }


	/*********************/
    /** Dialog callback **/
	/*********************/
    
	@Override
	public void onDialogSelect(int which) {
		SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
		Editor e = sharedPref.edit();
		e.putInt(PREF_SIZE_FONT, which);
		
		switch(which){
			case 0:
				Singleton.size = android.R.style.TextAppearance_Small;
				break;
			case 1:
				Singleton.size = android.R.style.TextAppearance_Medium;
				break;
			case 2:
				Singleton.size = android.R.style.TextAppearance_Large;
				break;
		}

		e.commit();
		
		notifyCurrentListDataChanged();
		Toast.makeText(this, R.string.msg_font_changed, Toast.LENGTH_SHORT).show();
	}



	@Override
	public void onDialogConfirmRemoveAll() {
    	int ret = getContentResolver().delete(TodoItemContract.TODO_URI, null, null);
		String msg = String.format(getString(R.string.msg_elements_removed), ret);
    	Toast.makeText(this,  msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Called whenever new data has been imported into the data base.
	 */
	@Override
	public void importDone() {
		onActivityResult(0,  Activity.RESULT_OK, null);		
	}
    
}
