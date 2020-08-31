package com.uav.autodebit.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.uav.autodebit.R;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.util.Utility;


public class SendLog extends AppCompatActivity implements View.OnClickListener{
    private AlertDialog alertDialog;
    private String logs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // make a dialog without a titlebar
        setFinishOnTouchOutside(false); // prevent users from dismissing the dialog by tapping outside
        setContentView(R.layout.activity_send_log);
        logs = getIntent().getStringExtra("logs");
        showConfirmation();
    }

    @Override
    public void onClick(View v) {

    }
    private void sendLogFile() {

        if (logs == null)
            return;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@anduriltechnologies.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Error reported from MyAPP");
        intent.putExtra(Intent.EXTRA_TEXT, "Log file attached."+logs); // do this so some email clients don't complain about empty body.
        startActivity(intent);
    }
    private void showConfirmation() {
        // method as shown above
        ExceptionsNotification.ExceptionHandling(this ,logs);

        alertDialog = new AlertDialog.Builder(SendLog.this).create();
        alertDialog.setTitle("Report Error!");
        alertDialog.setMessage("Ah, shoot. Seems like MyAPP faced an unhandled error.Would you like to report it to the developer team?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Report", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                sendLogFile();
                finishAffinity();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Utility.dismissDialog(SendLog.this,alertDialog);
                finishAffinity();
            }
        });
        alertDialog.show();
    }
}
