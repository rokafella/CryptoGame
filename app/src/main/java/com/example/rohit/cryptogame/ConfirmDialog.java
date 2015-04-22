package com.example.rohit.cryptogame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Rohit on 4/19/15.
 */
public class ConfirmDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle bundle = this.getArguments();
        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String sendButton = "Send";
        if (bundle.getInt("id") == 2) {
            sendButton = "Accept";
        }
        builder.setMessage(bundle.get("message").toString())
                .setPositiveButton(sendButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("Dialog", "send");
                        if (bundle.getInt("id") == 1) {
                            ((MainActivity)getActivity()).sendRequest(((MainActivity)getActivity()).ip,bundle.get("ip").toString(),1);
                        }
                        else if(bundle.getInt("id") == 2) {
                            Log.d("Dialog","Request Accepted");
                            ((MainActivity)getActivity()).sendRequest(((MainActivity)getActivity()).ip,bundle.get("message").toString(),2);
                            ((MainActivity)getActivity()).acceptRequest(bundle.get("message").toString());
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("Dialog", "cancel");
                        if(bundle.getInt("id") == 2) {
                            ((MainActivity)getActivity()).sendRequest(((MainActivity)getActivity()).ip,bundle.get("message").toString(),3);
                        }
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
