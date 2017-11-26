package pl.edu.amu.wmi.secretmessageapp;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {


    private Context context;
    private onAuthentifiactionSuccessListener listener;
    private CancellationSignal cancellationSignal;


    // Constructor
    public FingerprintHandler(Context mContext, onAuthentifiactionSuccessListener listener) {
        context = mContext;
        this.listener = listener;
    }


    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    public void cancelAuth(){
        cancellationSignal.cancel();
    }


    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        listener.onAuthentifiactionSuccess(false, errString.toString());
    }


    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        listener.onAuthentifiactionSuccess(false, helpString.toString());
    }


    @Override
    public void onAuthenticationFailed() {
        listener.onAuthentifiactionSuccess(false, null);
    }


    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        cancelAuth();
        listener.onAuthentifiactionSuccess(true, null);
    }


    public interface onAuthentifiactionSuccessListener{
        void onAuthentifiactionSuccess(Boolean success, @Nullable String message);
    }
}