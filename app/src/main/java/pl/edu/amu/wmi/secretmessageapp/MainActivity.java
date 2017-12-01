package pl.edu.amu.wmi.secretmessageapp;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import static android.text.TextUtils.isEmpty;

public class MainActivity extends AppCompatActivity {

    private static final String PASS_ALIAS = "PASS_ALIAS";

    private static final String MESSAGE_ALIAS = "MESSAGE_ALIAS";

    private static final String FINGERPRINT_ALIAS = "FINGERPRINT_ALIAS";

    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    private static final String sharedPrefKey = "sharedPrefKey";

    private static final String fingerprintAuthSharedPrefKey = "fingerprintAuthSharedPrefKey";

    private static final String passwordSharedPrefKey = "passwordSharedPrefKey";

    private static final String messageSharedPrefKey = "messageSharedPrefKey";

    private static final String ivSharedPassPrefKey = "ivSharedPassPrefKey";

    private static final String ivSharedMesPrefKey = "ivSharedMesPrefKey";

    private static final String TAG = MainActivity.class.getSimpleName();

    private KeyStore keyStore;

    private View createPasswordLayout;

    private EditText createPasswordEditText;

    private Button createPasswordButton;

    private View fingerPrintLayout;

    private Button enterFingerprintButton;

    private Button fingerprintChangeAuthorization;

    private TextView fingerprintInfoText;

    private EditText createMessageEditText;

    private View enterPasswordLayout;

    private EditText enterPasswordEditText;

    private Button enterPasswordButton;

    private Button passwordChangeAuthorization;

    private View showMessageLayout;

    private TextView messageTextView;

    private Button exitButton;

    private Button deleteDataButton;

    private SharedPreferences preferences;

    private int attempts = 0;

    private KeyguardManager keyguardManager;

    private FingerprintManager fingerprintManager;

    private boolean fingerprintAccepted = false;

    private boolean fingerprintAuthSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeResources();
        preferences = getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE);
        try {
            initKeyStore();
        } catch (KeyStoreException | CertificateException |
                NoSuchAlgorithmException | IOException e) {
            Log.e(TAG, "", e);
            showToast(getString(R.string.init_problem));
            return;
        }

//        if (checkIfMessageSaved()) {
//            if (checkIfFingerprintAuth()) {
//                initializeEnterFingerprint(false);
//            } else {
//                initializeEnterPasswordLayout();
//            }

//        } else {
        initializeSelectAuthMethodButton(false);
//        }
    }

    private void initializeResources() {
//        selectAuthMethodLayout = findViewById(R.id.select_authentication_method_layout);
//        passwordAuthButton = findViewById(R.id.password_auth_button);
//        fingerprintAuthButton = findViewById(R.id.fingerprint_auth_button);
        createPasswordLayout = findViewById(R.id.create_password_layout);
//        createPasswordEditText = findViewById(R.id.et_create_password);
        createPasswordButton = findViewById(R.id.create_password_continue);
//        createMessageLayout = findViewById(R.id.create_message_layout);
//        createMessageEditText = findViewById(R.id.create_message_edit_text);
        fingerPrintLayout = findViewById(R.id.fingerprint_layout);
        fingerprintChangeAuthorization = findViewById(R.id.fingerprint_change_auth_button);
        enterFingerprintButton = findViewById(R.id.fingerprint_enter_button);
        fingerprintInfoText = findViewById(R.id.fingerprint_info_text);
//        saveMessageButton = findViewById(R.id.save_message_button);
        enterPasswordLayout = findViewById(R.id.enter_password_layout);
        enterPasswordEditText = findViewById(R.id.enter_password_edit_text);
        enterPasswordButton = findViewById(R.id.enter_password_button);
        passwordChangeAuthorization = findViewById(R.id.password_change_auth_button);
        showMessageLayout = findViewById(R.id.show_message_layout);
        messageTextView = findViewById(R.id.message_text);
        exitButton = findViewById(R.id.exit_button);
        deleteDataButton = findViewById(R.id.delete_data_button);
    }

    private void initializeSelectAuthMethodButton(final boolean isJustChangePassword) {
//        selectAuthMethodLayout.setVisibility(View.VISIBLE);
//        passwordAuthButton.setOnClickListener(view -> {
//            selectAuthMethodLayout.setVisibility(View.GONE);
//            initializeCreatePasswordLayout(isJustChangePassword);
//        });
//        fingerprintAuthButton.setOnClickListener(view -> {
//            selectAuthMethodLayout.setVisibility(View.GONE);
//            initializeEnterFingerprint(isJustChangePassword);
//        });
    }

    private void initializeEnterFingerprint(final boolean isJustChangePassword) {
        fingerprintAccepted = false;
        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        fingerPrintLayout.setVisibility(View.VISIBLE);
        fingerprintInfoText.setText(getString(R.string.fingerprint));
        checkFingerprintRequirements(isJustChangePassword);
        enterFingerprintButton.setOnClickListener(view -> {
            if (fingerprintAccepted) {
                fingerprintAccepted = false;
                fingerprintAuthSelected = true;
//                if (isJustChangePassword || checkIfMessageSaved()) {
//                    String message = getSecretMessage();
//                    if (message != null) {
//                        fingerPrintLayout.setVisibility(View.GONE);
//                        initializeShowMessageLayout(message);
//                    } else {
//                        showToast(getString(R.string.processing_error));
//                    }
//                } else {
                fingerPrintLayout.setVisibility(View.GONE);
                fingerprintInfoText.setVisibility(View.GONE);
                initializeCreateMessageLayout();
//                }
            } else {
                showToast(getString(R.string.no_auth));
            }
        });
        fingerprintChangeAuthorization.setOnClickListener(view -> {
            if (fingerprintAccepted) {
                fingerprintAccepted = false;
                fingerPrintLayout.setVisibility(View.GONE);
                fingerprintInfoText.setVisibility(View.GONE);
                initializeSelectAuthMethodButton(isJustChangePassword);
            } else {
                showToast(getString(R.string.no_auth));
            }
        });
    }

    private void checkFingerprintRequirements(boolean isJustChangePassword) {
        if (!fingerprintManager.isHardwareDetected()) {
            fingerprintInfoText.setText(R.string.no_sensor);
        } else {
            // Checks whether fingerprint permission is set on manifest
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                fingerprintInfoText.setText(R.string.no_auth_permission);
            } else {
                // Check whether at least one fingerprint is registered
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    fingerprintInfoText.setText(R.string.no_saved_fingerprints);
                } else {
                    // Checks whether lock screen security is enabled or not
                    if (!keyguardManager.isKeyguardSecure()) {
                        fingerprintInfoText.setText(R.string.enable_screen_lock);
                    } else {
//                        encryptFingerPrint();
                        if (isJustChangePassword) {
                            showToast(getString(R.string.auth_method_changed));
                            saveFingerprintAuthToSharedPref(true);
                            resetPasswordSharedPreferences();
                            deleteKeystoreEntry(PASS_ALIAS);
                        }
                    }
                }
            }
        }
    }

//    @Override
//    public void onAuthenticationSuccess(Boolean success, @Nullable String message) {
//        if (success) {
//            fingerprintInfoText.setTextColor(ContextCompat.getColor(this, R.color.primary_light));
//            fingerprintInfoText.setText(R.string.auth_success);
//            fingerprintInfoText.setVisibility(View.VISIBLE);
//            fingerprintAccepted = true;
//        } else {
//            fingerprintInfoText.setTextColor(ContextCompat.getColor(this, R.color.error));
//            if (message == null) {
//                fingerprintInfoText.setText(R.string.auth_failed);
//            } else {
//                fingerprintInfoText.setText(message);
//            }
//            fingerprintInfoText.setVisibility(View.VISIBLE);
//            fingerprintAccepted = false;
//        }
//    }

    private void initializeCreatePasswordLayout(final boolean isJustChangePassword) {
        createPasswordLayout.setVisibility(View.VISIBLE);
        createPasswordEditText.setText("");
        createPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        createPasswordButton.setOnClickListener(view -> {
            if (!createPasswordEditText.getText().toString().equals("")) {
                if (isJustChangePassword) {
                    try {
                        encryptAndSave(PASS_ALIAS, createPasswordEditText.getText().toString());
                    } catch (UnrecoverableEntryException | NoSuchAlgorithmException | KeyStoreException |
                            NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | IOException |
                            InvalidAlgorithmParameterException | SignatureException | BadPaddingException |
                            IllegalBlockSizeException e) {
                        e.printStackTrace();
                        showToast(getString(R.string.encryption_error));
                        return;
                    }
                    saveFingerprintAuthToSharedPref(false);
                    deleteKeystoreEntry(FINGERPRINT_ALIAS);
                    createPasswordLayout.setVisibility(View.GONE);
                    initializeEnterPasswordLayout();
                    showToast(getString(R.string.auth_method_changed));
                } else {
                    createPasswordLayout.setVisibility(View.GONE);
                    initializeCreateMessageLayout();
                }
                fingerprintAuthSelected = false;

            } else {
                showToast(getString(R.string.no_password));
            }
        });
    }

    private void initializeCreateMessageLayout() {
//        createMessageLayout.setVisibility(View.VISIBLE);
        createMessageEditText.setText("");
//        saveMessageButton.setOnClickListener(view -> {
        if (!createMessageEditText.getText().toString().equals("")) {
            try {
                if (fingerprintAuthSelected) {
                    saveFingerprintAuthToSharedPref(true);
                } else {
                    encryptAndSave(PASS_ALIAS, createPasswordEditText.getText().toString());
                }

                encryptAndSave(MESSAGE_ALIAS, createMessageEditText.getText().toString());
            } catch (UnrecoverableEntryException | NoSuchAlgorithmException | KeyStoreException |
                    NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | IOException |
                    InvalidAlgorithmParameterException | SignatureException | BadPaddingException |
                    IllegalBlockSizeException e) {
                e.printStackTrace();
                showToast(getString(R.string.encryption_error));
                initializeSelectAuthMethodButton(false);
                return;
            }
//                createMessageLayout.setVisibility(View.GONE);
            createMessageEditText.setText("");
            createPasswordEditText.setText("");
            if (fingerprintAuthSelected) {
                initializeEnterFingerprint(false);
            } else {
                initializeEnterPasswordLayout();
            }

            showToast(getString(R.string.msg_saved));
        } else {
            showToast(getString(R.string.no_msg));
        }
//        });
    }

    private void initializeEnterPasswordLayout() {

        enterPasswordLayout.setVisibility(View.VISIBLE);
        enterPasswordEditText.setText("");
        enterPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        enterPasswordButton.setOnClickListener(view -> {

            if (!enterPasswordEditText.getText().toString().equals("")) {
                if (isPasswordCorrect(enterPasswordEditText.getText().toString())) {
                    String message = getSecretMessage();
                    if (message != null) {
                        enterPasswordLayout.setVisibility(View.GONE);
                        enterPasswordEditText.setText("");
                        initializeShowMessageLayout(message);
                    } else {
                        showToast(getString(R.string.processing_error));
                        enterPasswordEditText.setText("");
                    }
                } else {
                    enterPasswordEditText.setText("");
                    resetPasswordAfterThreeAttempts();
                }

            } else {
                showToast(getString(R.string.empty_password));
            }

        });

        passwordChangeAuthorization.setOnClickListener(view -> {
            if (!enterPasswordEditText.getText().toString().equals("")) {
                if (isPasswordCorrect(enterPasswordEditText.getText().toString())) {
                    enterPasswordLayout.setVisibility(View.GONE);
                    initializeCreatePasswordLayout(true);
                } else {
                    resetPasswordAfterThreeAttempts();
                    enterPasswordEditText.setText("");
                }

            } else {
                showToast(getString(R.string.empty_password));
            }
        });
    }

    private void initializeShowMessageLayout(String message) {
        showMessageLayout.setVisibility(View.VISIBLE);
        messageTextView.setText(message);
        exitButton.setOnClickListener(view -> finish());
        deleteDataButton.setOnClickListener(view -> {
            resetData();
            showMessageLayout.setVisibility(View.GONE);
            initializeSelectAuthMethodButton(false);
            showToast(getString(R.string.data_deleted));
        });
    }

    private void resetPasswordAfterThreeAttempts() {
        attempts = attempts + 1;
        if (attempts == 3) {
            attempts = 0;
            resetData();
            showToast(getString(R.string.attempts_exceeded));
            enterPasswordLayout.setVisibility(View.GONE);
            initializeCreatePasswordLayout(false);
        } else {
            showToast(getString(R.string.wrong_password));
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean isPasswordCorrect(String enteredPassword) {
        boolean isCorrect = false;
        String encryptedPasswordString = getPassFromSharedPreferences();
        byte[] encryptedPasswordBytes = stringToBytes(encryptedPasswordString);
        try {
            String descryptedPassString = decryptData(PASS_ALIAS, encryptedPasswordBytes, true);
            if (enteredPassword.equals(descryptedPassString)) {
                isCorrect = true;
            }
        } catch (UnrecoverableEntryException | NoSuchAlgorithmException | KeyStoreException |
                NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | IOException |
                InvalidAlgorithmParameterException | BadPaddingException |
                IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return isCorrect;
    }

    private String getSecretMessage() {
        String message = null;

        try {
            String encryptedMessage = getMessageFromSharedPreferences();
            byte[] encryptedMessageBytes = stringToBytes(encryptedMessage);
            message = decryptData(MESSAGE_ALIAS, encryptedMessageBytes, false);
        } catch (UnrecoverableEntryException | NoSuchAlgorithmException | KeyStoreException |
                NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | IOException |
                InvalidAlgorithmParameterException | BadPaddingException |
                IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return message;

    }

    private void initKeyStore() throws KeyStoreException, CertificateException,
            NoSuchAlgorithmException, IOException {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
    }

    private SecretKey getDescryptSecretKey(final String alias) throws NoSuchAlgorithmException,
            UnrecoverableEntryException, KeyStoreException {
        return ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
    }

    @NonNull
    private SecretKey getEncyptSecretKey(final String alias) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidAlgorithmParameterException {

        final KeyGenerator keyGenerator = KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);

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

    private String decryptData(final String alias, final byte[] encryptedData, boolean isPassword)
            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {

        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        final GCMParameterSpec spec = new GCMParameterSpec(128, stringToBytes(getIvFromSharedPreferences(isPassword)));
        cipher.init(Cipher.DECRYPT_MODE, getDescryptSecretKey(alias), spec);

        return new String(cipher.doFinal(encryptedData), "UTF-8");
    }

    private Cipher encryptAndSave(final String alias, @Nullable final String textToEncrypt)
            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            InvalidAlgorithmParameterException, SignatureException, BadPaddingException,
            IllegalBlockSizeException {

        Cipher cipher;

        if (alias.equals(FINGERPRINT_ALIAS)) {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } else {
            cipher = Cipher.getInstance("AES/GCM/NoPadding");
        }


        cipher.init(Cipher.ENCRYPT_MODE, getEncyptSecretKey(alias));

        if (!alias.equals(FINGERPRINT_ALIAS)) {
            if (!isEmpty(textToEncrypt)) {
                String encrypedString = bytesToString(cipher.doFinal(textToEncrypt.getBytes("UTF-8")));
                if (alias.equals(PASS_ALIAS)) {
                    saveIvToSharedPreferences(true, cipher.getIV());
                    savePasswordToSharedPreferences(encrypedString);
                    saveFingerprintAuthToSharedPref(false);
                } else {
                    saveIvToSharedPreferences(false, cipher.getIV());
                    saveMessageToSharedPreferences(encrypedString);
                    saveMessageSavedToSharedPreferences();
                }
            }
        }
        return cipher;
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

    private void resetPasswordSharedPreferences() {
        preferences.edit().remove(passwordSharedPrefKey).commit();
        preferences.edit().remove(ivSharedPassPrefKey).commit();
    }

    private void savePasswordToSharedPreferences(String password) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(passwordSharedPrefKey, password);
        editor.commit();
    }

    private void saveMessageToSharedPreferences(String message) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(messageSharedPrefKey, message);
        editor.commit();
    }

    private void saveIvToSharedPreferences(boolean isPassword, byte[] iv) {
        SharedPreferences.Editor editor = preferences.edit();
        if (isPassword) {
            editor.putString(ivSharedPassPrefKey, bytesToString(iv));
        } else {
            editor.putString(ivSharedMesPrefKey, bytesToString(iv));
        }

        editor.commit();
    }


    private boolean checkIfFingerprintAuth() {
        return preferences.getBoolean(fingerprintAuthSharedPrefKey, false);
    }

    private String getPassFromSharedPreferences() {
        return preferences.getString(passwordSharedPrefKey, null);
    }

    private String getMessageFromSharedPreferences() {
        return preferences.getString(messageSharedPrefKey, null);
    }

    private String getIvFromSharedPreferences(boolean isPassword) {
        if (isPassword) {
            return preferences.getString(ivSharedPassPrefKey, null);
        } else {
            return preferences.getString(ivSharedMesPrefKey, null);
        }
    }

    private String bytesToString(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private byte[] stringToBytes(String string) {
        return Base64.decode(string, Base64.DEFAULT);
    }

    private void resetData() {
        boolean keysDeleted = resetKeys();
        if (keysDeleted) {
            resetSharedPreferences();
        } else {
            showToast(getString(R.string.data_deletion_error));
        }
    }

    private void resetSharedPreferences() {
        preferences.edit().clear().commit();
    }

    private boolean deleteKeystoreEntry(String alias) {
        try {
            keyStore.deleteEntry(alias);
            return true;
        } catch (KeyStoreException e) {
            return false;
        }
    }

    private boolean resetKeys() {
        try {
            keyStore.deleteEntry(PASS_ALIAS);
            keyStore.deleteEntry(MESSAGE_ALIAS);
            keyStore.deleteEntry(FINGERPRINT_ALIAS);
            return true;
        } catch (KeyStoreException e) {
            return false;
        }
    }

    @Override
    protected void onResume() {
        if (fingerPrintLayout.getVisibility() == View.VISIBLE) {
            fingerprintInfoText.setVisibility(View.GONE);
            checkFingerprintRequirements(false);
        }
        super.onResume();
    }

//    @Override
//    protected void onPause() {
//        if (showMessageLayout.getVisibility() == View.VISIBLE) {
//            showMessageLayout.setVisibility(View.GONE);
//            if (checkIfFingerprintAuth()) {
//                fingerPrintLayout.setVisibility(View.VISIBLE);
//            } else {
//                enterPasswordLayout.setVisibility(View.VISIBLE);
//            }
//
//        } else if (createMessageLayout.getVisibility() == View.VISIBLE) {
//            createMessageEditText.setText("");
//            createMessageLayout.setVisibility(View.GONE);
//            selectAuthMethodLayout.setVisibility(View.VISIBLE);
//        } else if (fingerPrintLayout.getVisibility() == View.VISIBLE) {
//            fingerprintInfoText.setVisibility(View.GONE);
//        }
//        fingerprintAccepted = false;
//        if (helper != null) {
//            helper.cancelAuth();
//        }
//        super.onPause();
//    }

}
