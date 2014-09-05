package com.spider.udpmessenger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import helpers.UDPMessenger;


public class MessengerActivity extends Activity {

    // local var
    CheckBox broadCastCheckBox;
    private SharedPreferences sharedPreferences;
    private Boolean broadcasting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        //UI
        broadCastCheckBox = (CheckBox)findViewById(R.id.broadcasting);
        //initialize
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //load preference into local var
        loadPref();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.messenger, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
/*
		 * Because it's only ONE option in the menu.
		 * In order to make it simple, We always start SetPreferenceActivity
		 * without checking.
		 */

        Intent intent = new Intent();
        intent.setClass(MessengerActivity.this, SetPreferenceActivity.class);
        startActivityForResult(intent, 0);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*
		 * To make it simple, always re-load Preference setting.
		 */
        loadPref();
    }

    private void loadPref() {
        // broad to all preference
        boolean checkbox_preference = sharedPreferences.getBoolean("checkbox_preference", false);
        broadCastCheckBox.setChecked(checkbox_preference);
        broadcasting = checkbox_preference;
    }


    /** Called when the user touches the button */
    public void sendMessageSequence(View view) throws IOException, InterruptedException {
        // Do something in response to button click
        Button button = (Button)view;
        String messageSequence = sharedPreferences.getString(button.getText().toString(), "");

/*
        ExecutorService service =  Executors.newFixedThreadPool(10);
        UDPMessenger sendTask = new UDPMessenger(this,messageSequence,broadcasting);
        Future<String> future = service.submit(sendTask);
        String  result = null;
        try {
            result = future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(result);
*/

        Thread messengerThread = new Thread(new UDPMessenger(this,messageSequence,broadcasting));
        messengerThread.start();
    }


    private void showErrorDialog(String dialogTitle,String errorString){
        String okButtonString = "OK";
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle(dialogTitle);
        ad.setMessage(errorString);
        ad.show();
        return;
    }
}
