package com.example.rohit.cryptogame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.security.PublicKey;

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
                if (bundle.getInt("id") == 1) {
                    try {
                        final String k = bundle.getString("key");
                        final PublicKey key = ((MainActivity) getActivity()).getKeyFromString(k);
                        ((MainActivity) getActivity()).opponent = key;
                        byte[] encrypt = ((MainActivity) getActivity()).RSAEncrypt(((MainActivity) getActivity()).ip, key);
                        ((MainActivity) getActivity()).sendRequest(new String(Base64.encode(encrypt, Base64.DEFAULT)), bundle.get("ip").toString(), 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (bundle.getInt("id") == 2) {
                    Log.d("Dialog", "Request Accepted");
                    try {
                        byte[] encrypt = ((MainActivity) getActivity()).RSAEncrypt(((MainActivity) getActivity()).ip, ((MainActivity) getActivity()).opponent);
                        ((MainActivity) getActivity()).sendRequest(new String(Base64.encode(encrypt, Base64.DEFAULT)), bundle.get("message").toString(), 2);
                        ((MainActivity) getActivity()).acceptRequest(bundle.get("message").toString().split(":")[1].replaceAll("\\s+", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d("Dialog", "cancel");
                if (bundle.getInt("id") == 2) {
                    try {
                        byte[] encrypt = ((MainActivity) getActivity()).RSAEncrypt(((MainActivity) getActivity()).ip, ((MainActivity) getActivity()).opponent);
                        ((MainActivity) getActivity()).sendRequest(new String(Base64.encode(encrypt, Base64.DEFAULT)), bundle.get("message").toString(), 3);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
