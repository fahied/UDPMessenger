package com.spider.udpmessenger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import helpers.IPAddressValidator;

/**
 * Created by Spider on 01-Sep-14.
 */
public class SetPreferenceActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener{

    //local var
    SharedPreferences prefs;
    private IPAddressValidator ipAddressValidator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //intilization
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ipAddressValidator = new IPAddressValidator();
        //
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //register your activity as a valid listener
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregister your activity as a valid listener
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        SharedPreferences.Editor prefEditor = prefs.edit();
        if(s.equals(getString(R.string.key_destination_ip))){
            String optionValue = prefs.getString(s, "");
            if(!ipAddressValidator.validateIP(optionValue)){
                showErrorDialog("Wrong IP","The IP format is not correct.");
                prefEditor.putString(s, "127.0.0.1");
                prefEditor.commit();
                //reload();
            }
        }
        else if(s.equals(getString(R.string.key_port_number))){
            String optionValue = prefs.getString(s, "");
            if(!ipAddressValidator.validatePort(optionValue)){
                showErrorDialog("Wrong Port","I dont like the option");
                prefEditor.putString(s, "10002");
                prefEditor.commit();
            }
        }

    }


    private void showErrorDialog(String dialogTitle,String errorString){
        String okButtonString = "OK";
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle(dialogTitle);
        ad.setMessage(errorString);
        ad.setPositiveButton(okButtonString,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                reload();
            } } );
        ad.show();
        return;
    }

    private void reload(){
        startActivity(getIntent());
        finish();
    }
}
