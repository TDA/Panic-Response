package com.mabers.panicresponse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends Activity implements CompoundButton.OnCheckedChangeListener {
	
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	
	boolean fallDetection;
	Switch fallDetectionSwitch;
	
	Intent sensors;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		fallDetectionSwitch=(Switch)findViewById(R.id.fall_detection_switch);
		
		sharedPref = getSharedPreferences(getString(R.string.panic_response_settings_file ),
				Context.MODE_PRIVATE);
				
		fallDetection = sharedPref.getBoolean(getString(R.string.fall_detection_switch), false);
		
		if(fallDetection)
			fallDetectionSwitch.setChecked(true);
		else
			fallDetectionSwitch.setChecked(false);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.languages_array,android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		
		fallDetectionSwitch.setOnCheckedChangeListener( this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	      // Toast.makeText(this, "Fall Detection is " + (isChecked ? "on" : "off"),
	        //       Toast.LENGTH_SHORT).show();
	       editor = sharedPref.edit();
	       
	       editor.putBoolean(getString(R.string.fall_detection_switch), isChecked);
	       editor.commit();
	       /*
	       if(isChecked)
	       {
	    	   
				sensors = new Intent();
				sensors.setClass(this,SensorsService.class);
				startService(sensors);
	       }
	       else
	       {
	    	   stopService(sensors);
	       }*/
	       
	    }
	
	public void submitSettings(View v)
	{
		Toast.makeText(this,"Settings Saved",Toast.LENGTH_SHORT).show();
		
		if((sharedPref.getBoolean(getString(R.string.fall_detection_switch), false)))
		{
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
		else
		{
			Intent intent = new Intent(this, HelpButtonActivity.class);
			startActivity(intent);
		}
	}
	
	public void changeLanguage(View v)
	{
		startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
	}

	

}
