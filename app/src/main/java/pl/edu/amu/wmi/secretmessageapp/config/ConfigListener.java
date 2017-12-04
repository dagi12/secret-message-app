package pl.edu.amu.wmi.secretmessageapp.config;

import android.support.v4.app.Fragment;

import pl.edu.amu.wmi.secretmessageapp.cipher.KeyAlias;


/**
 * @author Eryk Mariankowski <eryk.mariankowski@247.codes> on 30.11.17.
 */
public interface ConfigListener {

    void onRegistered();

    void onMessageSaved(Fragment fragment);

    void onLoggedIn();

    void onAuthChanged(KeyAlias forMethod);
}
