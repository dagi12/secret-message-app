package pl.edu.amu.wmi.secretmessageapp.encryption;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
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
import pl.edu.amu.wmi.secretmessageapp.cipher.KeyAlias;
import pl.edu.amu.wmi.secretmessageapp.config.ConfigActivity_;
import timber.log.Timber;

/**
 * @author Eryk Mariankowski <eryk.mariankowski@247.codes> on 04.12.17.
 */
@EBean
public class KeyStoreService {

    static final String CIPHER_TYPE = "AES/GCM/NoPadding";

    @Pref
    EncryptionPrefs_ encryptionPrefs;

    KeyStore keyStore;

    @RootContext
    Context context;

    @SystemService
    KeyguardManager keyguardManager;

    @SystemService
    FingerprintManager fingerprintManager;

    private static final String STORE_NAME = "AndroidKeyStore";

    private Boolean fingerprintAuth;

    KeyStoreService() {
        try {
            keyStore = KeyStore.getInstance(STORE_NAME);
            keyStore.load(null);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            Timber.e(e, "Failed to get an instance of KeyStore");
        }
    }

    public Boolean getFingerprintAuth() {
        if (fingerprintAuth == null) {
            fingerprintAuth = encryptionPrefs.fingerprint().get();
        }
        return fingerprintAuth;
    }

    void setFingerprintAuth(Boolean fingerprintAuth) {
        encryptionPrefs.fingerprint().put(fingerprintAuth);
        this.fingerprintAuth = fingerprintAuth;
    }

    @SuppressLint("ApplySharedPref")
    public void resetData() {
        try {
            keyStore.deleteEntry(KeyAlias.MSG.name());
            keyStore.deleteEntry(KeyAlias.PASS.name());
            keyStore.deleteEntry(KeyAlias.FINGER.name());
        } catch (KeyStoreException e) {
            Timber.e(e);
        }
        encryptionPrefs
                .getSharedPreferences()
                .edit()
                .clear()
                .commit();
        Intent intent = new Intent(context, ConfigActivity_.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    SecretKey desCryptSecretKey(final KeyAlias alias) {
        try {
            KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) keyStore.getEntry(alias.name(), null);
            return entry.getSecretKey();
        } catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException e) {
            Timber.e(e);
        }
        return null;
    }

    void resetPassword() {
        encryptionPrefs.ivPass().remove();
        encryptionPrefs.encryptedPassword().remove();
        try {
            keyStore.deleteEntry(KeyAlias.PASS.name());
        } catch (KeyStoreException e) {
            Timber.e(e);
        }
    }

    Cipher getCommonCipher(KeyAlias alias) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, STORE_NAME);
        KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(alias.name(),
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build();
        keyGenerator.init(spec);
        SecretKey secretKey = keyGenerator.generateKey();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher;
    }

    Cipher getFingerprintCipher() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException {
        KeyGenParameterSpec fingerprintSpec = new KeyGenParameterSpec.Builder(KeyAlias.FINGER.name(),
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(
                        KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build();
        Cipher fingerprintCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        KeyGenerator fingerprintKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, STORE_NAME);
        fingerprintKeyGenerator.init(fingerprintSpec);
        fingerprintCipher.init(Cipher.ENCRYPT_MODE, fingerprintKeyGenerator.generateKey());
        return fingerprintCipher;
    }

    public String checkFingerprintPermission() {
        // czy sensor istnieje
        // noinspection ResourceType
        if (!fingerprintManager.isHardwareDetected()) {
            return context.getString(R.string.no_sensor);
        }

        // czy w ustawieniach jest włączony ekran blokady
        if (!keyguardManager.isKeyguardSecure()) {
            return context.getString(R.string.no_fingerprint1);
        }

        // czy zapisany jest chociaż jeden odcisk palca
        // noinspection ResourceType
        if (!fingerprintManager.hasEnrolledFingerprints()) {
            return context.getString(R.string.no_fingerprint2);
        }
        return null;
    }


}
