package com.ankoma88.mobiweekend;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.widget.EditText;

public class IdChooserFragment extends DialogFragment {
    public static final String EXTRA_ID_INPUT = "com.ankoma88.mobiweekend.flickrId";
    public static final String EDITTEXT ="Find out your ID on idgettr.com";

    private String mFlickrId;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EditText idInput = new EditText(getActivity());
        idInput.setInputType(InputType.TYPE_CLASS_TEXT);
        idInput.setText(EDITTEXT);
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.flickrId)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mFlickrId = idInput.getText().toString();
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setView(idInput)
                .create();
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent i = new Intent();
        i.putExtra(EXTRA_ID_INPUT, mFlickrId);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }
}
