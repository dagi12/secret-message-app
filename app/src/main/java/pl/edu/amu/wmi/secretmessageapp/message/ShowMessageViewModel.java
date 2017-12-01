package pl.edu.amu.wmi.secretmessageapp.message;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import pl.edu.amu.wmi.secretmessageapp.cipher.CipherStore;

/**
 * Stworzone przez Eryk Mariankowski dnia 01.12.17.
 */
@EBean
public class ShowMessageViewModel {

    @Bean
    CipherStore cipherStore;

    public void deleteData() {

    }
}
