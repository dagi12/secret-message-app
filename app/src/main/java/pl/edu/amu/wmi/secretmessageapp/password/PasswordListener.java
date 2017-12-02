package pl.edu.amu.wmi.secretmessageapp.password;

/**
 * @author Eryk Mariankowski <eryk.mariankowski@247.codes> on 02.12.17.
 */
public interface PasswordListener {

    void onPasswordVerified(String passwordText);
}
