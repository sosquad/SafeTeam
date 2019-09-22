package com.my.safeteam.dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private EditText editText;

    public static TimePickerFragment newInstance(EditText editText) {
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setEditText(editText);
        return fragment;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }


    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        String horas = Integer.toString(i);
        String minutos = Integer.toString(i1);

        if (i < 10) {
            horas = "0" + i;
        }
        if (i1 < 10) {
            minutos = "0" + i1;
        }

        this.editText.setText(horas + ":" + minutos + ":00");
    }
}