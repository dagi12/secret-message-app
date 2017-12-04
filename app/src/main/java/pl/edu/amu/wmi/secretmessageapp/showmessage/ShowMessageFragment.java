package pl.edu.amu.wmi.secretmessageapp.showmessage;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import com.shashank.sony.fancytoastlib.FancyToast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import pl.edu.amu.wmi.secretmessageapp.R;
import pl.edu.amu.wmi.secretmessageapp.cipher.KeyAlias;

import static android.widget.Toast.LENGTH_LONG;
import static com.shashank.sony.fancytoastlib.FancyToast.ERROR;

/**
 * Stworzone przez Eryk Mariankowski dnia 01.12.17.
 */
@EFragment(R.layout.fragment_show_message)
public class ShowMessageFragment extends Fragment {

    @ViewById(R.id.tv_message)
    TextView tvMessage;

    @ViewById(R.id.btn_change_auth_method)
    Button btnChangeAuthMethod;

    @Bean
    ShowMessageViewModel showMessageViewModel;

    private KeyAlias alias;

    @AfterViews
    void init() {
        @StringRes int disabledAuthMethodLabel;
        if (showMessageViewModel.getFingerprintAuth()) {
            alias = KeyAlias.FINGER;
            disabledAuthMethodLabel = R.string.change_for_password;
        } else {
            alias = KeyAlias.PASS;
            disabledAuthMethodLabel = R.string.change_for_fingerprint;
        }
        btnChangeAuthMethod.setText(getString(disabledAuthMethodLabel));
        String message = showMessageViewModel.decryptMessage();
        if (TextUtils.isEmpty(message)) {
            FancyToast.makeText(getActivity(), getString(R.string.processing_error), LENGTH_LONG, ERROR, false).show();
        } else {
            tvMessage.setText(message);
        }
    }

    @Click(R.id.btn_change_auth_method)
    void changeAuthMethod() {
        if (alias == KeyAlias.FINGER) {
            showMessageViewModel.changeForPassword(getActivity());
        } else {
            showMessageViewModel.changeForFingerprint(getActivity());
        }
    }


    @Click(R.id.delete_data_button)
    public void onDeleteDataClick() {
        showMessageViewModel.resetData();
    }

}
