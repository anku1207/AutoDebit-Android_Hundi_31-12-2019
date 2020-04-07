package com.uav.autodebit.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.uav.autodebit.R;
import com.uav.autodebit.override.UAVTextView;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.DataAdapterVO;
import com.uav.autodebit.vo.ServiceTypeVO;

import java.util.List;

public class SingleImage_With_Text_RecyclerViewAdapter  extends  RecyclerView.Adapter<SingleImage_With_Text_RecyclerViewAdapter.ProdectViewHolder>{
    Context mctx ;

    List<DataAdapterVO> productslist;
    int Activityname;



    public SingleImage_With_Text_RecyclerViewAdapter(Context mctx, List<DataAdapterVO> productslist, int Activityname) {
        this.mctx = mctx;
        this.productslist = productslist;
        this.Activityname=Activityname;
    }

    @Override
    public SingleImage_With_Text_RecyclerViewAdapter.ProdectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(mctx);
        View view=layoutInflater.inflate(Activityname,null);
       /* ProdectViewHolder holder=new ProdectViewHolder(view);
        return holder;*/
        return new SingleImage_With_Text_RecyclerViewAdapter.ProdectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SingleImage_With_Text_RecyclerViewAdapter.ProdectViewHolder holder, int position) {

        final DataAdapterVO dataAdapterVO=productslist.get(position);

        if(Utility.GetImage(mctx,dataAdapterVO.getImagename())!=null){
            holder.imageView.setImageDrawable(Utility.GetImage(mctx,dataAdapterVO.getImagename()));
        }
        holder.textView.setTxtAssociatedValue( dataAdapterVO.getAssociatedValue());
        holder.textView.setText( dataAdapterVO.getText());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mctx, ""+dataAdapterVO.getAssociatedValue(), Toast.LENGTH_SHORT).show();


            }
        });



    }

    @Override
    public int getItemCount() {
        return productslist.size();
    }
    public  class ProdectViewHolder extends RecyclerView.ViewHolder {

        UAVTextView textView;
        ImageView imageView;
        LinearLayout layout;
        public ProdectViewHolder(View itemView) {
            super(itemView);
             imageView=(ImageView)itemView.findViewById(R.id.listimage);
             textView=(UAVTextView) itemView.findViewById(R.id.listtext);
             layout=itemView.findViewById(R.id.layout);


        }
    }
}
