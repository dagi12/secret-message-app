package pl.edu.amu.wmi.secretmessageapp.message;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

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

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    public static final String MSG_INTENT_KEY = "MSG_INTENT_KEY";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Fragment fragment = ShowMessageFragment_
                .builder()
                .message(getIntent().getStringExtra(MSG_INTENT_KEY))
                .build();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }

    //    @Override
//    public void onAuthenticationSuccess(Boolean success, @Nullable String encryptedMessage) {
//        if (success) {
//            fingerprintInfoText.setTextColor(ContextCompat.getColor(this, R.color.primary_light));
//            fingerprintInfoText.setText(R.string.auth_success);
//            fingerprintInfoText.setVisibility(View.VISIBLE);
//            fingerprintAccepted = true;
//        } else {
//            fingerprintInfoText.setTextColor(ContextCompat.getColor(this, R.color.error));
//            if (encryptedMessage == null) {
//                fingerprintInfoText.setText(R.string.auth_failed);
//            } else {
//                fingerprintInfoText.setText(encryptedMessage);
//            }
//            fingerprintInfoText.setVisibility(View.VISIBLE);
//            fingerprintAccepted = false;
//        }
//    }




    private void initializeShowMessageLayout(String message) {
        showMessageLayout.setVisibility(View.VISIBLE);
        messageTextView.setText(message);
        exitButton.setOnClickListener(view -> finish());
        deleteDataButton.setOnClickListener(view -> {
            resetData();
            initializeSelectAuthMethodButton(false);
        });
    }




}
