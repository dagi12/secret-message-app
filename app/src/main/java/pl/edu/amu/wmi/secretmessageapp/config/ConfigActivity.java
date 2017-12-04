package pl.edu.amu.wmi.secretmessageapp.config;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Button;

import com.github.paolorotolo.appintro.AppIntro;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

import pl.edu.amu.wmi.secretmessageapp.MainActivity_;
import pl.edu.amu.wmi.secretmessageapp.R;
import pl.edu.amu.wmi.secretmessageapp.cipher.KeyAlias;
import pl.edu.amu.wmi.secretmessageapp.fingerprint.FingerprintDialogFragment_;
import pl.edu.amu.wmi.secretmessageapp.password.PasswordDialogFragment_;
import pl.edu.amu.wmi.secretmessageapp.setmessage.SetMessageFragment;
import pl.edu.amu.wmi.secretmessageapp.setmessage.SetMessageFragment_;


/**
 * @author Eryk Mariankowski <eryk.mariankowski@247.codes> on 30.11.17.
 */
@EActivity
public class ConfigActivity extends AppIntro implements ConfigListener {

    public static final String CHANGE_METHOD_KEY = "CHANGE_METHOD_KEY";

    @Bean
    ConfigViewModel configViewModel;

    private static final String TAG = ConfigActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // prosimy o uprawnienie do yżycia sensora odcisku palca
        askForPermissions(new String[]{Manifest.permission.USE_FINGERPRINT}, 2);

        // dodajemy pusty slajd z okienkiem
        addSlide(BlockedFragment_.builder().build());
        if (configViewModel.initConfig(this)) {
            if (!configViewModel.checkIfMessageSaved()) {
                configViewModel.signUpDialog(this, KeyAlias.FINGER, false);
                addSlide(SetMessageFragment_.builder().build());
                showSkipButton(false);
                setProgressButtonEnabled(false);
                ((Button) doneButton).setText(R.string.done);
            } else {
                String key = getIntent().getStringExtra(CHANGE_METHOD_KEY);
                if (TextUtils.isEmpty(key)) {
                    authenticate();
                } else if (key.equals(KeyAlias.FINGER.name())) {
                    configViewModel.signUpDialog(this, KeyAlias.FINGER, true);
                } else {
                    configViewModel.signUpDialog(this, KeyAlias.PASS, true);
                }
            }
        }
    }

    private void authenticate() {
        if (configViewModel.getFingerprintAuth()) {
            showFingerprintDialog();
        } else {
            showPasswordDialog();
        }
    }

    private void showPasswordDialog() {
        DialogFragment dialogFragment = PasswordDialogFragment_
                .builder()
                .build();
        dialogFragment.show(getFragmentManager(), TAG);
    }

    private void showFingerprintDialog() {
        DialogFragment dialogFragment = FingerprintDialogFragment_
                .builder()
                .build();
        dialogFragment.show(getFragmentManager(), TAG);
    }

    @Override
    public void onBackPressed() {
        // disable back button
    }

    @Override
    public void onRegistered() {
        setProgressButtonEnabled(true);
        pager.goToNextSlide();
    }

    @Override
    public void onMessageSaved(Fragment fragment) {
        pager.goToNextSlide();
        onDonePressed(fragment);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        SetMessageFragment setMessageFragment = (SetMessageFragment) currentFragment;
        String msg = setMessageFragment.etCreateMessage.getText().toString();
        configViewModel.saveMessage(msg);
        authenticate();
    }

    @Override
    public void onLoggedIn() {
        Intent intent = new Intent(this, MainActivity_.class);
        startActivity(intent);
    }

    @Override
    public void onAuthChanged(KeyAlias forMethod) {
        if (KeyAlias.PASS == forMethod) {
            authenticate();
        } else {
            onLoggedIn();
        }
    }


}
