package com.devfill.mybustrack.ui;

import java.net.HttpURLConnection;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.Space;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.text.format.Time;


import com.devfill.mybustrack.helper.DBHelper;
import com.devfill.mybustrack.service.DurationFinishService;
import com.devfill.mybustrack.ui.fragment.MapViewFragment;
import com.devfill.mybustrack.R;
import com.devfill.mybustrack.ui.fragment.ReminderFragment;
import com.devfill.mybustrack.ui.fragment.ReminderFragmentBase;
import com.devfill.mybustrack.internet.RouteApi;
import com.devfill.mybustrack.model.RouteResponse;
import com.devfill.mybustrack.internet.TrackApi;
import com.devfill.mybustrack.model.TrackInfo;
import com.devfill.mybustrack.ui.fragment.AddReminderFragment;
import com.devfill.mybustrack.ui.fragment.MapViewFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wallet.NotifyTransactionStatusRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements MapViewFragment.MapViewFragmentListener {

    public static String[] mEntries = new String[]{"Комсомольск - Кременчуг","Полтава - Харьков","Киев - Одесса", "Житомир - Львов","Саловка - Кременчук"};;


    public static final String LOG_TAG = "myLogs";
    public static final String LOG_TAG_DB = "dbLogs";

    public static final int IDM_GPS = 201;
    public static final int IDM_POINT_ON_MAP = 202;
    public static final int IDM_TYPE_SAT = 301;
    public static final int IDM_TYPE_NORM = 302;
    public static final int IDM_ADD_ROUT = 401;
    public static final int IDM_ADD_TIME = 402;
    public static final int IDM_DEL_REM = 501;
    public static final int IDM_DEL_REM1 = 502;
    public static final int IDM_REF_ROUT = 601;
    public static final int IDM_REF_TIME = 602;

    public static final int TYPE_GPS = 1;
    public static final int TYPE_POINT_ON_MAP = 2;
    public static final int STATUS_ON = 1;
    public static final int STATUS_OFF = 2;

    TextView tvLatitude;
    TextView tvLongitude;
    TextView tvDate;
    TextView tvTime;
    TextView viewMyZoom;


    String server_name = "http://www.mkdeveloper.ru";
    String myLatitude;
    String myLongitude;
    public static String arrayPoints;
    String routName;
    public static String durationReal;
    public  static String duration;
    public  static String distance;
    String position = "Горишние Плавни, Полтавська область, Украина";
    String destination = "Саловка, Полтавська область, Украина";
    String myKey = "AIzaSyANI2wjsY9Vd6Oq3HAG_2gVU6tW8ZkZx9g";
    int checkReminderStatus;

    String latitudeBus;
    String longitudeBus;
    String[] msgString = new String[4];

    Long  time;

    final int STATUS_RECIVE = 1;
    int currentFragmentView = 0;
    int chooseTypePoint = TYPE_POINT_ON_MAP;
    boolean satusSetting = true;
    boolean runTimer = true;
    boolean tableIsEmpty = true;
    public static boolean sendNoty = false;
    public static boolean timerService = false;
    long millis = 0;

    LocationManager locationManager;

    HttpURLConnection conn;
    FragmentTransaction ft;
    MapViewFragment mapViewFragment;
    DBHelper dbHelper;

    AddReminderFragment frag3;
    Long last_time; // время последней записи в БД, отсекаем по нему что нам/ тянуть с сервера, а что уже есть

    Timer myTimer = new Timer(); // Создаем таймер

    /// создаем объект для данных
    ContentValues cv = new ContentValues();

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setTheme(R.style.AppThemeNonDrawer);

        mapViewFragment = new MapViewFragment();

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().hide();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

     /*   if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    LocationService.MY_PERMISSION_ACCESS_COURSE_LOCATION );
        }*/
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 5, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                1000 * 10, 0,locationListener);

        // создаем объект для создания и управления версиями БД
        dbHelper = new DBHelper(this);

        if(!isMyServiceRunning(DurationFinishService.class)) {
            Log.d(LOG_TAG, "DurationFinishService is not runing");
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            // делаем запрос всех данных из таблицы mytable, получаем Cursor
            Cursor c = db.query("mytable", null, null, null, null, null, null);

            if (c.moveToFirst()) {
                tableIsEmpty = false;
                statDurationFinishService();

            }

        }

        task1();
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mapViewFragment, "Карта");
        adapter.addFragment(new ReminderFragmentBase(), "Напоминания");

        viewPager.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        SubMenu subMenuChooseTypePoint = menu.addSubMenu("Тип моего местоположения");
        subMenuChooseTypePoint.add(Menu.NONE, IDM_POINT_ON_MAP, Menu.NONE, "Точка на карте");
        subMenuChooseTypePoint.add(Menu.NONE, IDM_GPS, Menu.NONE, "Координаты GPS");
        SubMenu subMenuChooseTypeMap = menu.addSubMenu("Тип карты");
        subMenuChooseTypeMap.add(Menu.NONE, IDM_TYPE_SAT, Menu.NONE, "Вид со спутника");
        subMenuChooseTypeMap.add(Menu.NONE, IDM_TYPE_NORM, Menu.NONE, "Нормальный");


        return true;
    }
    void enableGPS (){
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //return true;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 5, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                1000 * 10, 0, locationListener);
    }
    void disableGPS (){
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //return true;
        }
        locationManager.removeUpdates(locationListener);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Операции для выбранного пункта меню
        switch (item.getItemId())
        {
            case IDM_TYPE_SAT:
                mapViewFragment.changeMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case IDM_TYPE_NORM:
                mapViewFragment.changeMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case IDM_GPS:
                chooseTypePoint = TYPE_GPS;
                Toast.makeText(this, "Выбран режим GPS", Toast.LENGTH_LONG).show();
                enableGPS();
                return true;
            case IDM_POINT_ON_MAP:
                runTimer = false;
                chooseTypePoint = TYPE_POINT_ON_MAP;
                disableGPS();
                mapViewFragment.createMarcker();
                satusSetting = false;

                Log.i(LOG_TAG, "Выбран пункт меню, satusSetting = " + satusSetting);
                //	mapViewFragment.changeMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    protected void onPause(){
        super.onPause();
        runTimer = false;
        //myTimer.cancel();

        disableGPS();

        Log.i(LOG_TAG, "Выход из приложения onPause");
    }
    protected void onResume(){
        super.onResume();

        runTimer = true;
        enableGPS();

        Log.i(LOG_TAG, "Возобновление работы приложения onResume");

    }
    protected void onDestroy(){
        super.onDestroy();
        // myTimer.cancel();
        runTimer = false;
        disableGPS();
        Log.i(LOG_TAG, "Выход из приложения onDestroy");

    }
    private void statDurationFinishService() {

        startService(new Intent(this, DurationFinishService.class));
        timerService = true;
    }
    private void stopDurationFinishService() {

        stopService(new Intent(this, DurationFinishService.class));
        timerService = false;
    }
    private void task1() {

        final Handler uiHandler = new Handler();
        myTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                if(runTimer){
                            updatelist();
                          //  goReqest();
                            getCoordinatesbus();
                            getRouteData();
                }
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(runTimer){
                            updatMap();

                        }
                    }
                });

            };

        }, 0L, 5L * 1000); // интервал - 60000 миллисекунд, 0 миллисекунд до первого запуска.
    }
    public void updatelist(){

        final Intent intent = new Intent(ReminderFragment.UPDATE_LIST);

        intent.putExtra("durationReal",durationReal);
        intent.putExtra("distance",distance);    //обновили граффик,
        intent.putExtra("routName", mEntries[4]);    //обновили граффик,
        sendBroadcast(intent);
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public void getRouteData(){

            try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RouteApi routeService = retrofit.create(RouteApi.class);


           Log.i(LOG_TAG,"getRoute" + " position " + position + " destination " + destination);

            routeService.getRoute(position, destination,myKey,false, "ru").enqueue(new Callback<RouteResponse>() {
                @Override
                public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {

                    Log.i(LOG_TAG,"onResponse. Приняли ответ от google" + response.toString());

                     arrayPoints = response.body().getPoints();
                     durationReal = response.body().getDurationRout();
                     distance = response.body().getDistanceRout();

                    Log.i(LOG_TAG,"durationReal " + durationReal + " distance " + distance);

                }
                @Override
                public void onFailure(Call<RouteResponse> call, Throwable t) {
                    Log.i(LOG_TAG,"onFailure. Ошибка REST запроса getRoute " + t.getMessage());
                }
            });
        }
        catch(Exception e){

            Log.i(LOG_TAG,"Ошибка REST запроса к серверу google getRoute " + e.getMessage());
        }
    }
    public void getCoordinatesbus(){

        try {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    // .client(getUnsafeOkHttpClient())
                    .baseUrl("maps.mkdeveloper.ru")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            TrackApi trackApi = retrofit.create(TrackApi.class);

            trackApi.getTrackInfo("select","1").enqueue(new Callback<TrackInfo>() {
                @Override
                public void onResponse(Call<TrackInfo> call, Response<TrackInfo> response) {
                    Log.i(LOG_TAG,"onResponse getTrackInfo "  + call.toString() + "\n" );

                    Log.i(LOG_TAG,"onResponse getTrackInfo "  + response.toString() + "\n" );
                    Log.i(LOG_TAG,"onResponse getTrackInfo getTrackId " + response.body().getTrackId() + "\n");
                    Log.i(LOG_TAG,"onResponse getTrackInfo getTime " + response.body().getTime()  + "\n");
                    Log.i(LOG_TAG,"onResponse getTrackInfo getLatitude " + response.body().getLatitude() + "\n");
                    Log.i(LOG_TAG,"onResponse getTrackInfo getLongitude " + response.body().getLongitude() + "\n");


                    longitudeBus = response.body().getLongitude();
                    latitudeBus = response.body().getLatitude();
                    Log.i(LOG_TAG,"substring  latitudeBus " +  latitudeBus.substring(0,2) + "\n");

                    String latitudeBusv;
                    String longitudeBusv;

                    try {
                        latitudeBusv = latitudeBus.substring(2, 4) + latitudeBus.substring(4, 10);
                        latitudeBusv = latitudeBusv.replaceAll("[^0-9]+", "");
                        int latitudeBusvInt = Integer.parseInt(latitudeBusv);
                        latitudeBusvInt = latitudeBusvInt / 60;

                        longitudeBusv = longitudeBus.substring(3, 5) + longitudeBus.substring(5, 11);
                        longitudeBusv = longitudeBusv.replaceAll("[^0-9]+", "");
                        int longitudeBusvInt = Integer.parseInt(longitudeBusv);
                        longitudeBusvInt = longitudeBusvInt / 60;

                        position = latitudeBus.substring(0,2) + "." + latitudeBusvInt + "," + longitudeBus.substring(0,3) + "." + longitudeBusvInt;

                    }
                    catch(Exception e){
                          Toast.makeText(getBaseContext(), "Ошибка принятых координат! ", Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onFailure(Call<TrackInfo> call, Throwable t) {
                    Log.i(LOG_TAG,"onFailure. Ошибка REST запроса getTrackInfo " + t.getMessage());
                }
            });
        }
        catch(Exception e){

            Log.i(LOG_TAG,"Ошибка REST запроса к серверу getCoordinatesbus" + e.getMessage());
        }
    }
    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            //  showLocation(location);
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            myLatitude = String.format(" %1$.4f",location.getLatitude());
            myLongitude = String.format(" %1$.4f",location.getLongitude());

            //   lat = Math.round(lat*1000)/1000;
            //   lng = Math.round(lng*1000)/1000;

            Log.i(LOG_TAG, "Получены координаты телефона \n lat: " + lat +  "\n lng: " + lng);
            if(chooseTypePoint == chooseTypePoint)
                destination = lat + "," + lng;

        }

        @Override
        public void onProviderDisabled(String provider) {
            // checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            //  checkEnabled();
            // showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };
    public void updatMap (){
        final Intent intent = new Intent(MapViewFragment.UPDATE_MAP);
        sendBroadcast(intent);
    }
    @Override
    public void choosePointEvent(LatLng latLng) {

        if(chooseTypePoint == TYPE_POINT_ON_MAP){
            destination = latLng.latitude + "," + latLng.longitude;
            Toast.makeText(this, "Выбран режим точки на карте", Toast.LENGTH_LONG).show();
            getRouteData();
            runTimer = true;
            satusSetting = true;
            Log.i(LOG_TAG, "Сработал интерфейс, координаты " + destination);
        }
        return;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<android.support.v4.app.Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mFragmentList.get(position);
        }


        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(android.support.v4.app.Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}

