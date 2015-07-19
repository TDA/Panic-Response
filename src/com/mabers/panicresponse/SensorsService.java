package com.mabers.panicresponse;



import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class SensorsService extends Service implements SensorEventListener {
	
	SensorEvent s_a,s_g,s_r;
	private SensorManager mSensorManagerAcc,mSensorManagerRV;
	private Sensor mSensorAcc,mSensorRV;
	private final IBinder mBinder = new SensorsServiceBinder();
	boolean sensorsStarted=false;
	Intent intent;
	int noOfNegatives=0,noOfCrits=0,check=0;
	boolean rotFall=false,accFall=false;
	boolean fall=false;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	public class SensorsServiceBinder extends Binder
	{
		SensorsService getService()
		{
			return SensorsService.this;
		}
	}
	
	@Override
    public void onCreate (){
	  super.onCreate();
	  
	  startSensors();
	  
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		stopSensors();
		
	}
	
	public int onStartCommand (Intent intent, int flags, int startId)
	{     
		startSensors();
        return START_STICKY;
	}
	
	public void startSensors()
	{
		sensorsStarted=true;
		mSensorManagerAcc = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensorAcc = mSensorManagerAcc.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		//Changelog:Sai 3.12pm 7/12/14
		if(mSensorAcc!=null && mSensorManagerAcc!=null){
			Toast toast = Toast.makeText(getApplicationContext(), "accelerometer present",Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP, 0, 0);
			toast.show();
			mSensorManagerAcc.registerListener(this, mSensorAcc, SensorManager.SENSOR_DELAY_NORMAL);  
			
		}
		else{
			Toast toast = Toast.makeText(getApplicationContext(), "No accelerometer",Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP, 0, 0);
			toast.show();
			
		}
			
	    
	    mSensorManagerRV = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	    mSensorRV = mSensorManagerRV.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
	    if(mSensorRV!=null && mSensorManagerRV!=null){
			Toast toast = Toast.makeText(getApplicationContext(), "RV present",Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP, 0, 0);
			toast.show();
			mSensorManagerRV.registerListener(this, mSensorRV, SensorManager.SENSOR_DELAY_NORMAL);
			   
		}
		else{
			Toast toast = Toast.makeText(getApplicationContext(), "No RV",Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP, 0, 0);
			toast.show();
			
		}
		
	    
	}
	
	public void stopSensors()
	{
		sensorsStarted=false;
		if(mSensorAcc!=null && mSensorManagerAcc!=null)
		mSensorManagerAcc.unregisterListener(this);
		
		if(mSensorRV!=null && mSensorManagerRV!=null)
		mSensorManagerRV.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
		if(event.sensor.equals(mSensorAcc))
		{
			setAccSensorEvent(event);
			
			detectFallAcc();
		}
		
		else if(event.sensor.equals(mSensorRV))
		{
			setRotSensorEvent(event);
			detectFallRot();
		}
		
	}
	
	void detectFallAcc()
	{
			
		if(accFall&&check<5)
		{
			if(rotFall)
			{
				setFall(true);
				sendMessage();
				check=0;
				accFall=false;
				rotFall=false;
			}
			else
			{
				check++;
			}
		}
		else
		{
			check=0;
			accFall=false;
			rotFall=false;
		}
		
		if((isInProbZone(s_a.values[1])&&s_a.values[2]<5)||(isInProbZone(s_a.values[2])&&s_a.values[1]<5)||(s_a.values[0]<2&&s_a.values[1]<2&&s_a.values[2]<2))
		{
			if((s_a.values[2]>-12)&&(s_a.values[2]<-6))
				return;
			//Toast toast = Toast.makeText(getApplicationContext(), "Trouble",Toast.LENGTH_SHORT);
			//toast.setGravity(Gravity.TOP, 0, 0);
			//toast.show();
			//Changelog:Sai 1.54am 9/12/14
			accFall=true;
			//Changelog:Sai 1.50am 9/12/14
			if(rotFall||accFall)
			{
				setFall(true);
				sendMessage();
				check=0;
				accFall=false;
				rotFall=false;
				
			}
		}
		
	}
	public boolean isInProbZone(float f)
	{
		if((f>-1)&&(f<1))
			return true;
		else
			return false;
	}
	void detectFallRot()
	{
		//Rotation Vector
				if(s_r.values[0]<0.015)
				{
					//setFall(true);
					//sendMessage();
					noOfNegatives++;
				}
				else
					noOfNegatives=0;
				
				if(noOfNegatives>=2)
				{
					rotFall=true;
					//setFall(true);
					//sendMessage();
				}
				else
					rotFall=false;
				if(rotFall&&accFall)
				{
						setFall(true);
						sendMessage();
						check=0;
						accFall=false;
						rotFall=false;
				}
				
		
		/*if(s_r.values[0]<0.015)
		{
			//setFall(true);
			//sendMessage();
			noOfNegatives++;
		}
		else
			noOfNegatives=0;
		if(noOfNegatives>=5)
		{
			setFall(true);
			sendMessage();
			noOfNegatives=0;
		}*/
	}
	
	private void sendMessage() {
		  Intent intent = new Intent("fall");
		  // add data
		  intent.putExtra("isFall", true);
		  Log.d("sender", "Sending message: true");
		  LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		  stopSensors();
		} 
	
	
	
	//setters n getters
	public void setAccSensorEvent(SensorEvent event)
	{
		s_a=event;
	}
	public SensorEvent getAccSensorEvent()
	{
		return s_a;
	}
	public void setRotSensorEvent(SensorEvent event)
	{
		s_r=event;
	}
	public SensorEvent getRotSensorEvent()
	{
		return s_r;
	}
	public boolean getSensorsStarted()
	{
		return sensorsStarted;
	}
	//Setter and Getter for fall
		public void setFall(boolean value)
		{
			fall=value;
		}
		public boolean getFall()
		{
			return fall;
		}

}
