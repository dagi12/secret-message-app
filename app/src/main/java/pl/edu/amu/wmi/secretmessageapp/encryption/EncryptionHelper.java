package pl.edu.amu.wmi.secretmessageapp.encryption;

import android.util.Base64;

/**
 * @author Eryk Mariankowski <eryk.mariankowski@247.codes> on 04.12.17.
 */
class EncryptionHelper {

    private EncryptionHelper() {

    }

    static String bytesToString(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    static byte[] stringToBytes(String string) {
        return Base64.decode(string, Base64.DEFAULT);
    }

}
