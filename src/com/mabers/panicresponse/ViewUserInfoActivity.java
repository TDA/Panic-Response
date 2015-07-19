package com.mabers.panicresponse;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class ViewUserInfoActivity extends Activity {

	String usnm, usadd, ct1n, ct2n, meds, eh;
	long ct1p, ct2p;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_user_info);
		TextView titleTV = (TextView) findViewById(R.id.topbar_title_tv);
		titleTV.setText(getResources().getString(R.string.title_activity_view_user_info));
		setUserInfo();
	}

	public void setUserInfo() {
		SharedPreferences mypreferences = getApplicationContext()
				.getSharedPreferences(getString(R.string.information_file),
						Context.MODE_PRIVATE);
		int isDataSaved = mypreferences.getInt(getString(R.string.isDataSaved),
				0);

		if (isDataSaved == 1) {
			usnm = mypreferences.getString(getString(R.string.user_Name),
					"no_name");
			usadd = mypreferences.getString(getString(R.string.user_address),
					"no_address");
			ct1n = mypreferences.getString(getString(R.string.careTaker1Name),
					"no careTaker1");
			ct2n = mypreferences.getString(getString(R.string.careTaker2Name),
					"no careTaker1");
			ct1p = mypreferences
					.getLong(getString(R.string.careTaker1Phone), 0);
			ct2p = mypreferences
					.getLong(getString(R.string.careTaker2Phone), 0);
			meds = mypreferences.getString(
					getString(R.string.medicinalDetails), "no meds");
			eh = mypreferences.getString(getString(R.string.emergencyHelp),
					"no help info");

			((TextView) findViewById(R.id.userNameView1)).setText(usnm);
			((TextView) findViewById(R.id.userAddressView1)).setText(usadd);
			((TextView) findViewById(R.id.careTaker1NameView1)).setText(ct1n);
			((TextView) findViewById(R.id.careTaker2NameView1)).setText(ct2n);
			((TextView) findViewById(R.id.MedicinalDetailsView1)).setText(meds);
			((TextView) findViewById(R.id.EmergencyHelpView1)).setText(eh);
			((TextView) findViewById(R.id.careTaker1PhoneView1)).setText(""
					+ ct1p);
			((TextView) findViewById(R.id.careTaker2PhoneView1)).setText(""
					+ ct2p);
		} else {
			Intent intent = new Intent(this, UserDetailsActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_user_info, menu);
		return true;
	}

	public void editUserInfo(View v) {
		Intent intent = new Intent(this, UserDetailsActivity.class);
		startActivity(intent);
	}

	public void careTaker1Call(View v) {
		call("" + ct1p);
	}

	public void careTaker2Call(View v) {
		call("" + ct2p);
	}

	public void call(String phone) {

		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:" + phone));
		startActivity(callIntent);
	}
	
	


}
