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

import helpers.UDPMessenger;


public class MessengerActivity extends Activity {

    // local var
    CheckBox broadCastCheckBox;
    private SharedPreferences sharedPreferences;
    private Boolean broadcasting;

    // custom udp messenger to send/broadcast message
    private UDPMessenger udpMessenger;
    // local ref var to preferences
    private String destinationIP;
    private int destinationPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        //UI
        broadCastCheckBox = (CheckBox)findViewById(R.id.broadcasting);

        //initialize
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //udp init
        udpMessenger = new UDPMessenger();
        try {
            udpMessenger.startUDPMessenger();
        } catch (SocketException e) {
            e.printStackTrace();
        }

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

        //Destination IP preference
        destinationIP   = sharedPreferences.getString(getString(R.string.key_destination_ip),"127.0.0.1");
        String port = sharedPreferences.getString(getString(R.string.key_port_number), "20002");
        destinationPort = Integer.parseInt(port);

    }


    /** Called when the user touches the button */
    public void sendMessageSequence(View view) throws IOException, InterruptedException {
        // Do something in response to button click
        Button button = (Button)view;
        String messageSequence = sharedPreferences.getString(button.getText().toString(), "");
        handleUDPRequest(messageSequence);
    }


    private void handleUDPRequest(String sequenceMessage) throws InterruptedException, IOException {
        if (sequenceMessage.equalsIgnoreCase(""))
        {
            showErrorDialog("Empty Sequence","Go to settings and define message sequence");
           return;
        }
        String[]parts = sequenceMessage.split("\n");
        for (int i = 0; i < parts.length;i++)
        {
            String part = parts[i];
            //if the message part is numeric then probably it is delay otherwise a message to be sent
            if (isNumeric(part))
            {
                int delay  = Integer.parseInt(parts[i]);
                Thread.sleep(delay);
            }
            else if (!part.equalsIgnoreCase(""))
            {
                if (broadcasting)
                    udpMessenger.broadcastUDPMessage(part,destinationPort);
                else udpMessenger.sendUDPMessage(part,destinationPort,destinationIP);
            }
            // if all messages has been sent
            if (i == parts.length-1)
            showErrorDialog("Messages sent successfully",sequenceMessage);
        }
    }

    public static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
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
