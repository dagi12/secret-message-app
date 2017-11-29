package pl.edu.amu.wmi.secretmessageapp;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;

class FingerprintHandler extends FingerprintManager.AuthenticationCallback {


    private Context context;

    private AuthenticationSuccessListener listener;

    private CancellationSignal cancellationSignal;


    // Constructor
    FingerprintHandler(Context mContext, AuthenticationSuccessListener listener) {
        context = mContext;
        this.listener = listener;
    }


    void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    void cancelAuth() {
        cancellationSignal.cancel();
    }


    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        listener.onAuthenticationSuccess(false, errString.toString());
    }


    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        listener.onAuthenticationSuccess(false, helpString.toString());
    }


    @Override
    public void onAuthenticationFailed() {
        listener.onAuthenticationSuccess(false, null);
    }


    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        cancelAuth();
        listener.onAuthenticationSuccess(true, null);
    }


}