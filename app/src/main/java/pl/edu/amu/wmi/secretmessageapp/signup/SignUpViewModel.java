package pl.edu.amu.wmi.secretmessageapp.signup;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import pl.edu.amu.wmi.secretmessageapp.encryption.EncryptionStore;

/**
 * Small helper class to manage text/icon around fingerprint authentication UI.
 */
@EBean
class SignUpViewModel {

    @Bean
    EncryptionStore encryptionStore;

    void saveFingerprint() {
        encryptionStore.saveFingerprint();
    }


    void savePassword(String password) {
        encryptionStore.savePass(password);
    }

}
