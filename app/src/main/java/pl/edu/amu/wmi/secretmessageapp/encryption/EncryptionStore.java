package pl.edu.amu.wmi.secretmessageapp.encryption;

import android.hardware.fingerprint.FingerprintManager;
import android.support.annotation.NonNull;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import pl.edu.amu.wmi.secretmessageapp.cipher.KeyAlias;
import timber.log.Timber;

import static pl.edu.amu.wmi.secretmessageapp.encryption.EncryptionHelper.bytesToString;


/**
 * Stworzone przez Eryk Mariankowski dnia 01.12.17.
 */
@EBean(scope = EBean.Scope.Singleton)
public class EncryptionStore {

    @Pref
    EncryptionPrefs_ encryptionPrefs;

    @Bean
    KeyStoreService keyStoreService;

    private static final String ENCODING = "UTF-8";

    public String decryptPassword() {
        String encryptedMessage = encryptionPrefs.encryptedPassword().get();
        byte[] encryptedMessageBytes = EncryptionHelper.stringToBytes(encryptedMessage);
        GCMParameterSpec spec = new GCMParameterSpec(128, EncryptionHelper.stringToBytes(encryptionPrefs.ivPass().get()));
        try {
            Cipher decryptCipher = Cipher.getInstance(KeyStoreService.CIPHER_TYPE);
            SecretKey secretKey = keyStoreService.desCryptSecretKey(KeyAlias.PASS);
            decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
            return new String(decryptCipher.doFinal(encryptedMessageBytes), ENCODING);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
            Timber.e(e);
        }
        return null;
    }

    public String decryptMessage() {
        String encryptedMessage = encryptionPrefs.encryptedMsg().get().trim();
        byte[] encryptedMessageBytes = EncryptionHelper.stringToBytes(encryptedMessage);
        String msgIv = encryptionPrefs.ivMsg().get().trim();
        GCMParameterSpec spec = new GCMParameterSpec(128, EncryptionHelper.stringToBytes(msgIv));
        try {
            SecretKey secretKey = keyStoreService.desCryptSecretKey(KeyAlias.MSG);
            Cipher decryptCipher = Cipher.getInstance(KeyStoreService.CIPHER_TYPE);
            decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
            return new String(decryptCipher.doFinal(encryptedMessageBytes), ENCODING);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            Timber.e(e);
        }
        return null;
    }

    public void saveMessage(@NonNull String message) {
        try {
            Cipher messageCipher = keyStoreService.getCommonCipher(KeyAlias.MSG);
            String encryptedMessage = EncryptionHelper.bytesToString(messageCipher.doFinal(message.getBytes(ENCODING)));
            encryptionPrefs.ivMsg().put(EncryptionHelper.bytesToString(messageCipher.getIV()));
            encryptionPrefs.encryptedMsg().put(encryptedMessage);
            encryptionPrefs.messageSaved().put(true);
        } catch (Exception e) {
            Timber.e(e, "Failed to encrypt message");
        }
    }

    public void savePass(@NonNull final String password) {
        try {
            Cipher passwordCipher = keyStoreService.getCommonCipher(KeyAlias.PASS);
            String encryptedString = bytesToString(passwordCipher.doFinal(password.getBytes(ENCODING)));
            encryptionPrefs.ivPass().put(bytesToString(passwordCipher.getIV()).trim());
            encryptionPrefs.encryptedPassword().put(encryptedString);
            keyStoreService.setFingerprintAuth(false);
            keyStoreService.keyStore.deleteEntry(KeyAlias.FINGER.name());
        } catch (Exception e) {
            Timber.e(e, "Failed to encrypt et_password");
        }
    }


    public FingerprintManager.CryptoObject crypto() {
        try {
            return new FingerprintManager.CryptoObject(keyStoreService.getFingerprintCipher());
        } catch (Exception e) {
            Timber.e(e, "Failed to get crypto");
        }
        return null;
    }

    public void saveFingerprint() {
        keyStoreService.setFingerprintAuth(true);
        keyStoreService.resetPassword();
        try {
            keyStoreService.keyStore.deleteEntry(KeyAlias.PASS.name());
        } catch (KeyStoreException e) {
            Timber.e(e, "Failed to delete password entry");
        }
    }

}

