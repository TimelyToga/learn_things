package com.timothyblumberg.autodidacticism.learnthings.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.timothyblumberg.autodidacticism.learnthings.R;
import com.timothyblumberg.autodidacticism.learnthings.activities.SettingsActivity;
import com.timothyblumberg.autodidacticism.learnthings.common.G;
import com.timothyblumberg.autodidacticism.learnthings.common.Util;

/**
 * Created by Tim on 8/17/14.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    boolean isStartTime;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int hour, minute;

        Bundle extras = this.getArguments();
        if (extras != null) {
            String whichTime = extras.getString(G.EXTRA_WHICH_TIME);
            if(whichTime.equals(G.START_TIME)){
                String[] array = G.curUser.startTime.split(":");
                hour = Integer.valueOf(array[0]);
                minute = Integer.valueOf(array[1]);
                isStartTime = true;
            } else {
                String[] array = G.curUser.endTime.split(":");
                hour = Integer.valueOf(array[0]);
                minute = Integer.valueOf(array[1]);
                isStartTime = false;
            }
        } else {
            hour = 0;
            minute = 0;
        }

        // Use the current time as the default values for the picker


        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Set title for this dialog
        if(isStartTime){
            getDialog().setTitle(getString(R.string.set_start_time));
        } else {
            getDialog().setTitle(getString(R.string.set_end_time));
        }
        View v = super.onCreateView(inflater, container, savedInstanceState);
        return v;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        String time = Util.makeTimeString(hourOfDay, minute);
        if(isStartTime){
            G.curUser.setTimes(time, "");
            SettingsActivity.updateTimeButtons(time, "");
        } else {
            G.curUser.setTimes("", time);
            SettingsActivity.updateTimeButtons("", time);
        }

    }
}
