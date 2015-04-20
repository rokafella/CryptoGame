package com.example.rohit.cryptogame;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by Rohit on 4/5/15.
 */
public class UDPServer {

    private Context context;
    private AsyncTask<Void, Void, Void> async;
    private boolean server_active;
    private DatagramSocket ds = null;

    public UDPServer(Context context) {
        this.context = context;
        server_active = false;
    }

    public void runServer() {
        server_active = true;
        async = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                byte[] Msg = new byte[4096];
                DatagramPacket dp = new DatagramPacket(Msg, Msg.length);

                try {
                    ds = new DatagramSocket(12345);
                    while (server_active) {
                        Log.d("Server", "Server is running");
                        ds.receive(dp);
                        Intent i = new Intent();
                        i.setAction("MessageReceived");
                        i.putExtra("Message",new String(Msg,0,dp.getLength()));
                        context.getApplicationContext().sendBroadcast(i);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    if (ds != null) {
                        ds.close();
                    }
                }
                return null;
            }
        };
        async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void stopServer() {
        server_active = false;
    }

    public void resumeServer() {
        server_active = true;
    }
}
