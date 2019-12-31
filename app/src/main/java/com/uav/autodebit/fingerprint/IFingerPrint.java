package com.uav.autodebit.fingerprint;

import android.hardware.fingerprint.FingerprintManager;

public interface IFingerPrint {
    void onAuthenticationError(int errMsgId,CharSequence errString);
    void onAuthenticationFailed();
    void onAuthenticationHelp(int helpMsgId,CharSequence helpString);
    void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result);
  }
