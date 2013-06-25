package com.smfandroid.sleektodo;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Html;
import com.smfandroid.sleektodo.R;

public class HelpDialog extends DialogFragment {

    @Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	
	    Builder builder = new Builder(getActivity());
	    builder.setMessage(Html.fromHtml(getString(R.string.msg_help)));
	    builder.setNeutralButton(R.string.button_continue, null);
/*	    builder.setPositiveButton(R.string.edit_button_remove_all, this);
	    builder.setNegativeButton(R.string.edit_button_cancel, this); */
	    return builder.create();
	}
}
