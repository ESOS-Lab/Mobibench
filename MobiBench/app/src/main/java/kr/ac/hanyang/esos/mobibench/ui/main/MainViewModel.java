package kr.ac.hanyang.esos.mobibench.ui.main;

import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private MutableLiveData<Boolean> checkSQLite = new MutableLiveData<Boolean>();
    private MutableLiveData<Boolean> checkFileIO = new MutableLiveData<Boolean>();

    public MutableLiveData<Boolean> getCheckSQLite() {
        return checkSQLite;
    }

    public MutableLiveData<Boolean> getCheckFileIO() {
        return checkFileIO;
    }
}
