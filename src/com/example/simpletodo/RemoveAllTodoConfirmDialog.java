package com.example.simpletodo;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class RemoveAllTodoConfirmDialog extends DialogFragment implements DialogInterface.OnClickListener {

    RemoveAllTodosDialogListener mListener;

    public interface RemoveAllTodosDialogListener {
        public void onDialogConfirmRemoveAll();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (RemoveAllTodosDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
 	   if(which == DialogInterface.BUTTON_POSITIVE)
 		   mListener.onDialogConfirmRemoveAll();
    }
    
    @Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	
	    Builder builder = new Builder(getActivity());
	    builder.setMessage(R.string.pick_areyousure);
	    builder.setPositiveButton(R.string.edit_button_remove_all, this);
	    builder.setNegativeButton(R.string.edit_button_cancel, this);
	    return builder.create();
	}
}
