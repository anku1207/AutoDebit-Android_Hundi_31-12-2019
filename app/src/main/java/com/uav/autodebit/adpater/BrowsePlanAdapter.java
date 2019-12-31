package com.uav.autodebit.adpater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uav.autodebit.Activity.Mobile_Prepaid_Recharge_Service;
import com.uav.autodebit.R;
import com.uav.autodebit.override.UAVTextView;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.DataAdapterVO;
import com.uav.autodebit.vo.ServiceTypeVO;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class BrowsePlanAdapter extends RecyclerView.Adapter<BrowsePlanAdapter.ProdectViewHolder> {
    Context mctx;
    List<DataAdapterVO> productslist;
    int Activityname;


    public BrowsePlanAdapter(Context mctx, List<DataAdapterVO> productslist, int Activityname) {
        this.mctx = mctx;
        this.productslist = productslist;
        this.Activityname = Activityname;
    }

    @Override
    public BrowsePlanAdapter.ProdectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mctx);
        View view = layoutInflater.inflate(Activityname, null);
       /* ProdectViewHolder holder=new ProdectViewHolder(view);
        return holder;*/
        return new BrowsePlanAdapter.ProdectViewHolder(view);
    }

    @Override

    public void onBindViewHolder(BrowsePlanAdapter.ProdectViewHolder holder, int position) {

        final DataAdapterVO pro = productslist.get(position);
        holder.talktime.setText("Talktime " + mctx.getString(R.string.Rs) + " "+pro.getText());
        holder.amount.setText( mctx.getString(R.string.Rs) +" "+pro.getMop());
        holder.validity.setText( "Validity: "+pro.getValidity());
        holder.desc.setText( pro.getText2());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent12 = new Intent();
                intent12.putExtra("amount",pro.getMop());
                Activity activity = (Activity) mctx;
                activity.setResult(RESULT_OK,intent12);
                activity.finish() ;
            }
        });







    }

    @Override
    public int getItemCount() {
        return productslist.size();
    }

    public class ProdectViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView talktime, desc,validity,amount;

        public ProdectViewHolder(View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            talktime = itemView.findViewById(R.id.talktime);
            desc = itemView.findViewById(R.id.desc);
            validity = itemView.findViewById(R.id.validity);
            amount=itemView.findViewById(R.id.amount);

        }
    }
}