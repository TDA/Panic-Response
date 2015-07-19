package com.mabers.panicresponse;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mabers.panicresponse.MyLocation.LocationResult;

public class EmergencyTriggeredActivity extends Activity {

	private boolean sirenStopped = false;

	private AlertTimer alertTimer;
	boolean timerCancelled=false,sendSMS=true;
	private long startTime = 5 * 1000, interval = 1 * 1000;

	String usnm, usadd, ct1n, ct2n, meds, eh;
	long ct1p, ct2p;

	private int timerLevel = 0;
	
	MyTimerTask myTask;
    Timer myTimer;
	
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;

	static double lat = 0, lng = 0;
	Location location;

	MyLocation myLocation = new MyLocation();

	Intent music;

	// Music Service
	private boolean mIsBound = false;
	private MusicService mServ;
	private ServiceConnection Scon = new ServiceConnection() {

		public void onServiceConnected(ComponentName name, IBinder binder) {
			mServ = ((MusicService.ServiceBinder) binder).getService();
		}

		public void onServiceDisconnected(ComponentName name) {
			mServ = null;
		}
	};

	void doBindService() {
		bindService(new Intent(this, MusicService.class), Scon,
				Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() {
		if (mIsBound) {
			unbindService(Scon);
			mIsBound = false;
		}
	}

	int i = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emergency_triggered);
		
		TextView titleTV = (TextView) findViewById(R.id.topbar_title_tv);
		titleTV.setText(getResources().getString(R.string.title_activity_emergency_triggered));
		
		sharedPref =getSharedPreferences(getString(R.string.panic_response_settings_file ),
				Context.MODE_PRIVATE);
		
		if(!(sharedPref.getBoolean(getString(R.string.isTriggered), true)))
				{
					timerCancelled=true;
					Intent intent = new Intent(this, MainActivity.class);
					startActivity(intent);
				}
		else
		{

		getCurrentLocation();

		playSiren();

		setInfoDetails();
		
		

		// Start The Timer
		timerLevel = 0;
		alertTimer = new AlertTimer(startTime, interval);
		alertTimer.start();
		
		/*
		 myTask = new MyTimerTask();
	     myTimer = new Timer();
	     myTask.getObj(this);
	     myTimer.schedule(myTask, 5000, 30000);*/
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.emergency_triggered, menu);
		return true;
	}

	public void setInfoDetails() {// Sets the details in the new Layout when
									// user presses the Help Button
		SharedPreferences mypreferences = getApplicationContext()
				.getSharedPreferences(getString(R.string.information_file),
						Context.MODE_PRIVATE);

		usnm = mypreferences
				.getString(getString(R.string.user_Name), "no_name");
		usadd = mypreferences.getString(getString(R.string.user_address),
				"no_address");

		ct1n = mypreferences.getString(getString(R.string.careTaker1Name),
				"no careTaker1");
		ct2n = mypreferences.getString(getString(R.string.careTaker2Name),
				"no careTaker1");
		ct1p = mypreferences.getLong(getString(R.string.careTaker1Phone), 0);
		ct2p = mypreferences.getLong(getString(R.string.careTaker2Phone), 0);

		meds = mypreferences.getString(getString(R.string.medicinalDetails),
				"no meds");
		eh = mypreferences.getString(getString(R.string.emergencyHelp),
				"no help info");

		TextView view = (TextView) findViewById(R.id.careTaker1NameView);
		view.setText(ct1n);
		view = (TextView) findViewById(R.id.careTaker2NameView);
		view.setText(ct2n);
		view = (TextView) findViewById(R.id.careTaker1PhoneView);
		view.setText("" + ct1p);
		view = (TextView) findViewById(R.id.careTaker2PhoneView);
		view.setText("" + ct2p);
		view = (TextView) findViewById(R.id.medicinalDetailsView);
		view.setText(meds);
		view = (TextView) findViewById(R.id.emergencyHelpView);
		view.setText(eh);
	}

	// TIMER BEGINS
	public class AlertTimer extends CountDownTimer {
		public AlertTimer(long startTime, long interval) {
			super(startTime, interval);
		}

		public void onFinish()

		{
			if((sharedPref.getBoolean(getString(R.string.isTriggered), true)))
				bachao(); // uses the timer to call n send txt msgs.
		}

		public void onTick(long millisUntilFinished) {// Called Every Second
														// progressText.setText(""+millisUntilFinished/1000);

		}
	}

	// TIMER ENDS

	private void bachao()// Do Stuff acc to the timer
	{// Called EveryTime the Timer is Finished
		switch (timerLevel) {
		// Msg Sent in 30 Secs
		case 0:
			sendTextMessages();
			timerLevel = 1;

			alertTimer = new AlertTimer(30000, 1000);
			alertTimer.start();
			break;
		// Call in 30 Secs
		case 1:
			call("" + ct1p);
			timerLevel = 2;
			alertTimer = new AlertTimer(30000, 1000);
			alertTimer.start();
			break;
		// Msg After 60 secs
		case 2:
			sendTextMessages();
			timerLevel = 3;
			alertTimer = new AlertTimer(60000, 1000);
			alertTimer.start();
			break;
		// Msg After 135Secs
		case 3:
			sendTextMessages();
			timerLevel = 4;
			alertTimer = new AlertTimer(45000, 1000);
			alertTimer.start();
			break;
		// Call After 225 Secs
		case 4:
			call("" + ct2p);
			timerLevel = 5;
			alertTimer = new AlertTimer(60000, 1000);
			alertTimer.start();
			break;
		// Msg After 285 secs
		case 5:
			sendTextMessages();
			timerLevel = 6;
			alertTimer = new AlertTimer(60000, 1000);
			alertTimer.start();
			break;
		// Msg After 375 secs
		case 6:
			sendTextMessages();
			timerLevel = 7;
			alertTimer = new AlertTimer(60000, 1000);
			alertTimer.start();
			break;
		default:
			alertTimer.cancel();
		}
	}

	// SIREN BEGINS
	private void playSiren() {// Play Music in the Background
		sirenStopped = false;

		switchProfile();
		setMaxVolume();

		music = new Intent();
		music.setClass(this, MusicService.class);
		startService(music);
	}

	private void stopSiren() {
		if (sirenStopped)
			return;

		sirenStopped = true;

		stopService(music);
	}

	public void silent(View view) {
		stopSiren();

		// Make the silent button go invisible.
		((Button) findViewById(R.id.silentButton))
				.setBackgroundColor(Color.TRANSPARENT);
	}

	public void switchProfile() {// Switch Profile from Silent To Normal
		final AudioManager mobilemode = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);
		mobilemode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
	}

	public void setMaxVolume() {
		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		am.setStreamVolume(AudioManager.STREAM_MUSIC,
				am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
	}

	// SIREN ENDS

	// GPS BEGINS
	LocationResult locationResult = new LocationResult() {

		@Override
		public void gotLocation(Location mylocation) {

			Location myLoc = mylocation;
			if(location!=null)
			{
			// Send message code should come here
			lat = myLoc.getLatitude();
			lng = myLoc.getLongitude();
			Log.e("inside location", "" + lat + "," + lng);
			}
			// Toast.makeText(getApplicationContext(),
			// ""+lat+", "+lng,Toast.LENGTH_SHORT).show();
		}

	};

	public void getCurrentLocation() {
		// myLocation = new MyLocation();
		myLocation.getLocation(getApplicationContext(), locationResult);

		//Toast.makeText(getApplicationContext(), "" + lat + ", " + lng,
			//	Toast.LENGTH_SHORT).show();

	}

	// GPS ENDS

	// CALL BEGINS
	public void callCareTaker1(View v) {
		call(new String("" + ct1p));
	}

	public void callCareTaker2(View v) {
		call(new String("" + ct2p));
	}

	public void sosCall(View v) {
		call("911");
	}

	public void call(String phone) {
		if(timerCancelled)
			return;
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:" + phone));
		startActivity(callIntent);
	}

	// CALL ENDS

	// TEXT BEGINS
	public void sendTextMessages()

	{
		sendSMS=true;
		if(timerCancelled)
		{
			Log.e("Message", "Timer Cancelled");
			return;
		}
		// phone=String.valueOf(phne);
		// getCurrentLocation();

		// SmsManager smsManager = SmsManager.getDefault();

		// For GPS and WIFI

		LocationResult locationResult = new LocationResult() {

			// String ph=phone;

			@Override
			public void gotLocation(Location mylocation) {
				// location=mylocation;

				lat = mylocation.getLatitude();
				lng = mylocation.getLongitude();

				Log.e("send msg location", "" + lat + "," + lng);

				Log.e("Message", getMessage(lat, lng));
				SmsManager smsManager = SmsManager.getDefault();
				// Toast.makeText(this, "SMS Sent",Toast.LENGTH_SHORT).show();
				// Toast.makeText(getBaseContext(), phone+"",
				// Toast.LENGTH_SHORT).show();
				if(!sendSMS)
					return;

				smsManager.sendTextMessage(getPhoneNumber1(), null,
						getMessage(lat, lng), null, null);
				smsManager.sendTextMessage(getPhoneNumber2(), null,
						getMessage(lat, lng), null, null);
				sendSMS=false;
			
				// Send message code should come here
				// String x = getMessage(lat,lng);

			}

		};

		// MyLocation myLocation = new MyLocation();

		myLocation.getLocation(getApplicationContext(), locationResult);

		// Toast.makeText(this, "SMS Sent",Toast.LENGTH_SHORT).show();

		 //smsManager.sendTextMessage("+1"+phone, null, getMessage(lat,lng),
		 //null, null);
	}

	public String getPhoneNumber1() {
		return new String("+1" + ct1p);
	}

	public String getPhoneNumber2() {
		return new String("+1" + ct2p);
	}

	private String getMessage(double lat, double lng) {// Sets The SMS Message
														// to be sent.
		String msg, generalMsg, url, urlPart1, urlPart2, urlPart3;
		urlPart1 = "www.google.co.in/search?q=";
		urlPart2 = "+";
		urlPart3 = "";
		generalMsg = usnm + getString(R.string.activated_emergency_trigger_msg)+"\n" + usnm
				+ "  " +getString(R.string.is_at) +" "+ lat + ", " + lng + "\n\n";
		url = urlPart1 + lat + urlPart2 + lng + urlPart3;
		msg = generalMsg + url;

		return msg;
	}

	// TEXT ENDS

	// CANCEL BUTTON PRESS
	public void cancelButtonClick(View v) {
		
		editor=sharedPref.edit();
		editor.putBoolean(getString(R.string.isTriggered), false);
		editor.commit();
		timerCancelled=true;
		alertTimer.cancel();
		timerLevel = 0;
		//myTimer.cancel();
		stopSiren();
		editor=sharedPref.edit();
		editor.putBoolean(getString(R.string.isTriggered ), false);
		editor.commit();
		Intent intent = new Intent(this, HelpButtonActivity.class);
		startActivity(intent);
		finish();
	}
	
	public void onBackPressed()
	{//Do Nothing When Back Button is pressed
		 // super.onBackPressed();

         //System.exit(0);     
	}
	
	


	

}
class MyTimerTask extends TimerTask {
	
	  int timerLevel=0;
	  EmergencyTriggeredActivity obj;
	  
	  public  void getObj(EmergencyTriggeredActivity obj)
	  {
		  this.obj=obj;
	  }
	  
	  public void run() {
		  
		switch(timerLevel)
		{
		case 0:
			obj.sendTextMessages();
			timerLevel++;
			break;
		case 1:
			obj.call("" + obj.ct1p);
			timerLevel++;
			break;
		case 2:
			obj.sendTextMessages();
			timerLevel++;
			break;
		case 3:
			obj.sendTextMessages();
			timerLevel++;
			break;
		case 4:
			obj.call("" + obj.ct2p);
			timerLevel++;
			break;
		case 5:
			obj.sendTextMessages();
			timerLevel++;
			break;
		case 6:
			obj.sendTextMessages();
			timerLevel++;
		case 7:
			obj.call("" + obj.ct1p);
			timerLevel++;
			break;
		case 8:
			obj.sendTextMessages();
			timerLevel++;
			break;
		default:
			timerLevel=0;	
		}
		  
	  }
	  
	  
	}

