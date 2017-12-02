package pl.edu.amu.wmi.secretmessageapp;

import android.app.Application;

import timber.log.Timber;

/**
 * @author Eryk Mariankowski <eryk.mariankowski@247.codes> on 02.12.17.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }

}
