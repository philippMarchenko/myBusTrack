package com.devfill.mybustrack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.Service;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.ActionBar;
import android.app.ActionBar.OnMenuVisibilityListener;
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
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText; import android.widget.TextView;
import android.widget.Toast;
import android.text.format.Time;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener,
        MapViewFragment.onSomeEventListener,
        Fragment2.onFragment2Listener,
        Fragment3.onFragment3Listener {

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


    String server_name = "http://www.sitesphilipp.ho.ua";
    String myLatitude;
    String myLongitude;
    String arrayPoints;
    String routName;
    static String durationReal;
    static String duration;
    String distance;
    String position = "Горишние Плавни, Полтавська область, Украина";
    String destination = "Саловка, Полтавська область, Украина";
    int checkReminderStatus;

    String latitude;
    String longitude;
    String[] msgString = new String[4];

    Long 	 time_ms;

    final int STATUS_RECIVE = 1;
    int currentFragmentView = 0;
    int chooseTypePoint = TYPE_POINT_ON_MAP;
    boolean satusSetting = true;
    boolean runTimer = true;
    boolean tableIsEmpty = true;
    static boolean sendNoty = false;
    static boolean timerService = false;
    long millis = 0;
    LocationManager locationManager;

    HttpURLConnection conn;
    FragmentTransaction ft;
    MapViewFragment mapViewFragment = new MapViewFragment();
    DBHelper dbHelper;
    //  Time time = new Time(Time.getCurrentTimezone());
    // Date date = new Date();
    Fragment2 frag2;
    Fragment3 frag3;
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

        frag2 = new Fragment2();
        frag3 = new Fragment3();

      //  ft = getFragmentManager().beginTransaction();
       // ft.add(R.id.container, mapViewFragment);
       // ft.commit();



      /*  ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Tab tab = bar.newTab();
        tab.setText("Карта");
        tab.setTabListener(this);
        bar.addTab(tab);

        tab = bar.newTab();
        tab.setText("Напоминания");
        tab.setTabListener(this);
        bar.addTab(tab);
*/

        // создаем объект для создания и управления версиями БД
        dbHelper = new DBHelper(this);




        if(!isMyServiceRunning(DurationFinishService.class))
        {
            Log.d(LOG_TAG, "DurationFinishService is not runing");
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            // делаем запрос всех данных из таблицы mytable, получаем Cursor
            Cursor c = db.query("mytable", null, null, null, null, null, null);

            if (c.moveToFirst()){
                tableIsEmpty = false;
                statDurationFinishService();

            }

        }




        task1();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mapViewFragment, "Карта");
        adapter.addFragment(new Fragment2(), "Настройки");

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
                    switch(currentFragmentView)	{
                        case 0:
                            goReqest();
                            //if(sendNoty){
                            //	sendNoty = false;
                            //	((CheckBox) frag2.getView().findViewById(R.id.checkReminder))
                            //.setChecked(false);
                            //	}

                            Log.d(LOG_TAG, "Первый таб выбран ");
                            break;
                        case 1:
                            goReqest();
                            Log.d(LOG_TAG, "Второй таб выбран ");
                            break;
                        default:
                            break;
                    }
                }
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(runTimer){
                            Log.d(LOG_TAG, "Функция  iHandler.post");
                            switch(currentFragmentView)	{

                                case 0:
                                    ((TextView) mapViewFragment.getView().findViewById(R.id.durationMapView))
                                            .setText(durationReal);
                                    ((TextView) mapViewFragment.getView().findViewById(R.id.distanceMapView))
                                            .setText(distance);
                                    mapViewFragment.drawRoute(arrayPoints);

                                    break;
                                case 1:
                                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                                    // делаем запрос всех данных из таблицы mytable, получаем Cursor
                                    Cursor c = db.query("mytable", null, null, null, null, null, null);

                                    if (c.moveToFirst()){
                                        tableIsEmpty = false;
                                        // определяем номера столбцов по имени в выборке
                                        int routNameColIndex = c.getColumnIndex("routName");
                                        int durationColIndex = c.getColumnIndex("duration");
                                        int durationRealColIndex = c.getColumnIndex("durationReal");
                                        int distanceColIndex = c.getColumnIndex("distance");
                                        int checkReminderStatusColIndex = c.getColumnIndex("checkReminderStatus");

                                        do {
                                            // получаем значения по номерам столбцов и пишем все в лог
                                            Log.d(LOG_TAG_DB,
                                                    "routName = " + c.getString(routNameColIndex) +
                                                            ", duration = " + c.getString(durationColIndex) +
                                                            ", durationReal = " + c.getString(durationRealColIndex) +
                                                            ", checkReminderStatus = " + c.getInt(checkReminderStatusColIndex) +
                                                            ", distance = " + c.getString(distanceColIndex));

                                            routName = c.getString(routNameColIndex);
                                            distance = c.getString(distanceColIndex);
                                            duration =  c.getString(durationColIndex);
                                            durationReal = c.getString(durationRealColIndex);
                                            checkReminderStatus = c.getInt(checkReminderStatusColIndex);

                                            frag2.checkReminderChange(checkReminderStatus);


                                            frag2.setValues(routName,distance,duration,durationReal);

                                            // переход на следующую строку
                                            // а если следующей нет (текущая - последняя), то false - выходим из цикла
                                        } while (c.moveToNext());

                                        frag2.setVisibleValues();
                                        if(!isMyServiceRunning(DurationFinishService.class))
                                        {
                                            statDurationFinishService();
                                        }
                                    }
                                    else{
                                        tableIsEmpty = true;
                                        frag2.setInvisibleValues();
                                        stopDurationFinishService();
                                    }


                                    break;
                                default:
                                    break;

                            }
                        }
                    }
                });

            };

        }, 0L, 3L * 1000); // интервал - 60000 миллисекунд, 0 миллисекунд до первого запуска.
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
    private void goReqest() {

        String ansver = null, lnk;
        lnk = server_name + "/track.php?event=select";

        try {
        /*    RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("https://maps.googleapis.com").setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();
            RouteApi routeService = restAdapter.create(RouteApi.class);
            RouteResponse routeResponse = routeService.getRoute(position, destination, false, "ru");*/

            Retrofit retrofit = new Retrofit.Builder().baseUrl("https://maps.googleapis.com")
                    .build();
            RouteApi routeService = retrofit.create(RouteApi.class);
            RouteResponse routeResponse = routeService.getRoute(position, destination, false, "ru");
            arrayPoints = routeResponse.getPoints();
            durationReal = routeResponse.getDurationRout();
            distance = routeResponse.getDistanceRout();

            // подключаемся к БД
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Log.d(LOG_TAG_DB, "--- Update in mytable: ---");
            // подготовим данные для вставки в виде пар: наименование столбца - значение
            cv.put("distance", distance);
            cv.put("durationReal", durationReal);
            int updCount = db.update("mytable", cv, "routName = ?",
                    new String[] { "Крем.-Саловка" });
            Log.d(LOG_TAG_DB, "updated rows count = " + updCount);
            Log.d(LOG_TAG_DB, "--- Rows in mytable: ---");
            // делаем запрос всех данных из таблицы mytable, получаем Cursor
            Cursor c = db.query("mytable", null, null, null, null, null, null);

            // ставим позицию курсора на первую строку выборки
            // если в выборке нет строк, вернется false
            if (c.moveToFirst()) {

                // определяем номера столбцов по имени в выборке
                int routNameColIndex = c.getColumnIndex("routName");
                int durationColIndex = c.getColumnIndex("duration");
                int durationRealColIndex = c.getColumnIndex("durationReal");
                int distanceColIndex = c.getColumnIndex("distance");

                do {
                    // получаем значения по номерам столбцов и пишем все в лог
                    Log.d(LOG_TAG_DB,
                            "routName = " + c.getString(routNameColIndex) +
                                    ", duration = " + c.getString(durationColIndex) +
                                    ", durationReal = " + c.getString(durationRealColIndex) +
                                    ", distance = " + c.getString(distanceColIndex));
                    // переход на следующую строку
                    // а если следующей нет (текущая - последняя), то false - выходим из цикла
                } while (c.moveToNext());
            } else
                Log.d(LOG_TAG_DB, "0 rows");
            c.close();

            // закрываем подключение к БД
            dbHelper.close();

        }
        catch(Exception e){

            Log.i(LOG_TAG,"Ошибка REST запроса к серверу google " + e.getMessage());
        }


        // создаем соединение ---------------------------------->
        try {
            Log.i(LOG_TAG,
                    "+ FoneService --------------- ОТКРОЕМ СОЕДИНЕНИЕ");

            conn = (HttpURLConnection) new URL(lnk)
                    .openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setDoInput(true);
            conn.connect();

        } catch (Exception e) {
            Log.i(LOG_TAG, "+ FoneService ошибка: " + e.getMessage());
        }
        // получаем ответ ---------------------------------->
        try {
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String bfr_st = null;
            while ((bfr_st = br.readLine()) != null) {
                sb.append(bfr_st);
            }

            Log.i(LOG_TAG, "+ FoneService - полный ответ сервера:\n"
                    + sb.toString());
            // сформируем ответ сервера в string
            // обрежем в полученном ответе все, что находится за "]"
            // это необходимо, т.к. json ответ приходит с мусором
            // и если этот мусор не убрать - будет невалидным
            ansver = sb.toString();
            ansver = ansver.substring(0, ansver.indexOf("]") + 1);

            is.close(); // закроем поток
            br.close(); // закроем буфер

        } catch (Exception e) {
            Log.i(LOG_TAG, "+ FoneService ошибка: " + e.getMessage());
        } finally {
            conn.disconnect();
            Log.i(LOG_TAG,"+ FoneService --------------- ЗАКРОЕМ СОЕДИНЕНИЕ");
        }

        // запишем ответ в БД ---------------------------------->
        if (ansver != null && !ansver.trim().equals("")) {

            Log.i(LOG_TAG,
                    "+ FoneService ---------- ответ содержит JSON:");

            try {
                // ответ превратим в JSON массив
                JSONArray ja = new JSONArray(ansver);
                JSONObject jo;

                Integer i = 0;

                while (i < ja.length()) {

                    // разберем JSON массив построчно
                    jo = ja.getJSONObject(i);

                    Log.i(LOG_TAG,
                            "=================>>> "
                                    + jo.getString("latitude")
                                    + " | " + jo.getString("longitude")
                                    + " | " + jo.getString("date")
                                    + " | " + jo.getString("time")
                                    + " | " + jo.getLong("time_ms"));


                    latitude = jo.getString("latitude");
                    longitude = jo.getString("longitude");

                    //position = latitude + "," + longitude;

                    msgString[0] = jo.getString("latitude");
                    msgString[1] = jo.getString("longitude");
                    msgString[2] = jo.getString("date");
                    msgString[3] = jo.getString("time");

                    Log.i(LOG_TAG,"Широта + Долгота = " + latitude + " " + longitude);

                    if(time_ms != jo.getLong("time_ms")){
                        //h.sendEmptyMessage(STATUS_RECIVE);
                    }
                    time_ms	= jo.getLong("time_ms");
                    i++;

                }
            } catch (Exception e) {
                // если ответ сервера не содержит валидный JSON
                Log.i(LOG_TAG,
                        "+ FoneService ---------- ошибка ответа сервера:\n"
                                + e.getMessage());
            }
        } else {
            // если ответ сервера пустой
            Log.i(LOG_TAG,
                    "+ FoneService ---------- ответ не содержит JSON!");
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
    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {

        ft = getFragmentManager().beginTransaction();

        switch(tab.getPosition()){

            case 0:
                //ft.remove(frag2);
               // ft.replace(R.id.container, mapViewFragment);
                currentFragmentView = 0;
                break;
            case 1:
                currentFragmentView = 1;
                //ft.remove(mapViewFragment);
               // ft.replace(R.id.container, frag2);

                break;
            default:
                break;

        }

        ft.commit();
    }
    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub

    }
    @Override
    public void choosePointEvent(LatLng latLng) {

        if(chooseTypePoint == TYPE_POINT_ON_MAP){
            destination = latLng.latitude + "," + latLng.longitude;
            Toast.makeText(this, "Выбран режим точки на карте", Toast.LENGTH_LONG).show();
            runTimer = true;
            satusSetting = true;
            Log.i(LOG_TAG, "Сработал интерфейс, координаты " + destination);
        }
        return;
    }
    @Override
    public void onClickAddEvent() {

        //Reminders.initReminder("Мой маршрут", "0 км", "0 мин", "0 мин");

        currentFragmentView = 3;
        ft = getFragmentManager().beginTransaction();
       // ft.replace(R.id.container, frag3);
        ft.commit();

    }
    @Override
    public void onClickRefEvent() {
        currentFragmentView = 3;
        ft = getFragmentManager().beginTransaction();
       // ft.replace(R.id.container, frag3);
        ft.commit();

    }
    @Override
    public void onClickDelEvent() {

        Reminders.deInitReminder();

        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG_DB, "--- Clear mytable: ---");
        // удаляем все записи
        int clearCount = db.delete("mytable", null, null);

        // закрываем подключение к БД
        dbHelper.close();

        frag2.setInvisibleValues();
    }
    @Override
    public void onClickSaveEvent(String strSpin,String strEt) {

        currentFragmentView = 1;
        ft = getFragmentManager().beginTransaction();
        //ft.replace(R.id.container, frag2);
        ft.commit();

        //Reminders.routeId[Reminders.count] = strSpin;
        //Reminders.duration[Reminders.count] = strEt;


        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor c = db.query("mytable", null, null, null, null, null, null);

        Log.d(LOG_TAG_DB, "--- Insert in mytable: ---");
        // подготовим данные для вставки в виде пар: наименование столбца - значение
        cv.put("routName", strSpin);
        cv.put("duration", strEt);


        if (c.moveToFirst()){	//если таблица не пуста то обновляем знаения
            int updCount = db.update("mytable", cv, "routName = ?",	//по наименованию маршрута
                    new String[] { strSpin });
        }
        else{	//если пуста
            // вставляем записьи и получаем ее ID
            long rowID = db.insert("mytable", null, cv);
            Log.d(LOG_TAG_DB, "row inserted, ID = " + rowID);
        }


        Log.d(LOG_TAG_DB, "--- Rows in mytable: ---");


        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int routNameColIndex = c.getColumnIndex("routName");
            int durationColIndex = c.getColumnIndex("duration");
            int durationRealColIndex = c.getColumnIndex("durationReal");
            int distanceColIndex = c.getColumnIndex("distance");
            int checkReminderStatusColIndex = c.getColumnIndex("checkReminderStatus");

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                Log.d(LOG_TAG_DB,
                        "routName = " + c.getString(routNameColIndex) +
                                ", duration = " + c.getString(durationColIndex) +
                                ", durationReal = " + c.getString(durationRealColIndex) +
                                ", distance = " + c.getString(distanceColIndex) +
                                ", checkReminderStatus = " + c.getInt(checkReminderStatusColIndex));
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG_DB, "0 rows");
        c.close();
        /*// вставляем запись и получаем ее ID
    long rowID = db.insert("mytable", null, cv);*/

        // Log.d(LOG_TAG_DB, "row inserted, ID = " + rowID);
        // закрываем подключение к БД
        dbHelper.close();

        frag2.setValues(routName,distance,duration,durationReal);
    }
    @Override
    public void onClickcheckReminder(int i) {

        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor c = db.query("mytable", null, null, null, null, null, null);

        cv.put("checkReminderStatus", i);

        int updCount = db.update("mytable", cv, "routName = ?",
                new String[] { routName });
        c.close();

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

