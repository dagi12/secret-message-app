package pl.edu.amu.wmi.secretmessageapp.setmessage;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import pl.edu.amu.wmi.secretmessageapp.encryption.EncryptionStore;

/**
 * @author Eryk Mariankowski <eryk.mariankowski@247.codes> on 02.12.17.
 */
@EBean
class SetMessageViewModel {

    @Bean
    EncryptionStore encryptionStore;

}
