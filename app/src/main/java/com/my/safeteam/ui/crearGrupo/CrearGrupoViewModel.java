package com.my.safeteam.ui.crearGrupo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CrearGrupoViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CrearGrupoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}