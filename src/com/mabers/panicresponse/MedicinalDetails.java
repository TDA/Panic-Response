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
import android.widget.TextView;

public class MedicinalDetails extends Activity {

	SharedPreferences sharedPref;
	String meds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_medicinal_details);
		
		TextView titleTV = (TextView) findViewById(R.id.topbar_title_tv);
		titleTV.setText(getResources().getString(R.string.title_activity_medicinal_details));

		sharedPref = getSharedPreferences(getString(R.string.information_file),
				Context.MODE_PRIVATE);
		int isDataSaved = sharedPref.getInt(getString(R.string.isDataSaved), 0);
		if (isDataSaved == 1)
			setData();

	}

	private void setData() {

		meds = sharedPref.getString(getString(R.string.medicinalDetails),
				"no meds");

		((TextView) findViewById(R.id.medicineText)).setText(meds);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.medicinal_details, menu);
		return true;
	}

	public void nextMeds(View v) {
		EditText medicines = (EditText) findViewById(R.id.medicineText);
		if (medicines.getText().toString().length() < 1) {
			medicines.setError("Enter Valid Data");
			medicines.requestFocus();
			return;
		} else {
			medicines.setError(null);
			medicines.clearFocus();
		}

		Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.medicinalDetails),
				((EditText) findViewById(R.id.medicineText)).getText()
						.toString());
		editor.commit();

		Intent intent = new Intent(this, EmergencyHelpActivity.class);
		startActivity(intent);
	}
	
	
	

}
