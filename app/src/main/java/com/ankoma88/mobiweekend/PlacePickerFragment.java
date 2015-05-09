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

public class PlacePickerFragment extends DialogFragment {

    public static final String EXTRA_PLACE_INPUT = "com.ankoma88.mobiweekend.placeInput";

    private String mPlace;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EditText placeInput = new EditText(getActivity());
        placeInput.setInputType(InputType.TYPE_CLASS_TEXT);
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.location)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPlace = placeInput.getText().toString();
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setView(placeInput)
                .create();
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent i = new Intent();
        i.putExtra(EXTRA_PLACE_INPUT, mPlace);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }
}
