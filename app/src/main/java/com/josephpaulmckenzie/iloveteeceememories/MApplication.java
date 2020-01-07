package com.josephpaulmckenzie.iloveteeceememories;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

public class MApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}