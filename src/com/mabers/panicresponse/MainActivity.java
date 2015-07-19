package com.mabers.panicresponse;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.hardware.SensorEvent;
import android.location.Location;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mabers.panicresponse.MyLocation.LocationResult;


public class MainActivity extends Activity {
	// private MainActivity ma;
	TextView progressText;
	private boolean timerHasStarted,
					triggerCancelled=false,
					fallDetection=false,
					sirenStopped = false,
					sensorsStarted;

	private AlertTimer alertTimer;
	private long startTime = 5 * 1000, interval = 1 * 1000;
	private ProgressBar alertProgress;
	
	SensorEvent sensorEvent_acc;
	
	//Access wifi?
	//WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE); 

	static double lat = 0, lng = 0;
	Location location;

	MyLocation myLocation = new MyLocation();

	Intent music,sensors;
	
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;

	/*
	 * // Music Service
	 
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
*/
	
	private boolean sIsBound = false;
	  private SensorsService sServ;
	  private ServiceConnection sServCon =new ServiceConnection(){

	  	public void onServiceConnected(ComponentName name, IBinder binder) {
	  		sServ = ((SensorsService.SensorsServiceBinder)binder).getService();
	  	}

	  	public void onServiceDisconnected(ComponentName name) {
	  		sServ = null;
	  	}
	  };

	  	void doBindService(){
	   		bindService(new Intent(this,SensorsService.class),
	  				sServCon,Context.BIND_AUTO_CREATE);
	   		//sensorsStarted=true;
	  		sIsBound = true;
	  	}

	  	void doUnbindService()
	  	{
	  		if(sIsBound)
	  		{
	  			sServ.stopSensors();
	  			sensorsStarted=false;
	  			unbindService(sServCon);
	        		sIsBound = false;
	  		}
	  	}
	
	int i = 0;
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
	  	  @Override
	  	  public void onReceive(Context context, Intent intent) {
	  	    // Extract data included in the Intent
	  	    if(intent.getBooleanExtra("isFall", false))
	  	    	{
	  					  	    		
	  	    		Log.d("receiver", "Got message: true");
	  	    		//unregisterReceiver(mMessageReceiver);
	  	    		/*
	  	    		editor=sharedPref.edit();
	  	    		editor.putBoolean(getString(R.string.fall_detection_switch), false);
	  	    		editor.commit();
	  	    		*/
	  	    		
	  	    		fall();
	  	    		
	  	    		//Toast toast = Toast.makeText(getApplicationContext(), "Broadcast Received",Toast.LENGTH_SHORT);
	  	    		//toast.setGravity(Gravity.TOP, 0, 0);
	  	    		//toast.show();
	  	    	}
	  	    else
	    		Log.d("receiver", "Got message: false");	  	    	
	  	  }
	  	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
				
		TextView titleTV = (TextView) findViewById(R.id.topbar_title_tv);
		titleTV.setText(getResources().getString(R.string.app_name));

		alertProgress = (ProgressBar) findViewById(R.id.alertProgress);
		progressText = (TextView) findViewById(R.id.progressText);		
		
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("fall"));

		/*
		boolean wifiEnabled = wifiManager.isWifiEnabled();
		if(!wifiEnabled)
			wifiManager.setWifiEnabled(true);
		 */
		getCurrentLocation();

		SharedPreferences mypreferences = getApplicationContext()
				.getSharedPreferences(getString(R.string.information_file),
						Context.MODE_PRIVATE);
		int isDataSaved = mypreferences.getInt(getString(R.string.isDataSaved),
				0);
		
		if (isDataSaved == 1) {
		} else {// Enter User Data.
			
			Intent intent = new Intent(this, UserDetailsActivity.class);
			startActivity(intent);
		}
		
		sharedPref =getSharedPreferences(getString(R.string.panic_response_settings_file ),
				Context.MODE_PRIVATE);
		fallDetection = sharedPref.getBoolean(getString(R.string.fall_detection_switch), true);

		if(!fallDetection)
		{
			Intent intent = new Intent(this, HelpButtonActivity.class);
			startActivity(intent);
		}
		
		doBindService();
		sensors = new Intent();
		sensors.setClass(this,SensorsService.class);
		startService(sensors);
		
		editor=sharedPref.edit();
		editor.putBoolean(getString(R.string.isTriggered ), false);
		editor.commit();
		
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		doUnbindService();
		
	}

	// Help Button onCLick
	public void helpCancelButtonClick(View v) {
		if (!timerHasStarted) 
		{
			helpButtonClick();
		} 
		else
		{
			
			editor=sharedPref.edit();
			editor.putBoolean(getString(R.string.isTriggered ), false);
			editor.commit();
			triggerCancelled=true;
			cancelButtonClick();
		}
	}
	
	public void fall()
	{		
		//Toast.makeText(getApplicationContext(), "Fall Detected",Toast.LENGTH_SHORT).show();
		helpButtonClick();
	}
	
	void helpButtonClick()
	{
		((Button) findViewById(R.id.helpButton)).setText(getString(R.string.cancel));

		alertProgress.setVisibility(View.VISIBLE);
		progressText.setVisibility(View.VISIBLE);
		
		alertTimer = new AlertTimer(startTime, interval);
		alertTimer.start();
		
		editor=sharedPref.edit();
		editor.putBoolean(getString(R.string.isTriggered ), true);
		editor.commit();
		
		sServ.stopSensors();
		stopService(sensors);
		doUnbindService();
		
		triggerCancelled=false;
		
		timerHasStarted = true;
		
		
		
		playSiren();
							
	}
	void cancelButtonClick()
	{
		editor = sharedPref.edit();
	       
	       editor.putBoolean(getString(R.string.fall_detection_switch), false);
	       editor.commit();
	       
		triggerCancelled=true;
		alertProgress.setVisibility(View.INVISIBLE);
		progressText.setVisibility(View.INVISIBLE);

		// stop the timer
		alertTimer.cancel();
		timerHasStarted = false;

		((Button) findViewById(R.id.helpButton)).setText(getString(R.string.help));

		stopSiren();
		
		/*doBindService();
		sensors = new Intent();
		sensors.setClass(this,SensorsService.class);
		startService(sensors);*/
		
		//Intent intent = new Intent(this, SettingsActivity.class);
		//startActivity(intent);	
			
		
	}

	// Class Implementing Timer
	public class AlertTimer extends CountDownTimer {
		public AlertTimer(long startTime, long interval) {
			super(startTime, interval);
		}

		public void onFinish()

		{
			if(triggerCancelled)
				return;
			stopSiren();
			TriggerEmergency();
		}

		public void onTick(long millisUntilFinished) {// Called Every Second
			progressText.setText("" + millisUntilFinished / 1000);

		}
	}

	public void TriggerEmergency() {
		if(triggerCancelled)
			return;
		Intent intent = new Intent(this, EmergencyTriggeredActivity.class);
		startActivity(intent);
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

	public void viewUserInfo(View v) {
		Intent intent = new Intent(this, ViewUserInfoActivity.class);
		startActivity(intent);
	}

	// For GPS and WIFI
	LocationResult locationResult = new LocationResult() {
		@Override
		public void gotLocation(Location mylocation) {

			Location myLoc = mylocation;

			// Send message code should come here
			if(location!=null)
			{
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
	
	public void settingsClick(View v)
	{
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}
	
	public void onBackPressed()
	{//Do Nothing When Back Button is pressed
		 // super.onBackPressed();

         //System.exit(0);     
	}	
}
