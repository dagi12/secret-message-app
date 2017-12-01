package pl.edu.amu.wmi.secretmessageapp.config;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;


import pl.edu.amu.wmi.secretmessageapp.fingerprint.FingerprintDialogFragment_;
import pl.edu.amu.wmi.secretmessageapp.fingerprint.PasswordDialogFragment_;
import pl.edu.amu.wmi.secretmessageapp.fingerprint.SignUpDialogFragment_;
import pl.edu.amu.wmi.secretmessageapp.fingerprint.Stage;
import pl.edu.amu.wmi.secretmessageapp.message.MainActivity;

/**
 * @author Eryk Mariankowski <eryk.mariankowski@247.codes> on 30.11.17.
 */
@EActivity
public class ConfigActivity extends AppIntro implements ConfigListener {

    private static final String TAG = ConfigActivity.class.getSimpleName();
    @Bean
    ConfigViewModel configViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        askForPermissions(new String[]{Manifest.permission.USE_FINGERPRINT}, 2);
        addSlide(BlockedFragment_.builder().build());
        if (configViewModel.initConfig(this)) {
            if (!configViewModel.checkIfMessageSaved()) {
                showDialog();
                addSlide(SetMessageFragment_.builder().build());
                showSkipButton(false);
                setProgressButtonEnabled(false);
            } else {
                if (configViewModel.checkIfFingerprintAuth()) {
                    showFingerprintDialog();
                } else {
                    showPasswordDialog();
                }
            }
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

    private void showDialog() {
        DialogFragment dialogFragment = SignUpDialogFragment_
                .builder()
                .stage(Stage.FINGERPRINT)
                .build();
        dialogFragment.show(getFragmentManager(), TAG);
    }

    @Override
    public void onRegistered(boolean withFingerprint) {
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
        String msg = setMessageFragment.message.getText().toString();
        configViewModel.saveMessage(msg);

    }

    @Override
    public void onLoggedIn(String msg) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.MSG_NAME, msg);
        startActivity(intent);
    }


}
