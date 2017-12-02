package pl.edu.amu.wmi.secretmessageapp.signup;

import android.content.Context;
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
import pl.edu.amu.wmi.secretmessageapp.fingerprint.FingerprintAuthCallback;
import pl.edu.amu.wmi.secretmessageapp.fingerprint.FingerprintViewModel;
import pl.edu.amu.wmi.secretmessageapp.helper.CommonDialogFragment;
import pl.edu.amu.wmi.secretmessageapp.password.PasswordListener;
import pl.edu.amu.wmi.secretmessageapp.password.PasswordViewModel;

/**
 * A dialog which uses fingerprint APIs to authenticate the user, and falls back to et_password
 * authentication if fingerprint is not available.
 */
@EFragment(value = R.layout.fragment_sign_up)
public class SignUpDialogFragment extends CommonDialogFragment implements PasswordListener, FingerprintAuthCallback {

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

    @ViewById(R.id.new_fingerprint_enrolled_description)
    TextView mNewFingerprintEnrolledTextView;

    @ViewById(R.id.et_password)
    EditText password;

    @ViewById(R.id.password_description)
    TextView mPasswordDescriptionTextView;

    @FragmentArg
    Stage stage;

    @Bean
    SignUpViewModel signUpViewModel;

    @Pref
    EncryptionPrefs_ encryptionPrefs;

    @Bean
    PasswordViewModel passwordViewModel;

    @Bean
    FingerprintViewModel fingerprintViewModel;

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

    @AfterViews
    protected void initView() {
        fingerprintViewModel.initFingerprintViewModel(fingerprintIcon, fingerprintStatus, this);
        getDialog().setTitle(getString(R.string.register));
        passwordViewModel.passwordActionListener(password, passwordLayout, this);
        updateStage();
        if (!fingerprintViewModel.isFingerprintAuthAvailable()) {
            goToBackup();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (stage == Stage.FINGERPRINT && fingerprintViewModel.startListening()) {
            fingerprintViewModel.authenticate();
            fingerprintIcon.setImageResource(R.drawable.ic_fp_40px);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        fingerprintViewModel.stopListening();
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
        fingerprintViewModel.stopListening();
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
    public void onPasswordVerified(String passwordText) {
        signUpViewModel.savePassword(passwordText);
        configListener.onRegistered(false);
        dismiss();
    }


    @Override
    public void showError(CharSequence error) {
        fingerprintViewModel.showError(error);
    }

    @Override
    public void onAuthenticated() {
        fingerprintViewModel.onAuthenticated(() -> {
            // FingerprintCallback from SignUpViewModel. Let the activity know that authentication was
            // successful.
            signUpViewModel.saveFingerprint();
            configListener.onRegistered(true);
            dismiss();
        });
    }

    @Override
    public void onError() {
        fingerprintIcon.postDelayed(this::goToBackup, FingerprintViewModel.ERROR_TIMEOUT_MILLIS);
    }

}