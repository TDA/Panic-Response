package com.mabers.panicresponse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class UserDetailsActivity extends Activity {
	char where = 'u';
	EditText naam;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	String usnm, usadd, ct1n, ct2n, meds, eh;
	long ct1p, ct2p;
	int isDataSaved;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info_form);
		startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
		
		TextView titleTV = (TextView) findViewById(R.id.topbar_title_tv);
		titleTV.setText(getResources().getString(R.string.title_activity_user_details));
		
		naam = (EditText) findViewById(R.id.userNameText);
		naam.requestFocus();
		sharedPref = getSharedPreferences(getString(R.string.information_file),
				Context.MODE_PRIVATE);
		int isDataSaved = sharedPref.getInt(getString(R.string.isDataSaved), 0);
		if (isDataSaved == 1)
			setData();
		editor = sharedPref.edit();
	}

	public void setData() {
		usnm = sharedPref.getString(getString(R.string.user_Name), "no_name");
		usadd = sharedPref.getString(getString(R.string.user_address),
				"no_address");

		((TextView) findViewById(R.id.userNameText)).setText(usnm);
		((TextView) findViewById(R.id.userAddressText)).setText(usadd);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.blnk, menu);
		return true;
	}

	public String name, address;

	public void saveInfo(View v) {
		EditText usNmText, usAdText;
		usNmText = (EditText) findViewById(R.id.userNameText);
		usAdText = (EditText) findViewById(R.id.userAddressText);

		if (usNmText.getText().toString().length() < 1) {
			usNmText.setError("Enter a Valid Name");
			usNmText.requestFocus();
			return;
		} else {
			usNmText.setError(null);
			usNmText.clearFocus();
		}

		if (usAdText.getText().toString().length() < 1) {
			usAdText.setError("Enter a Valid Address");
			usAdText.requestFocus();
			return;
		} else {
			usAdText.setError(null);
			usAdText.clearFocus();
		}

		editor.putString(getString(R.string.user_Name),
				((EditText) findViewById(R.id.userNameText)).getText()
						.toString());
		editor.putString(getString(R.string.user_address),
				((EditText) findViewById(R.id.userAddressText)).getText()
						.toString());
		editor.commit();

		Intent intent = new Intent(this, CareTakersInformationActivity.class);

		startActivity(intent);

	}
	

	

}
