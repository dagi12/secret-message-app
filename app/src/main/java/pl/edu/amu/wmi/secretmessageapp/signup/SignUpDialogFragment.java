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
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import pl.edu.amu.wmi.secretmessageapp.R;
import pl.edu.amu.wmi.secretmessageapp.cipher.EncryptionPrefs_;
import pl.edu.amu.wmi.secretmessageapp.config.ConfigListener;
import pl.edu.amu.wmi.secretmessageapp.helper.CommonDialogFragment;
import pl.edu.amu.wmi.secretmessageapp.password.PasswordListener;
import pl.edu.amu.wmi.secretmessageapp.password.PasswordViewModel;

/**
 * A dialog which uses fingerprint APIs to authenticate the user, and falls back to et_password
 * authentication if fingerprint is not available.
 */
@EFragment(value = R.layout.fragment_sign_up)
public class SignUpDialogFragment extends CommonDialogFragment implements AuthCallback, PasswordListener {

    @ViewById(R.id.til_password)
    TextInputLayout passwordLayout;

    @ViewById(R.id.btn_second_dialog_button)
    Button secondDialogButton;

    @ViewById(R.id.fingerprint_container)
    View fingerprintContainer;

    @ViewById(R.id.backup_container)
    View backupContainer;

    @ViewById(R.id.iv_fingerprint_icon)
    ImageView fingerprintIcon;

    @ViewById(R.id.tv_fingerprint_status)
    TextView fingerprintStatus;

    @ViewById(R.id.et_password)
    EditText password;

    @ViewById(R.id.password_description)
    TextView mPasswordDescriptionTextView;

    @ViewById(R.id.new_fingerprint_enrolled_description)
    TextView mNewFingerprintEnrolledTextView;

    @FragmentArg
    Stage stage;

    @Bean
    SignUpViewModel signUpViewModel;

    @Pref
    EncryptionPrefs_ encryptionPrefs;

    @Bean
    PasswordViewModel passwordViewModel;

    private final Runnable mResetErrorTextRunnable = () -> {
        Resources resources = getContext().getResources();
        fingerprintStatus.setTextColor(resources.getColor(R.color.hint_color, null));
        fingerprintStatus.setText(resources.getString(R.string.fingerprint_hint));
        fingerprintIcon.setImageResource(R.drawable.ic_fp_40px);
    };

    private static final long ERROR_TIMEOUT_MILLIS = 1600;

    private static final long SUCCESS_DELAY_MILLIS = 1300;

    private InputMethodManager inputMethodManager;

    private ConfigListener configListener;

    @Click(R.id.btn_second_dialog_button)
    void onSecondDialogButtonClick() {
        if (stage == Stage.FINGERPRINT) {
            goToBackup();
        } else {
            passwordViewModel.verifyPassword(password, passwordLayout, this);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpViewModel.setAuthCallback(this);
    }

    @AfterViews
    protected void initView() {
        getDialog().setTitle(getString(R.string.register));
        passwordViewModel.passwordActionListener(password, passwordLayout, this);
        updateStage();
        if (!signUpViewModel.isFingerprintAuthAvailable()) {
            goToBackup();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (stage == Stage.FINGERPRINT && signUpViewModel.startListening()) {
            signUpViewModel.authenticate();
            fingerprintIcon.setImageResource(R.drawable.ic_fp_40px);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        signUpViewModel.stopListening();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        configListener = (ConfigListener) getActivity();
        inputMethodManager = context.getSystemService(InputMethodManager.class);
    }

    /**
     * Switches to backup (et_password) screen. This either can happen when fingerprint is not
     * available or the user chooses to use the et_password authentication method by pressing the
     * button. This can also happen when the user had too many fingerprint attempts.
     */
    private void goToBackup() {
        stage = Stage.PASSWORD;
        updateStage();
        password.requestFocus();

        // Show the keyboard.
        password.postDelayed(() -> inputMethodManager.showSoftInput(password, 0), 500);

        // Fingerprint is not used anymore. Stop listening for it.
        signUpViewModel.stopListening();
    }

    private void updateStage() {
        switch (stage) {
            case FINGERPRINT:
                secondDialogButton.setText(R.string.use_password);
                fingerprintContainer.setVisibility(View.VISIBLE);
                backupContainer.setVisibility(View.GONE);
                break;
            case PASSWORD:
                secondDialogButton.setText(android.R.string.ok);
                fingerprintContainer.setVisibility(View.GONE);
                backupContainer.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void showError(CharSequence error) {
        fingerprintIcon.setImageResource(R.drawable.ic_fingerprint_error);
        fingerprintStatus.setText(error);
        fingerprintStatus.setTextColor(getResources().getColor(R.color.warning_color, null));
        fingerprintStatus.removeCallbacks(mResetErrorTextRunnable);
        fingerprintStatus.postDelayed(mResetErrorTextRunnable, ERROR_TIMEOUT_MILLIS);
    }

    @Override
    public void onAuthenticated() {
        fingerprintStatus.removeCallbacks(mResetErrorTextRunnable);
        fingerprintIcon.setImageResource(R.drawable.ic_fingerprint_success);
        fingerprintStatus.setTextColor(getResources().getColor(R.color.primary_dark, null));
        fingerprintStatus.setText(getString(R.string.fingerprint_success));
        fingerprintIcon.postDelayed(() -> {
            // FingerprintCallback from SignUpViewModel. Let the activity know that authentication was
            // successful.
            signUpViewModel.saveFingerprint();
            configListener.onRegistered(true);
            dismiss();
        }, SUCCESS_DELAY_MILLIS);
    }

    @Override
    public void onError() {
        fingerprintIcon.postDelayed(this::goToBackup, ERROR_TIMEOUT_MILLIS);
    }

    @Override
    public void onPasswordVerified(String passwordText) {
        signUpViewModel.savePassword(passwordText);
        configListener.onRegistered(false);
        dismiss();
    }

}
