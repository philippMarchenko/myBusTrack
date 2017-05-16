package com.devfill.mybustrack;


import android.app.Activity;
import android.app.Fragment;
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
import android.widget.EditText;


import android.widget.Spinner;
import android.widget.TextView;

 


public class Fragment3 extends Fragment {

	TextView durationView;
	TextView distanceView;
	TextView routeIdView;
	TextView durationRealView;
	EditText setDuration;
	String strSpin;
	String strEt;
	Button saveReminder;
	
	
	
	String[] data = {"����.-�������", "����.-����."};
	 
	
	public interface onFragment3Listener {
	    public void onClickSaveEvent(String strSpin,String strEt);
	  }
	onFragment3Listener fragment3Listener;
	@Override
	  public void onAttach(Activity activity) {
	    super.onAttach(activity);
	        try {
	        	fragment3Listener = (onFragment3Listener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
	        }
	  }
	
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
	View rootView =  inflater.inflate(R.layout.fragment3, null);

	saveReminder = (Button) rootView.findViewById(R.id.saveReminder);
	setDuration = (EditText) rootView.findViewById(R.id.setDuration); 
	
	
	 // �������
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, data);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    final Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
    spinner.setAdapter(adapter);
    // ���������
    spinner.setPrompt("Title");
    // �������� ������� 
  //  spinner.setSelection(2);
    // ������������� ���������� �������
    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
  @Override
  public void onItemSelected(AdapterView<?> parent, View view,
      int position, long id) {
	  
	  strSpin = spinner.getSelectedItem().toString();
    // ���������� ������� �������� ��������
    //Toast.makeText(getActivity(), "Position = " + position, Toast.LENGTH_SHORT).show();
  
  
  }
  @Override
  public void onNothingSelected(AdapterView<?> arg0) {
  }
});
	
	saveReminder.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
        	
        	strEt = setDuration.getText().toString() + " ���.";
        	
        	fragment3Listener.onClickSaveEvent(strSpin,strEt);
        }
      });
	
    return rootView;
  }
  
}