package pl.edu.amu.wmi.secretmessageapp.config;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.sharedpreferences.Pref;


import pl.edu.amu.wmi.secretmessageapp.cipher.CipherStore;
import pl.edu.amu.wmi.secretmessageapp.cipher.ConfigPrefs;
import pl.edu.amu.wmi.secretmessageapp.cipher.ConfigPrefs_;
import pl.edu.amu.wmi.secretmessageapp.cipher.EncryptionPrefs_;
import pl.edu.amu.wmi.secretmessageapp.cipher.EncryptionStore;
import pl.edu.amu.wmi.secretmessageapp.helper.DialogHelper;

/**
 * Stworzone przez Eryk Mariankowski dnia 01.12.17.
 */
@EBean
class ConfigViewModel {

    private static final String IS_MESSAGE_SAVED_PREF_KEY = "isMessageSavedPrefKey";
    private static final String MESSAGE_SHARED_PREF_KEY = "messageSharedPrefKey";
    private static final String FINGERPRINT_AUTH_SHARED_PREF_KEY = "fingerprintAuthSharedPrefKey";
    private static final String TAG = ConfigViewModel.class.getSimpleName();
    @RootContext
    Context context;
    @Bean
    CipherStore cipherStore;
    @Pref
    ConfigPrefs_ appPrefs;
    @Bean
    EncryptionStore encryptionStore;
    private SharedPreferences preferences;

    private SharedPreferences getPreferences() {
        if (preferences == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return preferences;
    }

    boolean checkIfMessageSaved() {
        return getPreferences().getBoolean(IS_MESSAGE_SAVED_PREF_KEY, false);
    }

    boolean checkIfFingerprintAuth() {
        return preferences.getBoolean(FINGERPRINT_AUTH_SHARED_PREF_KEY, false);
    }

    void saveMessage(String message) {
        preferences
                .edit()
                .putBoolean(IS_MESSAGE_SAVED_PREF_KEY, true)
                .putString(MESSAGE_SHARED_PREF_KEY, message)
                .apply();
    }

    boolean initConfig(Activity activity) {
        String result = cipherStore.checkFingerprintPermission();
        if (result != null) {
            DialogHelper.errorDialog(activity, result);
        } else {
            cipherStore.init();
            return true;
        }
        return false;
    }

    private String getSecretMessage() {
        String encryptedMessage = encryptionPrefs.encryptedMsg().get();
        return encryptionStore
                .decryptMessage(encryptedMessage);
    }

    @Pref
    EncryptionPrefs_ encryptionPrefs;

}
