package com.mabers.panicresponse;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class CareTakersInformationActivity extends Activity {

	SharedPreferences sharedPref;
	String ct1n, ct2n;
	Long ct1p, ct2p;
	ScrollView scrollBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_care_takers_information);
		
		scrollBar = (ScrollView)findViewById(R.id.editCareTakerScroll);
		
		TextView titleTV = (TextView) findViewById(R.id.topbar_title_tv);
		titleTV.setText(getResources().getString(R.string.title_activity_care_takers_information));

		sharedPref = getSharedPreferences(getString(R.string.information_file),
				Context.MODE_PRIVATE);
		int isDataSaved = sharedPref.getInt(getString(R.string.isDataSaved), 0);
		if (isDataSaved == 1)
			setData();
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.care_takers_information, menu);
		return true;
	}

	public void setData() {
		ct1n = sharedPref.getString(getString(R.string.careTaker1Name),
				"no careTaker1");
		ct2n = sharedPref.getString(getString(R.string.careTaker2Name),
				"no careTaker1");
		ct1p = sharedPref.getLong(getString(R.string.careTaker1Phone), 0);
		ct2p = sharedPref.getLong(getString(R.string.careTaker2Phone), 0);
		((TextView) findViewById(R.id.careTakerNameText1)).setText(ct1n);
		((TextView) findViewById(R.id.careTakerNameText2)).setText(ct2n);
		((TextView) findViewById(R.id.careTakerPhoneText1)).setText("" + ct1p);
		((TextView) findViewById(R.id.careTakerPhoneText2)).setText("" + ct2p);

	}

	public void nextCareTakers(View v) {

		EditText ct1Name, ct2Name, ct1Phone, ct2Phone;
		ct1Name = ((EditText) findViewById(R.id.careTakerNameText1));
		ct2Name = ((EditText) findViewById(R.id.careTakerNameText2));
		ct1Phone = ((EditText) findViewById(R.id.careTakerPhoneText1));
		ct2Phone = ((EditText) findViewById(R.id.careTakerPhoneText2));

		// Validate First Care Taker Name
		if (ct1Name.getText().toString().length() < 1) {
			ct1Name.setError("Enter a Valid Name");
			ct1Name.requestFocus();
			return;
		} else {
			ct1Name.setError(null);
			ct1Name.clearFocus();
		}

		// Validate First CareTaker's Phone Number.
		if ((ct1Phone != null)
				&& ((ct1Phone.getText().toString()).matches("[0-9]{10}"))) {
			ct1Phone.setError(null);
			ct1Phone.clearFocus();
		} else {
			ct1Phone.setError("Enter a valid Number");
			ct1Phone.requestFocus();
			return;
		}

		// Validate Second Care Taker Name
		if (ct2Name.getText().toString().length() < 1) {
			ct2Name.setError("Enter a Valid Name");
			ct2Name.requestFocus();
			return;
		} else {
			ct2Name.setError(null);
			ct2Name.clearFocus();
		}

		// Validate Second CareTakers Phone Number.
		if ((ct2Phone != null)
				&& ((ct2Phone.getText().toString()).matches("[0-9]{10}"))) {
			ct2Phone.setError(null);
			ct2Phone.clearFocus();
		} else {
			ct2Phone.setError("Enter a valid Number");
			ct2Phone.requestFocus();
			return;
		}

		Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.careTaker1Name),
				((EditText) findViewById(R.id.careTakerNameText1)).getText()
						.toString());
		editor.putLong(getString(R.string.careTaker1Phone), Long
				.parseLong(((EditText) findViewById(R.id.careTakerPhoneText1))
						.getText().toString()));
		editor.putString(getString(R.string.careTaker2Name),
				((EditText) findViewById(R.id.careTakerNameText2)).getText()
						.toString());
		editor.putLong(getString(R.string.careTaker2Phone), Long
				.parseLong(((EditText) findViewById(R.id.careTakerPhoneText2))
						.getText().toString()));

		editor.commit();

		Intent intent = new Intent(this, MedicinalDetails.class);

		startActivity(intent);
	}
	public void scrollDown()
	{
		scrollBar.fullScroll(View.FOCUS_DOWN);
	}	
	
	
}
