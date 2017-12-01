/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package pl.edu.amu.wmi.secretmessageapp.fingerprint;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.preference.PreferenceManager;
import android.view.View;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;

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
import pl.edu.amu.wmi.secretmessageapp.cipher.CipherStore;

/**
 * Small helper class to manage text/icon around fingerprint authentication UI.
 */
@EBean
class SignUpViewModel extends FingerprintManager.AuthenticationCallback {


    @Bean
    CipherStore cipherStore;

    @SystemService
    FingerprintManager mFingerprintManager;

    private CancellationSignal mCancellationSignal;

    private boolean mSelfCancelled;

    private AuthCallback authCallback;

    @RootContext
    Context context;

    private SharedPreferences preferences;

    @AfterInject
    void init() {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    boolean isFingerprintAuthAvailable() {
        // The line below prevents the false positive inspection from Android Studio
        // noinspection ResourceType
        return mFingerprintManager.isHardwareDetected()
                && mFingerprintManager.hasEnrolledFingerprints();
    }

    boolean startListening() {
        if (!isFingerprintAuthAvailable()) {
            return false;
        }

        mCancellationSignal = new CancellationSignal();
        mSelfCancelled = false;
        // The line below prevents the false positive inspection from Android Studio
        return true;
    }

    void stopListening() {
        if (mCancellationSignal != null) {
            mSelfCancelled = true;
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    void authenticate() {
        // noinspection ResourceType
        mFingerprintManager.authenticate(
                cipherStore.getCryptoObject(),
                mCancellationSignal,
                0,
                this,
                null);
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        if (!mSelfCancelled) {
            authCallback.showError(errString);
            authCallback.onError();
        }
    }

    /**
     * @return true if {@code password} is correct, false otherwise
     */
    boolean checkPassword(String password) {
        // Assume the password is always correct.
        // In the real world situation, the password needs to be verified in the server side.
        return password.length() >= 6;
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        authCallback.showError(helpString);
    }

    @Override
    public void onAuthenticationFailed() {
        authCallback.showError(context.getResources().getString(R.string.fingerprint_not_recognized));
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        authCallback.onAuthenticated();
    }

    void setAuthCallback(SignUpDialogFragment authCallback) {
        this.authCallback = authCallback;
    }

    void passwordVerified(boolean futureFingerprint) {
        preferences
                .edit()
                .putBoolean(
                        context.getString(R.string.use_fingerprint_to_authenticate_key),
                        futureFingerprint)
                .apply();
        if (futureFingerprint) {
            // Re-create the key so that fingerprints including new ones are validated.
            cipherStore.createKey();
        }
    }

    public void savePassword(String password) {
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
    }
}
