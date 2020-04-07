package com.uav.autodebit.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.uav.autodebit.R;

public class BarcodeScanner extends Base_Activity {


    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private TextView barcodeValue;
    AlertDialog.Builder builder;
    TextView barcodescantext ;

    private Activity myActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        ImageView back_activity_button=(ImageView)findViewById(R.id.back_activity_button);
        TextView  title=(TextView)findViewById(R.id.title);
        getSupportActionBar().hide();

        title.setText("Barcode Scanner");
        back_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


      /*  getSupportActionBar().setTitle("Barcode Scanner");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
*/


        barcodescantext = (TextView) findViewById(R.id.barcodescan);
        myActivity = this;
        builder = new AlertDialog.Builder(myActivity);

        cameraView = (SurfaceView) findViewById(R.id.surface_view);
        barcodeValue = (TextView) findViewById(R.id.barcode_value);

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();



        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @SuppressLint("MissingPermission")
            @Override

            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)

                        return;
                    cameraSource.start(holder);
                } catch (Exception e){
                    if(e.getMessage().equalsIgnoreCase("Fail to connect to camera service")){
                        Toast.makeText(myActivity, "Please allow camera from settings", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(myActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }



        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

                Intent intent12 = new Intent();
                intent12.putExtra("key",barcodeValue.getText().toString());

                setResult(RESULT_OK,intent12);
                finish() ;

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {

                    barcodeValue.post(new Runnable() {    // Use the post method of the TextView
                        public void run() {


                            barcodeValue.setText(    // Update the TextView
                                    barcodes.valueAt(0).displayValue
                            );



                            if (barcodes.valueAt(0).displayValue != null ||barcodes.valueAt(0).displayValue != "") {
                                barcodeDetector.release();
                            }



                        }


                    });


                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

