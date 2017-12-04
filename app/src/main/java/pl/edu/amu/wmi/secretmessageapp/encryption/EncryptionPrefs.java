package pl.edu.amu.wmi.secretmessageapp.encryption;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Stworzone przez Eryk Mariankowski dnia 01.12.17.
 */
@SharedPref(SharedPref.Scope.APPLICATION_DEFAULT)
public interface EncryptionPrefs {

    String ivPass();

    String ivMsg();

    String encryptedPassword();

    String encryptedMsg();

    boolean fingerprint();

    boolean messageSaved();
}
