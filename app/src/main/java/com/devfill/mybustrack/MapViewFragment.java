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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
   
    BroadcastReceiver broadcastReceiverMap;
	OnMarkerDragListener onMarkerDragListener;

	public static final String LOG_TAG = "mapFragmentView";

	public static final String UPDATE_MAP = "update_map";

	boolean markerStart = false;

	public interface MapViewFragmentListener {
	    public  void choosePointEvent(LatLng latLng);
	  }
	MapViewFragmentListener mapViewFragmentListener;
	@Override
	  public void onAttach(Activity activity) {
	    super.onAttach(activity);
	        try {
				mapViewFragmentListener = (MapViewFragmentListener) activity;
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

       // durationMapView = (TextView) rootView.findViewById(R.id.durationMapView);
        durationMap = (TextView) rootView.findViewById(R.id.durationMap);
      //  distanceMapView = (TextView) rootView.findViewById(R.id.distanceMapView);
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
    	googleMap.animateCamera(cameraUpdate);	//������� �

		onMarkerDragListener = new OnMarkerDragListener() {
			@Override
			public void onMarkerDragStart(Marker marker) {

			}

			@Override
			public void onMarkerDrag(Marker marker) {

			}

			@Override
			public void onMarkerDragEnd(Marker marker) {
				latLngMypoint = marker.getPosition();
			}
		};

		googleMap.setOnMarkerDragListener(onMarkerDragListener);
		//markerStart = true;

		/*googleMap.setOnMarkerDragListener(new OnMarkerDragListener() {

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
    	*/
        butChoosePoint.setOnClickListener(new OnClickListener() {
    	        public void onClick(View v) {
    	        	   	        	   	        	
    	        	Log.i(LOG_TAG, "Кнопка нажата координаты маркера" + latLngMypoint.latitude + " " + latLngMypoint.longitude);
    	        	mapViewFragmentListener.choosePointEvent(latLngMypoint);
    	        	
//    	        	durationMapView.setVisibility(TextView.VISIBLE);
    	        	durationMap.setVisibility(TextView.VISIBLE);
    	        //	distanceMapView.setVisibility(TextView.VISIBLE);
    	        	distanceMap.setVisibility(TextView.VISIBLE);
    	        	
    	        	butChoosePoint.setVisibility(Button.INVISIBLE);


    	        	googleMap.clear();

					//try {
					//	onMarkerDragListener.wait();
						//markerStart = false;
					//} catch (InterruptedException e) {
					//	e.printStackTrace();
					//}

    	        }
    	      });

		broadcastReceiverMap = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				drawRoute(MainActivity.arrayPoints);

				distanceMap.setText("Дистанция " + MainActivity.distance);
				durationMap.setText("До прибытия " + MainActivity.durationReal);

			}
		};


		getContext().registerReceiver(broadcastReceiverMap,new IntentFilter(UPDATE_MAP));

        return rootView;
    }

    public void changeMapType (int type) {
    	googleMap.setMapType(type);
        }
    public void createMarcker () {
    	
    	googleMap.clear();
    	
    	markerOption.position(new LatLng(
		    		48.99,33.67))
			.title("Мое расположение").draggable(true);
    	marker = googleMap.addMarker(markerOption);
    	
    	//durationMapView.setVisibility(TextView.INVISIBLE);
    	durationMap.setVisibility(TextView.INVISIBLE);
    	//distanceMapView.setVisibility(TextView.INVISIBLE);
    	distanceMap.setVisibility(TextView.INVISIBLE);
  
    	butChoosePoint.setVisibility(Button.VISIBLE);

		//googleMap.set

		//if(!markerStart) {
		//	onMarkerDragListener.notify();
		//}
    	    	
    	Toast.makeText(getActivity(), "Выберите точку на карте и нажмите кнопку", Toast.LENGTH_LONG).show();
    	Log.i(LOG_TAG, "Метод создания маркера");
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
 	        			.title("Моя маршрутка"));	//������ ������ �� ���������);
 	       	    } 
 	        else if (i == mPoints.size() - 1) {
 	        	googleMap.addMarker(markerOption
 	          			.position(mPoints.get(i))
 	          			.title("Мое расположение"));	//������ ������ �� ����� ��������);
 	        }
 	        line.add(mPoints.get(i));	//��������� � ��������� ��������� ����������
 	        latLngBuilder.include(mPoints.get(i));
 	    }
 		googleMap.addPolyline(line);	//��������� ��������� �� �����
 	    int size = getResources().getDisplayMetrics().widthPixels;
 	    LatLngBounds latLngBounds = latLngBuilder.build();
 	   
 	    CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, size, size, 100);
 	    googleMap.animateCamera(track);		//�������������� �����������

			 Log.i(LOG_TAG, "Отрисовка маршрута выполнена.Количество точек " + mPoints.size());
         } catch (Exception e) {
			 Log.i(LOG_TAG, "Отрисовка маршрута не удалась" + e.getMessage());
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