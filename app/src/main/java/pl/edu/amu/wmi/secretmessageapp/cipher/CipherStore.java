package pl.edu.amu.wmi.secretmessageapp.cipher;

import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.util.Log;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import pl.edu.amu.wmi.secretmessageapp.R;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static android.text.TextUtils.isEmpty;

/**
 * @author Eryk Mariankowski <eryk.mariankowski@247.codes> on 30.11.17.
 */
@EBean(scope = EBean.Scope.Singleton)
public class CipherStore {

    private static final String TAG = CipherStore.class.getSimpleName();
    static final String STORE_NAME = "AndroidKeyStore";
    private static final String DEFAULT_KEY_NAME = "default_key";
    @SystemService
    KeyguardManager keyguardManager;
    @SystemService
    FingerprintManager fingerprintManager;
    @Bean
    EncryptionStore encryptionStore;
    @RootContext
    Context context;
    private FingerprintManager.CryptoObject cryptoObject;

    private KeyStore keyStore;

    private KeyGenerator keyGenerator;

    private Cipher defaultCipher;

    public FingerprintManager.CryptoObject getCryptoObject() {
        return cryptoObject;
    }

    private void buildCiphers() {
        try {
            defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Log.e(TAG, "Failed to get an instance of Cipher", e);
        }
    }

    public void init() {
        initKeys();
        buildCiphers();
        createKey();
    }

    @Nullable
    public String checkFingerprintPermission() {
        // noinspection ResourceType
        if (!fingerprintManager.isHardwareDetected()) {
            return context.getString(R.string.no_sensor);
        }

        if (!keyguardManager.isKeyguardSecure()) {
            // Show a encryptedMessage that the user hasn't set up a fingerprint or lock screen.
            return context.getString(R.string.no_fingerprint1);
        }

        // Now the protection level of USE_FINGERPRINT permission is normal instead of dangerous.
        // See http://developer.android.com/reference/android/Manifest.permission.html#USE_FINGERPRINT
        // The line below prevents the false positive inspection from Android Studio
        // noinspection ResourceType
        if (!fingerprintManager.hasEnrolledFingerprints()) {
            // This happens when no fingerprints are registered.
            return context.getString(R.string.no_fingerprint2);
        }
        return null;
    }

    private void initKeys() {
        try {
            keyStore = KeyStore.getInstance(STORE_NAME);
        } catch (KeyStoreException e) {
            Log.e(TAG, "Failed to get an instance of KeyStore", e);
        }
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, STORE_NAME);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            Log.e(TAG, "Failed to get an instance of KeyGenerator", e);
        }
    }

    public void createKey() {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            keyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(DEFAULT_KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            // This is a workaround to avoid crashes on devices whose API level is < 24
            // because KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment is only
            // visible on API level +24.
            // Ideally there should be a compat library for KeyGenParameterSpec.Builder but
            // which isn't available yet.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(true);
            }
            keyGenerator.init(builder.build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | CertificateException | IOException e) {
            Log.e(TAG, "", e);
        }
    }


    public void saveFingerPrint() {
        try {
            encryptionPrefs.finger().put(true);
            encryptionStore.resetPassword();
            deleteKeystoreEntry(KeyAlias.PASS.name());
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }

    @Pref
    EncryptionPrefs_ encryptionPrefs;



    public void deleteData() {
        resetKeys();
        resetSharedPreferences();
    }

    private void resetSharedPreferences() {
        getDefaultSharedPreferences(context)
                .edit()
                .clear()
                .apply();
    }

    private SecretKey getDesCryptSecretKey(final String alias) throws NoSuchAlgorithmException,
            UnrecoverableEntryException, KeyStoreException {
        KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null);
        return entry.getSecretKey();
    }

    private boolean resetKeys() {
        try {
            keyStore.deleteEntry(KeyAlias.PASS.name());
            keyStore.deleteEntry(KeyAlias.MSG.name());
            keyStore.deleteEntry(KeyAlias.FINGER.name());
            return true;
        } catch (KeyStoreException e) {
            return false;
        }
    }

    private boolean deleteKeystoreEntry(String alias) {
        try {
            keyStore.deleteEntry(alias);
            return true;
        } catch (KeyStoreException e) {
            return false;
        }
    }


}
