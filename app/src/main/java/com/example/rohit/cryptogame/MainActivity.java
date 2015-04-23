package com.example.rohit.cryptogame;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Base64;
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

import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Cipher;


public class MainActivity extends Activity {

    private UDPClient client;
    private UDPServer server;
    private MessageReceiver messageReceiver;
    private FrameLayout fl;
    private SharedPreferences sharedPref;
    private FragmentManager fm;
    private UsernameFragment fragment;
    private FinalScreen scoreFragment;
    private TextView welcome_text;
    private HashMap<String, String> userList;
    private HashMap<String, String> pubKeys;
    private ListView listView;
    private String username;
    private ProgressBar spinner;
    private int userScore;
    public String ip;
    Timer scoreTimer;
    Timer ipTimer;
    public boolean isFinalActive;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    String publicKeyString;
    public PublicKey opponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        server = new UDPServer(this);
        client = new UDPClient();
        messageReceiver = new MessageReceiver();

        userScore = 0;
        isFinalActive = false;

        fragment = new UsernameFragment();
        userList = new HashMap<>();
        pubKeys = new HashMap<>();
        listView = (ListView) findViewById(R.id.listView);

        spinner = (ProgressBar) findViewById(R.id.progressBar1);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("List",listView.getItemAtPosition(position).toString());
                Bundle bundle = new Bundle();
                bundle.putString("message","Send a game request to "+listView.getItemAtPosition(position).toString());
                bundle.putInt("id", 1);
                bundle.putString("ip", listView.getItemAtPosition(position).toString().split(" ")[0]);
                bundle.putString("key",pubKeys.get(listView.getItemAtPosition(position).toString().split(" ")[0]));
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

        ipTimer = new Timer();
        ipTimer.scheduleAtFixedRate(new TimerTask() {
                                        @Override
                                        public void run() {
                                            broadCastSelf(ip + "-" + username+"-"+publicKeyString);
                                        }
                                    },
                0,
                5000);

        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KeyPair kp = kpg.genKeyPair();
            publicKey = kp.getPublic();
            publicKeyString = new String(Base64.encode(publicKey.getEncoded(),Base64.DEFAULT));
            privateKey = kp.getPrivate();
            //RSADecrypt(RSAEncrypt("Hello! World",publicKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] RSAEncrypt(final String plain, PublicKey key) throws Exception{
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(plain.getBytes());
        return encryptedBytes;
    }

    public PublicKey getKeyFromString(String s) throws Exception {
        byte[] b = Base64.decode(s,Base64.DEFAULT);
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(b));
    }

    public String RSADecrypt(final byte[] encryptedBytes) throws Exception{
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        String decrypted = new String(decryptedBytes);
        return decrypted;
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
        pubKeys.put(split[0],split[2]);
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
            to = to.split(":")[1].replaceAll("\\s+","");
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
        intent.putExtra("ip",message);
        startActivityForResult(intent, 1);
        Toast.makeText(this, message+" accepted the request", Toast.LENGTH_SHORT).show();
    }

    public void requestRejected(String substring) {
        spinner.setVisibility(View.GONE);
        Toast.makeText(this, substring+" rejected the request", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            isFinalActive = true;
            setUserScore(data.getIntExtra("score", 0));
            scoreFragment = new FinalScreen();
            broadcastScore(data.getStringExtra("ip"));
            fl.setVisibility(View.VISIBLE);
            fm.beginTransaction().add(R.id.frame_container,scoreFragment).commit();
            Toast.makeText(this,"GAME ENDED", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUserScore(int score) {
        userScore = score;
    }

    public int getUserScore() {
        return userScore;
    }

    public void broadcastScore(final String opponentIP) {
        ipTimer.cancel();
        scoreTimer = new Timer();
        scoreTimer.scheduleAtFixedRate(new TimerTask() {
                                           @Override
                                           public void run() {
                                               try {
                                                   byte[] encrypt = RSAEncrypt(Integer.toString(userScore), opponent);
                                                   client.sendMessage('$'+new String(Base64.encode(encrypt, Base64.DEFAULT)), opponentIP);
                                               } catch (Exception e){
                                                   e.printStackTrace();
                                               }
                                           }
                                       },
                0,
                1000);
    }

    public void updateScore(String s) {
        scoreTimer.cancel();
        scoreFragment.updateScore(s);
    }
}