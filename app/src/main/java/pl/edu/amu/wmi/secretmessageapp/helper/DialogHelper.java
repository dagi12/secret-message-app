package pl.edu.amu.wmi.secretmessageapp.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.view.ContextThemeWrapper;

import pl.edu.amu.wmi.secretmessageapp.R;

/**
 * @author Eryk Mariankowski <eryk.mariankowski@247.codes> on 30.11.17.
 */
public class DialogHelper {

    private DialogHelper() {
    }

    public static AlertDialog errorDialog(Context context, String message) {
        return new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.NoActionBarTheme_AlertDialogCustom))
                .setTitle(context.getString(R.string.error))
                .setInverseBackgroundForced(true)
                .setMessage(message)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}