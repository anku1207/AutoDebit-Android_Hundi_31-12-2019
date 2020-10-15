package com.uav.autodebit.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uav.autodebit.Activity.Home;
import com.uav.autodebit.R;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ServiceTypeVO;

import java.util.List;

public class Mobile_Dialog {

    public static void showdialog(Context context, List<ServiceTypeVO> serviceChargesVOS, View view ,String title){

        final Dialog dialog = new Dialog(context);
        // Include dialog.xml file
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.showmobileconfirmation);
        TextView tv = dialog.findViewById(R.id.heading);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if(title==null){
            tv.setVisibility(View.GONE);
        }else{
           tv.setVisibility(View.VISIBLE);
           tv.setText(title);
        }
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
