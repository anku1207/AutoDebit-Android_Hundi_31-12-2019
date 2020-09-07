package com.uav.autodebit.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uav.autodebit.Activity.Home;
import com.uav.autodebit.Activity.Mobile_Activity_Dialog;
import com.uav.autodebit.Activity.Mobile_Postpaid;
import com.uav.autodebit.Activity.Mobile_Prepaid_Recharge_Service;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.BannerVO;
import com.uav.autodebit.vo.ServiceChargesVO;
import com.uav.autodebit.vo.ServiceTypeVO;

import java.util.List;

import okhttp3.internal.Util;

public class Mobile_Dialog {

    public static void showdialog(Context context, List<ServiceTypeVO> serviceChargesVOS, View view ){

        final Dialog dialog = new Dialog(context);
        // Include dialog.xml file
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.showmobileconfirmation);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout mainlayout = dialog.findViewById(R.id.mainlayout);
        for(ServiceTypeVO serviceTypeVO : serviceChargesVOS){
            LinearLayout parent = new LinearLayout(context);
            LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(
                   0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,(float).5
            );
            layoutparams.gravity = Gravity.CENTER_HORIZONTAL;
            parent.setBackgroundResource(Utility.getClickBackgroundEffect(context));



            parent.setOrientation(LinearLayout.VERTICAL);
            parent.setLayoutParams(layoutparams);
            parent.setPadding(Utility.dpToPx(context,10),Utility.dpToPx(context,10),Utility.dpToPx(context,10),Utility.dpToPx(context,10));
            parent.setGravity(Gravity.CENTER);


            ImageView imageView = new ImageView(context);
            layoutparams = new LinearLayout.LayoutParams(
                   Utility.dpToPx(context,100),
                    Utility.dpToPx(context,100)
            );
            layoutparams.gravity = Gravity.CENTER;
            imageView.setLayoutParams(layoutparams);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageDrawable(Utility.GetImage(context,serviceTypeVO.getAppIcon()));

            TextView textView = new TextView(context);
            layoutparams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutparams.gravity = Gravity.CENTER;
            textView.setLayoutParams(layoutparams);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Utility.getColorWrapper(context,R.color.defaultTextColor));
           // textView.setTextSize(Utility.dpToPx(context,7));
            textView.setText(serviceTypeVO.getTitle());
            textView.setPadding(5,5,5,5);

            parent.addView(imageView);
            parent.addView(textView);
            mainlayout.addView(parent);


            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                    ((Home) context).startUserClickService(serviceTypeVO.getServiceTypeId() +"", view);
                }
            });
        }
        dialog.show();
    }
}
