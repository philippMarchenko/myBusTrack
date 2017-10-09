package com.devfill.mybustrack.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devfill.mybustrack.R;

public class ReminderFragmentBase extends Fragment {


    public String LOG_TAG = "ReminderFragmentBase";
    FragmentTransaction ft;

    ReminderFragment reminderFragment = new ReminderFragment();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.reminder_fragment_base, container, false);

        ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, reminderFragment);
        ft.commit();

        return  rootview;
    }

}
