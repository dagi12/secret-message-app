package pl.edu.amu.wmi.secretmessageapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.EActivity;

import pl.edu.amu.wmi.secretmessageapp.showmessage.ShowMessageFragment_;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Fragment fragment = ShowMessageFragment_
                .builder()
                .build();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }

}
