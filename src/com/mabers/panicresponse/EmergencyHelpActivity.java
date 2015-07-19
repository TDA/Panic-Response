package com.mabers.panicresponse;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class EmergencyHelpActivity extends Activity {

	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	String eh;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emergency_help);
		
		TextView titleTV = (TextView) findViewById(R.id.topbar_title_tv);
		titleTV.setText(getResources().getString(R.string.title_activity_emergency_help));
		
		

		sharedPref = getSharedPreferences(getString(R.string.information_file),
				Context.MODE_PRIVATE);
		int isDataSaved = sharedPref.getInt(getString(R.string.isDataSaved), 0);
		if (isDataSaved == 1)
			setData();
		editor = sharedPref.edit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.emergency_help, menu);
		return true;
	}

	private void setData() {
		eh = sharedPref.getString(getString(R.string.emergencyHelp),
				"no help info");
		((TextView) findViewById(R.id.emergencyHelpText)).setText(eh);
	}

	public void submit(View v) {
		EditText emerHelp = (EditText) findViewById(R.id.emergencyHelpText);
		if (emerHelp.getText().toString().length() < 1) {
			emerHelp.setError("Enter Valid Data");
			emerHelp.requestFocus();
			return;
		} else {
			emerHelp.setError(null);
			emerHelp.clearFocus();
		}

		editor.putString(getString(R.string.emergencyHelp),
				((EditText) findViewById(R.id.emergencyHelpText)).getText()
						.toString());
		editor.putInt(getString(R.string.isDataSaved), 1);

		editor.commit();
		
	    


		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);

	}
}
