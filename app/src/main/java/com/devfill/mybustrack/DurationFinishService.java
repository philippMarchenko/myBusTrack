package com.devfill.mybustrack;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


public class DurationFinishService extends Service implements OnLoadCompleteListener{

	final String LOG_TAG = "myLogs";
	final int MAX_STREAMS = 5;
	  
	SoundPool sp;
	int soundIdNoty;
	int streamIdNoty;
	
	Timer myTimer2 = new Timer(); // ������� ������
	NotificationManager notificationManager;
	
	public void onCreate() {
		    super.onCreate();
		    Log.d(LOG_TAG, "onCreate DurationFinishService");
	
		    sp = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
			sp.setOnLoadCompleteListener(this);
		    
			try {
				soundIdNoty = sp.load(getAssets().openFd("audio/Notify01.ogg"), 1);
			    } catch (IOException e) {
			    	  	 Log.i(LOG_TAG,"������ �������� ����� "+ e.getMessage());
			    }
			Log.d(LOG_TAG, "soundIdNoty = " + soundIdNoty);
			
			
			
		  }
	
	 
	public int onStartCommand(Intent intent, int flags, int startId) {
		 
		
			doDurationFinish();
			
			return super.onStartCommand(intent, flags, startId);
		 
	 }
	  public void onDestroy() {
		  super.onDestroy();
		  Log.d(LOG_TAG, "onDestroy DurationFinishService");
		  myTimer2.cancel();
		  
	  }
	  void sendNoty() {
		
		 String text = "��������, ����� " + MainActivity.duration +  " ��� ������� ����� �� �����!";
		 
		 Context context = getApplicationContext();
		 Intent notificationIntent = new Intent(context, MainActivity.class);
		 PendingIntent pIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		 Notification.Builder builder = new Notification.Builder(this)
		 	.setTicker("�����������!")
		 	.setContentTitle("������ ��������")
		 	.setContentText(text)
		 	.setContentIntent(pIntent);
		// 	.setSmallIcon(R.drawable.check);
		 	//.addAction(R.drawable.check, "��������� ����������",
		 	//	pIntent).setAutoCancel(true);

		 
		 Notification notification = new Notification.BigTextStyle(builder)
			.bigText(text).build();

		 notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		 notificationManager.notify(1, notification);

		 	 
		// Notification notification = bI.build();
		//startForeground(101, notification);
		 
		 
	  }
	void doDurationFinish() {
		
		final Handler uiHandler = new Handler();
 		myTimer2.schedule(new TimerTask() { // ���������� ������
 			@Override
 	  	    public void run() {
 				
 				
 				uiHandler.post(new Runnable() {
 	  	            @Override
 	  	            public void run() {
 	  	         	String durReal = MainActivity.durationReal;
	    	    	String dur = MainActivity.duration;
	    	    	
	    	    	int durRealInt = 0;
	    	    	int durInt = 0;
	    	    	try{
	    	    		if(!(durReal==null)){
	    	    			durReal = durReal.replaceAll("[^0-9]+", "");
	    	    			durRealInt = Integer.parseInt(durReal);
	    	    			}
	    	    		if(!(dur==null))
	    	    			dur = dur.replaceAll("[^0-9]+", "");
	    	    			durInt = Integer.parseInt(dur);
	    	    			}
	    	    	catch(Exception e){
	    				Log.i(LOG_TAG,"������ �������������� ������ � int " + e.getMessage());
	    					}
	    	    	Log.d(LOG_TAG, "durReal = " + durReal + " dur = " + dur);
	    	    	Log.d(LOG_TAG, "durInt = " + durInt + " durRealInt = " + durRealInt);
	     		
	    	    	if(durInt > durRealInt && MainActivity.sendNoty){
	     				Log.i(LOG_TAG,"����� �����, ������������� ");
	     				sp.play(soundIdNoty, 1, 1, 0, 0, 1); 
	     				sendNoty();
	    	    	}
 	  	           }
 	  	        });	
 		   };
			
 	  	}, 0L, 5L * 1000);
	 }
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void onLoadComplete(SoundPool arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
}