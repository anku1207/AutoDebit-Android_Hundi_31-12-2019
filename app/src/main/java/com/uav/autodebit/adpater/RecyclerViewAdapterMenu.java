package com.uav.autodebit.adpater;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.uav.autodebit.Activity.Dmrc_NewAndExist_Card_Dialog;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.usersservices.ui.MandateRevokeServiceWiseActivity;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ServiceTypeVO;


import java.util.List;

/**
 * Created by anku on 10/26/2017.
 */
/*
recyclerview.adapter
recyclerview.viewholder
*/

public class RecyclerViewAdapterMenu extends RecyclerView.Adapter<RecyclerViewAdapterMenu.ProdectViewHolder>{
    Context mctx ;
    List<ServiceTypeVO> serviceTypeVOS;
    int Activityname;



    public RecyclerViewAdapterMenu(Context mctx, List<ServiceTypeVO> serviceTypeVOS, int Activityname) {
        this.mctx = mctx;
        this.serviceTypeVOS = serviceTypeVOS;
        this.Activityname=Activityname;
    }

    @Override
    public ProdectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_service_design, parent, false);
        return new ProdectViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ProdectViewHolder holder, int position) {

         final ServiceTypeVO pro=serviceTypeVOS.get(position);
         holder.textview.setText(pro.getTitle());
         holder.imageView.setImageDrawable(Utility.GetImage(mctx,pro.getAppIcon()));
         holder.mainlayout.setEnabled(true);


         /* holder.mailmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(pro.getActivityname()!=null){
                   mctx.startActivity(new Intent(mctx,pro.getActivityname()));
               }else {
                   Toast.makeText(mctx, "Something is wrong ", Toast.LENGTH_SHORT).show();
               }
            }
        });*/
        holder.mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer serviceTypeID = pro.getServiceTypeId();
                if(serviceTypeID== ApplicationConstant.Dmrc){
                    mctx.startActivity(new Intent(mctx, Dmrc_NewAndExist_Card_Dialog.class));
                }else{
                    Intent intent = new Intent(mctx, MandateRevokeServiceWiseActivity.class);
                    intent.putExtra("serviceID",serviceTypeID);
                    mctx.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceTypeVOS.size();
    }
    public  class ProdectViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mainlayout;
        ImageView imageView;
        TextView textview;
        public ProdectViewHolder(View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
            textview=itemView.findViewById(R.id.textview);
            mainlayout=itemView.findViewById(R.id.mainlayout);

        }
    }
}
