package com.example.rohit.cryptogame;

import android.app.Activity;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    private UDPClient client;
    private TextView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UDPServer server = new UDPServer(this);
        client = new UDPClient();
        MessageReceiver messageReceiver = new MessageReceiver();

        messageReceiver.setMainActivity(this);
        server.runServer();

        list = (TextView) findViewById(R.id.txt_list);

        Button send = (Button) findViewById(R.id.btn_send);

        IntentFilter intentFilter = new IntentFilter("MessageReceived");
        registerReceiver(messageReceiver,intentFilter);

        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        final String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
                                  @Override
                                  public void run() {
                                      broadCastSelf(ip);
                                  }
                              },
                0,
                2000);
    }

    private void broadCastSelf(String s) {
        Log.d("My IP",s);
        client.sendMessage("*"+s,"255.255.255.255");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateList(String message) {
        list.append("\n"+ message);
    }
}
