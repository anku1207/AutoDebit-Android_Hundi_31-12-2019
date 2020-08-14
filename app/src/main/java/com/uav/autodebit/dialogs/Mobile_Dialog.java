package com.uav.autodebit.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.uav.autodebit.Activity.Home;
import com.uav.autodebit.Activity.Mobile_Activity_Dialog;
import com.uav.autodebit.Activity.Mobile_Postpaid;
import com.uav.autodebit.Activity.Mobile_Prepaid_Recharge_Service;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.vo.BannerVO;

public class Mobile_Dialog {

    Context context;
    String serviceid;
    View view;

    public Mobile_Dialog(Context context,String serviceid,View view ){
        this.context = context;
        this.serviceid = serviceid;
        this.view=view;

    }
    public void showdialog(){

        final Dialog dialog = new Dialog(context);
        // Include dialog.xml file
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.showmobileconfirmation);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout prepaid_ly = dialog.findViewById(R.id.prepaid_ly);
        prepaid_ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.cancel();
                ((Home) context).startUserClickService(ApplicationConstant.MobilePrepaid +"", view);
               /* Intent intent;
                intent =new Intent(context, Mobile_Prepaid_Recharge_Service.class);
                intent.putExtra("serviceid",serviceid+"");
                context.startActivity(intent);*/

            }
        });
        LinearLayout postpaid_lay = dialog.findViewById(R.id.postpaid_lay);
        postpaid_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();

                ((Home) context).startUserClickService(ApplicationConstant.MobilePostpaid+"", view);
               /* Intent intent;
                intent =new Intent(context, Mobile_Postpaid.class);
                intent.putExtra("serviceid",serviceid+"");
                context.startActivity(intent);*/

            }
        });
        dialog.show();

    }

}
