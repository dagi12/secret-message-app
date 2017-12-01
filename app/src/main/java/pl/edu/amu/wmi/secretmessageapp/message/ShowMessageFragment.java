package pl.edu.amu.wmi.secretmessageapp.message;

import android.support.v4.app.Fragment;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import pl.edu.amu.wmi.secretmessageapp.R;

/**
 * Stworzone przez Eryk Mariankowski dnia 01.12.17.
 */
@EFragment(R.layout.fragment_show_message)
public class ShowMessageFragment extends Fragment {



    @Click(R.id.delete_data_button)
    public void onDeleteDataClick() {

    }

}
