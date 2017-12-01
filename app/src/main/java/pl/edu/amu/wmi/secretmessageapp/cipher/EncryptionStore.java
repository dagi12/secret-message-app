package pl.edu.amu.wmi.secretmessageapp.cipher;

import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import static android.text.TextUtils.isEmpty;
import static pl.edu.amu.wmi.secretmessageapp.cipher.CipherStore.STORE_NAME;

/**
 * Stworzone przez Eryk Mariankowski dnia 01.12.17.
 */
@EBean
public class EncryptionStore {

    private static final String TAG = EncryptionStore.class.getSimpleName();
    @Pref
    EncryptionPrefs_ encryptionPrefs;

    public static String bytesToString(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static byte[] stringToBytes(String string) {
        return Base64.decode(string, Base64.DEFAULT);
    }

    public String decryptMessage(String encryptedMessage) {
        try {
            byte[] encryptedMessageBytes = stringToBytes(encryptedMessage);
            return decryptData(KeyAlias.MSG.name(), encryptedMessageBytes, false);
        } catch (Exception e) {
            Log.e(TAG, "Decrypion failed", e);
        }
        return null;
    }

    @NonNull
    SecretKey getEncryptSecretKey(final String alias) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidAlgorithmParameterException {

        final KeyGenerator keyGenerator = KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES, STORE_NAME);

        if (alias.equals(FINGERPRINT_ALIAS)) {
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(alias,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
        } else {
            keyGenerator.init(new KeyGenParameterSpec.Builder(alias,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build());
        }
        return keyGenerator.generateKey();
    }


//    private boolean encryptFingerPrint() {
//        Cipher cipher;
//        try {
//            cipher = encryptAndSave(FINGERPRINT_ALIAS, null);
//        } catch (UnrecoverableEntryException | NoSuchAlgorithmException | KeyStoreException |
//                NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | IOException |
//                InvalidAlgorithmParameterException | SignatureException | BadPaddingException |
//                IllegalBlockSizeException e) {
//            e.printStackTrace();
//            showToast("Błąd podczas procesu szyfrowania");
//            return false;
//        }
//
//        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
//        helper = new FingerprintHandler(this, this);
//        helper.startAuth(fingerprintManager, cryptoObject);
//        return true;
//    }

    private String decryptData(final String alias, final byte[] encryptedData, boolean isPassword)
            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        final GCMParameterSpec spec = new GCMParameterSpec(128, stringToBytes(getIvFromSharedPreferences(isPassword)));
        cipher.init(Cipher.DECRYPT_MODE, getDesCryptSecretKey(alias), spec);

        return new String(cipher.doFinal(encryptedData), "UTF-8");
    }

    private void saveFingerprintAuthToSharedPref(boolean isFingerprint) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(fingerprintAuthSharedPrefKey, isFingerprint);
        editor.commit();
    }

    private void saveMessageSavedToSharedPreferences() {
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putBoolean(isMessageSavedPrefKey, true);
//        editor.commit();
    }

    void resetPassword() {
        encryptionPrefs.ivPass().remove();
        encryptionPrefs.encryptedPassword().remove();
    }

    public void saveMessage() {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, getEncryptSecretKey(KeyAlias.PASS.name()));
            encryptionPrefs.ivMsg().put(bytesToString(cipher.getIV()));
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }


        saveIvToSharedPreferences(false, cipher.getIV());
        saveMessageToSharedPreferences(encrypedString);
        saveMessageSavedToSharedPreferences();
    }

    private String getPassFromSharedPreferences() {
        return preferences.getString(passwordSharedPrefKey, null);
    }


    private String getIvFromSharedPreferences(boolean isPassword) {
        if (isPassword) {
            return preferences.getString(ivSharedPassPrefKey, null);
        } else {
            return preferences.getString(ivSharedMesPrefKey, null);
        }
    }

    private Cipher savePassword(@NonNull final String textToEncrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, getEncryptSecretKey(KeyAlias.PASS.name()));
            encryptionPrefs.ivPass().put(bytesToString(cipher.getIV()));
            String encryptedString = bytesToString(cipher.doFinal(textToEncrypt.getBytes("UTF-8")));
            savePasswordToSharedPreferences(encryptedString);
            saveFingerprintAuthToSharedPref(false);
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }

        if (!alias.equals(FINGERPRINT_ALIAS)) {
            if (!isEmpty(textToEncrypt)) {

                if (alias.equals(PASS_ALIAS)) {

                } else {

                }
            }
        }
        return cipher;
    }


}
