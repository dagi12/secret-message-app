package pl.edu.amu.wmi.secretmessageapp;

import android.support.annotation.Nullable;

interface AuthenticationSuccessListener {
    void onAuthenticationSuccess(Boolean success, @Nullable String message);
}