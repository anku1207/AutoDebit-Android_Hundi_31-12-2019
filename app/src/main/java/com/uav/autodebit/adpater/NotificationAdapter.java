package com.uav.autodebit.adpater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uav.autodebit.R;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.CustomerNotificationVO;
import com.uav.autodebit.vo.DataAdapterVO;
import com.uav.autodebit.vo.ServiceTypeVO;

import java.util.List;

public class NotificationAdapter  extends RecyclerView.Adapter<NotificationAdapter.ProdectViewHolder>{
    Context mctx ;
    List<CustomerNotificationVO> customerNotificationVOS;

    public NotificationAdapter(Context mctx, List<CustomerNotificationVO> customerNotificationVOS) {
        this.mctx = mctx;
        this.customerNotificationVOS = customerNotificationVOS;
    }

    @Override
    public NotificationAdapter.ProdectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_design, parent, false);
        return new NotificationAdapter.ProdectViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(NotificationAdapter.ProdectViewHolder holder, int position) {

        final CustomerNotificationVO pro=customerNotificationVOS.get(position);


        holder.notification_title.setText(pro.getTitle());
        holder.notification_desc.setText(pro.getMessage());
        holder.notification_date.setText(pro.getCreatedAt());

        if(pro.getImage()!=null){
            Picasso.with(mctx).load(pro.getImage())
                    .into(holder.notification_image);
        }else{
            holder.notification_image.setImageDrawable(null);
        }

        if(pro.getServiceIcon()!=null){
            Picasso.with(mctx).load(pro.getServiceIcon()).fit()
                    .into(holder.notification_icon);

        }else {
            holder.notification_icon.setImageDrawable(null);
        }



    }

    @Override
    public int getItemCount() {
        return customerNotificationVOS.size();
    }
    public  class ProdectViewHolder extends RecyclerView.ViewHolder {

        LinearLayout notification_mainlayout;
        ImageView notification_image,notification_icon;
        TextView notification_title,notification_desc,notification_date;
        public ProdectViewHolder(View itemView) {
            super(itemView);
            notification_mainlayout=itemView.findViewById(R.id.notification_mainlayout);
            notification_image=itemView.findViewById(R.id.notification_image);
            notification_title=itemView.findViewById(R.id.notification_title);
            notification_desc=itemView.findViewById(R.id.notification_desc);
            notification_date=itemView.findViewById(R.id.notification_date);
            notification_icon=itemView.findViewById(R.id.notification_icon);

        }
    }
}
