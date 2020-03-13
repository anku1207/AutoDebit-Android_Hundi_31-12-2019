package com.uav.autodebit.Activity;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uav.autodebit.R;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.DataAdapterVO;

public class DynamicLayout {

    public static LinearLayout billMinLayout(Context context, DataAdapterVO dataAdapterVO){
        LinearLayout parent_min_layout = new LinearLayout(context);
        parent_min_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        parent_min_layout.setOrientation(LinearLayout.VERTICAL);


        TextView textView = Utility.getTextView(context,"Important Information");
        textView.setLayoutParams(Utility.getLayoutparams(context,0,0,0,0));
        parent_min_layout.addView(textView);


        LinearLayout parent = new LinearLayout(context);
        parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        parent.setOrientation(LinearLayout.HORIZONTAL);

        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(Utility.getPixelsFromDPs(context,15), Utility.getPixelsFromDPs(context,15), (float) .1));
        imageView.setImageDrawable(context.getDrawable(R.drawable.iconcheck));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);;

        TextView text1 = new TextView(context);
        text1.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,(float)1));
        text1.setText("Minimum amount allowed is Rs "+dataAdapterVO.getMinTxnAmount().toString());
        parent.addView(imageView);
        parent.addView(text1);
        parent_min_layout.addView(parent);
        return parent_min_layout;
    }
}
