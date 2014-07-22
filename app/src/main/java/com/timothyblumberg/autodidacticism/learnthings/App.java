package com.timothyblumberg.autodidacticism.learnthings;

import android.app.Activity;
import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class App extends Application implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "App";

    private static App instance;
    private static DBHelper sDbHelper;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

//        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//
//        Display display = wm.getDefaultDisplay();
//
//        Point size = new Point();
//        display.getSize(size);
//        Globals.setDisplayWidth(size.x);
//        Globals.setDisplayHeight(size.y);

        registerActivityLifecycleCallbacks(this);
    }

    @Override
    protected void finalize() throws Throwable {

        if (sDbHelper != null) {
            sDbHelper.close();
        }
        final SQLiteDatabase db = getWritableDB();
        if (db != null && db.isOpen()) {
            db.close();
        }
        super.finalize();
    }

    public static App getInstance() {
        return instance;
    }

    public static SQLiteDatabase getWritableDB() {
        return getDBHelper().getWritableDatabase();
    }

    public static DBHelper getDBHelper() {
        if (sDbHelper == null) {
            sDbHelper = new DBHelper(getInstance());
        }
        return sDbHelper;
    }

    @Override public void onActivityResumed(Activity activity) {

    }

    @Override public void onActivityStarted(Activity activity) {

    }

    @Override public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override public void onActivityStopped(Activity activity) {

    }

    @Override public void onActivityPaused(Activity activity) {

    }

    @Override public void onActivityDestroyed(Activity activity) {

    }

    @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

}