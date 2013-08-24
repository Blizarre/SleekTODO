package com.smfandroid.sleektodo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.Toast;

import com.smfandroid.sleektodo.EditTextDialog.EditTextDialogInterface;

public class CategoriesEditorActivity extends FragmentActivity implements EditTextDialogInterface {
	
	CategoryManager mCatManager;
	ArrayAdapter<String> mArrayList;
	boolean mChangesHappened;
	int mCurrentSelectedItem = -1;
	
	 void setCategoriesChanged() {
		 mChangesHappened = true;
		 mArrayList.notifyDataSetChanged();
	 }
	
	@Override
	public void onBackPressed() {
		if(mChangesHappened)
			setResult(RESULT_OK);
		else
			setResult(RESULT_CANCELED);
		super.onBackPressed();
	};
	
	public void onOk(View v) {
		onBackPressed();
	}
	
	public void addCategory(View v) {
		int currentNbCat = mCatManager.getNbCategory();
		mCatManager.setNbCategory(currentNbCat + 1);
		setCategoriesChanged();
		mChangesHappened = true;
	}
	
	public void removeCategory(View v) {
		int currentNbCat = mCatManager.getNbCategory();
		if(currentNbCat <= 2) {
			Toast.makeText(this, R.string.msg_error_removing_cat, Toast.LENGTH_SHORT).show();
		} else {
			mCatManager.setNbCategory(currentNbCat - 1);
			setCategoriesChanged();
		}
		mChangesHappened = true;
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_editor);
        
        mCatManager = new CategoryManager(this);
        mChangesHappened = false;

        ListView lView = (ListView)findViewById(R.id.cat_list);
        mArrayList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mCatManager.getCategoryArray());

        lView.setAdapter(mArrayList);
        lView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				EditTextDialog dlg = new EditTextDialog();
				Bundle bd = new Bundle();
				bd.putString(EditTextDialog.BUNDLE_KEY_DEFAULT_VAL, mCatManager.getCategoryName(position));
				dlg.setArguments(bd);
				mCurrentSelectedItem = position;
				dlg.show(getSupportFragmentManager(), "EditTextDialog");
			}
		});
	}

	@Override
	public void setNewValue(String val) {
		mCatManager.setCategoryName(mCurrentSelectedItem,  val);
		setCategoriesChanged();
		mCurrentSelectedItem = -1;
	}

}
