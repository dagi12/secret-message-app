package pl.edu.amu.wmi.secretmessageapp.cipher;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.util.Base64;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import pl.edu.amu.wmi.secretmessageapp.R;
import pl.edu.amu.wmi.secretmessageapp.config.ConfigActivity_;
import timber.log.Timber;


/**
 * Stworzone przez Eryk Mariankowski dnia 01.12.17.
 */
@EBean(scope = EBean.Scope.Singleton)
public class EncryptionStore {

    @Pref
    EncryptionPrefs_ encryptionPrefs;

    @RootContext
    Context context;

    @SystemService
    KeyguardManager keyguardManager;

    @SystemService
    FingerprintManager fingerprintManager;

    private static final String CIPHER_TYPE = "AES/GCM/NoPadding";

    private static final String ENCODING = "UTF-8";

    private static final String STORE_NAME = "AndroidKeyStore";

    private KeyStore keyStore;

    private static String bytesToString(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private static byte[] stringToBytes(String string) {
        return Base64.decode(string, Base64.DEFAULT);
    }

    public void init() {
        try {
            keyStore = KeyStore.getInstance(STORE_NAME);
            keyStore.load(null);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            Timber.e(e, "Failed to get an instance of KeyStore");
        }
    }

    public String decryptPassword() {
        String encryptedMessage = encryptionPrefs.encryptedPassword().get();
        byte[] encryptedMessageBytes = stringToBytes(encryptedMessage);
        GCMParameterSpec spec = new GCMParameterSpec(128, stringToBytes(encryptionPrefs.ivPass().get()));
        try {
            Cipher decryptCipher = Cipher.getInstance(CIPHER_TYPE);
            SecretKey secretKey = desCryptSecretKey(KeyAlias.PASS);
            decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
            return new String(decryptCipher.doFinal(encryptedMessageBytes), ENCODING);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
            Timber.e(e);
        }
        return null;
    }

    private SecretKey desCryptSecretKey(final KeyAlias alias) {
        try {
            KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) keyStore.getEntry(alias.name(), null);
            return entry.getSecretKey();
        } catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException e) {
            Timber.e(e);
        }
        return null;
    }

    public String decryptMessage() {
        String encryptedMessage = encryptionPrefs.encryptedMsg().get().trim();
        byte[] encryptedMessageBytes = stringToBytes(encryptedMessage);
        String msgIv = encryptionPrefs.ivMsg().get().trim();
        GCMParameterSpec spec = new GCMParameterSpec(128, stringToBytes(msgIv));
        try {
            SecretKey secretKey = desCryptSecretKey(KeyAlias.MSG);
            Cipher decryptCipher = Cipher.getInstance(CIPHER_TYPE);
            decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
            return new String(decryptCipher.doFinal(encryptedMessageBytes), ENCODING);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            Timber.e(e);
        }
        return null;
    }

    public void resetPassword() {
        encryptionPrefs.ivPass().remove();
        encryptionPrefs.encryptedPassword().remove();
        try {
            keyStore.deleteEntry(KeyAlias.PASS.name());
        } catch (KeyStoreException e) {
            Timber.e(e);
        }
    }

    private Cipher getCommonCipher(KeyAlias alias) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException {
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

    private Cipher getFingerprintCipher() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException {
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

    public void saveMessage(@NonNull String message) {
        try {
            Cipher messageCipher = getCommonCipher(KeyAlias.MSG);
            String encryptedMessage = bytesToString(messageCipher.doFinal(message.getBytes(ENCODING)));
            encryptionPrefs.ivMsg().put(bytesToString(messageCipher.getIV()));
            encryptionPrefs.encryptedMsg().put(encryptedMessage);
            encryptionPrefs.messageSaved().put(true);
        } catch (Exception e) {
            Timber.e(e, "Failed to encrypt message");
        }
    }

    public void savePass(@NonNull final String password) {
        try {
            Cipher passwordCipher = getCommonCipher(KeyAlias.PASS);
            String encryptedString = bytesToString(passwordCipher.doFinal(password.getBytes(ENCODING)));
            encryptionPrefs.ivPass().put(bytesToString(passwordCipher.getIV()).trim());
            encryptionPrefs.encryptedPassword().put(encryptedString);
            encryptionPrefs.fingerprint().put(false);
            keyStore.deleteEntry(KeyAlias.FINGER.name());
        } catch (Exception e) {
            Timber.e(e, "Failed to encrypt et_password");
        }
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

    public FingerprintManager.CryptoObject crypto() {
        try {
            return new FingerprintManager.CryptoObject(getFingerprintCipher());
        } catch (Exception e) {
            Timber.e(e, "Failed to get crypto");
        }
        return null;
    }


}

