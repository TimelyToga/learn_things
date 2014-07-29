package com.timothyblumberg.autodidacticism.learnthings;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.timothyblumberg.autodidacticism.learnthings.common.DBHelper;
import com.timothyblumberg.autodidacticism.learnthings.dirtywork.AppModule;

import dagger.ObjectGraph;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class App extends Application implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "App";

    private static App instance;
    public static DBHelper sDbHelper;
    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault("fonts/GothamRnd-Book.otf", R.attr.fontPath);
        instance = this;

        objectGraph = ObjectGraph.create(new AppModule(this));
        objectGraph.injectStatics();

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

    public static Context getAppContext() {
        return instance;
    }

    public static SQLiteDatabase getWritableDB() {
        return getDBHelper().getWritableDatabase();
    }

    public static DBHelper getDBHelper() {
        if (sDbHelper == null) {
            sDbHelper = new DBHelper(getAppContext());
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

    public void inject(Object object) {
        objectGraph.inject(object);
    }


}