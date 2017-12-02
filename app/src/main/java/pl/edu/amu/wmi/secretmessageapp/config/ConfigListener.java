package pl.edu.amu.wmi.secretmessageapp.config;

import android.support.v4.app.Fragment;

/**
 * @author Eryk Mariankowski <eryk.mariankowski@247.codes> on 30.11.17.
 */
public interface ConfigListener {

    void onRegistered(boolean withFingerprint);

    void onMessageSaved(Fragment fragment);

    void onLoggedIn();
}
