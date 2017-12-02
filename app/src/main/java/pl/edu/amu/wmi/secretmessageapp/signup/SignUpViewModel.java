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

package pl.edu.amu.wmi.secretmessageapp.signup;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import pl.edu.amu.wmi.secretmessageapp.R;
import pl.edu.amu.wmi.secretmessageapp.cipher.EncryptionPrefs_;
import pl.edu.amu.wmi.secretmessageapp.cipher.EncryptionStore;

/**
 * Small helper class to manage text/icon around fingerprint authentication UI.
 */
@EBean
class SignUpViewModel extends FingerprintManager.AuthenticationCallback {

    @SystemService
    FingerprintManager mFingerprintManager;

    @RootContext
    Context context;

    @Bean
    EncryptionStore encryptionStore;

    @Pref
    EncryptionPrefs_ encryptionPrefs;

    private CancellationSignal mCancellationSignal;

    private boolean mSelfCancelled;

    private AuthCallback authCallback;

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
                encryptionStore.crypto(),
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

    void savePassword(String password) {
        encryptionStore.savePass(password);
    }

    void saveFingerprint() {
        encryptionPrefs.fingerprint().put(true);
        encryptionStore.resetPassword();
    }


}
