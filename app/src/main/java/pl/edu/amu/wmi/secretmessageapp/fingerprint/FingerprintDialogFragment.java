package pl.edu.amu.wmi.secretmessageapp.fingerprint;

import android.widget.Button;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import pl.edu.amu.wmi.secretmessageapp.R;
import pl.edu.amu.wmi.secretmessageapp.config.ConfigListener;
import pl.edu.amu.wmi.secretmessageapp.helper.CommonDialogFragment;

/**
 * Stworzone przez Eryk Mariankowski dnia 01.12.17.
 */
@EFragment(value = R.layout.fragment_fingerprint)
public class FingerprintDialogFragment extends CommonDialogFragment {


    private ConfigListener configListener;

    @Bean
    FingerprintViewModel fingerprintViewModel;

    @ViewById(R.id.btn_second_dialog_button)
    Button secondDialogButton;

    @AfterViews
    protected void initView() {
        configListener = (ConfigListener) getActivity();
        getDialog().setTitle(getString(R.string.log_in));
        secondDialogButton.setText(android.R.string.ok);
    }

}
