package com.smfandroid.sleektodo;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class FontSizeDialog extends DialogFragment implements DialogInterface.OnClickListener {

    NoticeDialogListener mListener;

    public interface NoticeDialogListener {
        public void onDialogSelect(int which);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
 	   mListener.onDialogSelect(which);
    }
    
    @Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	
	    Builder builder = new Builder(getActivity());
	    builder.setTitle(R.string.pick_font_size);
	    
	    builder.setItems(R.array.font_size, this);
	    return builder.create();
	}
}
