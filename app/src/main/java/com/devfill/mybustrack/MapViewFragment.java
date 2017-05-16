package com.devfill.mybustrack;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

 public class MapViewFragment extends android.support.v4.app.Fragment {

	MapView mMapView;
	private GoogleMap googleMap;
	MarkerOptions markerOption = new MarkerOptions();
	Marker marker;
	java.util.List<LatLng> mPoints;
	LatLng latLngMypoint;
	TextView durationMapView;
    TextView durationMap;
    TextView distanceMapView;
    TextView distanceMap;
    Button butChoosePoint;
   
    
	public static final String LOG_TAG = "myLogs";
    
	public interface onSomeEventListener {
	    public  void choosePointEvent(LatLng latLng);
	  }
	onSomeEventListener someEventListener;
	@Override
	  public void onAttach(Activity activity) {
	    super.onAttach(activity);
	        try {
	          someEventListener = (onSomeEventListener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
	        }
	  }
	 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment1, container, false);
        
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        durationMapView = (TextView) rootView.findViewById(R.id.durationMapView);
        durationMap = (TextView) rootView.findViewById(R.id.durationMap);
        distanceMapView = (TextView) rootView.findViewById(R.id.distanceMapView);
        distanceMap = (TextView) rootView.findViewById(R.id.distanceMap);
        butChoosePoint = (Button) rootView.findViewById(R.id.butChoosePoint);
        
        butChoosePoint.setVisibility(TextView.INVISIBLE);
        
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        googleMap = mMapView.getMap();
       
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);	//��� ����� MAP_TYPE_NORMAL
    	CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(
    		48.99,33.67), 5);		//��������� ��������� ����� �� ������� � ����� 5
    	googleMap.animateCamera(cameraUpdate);	//������� ���
    	
    	googleMap.setOnMarkerDragListener(new OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker marker) {
                // TODO Auto-generated method stub
                //Here your code
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
            	latLngMypoint = marker.getPosition();
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                // TODO Auto-generated method stub

            }
        });
    	
        butChoosePoint.setOnClickListener(new OnClickListener() {
    	        public void onClick(View v) {
    	        	   	        	   	        	
    	        	Log.i(LOG_TAG, "������ ������ ���������� ������� " + latLngMypoint.latitude + " " + latLngMypoint.longitude);
    	        	someEventListener.choosePointEvent(latLngMypoint);
    	        	
    	        	durationMapView.setVisibility(TextView.VISIBLE);
    	        	durationMap.setVisibility(TextView.VISIBLE);
    	        	distanceMapView.setVisibility(TextView.VISIBLE);
    	        	distanceMap.setVisibility(TextView.VISIBLE);
    	        	
    	        	butChoosePoint.setVisibility(Button.INVISIBLE);
    	        	
    	        	
    	        	googleMap.clear();
    	        }
    	      });
    	
        return rootView;
    }

    public void changeMapType (int type) {
    	googleMap.setMapType(type);
        }
    
    public void createMarcker () {
    	
    	googleMap.clear();
    	
    	markerOption.position(new LatLng(
		    		48.99,33.67))
			.title("��� ������������").draggable(true);
    	marker = googleMap.addMarker(markerOption);
    	
    	durationMapView.setVisibility(TextView.INVISIBLE);
    	durationMap.setVisibility(TextView.INVISIBLE);
    	distanceMapView.setVisibility(TextView.INVISIBLE);
    	distanceMap.setVisibility(TextView.INVISIBLE);
  
    	butChoosePoint.setVisibility(Button.VISIBLE);
    	
    	
    	    	
    	Toast.makeText(getActivity(), "�������� ����� �� ����� � ������� ������", Toast.LENGTH_LONG).show();
    	Log.i(LOG_TAG, "����� �������� �������");
    }
    public void drawRoute (String arrayPoints) {	//������� ��������� ��������
    	 
    	 try {
    		//PolyUtil pu = new PolyUtil();	//������� ������ ��� ������������� ������ ��� ��������� ��������
 		    mPoints = PolyUtil.decode(arrayPoints);	//���������� ������ � ��������� ��� � ����� ��������� LatLng
 			PolylineOptions line = new PolylineOptions();	//����� ������� ����� ���������
 			line.width(4f).color(R.color.wallet_secondary_text_holo_dark);
 			LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
 			googleMap.clear();
 		for (int i = 0; i < mPoints.size(); i++) {	//���������� ����� ���������
 	        if (i == 0) {
 	        	googleMap.addMarker(markerOption
 	        			.position(mPoints.get(i))
 	        			.title("��� ���������"));	//������ ������ �� ���������);
 	       	    } 
 	        else if (i == mPoints.size() - 1) {
 	        	googleMap.addMarker(markerOption
 	          			.position(mPoints.get(i))
 	          			.title("��� ������������"));	//������ ������ �� ����� ��������);
 	        }
 	        line.add(mPoints.get(i));	//��������� � ��������� ��������� ����������
 	        latLngBuilder.include(mPoints.get(i));
 	    }
 		googleMap.addPolyline(line);	//��������� ��������� �� �����
 	    int size = getResources().getDisplayMetrics().widthPixels;
 	    LatLngBounds latLngBounds = latLngBuilder.build();
 	   
 	    CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, size, size, 100);
 	    googleMap.animateCamera(track);		//�������������� �����������
    	        
    	        Log.i(LOG_TAG, "��������� �������� ���������");
         } catch (Exception e) {
        	 Log.i(LOG_TAG, "��������� �������� �� �������" + e.getMessage()); 
         }
    	 
    }
    
 
    
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        Log.i(LOG_TAG, "MapViewFragment onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        Log.i(LOG_TAG, "MapViewFragment onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        Log.i(LOG_TAG, "MapViewFragment onDestroy");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
        Log.i(LOG_TAG, "MapViewFragment onLowMemory");
    }
}