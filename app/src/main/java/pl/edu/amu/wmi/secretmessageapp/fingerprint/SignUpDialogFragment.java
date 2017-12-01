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

import android.app.DialogFragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

import pl.edu.amu.wmi.secretmessageapp.R;
import pl.edu.amu.wmi.secretmessageapp.config.ConfigActivity;
import pl.edu.amu.wmi.secretmessageapp.config.ConfigListener;

/**
 * A dialog which uses fingerprint APIs to authenticate the user, and falls back to password
 * authentication if fingerprint is not available.
 */
@EFragment(value = R.layout.fragment_sign_up)
public class SignUpDialogFragment extends DialogFragment implements AuthCallback {

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

    @ViewById(R.id.password)
    EditText mPassword;


    @ViewById(R.id.password_description)
    TextView mPasswordDescriptionTextView;

    @ViewById(R.id.new_fingerprint_enrolled_description)
    TextView mNewFingerprintEnrolledTextView;

    @FragmentArg
    Stage stage;

    @Bean
    SignUpViewModel signUpViewModel;

    private final Runnable mResetErrorTextRunnable = () -> {
        Resources resources = getContext().getResources();
        fingerprintStatus.setTextColor(resources.getColor(R.color.hint_color, null));
        fingerprintStatus.setText(resources.getString(R.string.fingerprint_hint));
        fingerprintIcon.setImageResource(R.drawable.ic_fp_40px);
    };

    private static final long ERROR_TIMEOUT_MILLIS = 1600;

    private static final long SUCCESS_DELAY_MILLIS = 1300;

    private InputMethodManager inputMethodManager;

    private ConfigListener configActivity;

    @Click(R.id.btn_second_dialog_button)
    void onSecondDialogButtonClick() {
        if (stage == Stage.FINGERPRINT) {
            goToBackup();
        } else {
            verifyPassword();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FingerprintDialog);
        signUpViewModel.setAuthCallback(this);
        setCancelable(false);
    }

    @AfterViews
    protected void initView() {
        getDialog().setTitle(getString(R.string.register));
        mPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                verifyPassword();
                return true;
            }
            return false;
        });
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
        configActivity = (ConfigActivity) getActivity();
        inputMethodManager = context.getSystemService(InputMethodManager.class);
    }

    /**
     * Switches to backup (password) screen. This either can happen when fingerprint is not
     * available or the user chooses to use the password authentication method by pressing the
     * button. This can also happen when the user had too many fingerprint attempts.
     */
    private void goToBackup() {
        stage = Stage.PASSWORD;
        updateStage();
        mPassword.requestFocus();

        // Show the keyboard.
        mPassword.postDelayed(() -> inputMethodManager.showSoftInput(mPassword, 0), 500);

        // Fingerprint is not used anymore. Stop listening for it.
        signUpViewModel.stopListening();
    }

    /**
     * Checks whether the current entered password is correct, and dismisses the the dialog and
     * let's the activity know about the result.
     */
    private void verifyPassword() {
        String password = mPassword.getText().toString();
        if (!signUpViewModel.checkPassword(password)) {
            passwordLayout.setError(getString(R.string.short_password));
            return;
        }
        signUpViewModel.savePassword(password);
        configActivity.onRegistered(false);
        mPassword.setText("");
        dismiss();
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
            configActivity.onRegistered(true);
            dismiss();
        }, SUCCESS_DELAY_MILLIS);
    }

    @Override
    public void onError() {
        fingerprintIcon.postDelayed(this::goToBackup, ERROR_TIMEOUT_MILLIS);
    }

}
