package com.uav.autodebit.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.uav.autodebit.Activity.Notifications;
import com.uav.autodebit.R;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.CustomerNotificationVO;

import org.json.JSONObject;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ProdectViewHolder>{
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

        if(pro.getBigImage()!=null && !pro.getBigImage().equals("")){
            Picasso.with(mctx).load(pro.getBigImage())
                    .into(holder.notification_image);
        }else{
            holder.notification_image.setImageDrawable(null);
        }

        if(pro.getServiceIcon()!=null && !pro.getServiceIcon().equals("")){
            Picasso.with(mctx).load(pro.getServiceIcon()).fit()
                    .into(holder.notification_icon);

        }else {
            holder.notification_icon.setImageDrawable(null);
        }

        holder.notification_mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if(pro.getMoveActivity()!=null){
                        JSONObject jsonObject = new JSONObject(pro.getMoveActivity());
                        if(jsonObject.getBoolean("action")){
                            ((Notifications)mctx).setActionOnNotificationClick(pro.getMoveActivity());
                        }
                    }
                }catch (Exception e){
                    ExceptionsNotification.ExceptionHandling(mctx , Utility.getStackTrace(e));
                }

            }
        });



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
