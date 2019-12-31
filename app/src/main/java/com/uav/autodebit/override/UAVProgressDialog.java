package com.uav.autodebit.override;

import android.app.ProgressDialog;
import android.content.Context;

public class UAVProgressDialog extends ProgressDialog {

    public UAVProgressDialog(Context context ) {
        super(context);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
        this.setMessage("Processing...");

    }


}
