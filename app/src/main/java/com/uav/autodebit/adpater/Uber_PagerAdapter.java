package com.uav.autodebit.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;
import com.uav.autodebit.R;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;
import com.uav.autodebit.vo.UberVoucherVO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Uber_PagerAdapter extends PagerAdapter {

    private List<UberVoucherVO> models;
    private LayoutInflater layoutInflater;
    private Context context;


    public Uber_PagerAdapter(List<UberVoucherVO> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ViewGroup itemView = (ViewGroup) LayoutInflater.from(container.getContext())
                .inflate(R.layout.uber_voucher_card_design, container, false);

        LinearLayout mainlayout;
        TextView name,status,summary;
        ImageView imageview;

        mainlayout=itemView.findViewById(R.id.mainlayout);
        imageview=itemView.findViewById(R.id.imageview);
        summary=itemView.findViewById(R.id.summary);

        UberVoucherVO pro=models.get(position);
        if( pro.getVoucherExprieDate()!=null && pro.getIssueAt()!=null){

            summary.setText("Voucher No.  "+pro.getVoucherNo() +"\n"+"Issue Date : "+new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date(pro.getIssueAt())) +"\n"+"ExprieDate : "+ new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date(pro.getVoucherExprieDate())));
        }else {
            summary.setText("");
        }
        if(pro.getImage()!=null){
            Picasso.with(context)
                    .load(pro.getImage())
                    .fit()
                    .error(R.drawable.dmrc)
                    .placeholder(R.drawable.dmrc)
                    .into(imageview);
        }else {
            imageview.setImageURI(null);
        }
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}

