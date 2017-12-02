package pl.edu.amu.wmi.secretmessageapp.helper;

import android.app.AlertDialog;
import android.content.Context;

import pl.edu.amu.wmi.secretmessageapp.R;

/**
 * @author Eryk Mariankowski <eryk.mariankowski@247.codes> on 30.11.17.
 */
public class DialogHelper {

    private DialogHelper() {
    }

    public static AlertDialog errorDialog(Context context, String message) {
        return new AlertDialog.Builder(context, R.style.AppTheme_AlertDialogCustom)
                .setTitle(context.getString(R.string.error))
                .setMessage(message)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}