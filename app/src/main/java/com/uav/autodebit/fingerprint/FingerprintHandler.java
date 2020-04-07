package com.uav.autodebit.fingerprint;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import androidx.core.app.ActivityCompat;


@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private CancellationSignal cancellationSignal ;
    private Context context;
    private IFingerPrint iFingerPrint;



    public FingerprintHandler(Context mContext) {
        context = mContext;
        this.cancellationSignal = new CancellationSignal();;
    }

    public  void cancel(){
        cancellationSignal.cancel();
    }



    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject,IFingerPrint iFingerPrint) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.iFingerPrint=iFingerPrint;

        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);

    }

    @Override
    public void onAuthenticationError(int errMsgId,
                                      CharSequence errString) {
        iFingerPrint.onAuthenticationError(errMsgId,errString);
    }

    @Override
    public void onAuthenticationFailed() {
        iFingerPrint.onAuthenticationFailed();

    }

    @Override
    public void onAuthenticationHelp(int helpMsgId,
                                     CharSequence helpString) {
      iFingerPrint.onAuthenticationHelp(helpMsgId,helpString);
    }


    @Override
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult result) {
        iFingerPrint.onAuthenticationSucceeded(result);
    }


}
