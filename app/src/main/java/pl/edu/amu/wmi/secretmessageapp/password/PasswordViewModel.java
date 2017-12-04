package pl.edu.amu.wmi.secretmessageapp.password;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.sharedpreferences.Pref;

import pl.edu.amu.wmi.secretmessageapp.R;
import pl.edu.amu.wmi.secretmessageapp.encryption.EncryptionPrefs_;
import pl.edu.amu.wmi.secretmessageapp.encryption.EncryptionStore;
import pl.edu.amu.wmi.secretmessageapp.encryption.KeyStoreService;

/**
 * @author Eryk Mariankowski <eryk.mariankowski@247.codes> on 02.12.17.
 */
@EBean
public class PasswordViewModel {

    @Bean
    EncryptionStore encryptionStore;

    @Pref
    EncryptionPrefs_ encryptionPrefs;

    @RootContext
    Context context;

    @Bean
    KeyStoreService keyStoreService;

    private int attempts = 0;

    void resetPasswordAfterThreeAttempts() {
        attempts = attempts + 1;
        if (attempts == 3) {
            attempts = 0;
            keyStoreService.resetData();
        }
    }

    boolean isPasswordCorrect(String enteredPassword) {
        return enteredPassword.equals(encryptionStore.decryptPassword());
    }

    public void passwordActionListener(EditText password, TextInputLayout passwordLayout, PasswordListener passwordListener) {
        password.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                verifyPassword(password, passwordLayout, passwordListener);
                return true;
            }
            return false;
        });
    }

    /**
     * Checks whether the current entered et_password is correct, and dismisses the the dialog and
     * let's the activity know about the result.
     */
    public void verifyPassword(EditText password, TextInputLayout passwordLayout, PasswordListener passwordListener) {
        String passwordText = password.getText().toString();
        if (!checkPassword(passwordText)) {
            passwordLayout.setError(context.getString(R.string.short_password));
            return;
        }
        passwordListener.onPasswordVerified(passwordText);
    }

    /**
     * @return true if {@code et_password} is correct, false otherwise
     */
    private boolean checkPassword(String password) {
        return password.length() >= 6;
    }

}
