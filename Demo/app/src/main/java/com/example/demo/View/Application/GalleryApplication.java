package com.example.demo.View.Application;

import android.app.Application;
import android.content.Context;

import com.example.demo.DataModel.Db.Database;
import com.example.demo.Util.ApplicationManager;

public class GalleryApplication extends Application {
    private static GalleryApplication mInstance;
    public static volatile Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this.getApplicationContext();
        Database.initializeInstance(this);
    }

    public static synchronized GalleryApplication getInstance() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ApplicationManager.prepareApplication(this);
    }


    public static Context getAppContext() {
        return applicationContext;
    }

}
