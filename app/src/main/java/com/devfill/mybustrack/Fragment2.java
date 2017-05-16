package com.devfill.mybustrack;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;

import android.widget.TextView;
import android.widget.Toast;
 


public class Fragment2 extends android.support.v4.app.Fragment {
	TextView durationView;
	TextView distanceView;
	TextView routeIdView;
	TextView durationRealView;
	TextView duration;
	TextView distance;
	TextView routeId;
	TextView durationReal;
	TextView statusReminder;
	
	String strCatName;
	Button addReminder;
	Button refactorReminder;
	Button delReminder;
	CheckBox checkReminder;
	
	DBHelper dbHelper;
	/// ������� ������ ��� ������
	  ContentValues cv = new ContentValues();
	
	 
	public static final String LOG_TAG = "myLogs";
	public static final String LOG_TAG_DB = "dbLogs";
	
	public interface onFragment2Listener {
		public void onClickAddEvent();
		public void onClickRefEvent();
		public void onClickDelEvent();
		public void onClickcheckReminder(int i);
	  }
	onFragment2Listener fragment2Listener;
	@Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	        try {
	        	fragment2Listener = (onFragment2Listener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
	        }
	  }
	
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
	View rootView =  inflater.inflate(R.layout.fragment2, null);
    
	durationView = (TextView) rootView.findViewById(R.id.durationView);
	distanceView = (TextView) rootView.findViewById(R.id.distanceView); 
	routeIdView = (TextView) rootView.findViewById(R.id.routeIdView);
	durationRealView = (TextView) rootView.findViewById(R.id.durationRealView);
	duration = (TextView) rootView.findViewById(R.id.duration);
	distance= (TextView) rootView.findViewById(R.id.distance); 
	routeId = (TextView) rootView.findViewById(R.id.routeId);
	durationReal = (TextView) rootView.findViewById(R.id.durationReal);
	statusReminder = (TextView) rootView.findViewById(R.id.statusReminder);
	
	checkReminder = (CheckBox) 	rootView.findViewById(R.id.checkReminder);
	
	addReminder = (Button) rootView.findViewById(R.id.addReminder);
	refactorReminder = (Button) rootView.findViewById(R.id.refactorReminder);
	delReminder = (Button) rootView.findViewById(R.id.delReminder);
	
	

	
	
	// ������� ������ ��� �������� � ���������� �������� ��
    dbHelper = new DBHelper(getActivity());
	
        
    SQLiteDatabase db = dbHelper.getWritableDatabase();
	// ������ ������ ���� ������ �� ������� mytable, �������� Cursor 
	Cursor c = db.query("mytable", null, null, null, null, null, null);
	if (c.moveToFirst()){
		setVisibleValues ();
	  	     // ���������� ������ �������� �� ����� � �������
	  	        int routNameColIndex = c.getColumnIndex("routName");
	  	        int durationColIndex = c.getColumnIndex("duration");
	  	        int durationRealColIndex = c.getColumnIndex("durationReal");
	  	        int distanceColIndex = c.getColumnIndex("distance");
	  	        int checkReminderStatusColIndex = c.getColumnIndex("checkReminderStatus");
	  	      do {
 	 	          // �������� �������� �� ������� �������� � ����� ��� � ���
 	 	          Log.d(LOG_TAG_DB,
 	 	              "routName = " + c.getString(routNameColIndex) + 
 	 	              ", duration = " + c.getString(durationColIndex) + 
 	 	              ", durationReal = " + c.getString(durationRealColIndex) + 
 	 	              ", distance = " + c.getString(distanceColIndex));
 	 	          
 	 	          routeIdView.setText(c.getString(routNameColIndex));
 	 	          distanceView.setText(c.getString(distanceColIndex));
 	 	          durationView.setText(c.getString(durationColIndex));
 	 	          durationRealView.setText(c.getString(durationRealColIndex));
 	 	       
 	 	          checkReminderChange(c.getInt(checkReminderStatusColIndex));
 	 	          
 	 	          // ������� �� ��������� ������ 
 	 	          // � ���� ��������� ��� (������� - ���������), �� false - ������� �� �����
 	 	        } while (c.moveToNext());
	  	      }
	else{
		setInvisibleValues ();
	}
	
	
	
	checkReminder.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
        	
        	if(checkReminder.isChecked()){
        		MainActivity.sendNoty = true;
        		checkReminder.setText("����������� ������������");
        		fragment2Listener.onClickcheckReminder(1);
        	}
        	else{
        		checkReminder.setText("����������� �� ������������");
        		MainActivity.sendNoty = false;
        		fragment2Listener.onClickcheckReminder(0);
        	}
        	
        	
        	
        	 Log.i(LOG_TAG, "ceckReminder.setOnClickListener " + checkReminder.isChecked());
        	 Log.i(LOG_TAG, "MainActivity.sendNoty " + MainActivity.sendNoty);
        }
      });
	delReminder.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
        	
        	fragment2Listener.onClickDelEvent();
        }
      });
	refactorReminder.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
        	
        	fragment2Listener.onClickRefEvent();
        }
      });
	addReminder.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
        	
        	fragment2Listener.onClickAddEvent();
        }
      });
   
    return rootView;
  }
  public void checkReminderChange(int i){
	  if(i == 0){
		checkReminder.setChecked(false);
  		checkReminder.setText("����������� �� ������������");
  		MainActivity.sendNoty = false;
  		
  	}
  	else{
  		checkReminder.setChecked(true);
  		checkReminder.setText("����������� ������������");
  		MainActivity.sendNoty = true;
  		}
	}
  public void setInvisibleValues(){
		 durationView.setVisibility(TextView.INVISIBLE);
		 distanceView.setVisibility(TextView.INVISIBLE);
		 routeIdView.setVisibility(TextView.INVISIBLE);
		 durationRealView.setVisibility(TextView.INVISIBLE);
		 duration.setVisibility(TextView.INVISIBLE);
		 distance.setVisibility(TextView.INVISIBLE);
		 routeId.setVisibility(TextView.INVISIBLE);
		 durationReal.setVisibility(TextView.INVISIBLE);
		 checkReminder.setVisibility(TextView.INVISIBLE);
		 statusReminder.setVisibility(TextView.VISIBLE);
		 
		 
		 addReminder.setVisibility(TextView.VISIBLE);
		 refactorReminder.setVisibility(TextView.INVISIBLE); 
}
  public void setValues(String routeN,String dist,String dur,String durR){
	  	 durationView.setText(dur);
		 distanceView.setText(dist);
		 routeIdView.setText(routeN);
		 durationRealView.setText(durR);
  }
  public void setVisibleValues(){
		 durationView.setVisibility(TextView.VISIBLE);
		 distanceView.setVisibility(TextView.VISIBLE);
		 routeIdView.setVisibility(TextView.VISIBLE);
		 durationRealView.setVisibility(TextView.VISIBLE);
		 duration.setVisibility(TextView.VISIBLE);
		 distance.setVisibility(TextView.VISIBLE);
		 routeId.setVisibility(TextView.VISIBLE);
		 durationReal.setVisibility(TextView.VISIBLE);
		 checkReminder.setVisibility(TextView.VISIBLE);
		 statusReminder.setVisibility(TextView.INVISIBLE);
		 
		 addReminder.setVisibility(TextView.INVISIBLE);
	     refactorReminder.setVisibility(TextView.VISIBLE);
}
  @Override
  public void onResume() {
      super.onResume();
      
    	  
      Log.i(LOG_TAG, "Fragment2 onResume");
  }

  @Override
  public void onPause() {
      super.onPause();
    
      Log.i(LOG_TAG, "Fragment2 onPause");
  }

  @Override
  public void onDestroy() {
      super.onDestroy();
     
      Log.i(LOG_TAG, "Fragment2 onDestroy");
  }

  @Override
  public void onLowMemory() {
      super.onLowMemory();
      
      Log.i(LOG_TAG, "Fragment2 onLowMemory");
  }
}