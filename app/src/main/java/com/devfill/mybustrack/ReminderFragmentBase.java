package com.devfill.mybustrack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ReminderFragmentBase extends Fragment implements ReminderFragment.IReminderFragmentlistener,
        AddReminderFragment.IAddReminderFragment {


    public String LOG_TAG = "ReminderFragmentBase";
    FragmentTransaction ft;

    ReminderFragment reminderFragment = new ReminderFragment(this);
    AddReminderFragment addReminderFragment = new AddReminderFragment(this);


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.reminder_fragment_base, container, false);


        ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, reminderFragment);
        ft.commit();

        return  rootview;
    }

    @Override
    public void onClickFab() {
        //Toast.makeText(getContext(), "onClick FloatingActionButton", Toast.LENGTH_SHORT).show();

        ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, addReminderFragment);
        ft.commit();
    }

    @Override
    public void onClickSaveEvent() {
        ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, reminderFragment);
        ft.commit();
    }
}
