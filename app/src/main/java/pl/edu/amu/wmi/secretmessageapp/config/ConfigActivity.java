package pl.edu.amu.wmi.secretmessageapp.config;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.github.paolorotolo.appintro.AppIntro;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

import pl.edu.amu.wmi.secretmessageapp.MainActivity;
import pl.edu.amu.wmi.secretmessageapp.cipher.CipherStore;
import pl.edu.amu.wmi.secretmessageapp.fingerprint.FingerprintDialogFragment_;
import pl.edu.amu.wmi.secretmessageapp.fingerprint.Stage;
import pl.edu.amu.wmi.secretmessageapp.helper.DialogHelper;

/**
 * @author Eryk Mariankowski <eryk.mariankowski@247.codes> on 30.11.17.
 */
@EActivity
public class ConfigActivity extends AppIntro implements ConfigListener {

    @Bean
    CipherStore cipherStore;

    private static final String IS_MESSAGE_SAVED_PREF_KEY = "isMessageSavedPrefKey";

    private static final String TAG = ConfigActivity.class.getSimpleName();

    private SharedPreferences preferences;

    public SharedPreferences getPreferences() {
        if (preferences == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
        }
        return preferences;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkIfMessageSaved()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            initConfig();
        }
    }

    private void initConfig() {
        askForPermissions(new String[]{Manifest.permission.USE_FINGERPRINT}, 2);
        addSlide(BlockedFragment_.builder().build());
        String result = cipherStore.checkFingerprintPermission();
        if (result != null) {
            DialogHelper.errorDialog(this, result);
        } else {
            showDialog();
            addSlide(MessageFragment_.builder().build());
            showSkipButton(false);
            setProgressButtonEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {
        // disable back button
    }

    private void showDialog() {
        DialogFragment dialogFragment = FingerprintDialogFragment_
                .builder()
                .stage(Stage.FINGERPRINT)
                .build();
        dialogFragment.show(getFragmentManager(), TAG);
    }


    private boolean checkIfMessageSaved() {
        return getPreferences().getBoolean(IS_MESSAGE_SAVED_PREF_KEY, false);
    }

    public void onAuthenticated(boolean withFingerprint) {
        pager.goToNextSlide();
    }

}
