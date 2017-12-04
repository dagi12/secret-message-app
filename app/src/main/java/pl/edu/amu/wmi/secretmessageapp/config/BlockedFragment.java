package pl.edu.amu.wmi.secretmessageapp.config;

import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.ISlidePolicy;

import org.androidannotations.annotations.EFragment;

/**
 * @author Eryk Mariankowski <eryk.mariankowski@247.codes> on 01.12.17.
 */
@EFragment
public class BlockedFragment extends Fragment implements ISlidePolicy {

    /**
     * Whether the user has fulfilled the slides policy and should be allowed to navigate through the intro further.
     * If false is returned, {@link #onUserIllegallyRequestedNextPage()} will be called.
     *
     * @return True if the user should be allowed to leave the slide, else false.
     */
    @Override
    public boolean isPolicyRespected() {
        return false;
    }

    /**
     * Called if a user tries to go to the next slide while into navigation has been locked.
     */
    @Override
    public void onUserIllegallyRequestedNextPage() {
        // stub
    }
}
