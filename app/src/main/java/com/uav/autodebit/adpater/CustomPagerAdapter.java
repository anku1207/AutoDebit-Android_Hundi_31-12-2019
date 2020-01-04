package com.uav.autodebit.adpater;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.squareup.picasso.Picasso;
import com.uav.autodebit.R;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CustomPagerAdapter extends PagerAdapter {

  private List<DMRC_Customer_CardVO> models;
  private LayoutInflater layoutInflater;
  private Context context;


    public CustomPagerAdapter(List<DMRC_Customer_CardVO> models, Context context) {
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
                .inflate(R.layout.design_dmrc_card_list, container, false);

        LinearLayout mainlayout;
        TextView name,cardnumber,status,issuedate;
        ImageView imageview;

        mainlayout=itemView.findViewById(R.id.mainlayout);
        name=itemView.findViewById(R.id.name);
        name.setText("  "+models.get(position).getCustomerName());
        cardnumber=itemView.findViewById(R.id.cardnumber);
        status=itemView.findViewById(R.id.status);
        issuedate=itemView.findViewById(R.id.issuedate);
        imageview=itemView.findViewById(R.id.imageview);

        DMRC_Customer_CardVO pro=models.get(position);
        name.setText("  "+pro.getCustomerName());
        status.setText("  "+pro.getDmrccardStaus().getStatusName());
        cardnumber.setText("Card No. \n"+pro.getCardNo());

        if(pro.getIssueDate()!=null){
            Date date =new Date(pro.getIssueDate());
            issuedate.setText("Issue Date \n"+new SimpleDateFormat("dd-MM-yyyy").format(date));
        }else {
            issuedate.setText("");
        }
        if(pro.getImage()!=null){
            Picasso.with(context).load(pro.getImage()).fit()
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
