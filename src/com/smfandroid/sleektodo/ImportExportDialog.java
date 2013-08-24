package com.smfandroid.sleektodo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.smfandroid.sleektodo.xml.Category;
import com.smfandroid.sleektodo.xml.XmlTodoDeSerialize;
import com.smfandroid.sleektodo.xml.XmlTodoDeSerialize.TodoReadException;
import com.smfandroid.sleektodo.xml.XmlTodoSerialize;
import com.smfandroid.sleektodo.xml.XmlTodoSerialize.TodoAddException;

public class ImportExportDialog extends DialogFragment implements OnClickListener {

	interface ImportExportDialogListener {
		public void importDone();
	}
	
	protected ImportExportDialogListener mListener;
	

	private final String PREF_IMP_EXP_DIR = "io_default_dir";
	private final String TAG = getClass().getSimpleName();
	
	private EditText mEditText;
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ImportExportDialogListener so we can send events to the host
            mListener = (ImportExportDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ImportExportDialogListener");
        }
    }
    

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Builder builder = new Builder(getActivity());
		builder.setTitle(Html.fromHtml(getString(R.string.title_import_export)));
		builder.setNeutralButton(R.string.button_return, this);
		builder.setPositiveButton(R.string.button_export, this);
		builder.setNegativeButton(R.string.button_import, this);
		
		mEditText = new EditText(getActivity());
		mEditText.setText(getDefaultDir());

		builder.setView(mEditText);		
		
		return builder.create();
	}

	/**
	 * 
	 * Return the backedUp xml file name or create a new one in the default external storage
	 * named "SleekTodoData.xml"
	 * 
	 * @return the default path for the xml file
	 */
	private CharSequence getDefaultDir() {
		SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
		String defaultXmlFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "SleekTodoData.xml").toString();
		return pref.getString(PREF_IMP_EXP_DIR, defaultXmlFile);
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		String path = mEditText.getText().toString();
		SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
		Editor ed = pref.edit();
		ed.putString(PREF_IMP_EXP_DIR, path);
		ed.commit();

		switch (arg1) {
		case DialogInterface.BUTTON_POSITIVE :
			exportTodos(path);
			break;
		case DialogInterface.BUTTON_NEGATIVE :
			importTodos(path);
			mListener.importDone();
			break;
		default:
			return;
		}
	}

	private void exportTodos(String path) {
		String projection[] = {
				TodoItemContract.COLUMN_NAME_TEXT,
				TodoItemContract.COLUMN_NAME_CHECKED,
				TodoItemContract.COLUMN_NAME_FLAG,
				TodoItemContract.COLUMN_NAME_CATEGORY,
		};
		XmlTodoSerialize ser = new XmlTodoSerialize();
		Cursor c = this.getActivity().getContentResolver().query(TodoItemContract.TODO_URI, projection, null, null, null);
		TodoItem item;
		
		try {
			ser.open(path);

			if(c.moveToFirst()) {
				while(!c.isAfterLast()) {
					item = TodoItem.fromCursor(c);
					ser.exportTodoItem(item);
					c.moveToNext();
				}
				ser.exportCategoryList(new CategoryManager(getActivity()));
				Toast.makeText(getActivity(), String.format(getString(R.string.msg_elements_written), c.getCount()) , Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), getText(R.string.msg_export_empty), Toast.LENGTH_SHORT).show();
			}

			ser.close();
		} catch (FileNotFoundException e) {
			Toast.makeText(getActivity(), R.string.msg_cannot_write_to_file, Toast.LENGTH_SHORT).show();
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(getActivity(), R.string.msg_error_writing_to_file, Toast.LENGTH_SHORT).show();
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		} catch (TodoAddException e) {
			Toast.makeText(getActivity(), R.string.msg_error_writing_to_file, Toast.LENGTH_SHORT).show();
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		} finally {		
			c.close();	
		}
	}

	private void importTodos(String path) {
		CategoryManager cm = new CategoryManager(getActivity());
		try {
			List<TodoItem> lTodoItems = XmlTodoDeSerialize.parseTodoItems(path);
	    	ContentValues initValues = new ContentValues();

			for(TodoItem t:lTodoItems) {
				initValues.put(TodoItemContract.COLUMN_NAME_CHECKED, t.mIsChecked); // Always unchecked by default
		     	initValues.put(TodoItemContract.COLUMN_NAME_FLAG, t.mFlag);
				initValues.put(TodoItemContract.COLUMN_NAME_TEXT, t.mText);
				initValues.put(TodoItemContract.COLUMN_NAME_CATEGORY, t.mCategory);
				initValues.put(TodoItemContract.COLUMN_NAME_LONGTEXT , t.mLongText);
				initValues.put(TodoItemContract.COLUMN_NAME_DATE , t.mDate);
				getActivity().getContentResolver().insert(TodoItemContract.TODO_URI, initValues);		
			}
			
			List<Category> lCategories = XmlTodoDeSerialize.parseCategories(path);
			cm.setNbCategory(lCategories.size());
			for(Category c:lCategories) {
				cm.setCategoryName(c.id, c.mText);
			}
			
			
			Toast.makeText(getActivity(), String.format(getString(R.string.msg_elements_read), lTodoItems.size()) , Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Toast.makeText(getActivity(), R.string.msg_error_reading_from_file, Toast.LENGTH_SHORT).show();
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
			e.printStackTrace();
		} catch (TodoReadException e) {
			Toast.makeText(getActivity(), R.string.msg_error_reading_from_file, Toast.LENGTH_SHORT).show();
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}
		
	}
}

