package pl.edu.amu.wmi.secretmessageapp.message;

import android.app.KeyguardManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import pl.edu.amu.wmi.secretmessageapp.R;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    public static final String MSG_NAME = "MSG_NAME";
    private static final String passwordSharedPrefKey = "passwordSharedPrefKey";
    private static final String ivSharedPassPrefKey = "ivSharedPassPrefKey";
    private static final String ivSharedMesPrefKey = "ivSharedMesPrefKey";

    @Bean
    MainViewModel mainViewModel;
    private int attempts = 0;

    private KeyguardManager keyguardManager;

    private FingerprintManager fingerprintManager;

    private boolean fingerprintAccepted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mainViewModel.checkIfFingerprintAuth()) {
            initializeEnterFingerprint(false);
        } else {
            initializeEnterPasswordLayout();
        }
    }


    private void initializeEnterFingerprint(final boolean isJustChangePassword) {
        fingerprintAccepted = false;
        enterFingerprintButton.setOnClickListener(view -> {
            if (fingerprintAccepted) {
                fingerprintAccepted = false;
                fingerprintAuthSelected = true;
//                if (isJustChangePassword || checkIfMessageSaved()) {
//                    String encryptedMessage = getSecretMessage();
//                    if (encryptedMessage != null) {
//                        fingerPrintLayout.setVisibility(View.GONE);
//                        initializeShowMessageLayout(encryptedMessage);
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

//    @Override
//    public void onAuthenticationSuccess(Boolean success, @Nullable String encryptedMessage) {
//        if (success) {
//            fingerprintInfoText.setTextColor(ContextCompat.getColor(this, R.color.primary_light));
//            fingerprintInfoText.setText(R.string.auth_success);
//            fingerprintInfoText.setVisibility(View.VISIBLE);
//            fingerprintAccepted = true;
//        } else {
//            fingerprintInfoText.setTextColor(ContextCompat.getColor(this, R.color.error));
//            if (encryptedMessage == null) {
//                fingerprintInfoText.setText(R.string.auth_failed);
//            } else {
//                fingerprintInfoText.setText(encryptedMessage);
//            }
//            fingerprintInfoText.setVisibility(View.VISIBLE);
//            fingerprintAccepted = false;
//        }
//    }


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
            initializeSelectAuthMethodButton(false);
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


}
