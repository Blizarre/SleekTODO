package com.smfandroid.sleektodo;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;

public class EditTextDialog extends DialogFragment implements OnClickListener {

	interface EditTextDialogInterface {
		public void setNewValue(String val);
	}
	
	protected EditTextDialogInterface mListener;
	
	private EditText mEditText;
	public static String BUNDLE_KEY_DEFAULT_VAL = "defval";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Builder builder = new Builder(getActivity());
		builder.setPositiveButton(R.string.button_ok, this);
		builder.setNeutralButton(R.string.button_cancel, this);
		
		mEditText = new EditText(getActivity());
		mEditText.setText(getArguments().getString(BUNDLE_KEY_DEFAULT_VAL));

		builder.setView(mEditText);		
		
		return builder.create();
	}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the EditTextDialogInterface so we can send events to the host
            mListener = (EditTextDialogInterface)activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement EditTextDialogInterface");
        }
    }	

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		if(arg1 == Dialog.BUTTON_POSITIVE) {
			String newCatName = mEditText.getText().toString();
			mListener.setNewValue(newCatName);
		}
	}
}

