package pl.edu.amu.wmi.secretmessageapp.config;

/**
 * @author Eryk Mariankowski <eryk.mariankowski@247.codes> on 30.11.17.
 */
public interface ConfigListener {

    void onAuthenticated(boolean withFingerprint);
}
