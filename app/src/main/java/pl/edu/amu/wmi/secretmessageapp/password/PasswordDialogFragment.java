package pl.edu.amu.wmi.secretmessageapp.password;

import android.support.design.widget.TextInputLayout;
import android.widget.Button;
import android.widget.EditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import pl.edu.amu.wmi.secretmessageapp.R;
import pl.edu.amu.wmi.secretmessageapp.config.ConfigListener;
import pl.edu.amu.wmi.secretmessageapp.helper.CommonDialogFragment;

/**
 * Stworzone przez Eryk Mariankowski dnia 01.12.17.
 */
@EFragment(value = R.layout.fragment_password_container)
public class PasswordDialogFragment extends CommonDialogFragment implements PasswordListener {

    @ViewById(R.id.et_password)
    EditText etPassword;

    @ViewById(R.id.til_password)
    TextInputLayout tilPassword;

    @ViewById(R.id.btn_second_dialog_button)
    Button secondDialogButton;

    @Bean
    PasswordViewModel passwordViewModel;

    private ConfigListener configListener;

    @Click(R.id.btn_second_dialog_button)
    void onDialogButton() {
        passwordViewModel.verifyPassword(etPassword, tilPassword, this);
    }

    @AfterViews
    protected void initView() {
        configListener = (ConfigListener) getActivity();
        getDialog().setTitle(getString(R.string.log_in));
        passwordViewModel.passwordActionListener(etPassword, tilPassword, this);
        secondDialogButton.setText(android.R.string.ok);
    }

    @Override
    public void onPasswordVerified(String passwordText) {
        if (passwordViewModel.isPasswordCorrect(passwordText)) {
            configListener.onLoggedIn();
        } else {
            passwordViewModel.resetPasswordAfterThreeAttempts();
            tilPassword.setError(getString(R.string.wrong_password));
        }
    }

}
