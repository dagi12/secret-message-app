package pl.edu.amu.wmi.secretmessageapp.config;

import android.app.Activity;
import android.content.Context;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.sharedpreferences.Pref;

import pl.edu.amu.wmi.secretmessageapp.cipher.EncryptionPrefs_;
import pl.edu.amu.wmi.secretmessageapp.cipher.EncryptionStore;
import pl.edu.amu.wmi.secretmessageapp.helper.DialogHelper;

/**
 * Stworzone przez Eryk Mariankowski dnia 01.12.17.
 */
@EBean
class ConfigViewModel {

    @RootContext
    Context context;

    @Bean
    EncryptionStore encryptionStore;

    @Pref
    EncryptionPrefs_ encryptionPrefs;

    void saveMessage(String message) {
        encryptionPrefs.messageSaved().put(true);
        encryptionPrefs.encryptedMsg().put(message);
    }

    boolean initConfig(Activity activity) {
        // sprawdzamy uprawnienia do korzystania z sensora
        String result = encryptionStore.checkFingerprintPermission();
        if (result != null) {
            DialogHelper.errorDialog(activity, result);
        } else {
            encryptionStore.init();
            return true;
        }
        return false;
    }

    boolean checkIfMessageSaved() {
        return encryptionPrefs.messageSaved().getOr(false);
    }

    boolean checkIfFingerprintAuth() {
        return encryptionPrefs.fingerprint().get();
    }

}
