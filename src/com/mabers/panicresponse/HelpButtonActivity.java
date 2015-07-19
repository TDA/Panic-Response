package com.mabers.panicresponse;




import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HelpButtonActivity extends Activity {
	
	private AlertTimer alertTimer;
	private long startTime = 5 * 1000, interval = 1 * 1000;
	private ProgressBar alertProgress;
	TextView progressText;
	
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	
	static double lat = 0, lng = 0;
	Location location;

	MyLocation myLocation = new MyLocation();
	
	private boolean timerHasStarted,sirenStopped = false;


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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help_button);
		
		sharedPref =getSharedPreferences(getString(R.string.panic_response_settings_file ),
				Context.MODE_PRIVATE);
		
		TextView titleTV = (TextView) findViewById(R.id.topbar_title_tv);
		titleTV.setText(getResources().getString(R.string.app_name));
		
		alertProgress = (ProgressBar) findViewById(R.id.alertProgressHB);
		progressText = (TextView) findViewById(R.id.progressTextHB);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.help_button, menu);
		return true;
	}
	
	public void helpCancelButtonClick(View v)
	{
		if (!timerHasStarted) {
			
			editor=sharedPref.edit();
			editor.putBoolean(getString(R.string.isTriggered ), true);
			editor.commit();
			
			alertTimer = new AlertTimer(startTime, interval);
			alertTimer.start();
			timerHasStarted = true;
			((Button) findViewById(R.id.helpButtonHB)).setText(getString(R.string.cancel));

			alertProgress.setVisibility(View.VISIBLE);
			progressText.setVisibility(View.VISIBLE);
			playSiren();
		} 
		else 
		{
			editor=sharedPref.edit();
			editor.putBoolean(getString(R.string.isTriggered ), false);
			editor.commit();
			
			alertProgress.setVisibility(View.INVISIBLE);
			progressText.setVisibility(View.INVISIBLE);

			// stop the timer
			alertTimer.cancel();
			timerHasStarted = false;

			((Button) findViewById(R.id.helpButtonHB)).setText(getString(R.string.help));

			stopSiren();
		}
	}
	
	public class AlertTimer extends CountDownTimer {
		public AlertTimer(long startTime, long interval) {
			super(startTime, interval);
		}

		public void onFinish()

		{
			triggerEmergency();
		}

		public void onTick(long millisUntilFinished) {// Called Every Second
			progressText.setText("" + millisUntilFinished / 1000);

		}
	}
	
	void triggerEmergency()
	{
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
