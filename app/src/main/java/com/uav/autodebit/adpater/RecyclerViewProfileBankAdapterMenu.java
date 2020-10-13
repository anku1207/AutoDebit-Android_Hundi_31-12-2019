package com.uav.autodebit.adpater;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.uav.autodebit.Activity.Enach_Mandate;
import com.uav.autodebit.Activity.Profile_Activity;
import com.uav.autodebit.R;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ServiceTypeVO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by anku on 10/26/2017.
 */
/*
recyclerview.adapter
recyclerview.viewholder
*/

public class RecyclerViewProfileBankAdapterMenu extends RecyclerView.Adapter<RecyclerViewProfileBankAdapterMenu.ProdectViewHolder>{
    Context mctx ;
    List<ServiceTypeVO> serviceTypeVOS;
    int Activityname;



    public RecyclerViewProfileBankAdapterMenu(Context mctx, List<ServiceTypeVO> serviceTypeVOS, int Activityname) {
        this.mctx = mctx;
        this.serviceTypeVOS = serviceTypeVOS;
        this.Activityname=Activityname;
    }

    @Override
    public ProdectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(Activityname, parent, false);
        return new ProdectViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ProdectViewHolder holder, int position) {

         final ServiceTypeVO pro=serviceTypeVOS.get(position);
         holder.accountdetails.setText(pro.getTitle());
         holder.imageView.setImageDrawable(Utility.GetImage(mctx,pro.getAppIcon()));
         holder.mainlayout.setEnabled(true);


         holder.mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  ((Profile_Activity)mctx).bankDetails(pro.getAnonymousInteger());
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
        TextView accountdetails;
        public ProdectViewHolder(View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
            accountdetails=itemView.findViewById(R.id.accountdetails);
            mainlayout=itemView.findViewById(R.id.mainlayout);

        }
    }
}
