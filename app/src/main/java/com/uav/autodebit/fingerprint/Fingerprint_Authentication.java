package com.uav.autodebit.fingerprint;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;

import android.support.v4.app.ActivityCompat;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uav.autodebit.R;
import com.uav.autodebit.util.Utility;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Fingerprint_Authentication {

    private static final String KEY_NAME = "autoPe";
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private IFingerPrint iFingerPrint;
    private Context context;
    private FingerprintHandler helper;
    private  LinearLayout fingerprintlayout;



    public  void startFingerPrintScanning(Context context, LinearLayout fingerprintlayout, TextView textView, IFingerPrint iFingerPrint){

            this.fingerprintlayout=fingerprintlayout;
            this.context=context;

            boolean fingerprintvalid=true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                textView.setText("Touch");

                ImageView image = new ImageView( context);
                LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(Utility.dpToPx(context,35),Utility.dpToPx(context,35));

                vp.setMargins(10, 0,0,0);
                image.setLayoutParams(vp);
                image.setMaxHeight(70);
                image.setMaxWidth(70);
                // other image settings
                image.setImageDrawable(context.getDrawable(R.drawable.fingerprint));
                fingerprintlayout.addView(image);

                LinearLayout.LayoutParams textvp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

                textvp.setMargins(10, Utility.dpToPx(context,10),0,0);

                TextView textview = new TextView( context);
                textview.setLayoutParams(textvp);
                textview.setText("Sensor To Login");
                textview.setTextColor(context.getColor(R.color.white));
                fingerprintlayout.addView(textview);

                keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                fingerprintManager =(FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);

               if (!fingerprintManager.isHardwareDetected()) {
                    fingerImageOrTextNullSet(textview,image);
                    textView.setText("Your device doesn't support fingerprint authentication");
                    fingerprintvalid=false;

                }

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                    fingerImageOrTextNullSet(textview,image);
                    textView.setText("Please enable the fingerprint permission");
                    fingerprintvalid=false;
                }

                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    fingerImageOrTextNullSet(textview,image);
                    textView.setText("No fingerprint configured. Please register at least one fingerprint in your device's Settings");
                    fingerprintvalid=false;
                }

                if(fingerprintvalid){
                    if (!keyguardManager.isKeyguardSecure()) {
                        fingerImageOrTextNullSet(textview,image);
                        textView.setText("Please enable lockscreen security in your device's Settings");
                    } else {
                        try {
                            generateKey();
                        } catch (FingerprintException e) {
                            e.printStackTrace();
                        }
                        if (initCipher()) {
                            cryptoObject = new FingerprintManager.CryptoObject(cipher);
                            helper = new FingerprintHandler(context);
                            helper.startAuth(fingerprintManager, cryptoObject,iFingerPrint);
                        }
                    }
                }
            }
    }

    public void fingerImageOrTextNullSet(TextView textView ,ImageView imageView){
        textView.setText(null);
        imageView.setImageDrawable(null);
    }





    public  void cancel(){
        if(helper!=null){
            helper.cancel();
            fingerprintlayout.removeAllViews();
        }

    }



    @TargetApi(Build.VERSION_CODES.M)
    private void generateKey() throws FingerprintException {
        try {

            keyStore = KeyStore.getInstance("AndroidKeyStore");


            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }


    }


    @TargetApi(Build.VERSION_CODES.M)
    public boolean initCipher() {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }



    private class FingerprintException extends Exception {

        public FingerprintException(Exception e) {
            super(e);
        }
    }
}