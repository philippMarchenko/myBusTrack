package com.devfill.mybustrack.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.devfill.mybustrack.adapter.ReminderAdapter;
import com.devfill.mybustrack.helper.DBHelper;
import com.devfill.mybustrack.R;
import com.devfill.mybustrack.helper.RecyclerTouchListener;
import com.devfill.mybustrack.model.Reminder;

import java.util.ArrayList;
import java.util.List;

public class ReminderFragment extends Fragment implements ReminderAdapter.IReminderAdapterListener {


    public String LOG_TAG = "ReminderFragmentLogs";


    private ReminderAdapter reminderAdapter;
    private RecyclerView recyclerView;
    private List<Reminder> reminderList = new ArrayList<>();
    public static final String LOG_TAG_DB = "ReminderFragmentdbLogs";
    DBHelper dbHelper;

    FragmentTransaction ft;

    BroadcastReceiver broadcastReceiver;
    public static String UPDATE_LIST = "updateList";


    public ReminderFragment(){}


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.reminder_fragment, container, false);

        final AddReminderFragment addReminderFragment = new AddReminderFragment();

        recyclerView = (RecyclerView) rootview.findViewById(R.id.recycler_view_settings);
        reminderAdapter = new ReminderAdapter(getContext(),getActivity(),reminderList,this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(reminderAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(),
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                Toast.makeText(getContext(), "Single Click on position        :"+position,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getContext(), "Long press on position :"+position,
                        Toast.LENGTH_LONG).show();
            }
        }));

        Log.i(LOG_TAG,"onCreateView ReminderFragment");

        FloatingActionButton fab = (FloatingActionButton) rootview.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, addReminderFragment);
                ft.commit();

            }
        });

        dbHelper = new DBHelper(getContext());

        initReminderList();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

               String durationReal = intent.getStringExtra("durationReal");
               String distance = intent.getStringExtra("distance");
               String routName = intent.getStringExtra("routName");

                Log.d(LOG_TAG, "onReceive. routeName " + routName + " distance " + distance);

              for(int i = 0; i < reminderList.size(); i ++){

                  if(reminderList.get(i).getRoute().equals(routName)){  //если имя маршрута , который монитори совпадет с именем маршрута в списке напоминаний
                      reminderList.get(i).setDistance(distance);           //покажем дистанци. и время
                      reminderList.get(i).setDurationReal(durationReal);
                      reminderAdapter.notifyDataSetChanged();
                  }
              }

            }
        };

        getContext().registerReceiver(broadcastReceiver,new IntentFilter(UPDATE_LIST));

        return  rootview;
    }

    public  void initReminderList (){

        Reminder r;
        reminderList.clear();

        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor c = db.query("mytable", null, null, null, null, null, null);

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

                r = new Reminder(c.getString(routNameColIndex),c.getString(distanceColIndex),c.getString(durationColIndex),c.getString(durationRealColIndex));
                reminderList.add(r);

              } while (c.moveToNext());
            } else
                Log.d(LOG_TAG_DB, "0 rows");
                c.close();
                dbHelper.close();

            Log.d(LOG_TAG_DB, "Init list.Count reminder " + reminderList.size());

            reminderAdapter.notifyDataSetChanged();

    }

    @Override
    public void onClickDeleteItem(int position,String routeName) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor c = db.query("mytable", null, null, null, null, null, null);
        int x = db.delete("mytable","routName = ?",	//по наименованию маршрута
                new String[] { routeName });

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

        reminderList.remove(position);
        reminderAdapter.notifyDataSetChanged();

    }

}
