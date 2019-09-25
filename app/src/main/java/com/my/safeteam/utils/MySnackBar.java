package com.my.safeteam.utils;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class MySnackBar {
    public Snackbar snackBar(String msg, View v, String action, View.OnClickListener listener) {
        return Snackbar.make(v, msg, Snackbar.LENGTH_LONG).setAction(action, listener);
    }
}
