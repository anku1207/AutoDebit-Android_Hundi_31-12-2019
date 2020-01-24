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
import com.uav.autodebit.vo.DMRC_Customer_CardVO;
import com.uav.autodebit.vo.DataAdapterVO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class History_List_Adapter  extends RecyclerView.Adapter<History_List_Adapter.ProdectViewHolder>{
    Context mctx ;
    List<DataAdapterVO> historyList;
    int Activityname;

    public History_List_Adapter(Context mctx, List<DataAdapterVO> historyList, int Activityname) {
        this.mctx = mctx;
        this.historyList = historyList;
        this.Activityname=Activityname;
    }

    @Override
    public History_List_Adapter.ProdectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(Activityname, parent, false);
        return new History_List_Adapter.ProdectViewHolder(itemView);

       /* View rootView = LayoutInflater.from(mctx).inflate(R.layout.design_dmrc_card_list, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(lp);
        return new ProdectViewHolder(rootView);
*/

    }

    @Override
    public void onBindViewHolder(History_List_Adapter.ProdectViewHolder holder, int position) {

        final DataAdapterVO pro=historyList.get(position);

        holder.serviceType.setText(pro.getText());
        holder.transactionNumber.setText(pro.getText2());
        holder.amount.setText(mctx.getString(R.string.Rs)+" "+pro.getMop());
        holder.day.setText(pro.getInvoiceDate());

        if(pro.getImageUrl()!=null){
            Picasso.with(mctx).load(pro.getImageUrl()).fit()
                    .error(R.drawable.loadimage)
                    .placeholder(R.drawable.loadimage)
                    .into(holder.icon_service);
        }else {
            holder.icon_service.setImageURI(null);
        }
    }
    @Override
    public int getItemCount() {
        return historyList.size();
    }
    public class ProdectViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mainlayout;
        TextView serviceType,transactionNumber,amount,day;
        ImageView icon_service;

        public ProdectViewHolder(View itemView) {
            super(itemView);
            mainlayout=itemView.findViewById(R.id.mainlayout);
            serviceType=itemView.findViewById(R.id.serviceType);
            transactionNumber=itemView.findViewById(R.id.transactionNumber);
            amount=itemView.findViewById(R.id.amount);
            day=itemView.findViewById(R.id.day);
            icon_service=itemView.findViewById(R.id.icon_service);


        }
    }
}
