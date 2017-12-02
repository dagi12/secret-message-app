//package pl.edu.amu.wmi.secretmessageapp.cipher;
//
//import android.app.KeyguardManager;
//import android.content.Context;
//import android.hardware.fingerprint.FingerprintManager;
//import android.support.annotation.Nullable;
//
//import org.androidannotations.annotations.Bean;
//import org.androidannotations.annotations.EBean;
//import org.androidannotations.annotations.RootContext;
//import org.androidannotations.annotations.SystemService;
//import org.androidannotations.annotations.sharedpreferences.Pref;
//
//import javax.crypto.Cipher;
//import javax.crypto.KeyGenerator;
//
//import pl.edu.amu.wmi.secretmessageapp.R;
//
///**
// * @author Eryk Mariankowski <eryk.mariankowski@247.codes> on 30.11.17.
// */
//@EBean(scope = EBean.Scope.Singleton)
//public class CipherStore {
//
//
//    @Bean
//    EncryptionStore encryptionStore;
//
//    @RootContext
//    Context context;
//
//    @Pref
//    EncryptionPrefs_ encryptionPrefs;
//
//    public void init() {
////        createKey();
//    }
//
////    public void createKey() {
////        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
////        // for your flow. Use of keys is necessary if you need to know if the set of
////        // enrolled fingerprints has changed.
////        try {
////            keyStore.load(null);
////            // Set the alias of the entry in Android KeyStore where the key will appear
////            // and the constrains (purposes) in the constructor of the Builder
////
////            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(DEFAULT_KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
////                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
////                    // Require the user to authenticate with a fingerprint to authorize every use
////                    // of the key
////                    .setUserAuthenticationRequired(true)
////                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
////
////            // This is a workaround to avoid crashes on devices whose API level is < 24
////            // because KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment is only
////            // visible on API level +24.
////            // Ideally there should be a compat library for KeyGenParameterSpec.Builder but
////            // which isn't available yet.
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
////                builder.setInvalidatedByBiometricEnrollment(true);
////            }
////            keyGenerator.init(builder.build());
////            keyGenerator.generateKey();
////        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | CertificateException | IOException e) {
////            Timber.e(e);
////        }
////    }
//
////    public void saveFingerPrint() {
////        try {
////            encryptionPrefs.fingerprint().put(true);
////            encryptionStore.resetPassword();
////            deleteKeystoreEntry(KeyAlias.PASS.name());
////        } catch (Exception e) {
////            Timber.e(e);
////        }
////    }
//
////
//
//}
