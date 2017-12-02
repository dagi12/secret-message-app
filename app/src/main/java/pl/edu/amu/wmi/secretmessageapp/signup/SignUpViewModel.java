package pl.edu.amu.wmi.secretmessageapp.signup;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import pl.edu.amu.wmi.secretmessageapp.cipher.EncryptionPrefs_;
import pl.edu.amu.wmi.secretmessageapp.cipher.EncryptionStore;

/**
 * Small helper class to manage text/icon around fingerprint authentication UI.
 */
@EBean
class SignUpViewModel {

    @Bean
    EncryptionStore encryptionStore;

    @Pref
    EncryptionPrefs_ encryptionPrefs;

    void saveFingerprint() {
        encryptionPrefs.fingerprint().put(true);
        encryptionStore.resetPassword();
    }

    void savePassword(String password) {
        encryptionStore.savePass(password);
    }

}
