package pl.edu.amu.wmi.secretmessageapp.signup;

/**
 * @author Eryk Mariankowski <eryk.mariankowski@247.codes> on 01.12.17.
 */
interface AuthCallback {
    void showError(CharSequence error);

    void onAuthenticated();

    void onError();
}
