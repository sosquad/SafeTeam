package com.my.safeteam.ui.crearReunion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CrearReunionViewModel extends ViewModel {

    private MutableLiveData<String> mText;


    public CrearReunionViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is crearReunion fragment");

    }

    public LiveData<String> getText() {
        return mText;
    }
}