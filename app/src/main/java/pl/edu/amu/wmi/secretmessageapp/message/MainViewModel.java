package pl.edu.amu.wmi.secretmessageapp.message;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * Stworzone przez Eryk Mariankowski dnia 01.12.17.
 */
@EBean
public class MainViewModel {

    @RootContext
    Context context;

    private SharedPreferences preferences;

    @AfterInject
    void init() {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }




}
