package com.example.masterchef.ui.customer.jointable;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class JoinTableViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public JoinTableViewModel() {
        mText = new MutableLiveData<>();
    }

}