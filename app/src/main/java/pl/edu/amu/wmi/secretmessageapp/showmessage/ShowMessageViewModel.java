package pl.edu.amu.wmi.secretmessageapp.showmessage;

import android.app.Activity;
import android.content.Intent;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import pl.edu.amu.wmi.secretmessageapp.cipher.KeyAlias;
import pl.edu.amu.wmi.secretmessageapp.config.ConfigActivity;
import pl.edu.amu.wmi.secretmessageapp.config.ConfigActivity_;
import pl.edu.amu.wmi.secretmessageapp.encryption.EncryptionStore;
import pl.edu.amu.wmi.secretmessageapp.encryption.KeyStoreService;


/**
 * Stworzone przez Eryk Mariankowski dnia 01.12.17.
 */
@EBean
class ShowMessageViewModel {

    @Bean
    EncryptionStore encryptionStore;

    @Bean
    KeyStoreService keyStoreService;

    void resetData() {
        keyStoreService.resetData();
    }

    String decryptMessage() {
        return encryptionStore.decryptMessage();
    }

    boolean getFingerprintAuth() {
        return keyStoreService.getFingerprintAuth();
    }

    private void startConfigActivity(Activity activity, KeyAlias stage) {
        Intent intent = new Intent(activity, ConfigActivity_.class);
        intent.putExtra(ConfigActivity.CHANGE_METHOD_KEY, stage.name());
        activity.startActivity(intent);
        activity.finish();
    }

    void changeForPassword(Activity activity) {
        startConfigActivity(activity, KeyAlias.PASS);
    }

    void changeForFingerprint(Activity activity) {
        startConfigActivity(activity, KeyAlias.FINGER);
    }

}
