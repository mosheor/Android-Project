package com.example.ben.final_project;

import android.app.Application;
import android.content.Context;

/**
 * Created by mazliachbe on 25/06/2017.
 */

/**
 * MyApplication in order to get the app context
 */
public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
