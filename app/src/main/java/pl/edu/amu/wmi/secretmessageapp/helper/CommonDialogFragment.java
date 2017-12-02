package pl.edu.amu.wmi.secretmessageapp.helper;

import android.app.DialogFragment;
import android.os.Bundle;

import pl.edu.amu.wmi.secretmessageapp.R;

/**
 * @author Eryk Mariankowski <eryk.mariankowski@247.codes> on 02.12.17.
 */
public class CommonDialogFragment extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FingerprintDialog);
        setCancelable(false);
    }

}
