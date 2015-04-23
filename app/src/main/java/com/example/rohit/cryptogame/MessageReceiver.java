package com.example.rohit.cryptogame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Rohit on 4/5/15.
 */
public class MessageReceiver extends BroadcastReceiver {

    MainActivity mainActivity = null;

    public void setMainActivity(MainActivity main) {
        mainActivity = main;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String message = extras.getString("Message");
            if (message.length()>0 && message.charAt(0) == '*') {
                mainActivity.updateList(message.substring(1));
            }
            else if (message.length()>0 && message.charAt(0) == '$') {
                try {
                    String s = mainActivity.RSADecrypt(Base64.decode(message.substring(1), Base64.DEFAULT));
                    mainActivity.updateScore(s);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (message.length()>0 && message.charAt(0) == '@') {
                try {
                    String s = mainActivity.RSADecrypt(Base64.decode(message.substring(1), Base64.DEFAULT));
                    mainActivity.showRequest(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (message.length()>0 && message.charAt(0) == '!') {
                try {
                    String s = mainActivity.RSADecrypt(Base64.decode(message.substring(1), Base64.DEFAULT));
                    mainActivity.requestRejected(s);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (message.length()>0 && message.charAt(0) == '#') {
                try {
                    String s = mainActivity.RSADecrypt(Base64.decode(message.substring(1), Base64.DEFAULT));
                    mainActivity.acceptRequest(s);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
            }
            Log.d("Broadcast", message);
        }
    }
}