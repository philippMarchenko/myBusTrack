package com.devfill.mybustrack;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Calendar;
import java.util.List;


public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.MyViewHolder>  {

    private static final String LOG_TAG = "ReminderTag";

    public static Context mContext;
    private Activity myActivity;
    public static List<Reminder> mReminderList;
    IReminderAdapterListener mIReminderAdapterListener;

    public interface IReminderAdapterListener {
        public void onClickDeleteItem(int position,String routename);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView route,distance,duration,durationReal;
        public Button deleteReminder;

        public MyViewHolder(View v) {
            super(v);
            this.route = (TextView) v.findViewById(R.id.route);
            this.distance = (TextView) v.findViewById(R.id.distance);
            this.duration = (TextView) v.findViewById(R.id.duration);
            this.durationReal = (TextView) v.findViewById(R.id.durationReal);
            this.deleteReminder = (Button) v.findViewById(R.id.deleteReminder);

        }
    }


    public ReminderAdapter(Context context, Activity activity,List<Reminder> reminderList,IReminderAdapterListener iReminderAdapterListener) {
        mReminderList = reminderList;
        mContext = context;
        myActivity = activity;
        mIReminderAdapterListener = iReminderAdapterListener;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.reminder, viewGroup, false);
            return new MyViewHolder(v);

    }
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        final Reminder reminder = mReminderList.get(position);

        viewHolder.route.setText(reminder.getRoute());
        viewHolder.distance.setText("Дистанция " + reminder.getDistance());
        viewHolder.duration.setText("Напомню за " + reminder.getDuration());
        viewHolder.durationReal.setText("До прибытия " + reminder.getDurationReal());


        viewHolder.deleteReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIReminderAdapterListener.onClickDeleteItem(position, reminder.getRoute());

            }
        });

    }

    @Override
    public int getItemCount() {
        return mReminderList.size();
    }

}
