package com.example.rohit.cryptogame;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {

    private UDPClient client;
    private UDPServer server;
    private MessageReceiver messageReceiver;
    private FrameLayout fl;
    private SharedPreferences sharedPref;
    private FragmentManager fm;
    private UsernameFragment fragment;
    private TextView welcome_text;
    private HashMap<String, String> userList;
    private ListView listView;
    private String username;
    private ProgressBar spinner;
    public String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        server = new UDPServer(this);
        client = new UDPClient();
        messageReceiver = new MessageReceiver();

        fragment = new UsernameFragment();
        userList = new HashMap<>();
        listView = (ListView) findViewById(R.id.listView);

        spinner = (ProgressBar) findViewById(R.id.progressBar1);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("List",listView.getItemAtPosition(position).toString());
                Bundle bundle = new Bundle();
                bundle.putString("message","Send a game request to "+listView.getItemAtPosition(position).toString());
                bundle.putInt("id", 1);
                bundle.putString("ip",listView.getItemAtPosition(position).toString().split(" ")[0]);
                ConfirmDialog cd = new ConfirmDialog();
                cd.setArguments(bundle);
                cd.show(fm, "Dialog");
            }
        });

        messageReceiver.setMainActivity(this);

        server.runServer();

        sharedPref = getPreferences(Context.MODE_PRIVATE);

        IntentFilter intentFilter = new IntentFilter("MessageReceived");
        registerReceiver(messageReceiver,intentFilter);

        welcome_text = (TextView) findViewById(R.id.txt_welcome);

        username = sharedPref.getString("username","default");

        fl = (FrameLayout) findViewById(R.id.frame_container);
        fm = getFragmentManager();

        if(username.equals("default")) {
            showUserFrag();
        }
        else {
            welcome_text.setText("Welcome " + username);
        }
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
                                  @Override
                                  public void run() {
                                      broadCastSelf(ip+"-"+username);
                                  }
                              },
                0,
                5000);
    }

    private void broadCastSelf(String s) {
        Log.d("My IP",s);
        client.sendMessage("*"+s,"255.255.255.255");
    }

    @Override
    protected void onStop() {
        try{
            unregisterReceiver(messageReceiver);
        }
        catch (Exception e) {
            Log.d("Unregister","Receiver unregistered");
        }
        finally {
            super.onStop();
        }
    }

    @Override
    protected void onPause() {
        server.stopServer();
        super.onPause();
    }

    @Override
    protected void onResume() {
        server.resumeServer();
        super.onResume();
    }

    @Override
    protected void onRestart() {
        IntentFilter intentFilter = new IntentFilter("MessageReceived");
        registerReceiver(messageReceiver,intentFilter);
        super.onRestart();
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
        else if (id == R.id.actions_name) {
            showUserFrag();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUserFrag() {
        fl.setVisibility(View.VISIBLE);
        fm.beginTransaction().add(R.id.frame_container,fragment).commit();
    }

    public void saveUser(String s) {
        Log.d("UsernameFromFrag",s);
        username = s;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username",s);
        editor.commit();
        fm.beginTransaction().remove(fragment).commit();
        welcome_text.setText("Welcome " + s);
        fl.setVisibility(View.GONE);
    }

    public void updateList(String s) {
        String[] split = s.split("-");
        if(!userList.containsKey(split[0]) || !userList.get(split[0]).equals(split[1])){
            userList.put(split[0],split[1]);
            List<String> list = getList(userList);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,list);
            listView.setAdapter(adapter);
        }
    }

    public List<String> getList(HashMap<String, String> hm) {
        List<String> list = new ArrayList<>();
        for (String s : hm.keySet()) {
            list.add(s+" ("+hm.get(s)+")");
        }
        return list;
    }

    public void sendRequest(String from, String to, int operation) {
        if (operation == 1) {
            spinner.setVisibility(View.VISIBLE);
            client.sendMessage("@"+from,to);
        }
        else if (operation == 2) {
            to = to.split(":")[1].replaceAll("\\s+","");;
            client.sendMessage("#"+from,to);
        }
        else if (operation == 3) {
            to = to.split(":")[1].replaceAll("\\s+","");
            client.sendMessage("!"+from,to);
        }
    }

    public void showRequest(String ip) {
        Bundle bundle = new Bundle();
        bundle.putString("message","Accept game request from: "+ip);
        bundle.putInt("id", 2);
        ConfirmDialog cd = new ConfirmDialog();
        cd.setArguments(bundle);
        cd.show(fm,"Dialog2");
    }

    public void acceptRequest(String message) {
        spinner.setVisibility(View.GONE);
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        Toast.makeText(this, message+" accepted the request", Toast.LENGTH_SHORT);
    }

    public void requestRejected(String substring) {
        spinner.setVisibility(View.GONE);
        Toast.makeText(this, substring+" rejected the request", Toast.LENGTH_LONG);
    }
}
