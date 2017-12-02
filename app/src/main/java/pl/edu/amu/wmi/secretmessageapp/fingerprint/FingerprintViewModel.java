package pl.edu.amu.wmi.secretmessageapp.fingerprint;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;

import pl.edu.amu.wmi.secretmessageapp.R;
import pl.edu.amu.wmi.secretmessageapp.cipher.EncryptionStore;

/**
 * @author Eryk Mariankowski <eryk.mariankowski@247.codes> on 02.12.17.
 */
@EBean
public class FingerprintViewModel extends FingerprintManager.AuthenticationCallback {

    public static final long ERROR_TIMEOUT_MILLIS = 1600;

    @RootContext
    Context context;

    @SystemService
    FingerprintManager mFingerprintManager;

    @Bean
    EncryptionStore encryptionStore;

    private static final long SUCCESS_DELAY_MILLIS = 1300;

    private FingerprintAuthCallback fingerprintAuthCallback;

    private ImageView fingerprintIcon;

    private TextView fingerprintStatus;

    private final Runnable mResetErrorTextRunnable = () -> {
        Resources resources = context.getResources();
        fingerprintStatus.setTextColor(resources.getColor(R.color.hint_color, null));
        fingerprintStatus.setText(resources.getString(R.string.fingerprint_hint));
        fingerprintIcon.setImageResource(R.drawable.ic_fp_40px);
    };

    private boolean mSelfCancelled;

    private CancellationSignal mCancellationSignal;

    public void initFingerprintViewModel(ImageView fingerprintIcon,
                                         TextView fingerprintStatus,
                                         FingerprintAuthCallback fingerprintAuthCallback) {
        this.fingerprintIcon = fingerprintIcon;
        this.fingerprintStatus = fingerprintStatus;
        this.fingerprintAuthCallback = fingerprintAuthCallback;
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        if (!mSelfCancelled) {
            fingerprintAuthCallback.showError(errString);
            fingerprintAuthCallback.onError();
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        fingerprintAuthCallback.showError(helpString);
    }

    @Override
    public void onAuthenticationFailed() {
        fingerprintAuthCallback.showError(context.getResources().getString(R.string.fingerprint_not_recognized));
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        fingerprintAuthCallback.onAuthenticated();
    }

    public void authenticate() {
        // noinspection ResourceType
        mFingerprintManager.authenticate(
                encryptionStore.crypto(),
                mCancellationSignal,
                0, this,
                null);
    }

    public boolean isFingerprintAuthAvailable() {
        // The line below prevents the false positive inspection from Android Studio
        // noinspection ResourceType
        return mFingerprintManager.isHardwareDetected()
                && mFingerprintManager.hasEnrolledFingerprints();
    }

    public boolean startListening() {
        if (!isFingerprintAuthAvailable()) {
            return false;
        }

        mCancellationSignal = new CancellationSignal();
        mSelfCancelled = false;
        // The line below prevents the false positive inspection from Android Studio
        return true;
    }

    public void stopListening() {
        if (mCancellationSignal != null) {
            mSelfCancelled = true;
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    public void onAuthenticated(Runnable runnable) {
        fingerprintStatus.removeCallbacks(mResetErrorTextRunnable);
        fingerprintIcon.setImageResource(R.drawable.ic_fingerprint_success);
        fingerprintStatus.setTextColor(context.getResources().getColor(R.color.primary_dark, null));
        fingerprintStatus.setText(context.getString(R.string.fingerprint_success));
        fingerprintIcon.postDelayed(runnable, SUCCESS_DELAY_MILLIS);
    }

    public void showError(CharSequence error) {
        fingerprintIcon.setImageResource(R.drawable.ic_fingerprint_error);
        fingerprintStatus.setText(error);
        fingerprintStatus.setTextColor(context.getResources().getColor(R.color.warning_color, null));
        fingerprintStatus.removeCallbacks(mResetErrorTextRunnable);
        fingerprintStatus.postDelayed(mResetErrorTextRunnable, ERROR_TIMEOUT_MILLIS);
    }
}
