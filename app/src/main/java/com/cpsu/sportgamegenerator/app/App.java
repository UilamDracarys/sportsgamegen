package com.cpsu.sportgamegenerator.app;

import android.app.Application;
import android.content.Context;

import com.cpsu.sportgamegenerator.Utils.DBHelper;
import com.cpsu.sportgamegenerator.Utils.DatabaseManager;


/**
 * Created by William on 12/17/2018.
 */

public class App extends Application {
    private static Context context;
    private static DBHelper dbHelper;

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = this.getApplicationContext();
        dbHelper = new DBHelper();
        DatabaseManager.initializeInstance(dbHelper);
    }

    public static Context getContext(){
        return context;
    }
}
