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
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categry_editor);
        
        mCatManager = new CategoryManager(this);
        mChangesHappened = false;

        NumberPicker nPick = (NumberPicker)findViewById(R.id.cat_num_pick);

        nPick.setMinValue(0);
        nPick.setMaxValue(50);
        nPick.setValue(mCatManager.getNbCategory());
        
        nPick.setOnValueChangedListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				if(newVal < mCatManager.getNbCategory()) {
					// TODO: ask user if it is really the desired effect
					Toast.makeText(CategoriesEditorActivity.this,  "Move items ?", Toast.LENGTH_SHORT).show();
				}
				mCatManager.setNbCategory(newVal);
				setCategoriesChanged();
			}
		});

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
