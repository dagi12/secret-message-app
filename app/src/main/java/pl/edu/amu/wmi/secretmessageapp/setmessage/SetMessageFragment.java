package pl.edu.amu.wmi.secretmessageapp.setmessage;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.github.paolorotolo.appintro.ISlidePolicy;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import pl.edu.amu.wmi.secretmessageapp.R;
import pl.edu.amu.wmi.secretmessageapp.config.ConfigListener;

/**
 * @author Eryk Mariankowski <eryk.mariankowski@247.codes> on 30.11.17.
 */
@EFragment(R.layout.fragment_set_message)
public class SetMessageFragment extends Fragment implements ISlidePolicy {

    @ViewById(R.id.et_create_message)
    public EditText etCreateMessage;

    @ViewById(R.id.til_create_message)
    TextInputLayout messageLayout;

    @Bean
    SetMessageViewModel setMessageViewModel;

    private ConfigListener configListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configListener = (ConfigListener) getActivity();
    }

    @AfterViews
    public void init() {
        etCreateMessage.setOnEditorActionListener((v, actionId, event) ->
                actionId == EditorInfo.IME_ACTION_DONE && manualDone());
    }

    private boolean manualDone() {
        if (isPolicyRespected()) {
            configListener.onMessageSaved(this);
            return true;
        }
        onUserIllegallyRequestedNextPage();
        return false;
    }

    /**
     * Whether the user has fulfilled the slides policy and should be allowed to navigate through the intro further.
     * If false is returned, {@link #onUserIllegallyRequestedNextPage()} will be called.
     *
     * @return True if the user should be allowed to leave the slide, else false.
     */
    @Override
    public boolean isPolicyRespected() {
        return etCreateMessage.getText().toString().length() >= 6;
    }

    /**
     * Called if a user tries to go to the next slide while into navigation has been locked.
     */
    @Override
    public void onUserIllegallyRequestedNextPage() {
        messageLayout.setError(getString(R.string.short_msg));
    }


}
