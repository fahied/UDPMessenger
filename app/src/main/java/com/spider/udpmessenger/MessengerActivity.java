package com.spider.udpmessenger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.CheckBox;


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


}
