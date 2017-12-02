package pl.edu.amu.wmi.secretmessageapp.showmessage;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import pl.edu.amu.wmi.secretmessageapp.cipher.EncryptionStore;

/**
 * Stworzone przez Eryk Mariankowski dnia 01.12.17.
 */
@EBean
class ShowMessageViewModel {

    @Bean
    EncryptionStore encryptionStore;


    void resetData() {
        encryptionStore.resetData();
    }

    String decryptMessage() {
        return encryptionStore.decryptMessage();
    }

}
