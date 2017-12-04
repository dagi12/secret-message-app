package pl.edu.amu.wmi.secretmessageapp.config;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.sharedpreferences.Pref;

import pl.edu.amu.wmi.secretmessageapp.cipher.KeyAlias;
import pl.edu.amu.wmi.secretmessageapp.encryption.EncryptionPrefs_;
import pl.edu.amu.wmi.secretmessageapp.encryption.EncryptionStore;
import pl.edu.amu.wmi.secretmessageapp.encryption.KeyStoreService;
import pl.edu.amu.wmi.secretmessageapp.helper.DialogHelper;
import pl.edu.amu.wmi.secretmessageapp.signup.SignUpDialogFragment_;


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

    @Bean
    KeyStoreService keyStoreService;

    private static final String TAG = ConfigViewModel.class.getSimpleName();

    void saveMessage(String message) {
        encryptionStore.saveMessage(message);
    }

    void signUpDialog(Activity activity, KeyAlias keyAlias, boolean changeAuth) {
        DialogFragment dialogFragment = SignUpDialogFragment_
                .builder()
                .keyAlias(keyAlias)
                .changeAuth(changeAuth)
                .build();
        dialogFragment.show(activity.getFragmentManager(), TAG);
    }

    boolean initConfig(Activity activity) {
        // sprawdzamy uprawnienia do korzystania z sensora
        String result = keyStoreService.checkFingerprintPermission();
        if (result != null) {
            DialogHelper.errorDialog(activity, result);
            return false;
        }
        return true;
    }

    boolean checkIfMessageSaved() {
        return encryptionPrefs.messageSaved().get();
    }

    boolean getFingerprintAuth() {
        return keyStoreService.getFingerprintAuth();
    }

}
