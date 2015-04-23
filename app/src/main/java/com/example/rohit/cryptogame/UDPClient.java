package com.example.rohit.cryptogame;

import android.os.AsyncTask;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Rohit on 4/5/15.
 */
public class UDPClient {

    private AsyncTask<Void, Void, Void> async;

    public void sendMessage(final String message, final String address) {
        async = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                DatagramSocket ds = null;
                try {
                    ds = new DatagramSocket();
                    InetAddress addr = InetAddress.getByName(address);
                    DatagramPacket dp = new DatagramPacket(message.getBytes(),message.length(),addr,12345);
                    ds.setBroadcast(true);
                    Log.d("Client", "Sending message "+message+" to -"+address+"-");
                    ds.send(dp);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    if (ds!= null) {
                        ds.close();
                    }
                }
                return null;
            }
        };
        async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
