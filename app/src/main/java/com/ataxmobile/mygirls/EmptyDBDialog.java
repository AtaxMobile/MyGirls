package com.ataxmobile.mygirls;


import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;

public class EmptyDBDialog extends DialogFragment implements DialogInterface.OnClickListener {


    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.TitleDBEmpty).setPositiveButton(R.string.yes, this);

        View view = getActivity().getLayoutInflater().inflate(R.layout.emptydb_dialog_layout, null);
        adb.setView(view);
        return adb.create();
    }


    public void onClick(DialogInterface dialog, int which) {
        int i = 0; /*
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                i = R.string.yes;
                break;
            case Dialog.BUTTON_NEGATIVE:
                break;
            case Dialog.BUTTON_NEUTRAL:
                break;
        } */
    }

}
