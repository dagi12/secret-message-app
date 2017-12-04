package pl.edu.amu.wmi.secretmessageapp.fingerprint;

import android.widget.ImageView;
import android.widget.TextView;

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
@EFragment(value = R.layout.fragment_fingerprint_container)
public class FingerprintDialogFragment extends CommonDialogFragment implements FingerprintAuthCallback {

    @Bean
    FingerprintViewModel fingerprintViewModel;

    @ViewById(R.id.iv_fingerprint_icon)
    ImageView fingerprintIcon;

    @ViewById(R.id.tv_fingerprint_status)
    TextView fingerprintStatus;

    @ViewById(R.id.new_fingerprint_enrolled_description)
    TextView mNewFingerprintEnrolledTextView;

    private ConfigListener configListener;

    @AfterViews
    protected void initView() {
        configListener = (ConfigListener) getActivity();
        getDialog().setTitle(getString(R.string.log_in));
        fingerprintViewModel.initFingerprintViewModel(fingerprintIcon, fingerprintStatus, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fingerprintViewModel.startListening()) {
            fingerprintViewModel.authenticate();
            fingerprintIcon.setImageResource(R.drawable.ic_fp_40px);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        fingerprintViewModel.stopListening();
    }

    @Override
    public void showError(CharSequence error) {
        fingerprintViewModel.showError(error);
    }

    @Override
    public void onAuthenticated() {
        fingerprintViewModel.onAuthenticated(() -> {
            // FingerprintCallback from SignUpViewModel. Let the activity know that authentication was
            // successful.
            configListener.onLoggedIn();
            dismiss();
        });
    }

    @Override
    public void onError() {
        showError(getString(R.string.fingerprint_not_recognized));
    }

}
