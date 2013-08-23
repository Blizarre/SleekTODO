package com.smfandroid.sleektodo;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;

public class HelpDialog extends DialogFragment {

    @Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	
	    Builder builder = new Builder(getActivity());
	    builder.setMessage(Html.fromHtml(getString(R.string.msg_help)));
	    builder.setNeutralButton(R.string.button_continue, null);
	    return builder.create();
	}
}
