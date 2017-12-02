package pl.edu.amu.wmi.secretmessageapp.fingerprint;

/**
 * @author Eryk Mariankowski <eryk.mariankowski@247.codes> on 01.12.17.
 */
public interface FingerprintAuthCallback {
    void showError(CharSequence error);

    void onAuthenticated();

    void onError();
}
