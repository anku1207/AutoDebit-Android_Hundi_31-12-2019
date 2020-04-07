package com.uav.autodebit.adpater;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.uav.autodebit.R;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.DataAdapterVO;
import com.uav.autodebit.vo.ServiceTypeVO;

import java.util.List;

public class SingleImageRecyclerViewAdapter extends  RecyclerView.Adapter<SingleImageRecyclerViewAdapter.ProdectViewHolder>{
        Context mctx ;
        List<ServiceTypeVO> productslist;
        int Activityname;



        public SingleImageRecyclerViewAdapter(Context mctx, List<ServiceTypeVO> productslist, int Activityname) {
        this.mctx = mctx;
        this.productslist = productslist;
        this.Activityname=Activityname;
        }

        @Override
        public ProdectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater= LayoutInflater.from(mctx);
          View view=layoutInflater.inflate(Activityname,null);
       /* ProdectViewHolder holder=new ProdectViewHolder(view);
        return holder;*/
        return new ProdectViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ProdectViewHolder holder, int position) {

        final ServiceTypeVO pro=productslist.get(position);

        holder.imageView.setImageDrawable(Utility.GetImage(mctx,pro.getAppIcon()));
        holder.mailmenu.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

                Toast.makeText(mctx, ""+pro.getTitle(), Toast.LENGTH_SHORT).show();


        }
        });



        }

        @Override
        public int getItemCount() {
                return productslist.size();
                }
        public  class ProdectViewHolder extends RecyclerView.ViewHolder {

            LinearLayout mailmenu;
            ImageView imageView;
            public ProdectViewHolder(View itemView) {
                super(itemView);
                imageView=itemView.findViewById(R.id.imageView);
                mailmenu=itemView.findViewById(R.id.layout);

            }
        }
}
