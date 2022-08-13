package com.example.chaitanya.imtocal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    //private SendDataListener listener;
    public DatabaseHelper Im2CalDb;
    public static final String PREFS_NAME = "MyPrefsFile";
    GlobalClass globalClass;
    //public SocketClient socketClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG,"Im2Cal app started!!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //socketClient = new SocketClient();
        globalClass = (GlobalClass)getApplicationContext();
        //setSendDataListener(globalClass.socketClient);
        runTcpClientThread();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                //listener.sendDataTCP();
            }
        });
        Im2CalDb  = new DatabaseHelper(this);

    }
    public void runTcpClientThread(){
        SharedPreferences preferences =  getSharedPreferences(PREFS_NAME,0);
        boolean tcpFlag = preferences.getBoolean("TCP_FLAG",false);
        if(!tcpFlag){
            preferences.edit().putBoolean("TCP_FLAG",true);
            //tcpClient.execute();
            Log.d(LOG_TAG,"about to start socketclient!!");
            MainActivityFragment mainActivityFragment = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            globalClass.socketClient.addUpdateListener(mainActivityFragment);
            globalClass.socketClient.start_thread();

        }
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
        if (id == R.id.action_debug) {
            startActivity(new Intent(this,DebugActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /*
    public void setSendDataListener(SendDataListener listener){
        this.listener = listener;
    }
    public interface SendDataListener {
        public void sendDataTCP();
    }
*/




}
