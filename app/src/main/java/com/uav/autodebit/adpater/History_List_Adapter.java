package com.uav.autodebit.adpater;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uav.autodebit.Activity.HistorySummary;
import com.uav.autodebit.R;
import com.uav.autodebit.vo.DataAdapterVO;

import java.util.List;

public class History_List_Adapter  extends RecyclerView.Adapter<History_List_Adapter.ProdectViewHolder>{
    Context mctx ;
    List<DataAdapterVO> historyList;
    int Activityname;

    Integer index;

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
        holder.cardno.setText(pro.getNumber());
        holder.service_name.setText(pro.getServiceName());
        holder.txn_id.setText(pro.getTxnId());
        holder.date.setText(pro.getTxnDate());
        holder.service_charge.setText(pro.getServiceCharge());
        holder.netamt.setText(pro.getNetAmt());
        holder.debitdate.setText(pro.getDebitDate());
        holder.status.setText("Debit Success : "+pro.getStatus());
        holder.amount.setText(pro.getAmt());

        holder.mainlayout.setTag(pro.getCustmerPassBookId());
        holder.mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*index=position;
                notifyDataSetChanged();*/
                mctx.startActivity(new Intent(mctx, HistorySummary.class).putExtra("historyId",pro.getCustmerPassBookId().toString()));
            }
        });

       /* if(index!=null && index==position){
            holder.mainlayout.setBackgroundColor(Color.parseColor("#D0D3D4"));
        }else {
            holder.mainlayout.setBackgroundColor(Color.parseColor("#ffffff"));
        }*/
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }
    public class ProdectViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mainlayout;
        TextView cardno,service_name,txn_id,date,amount,service_charge,netamt,debitdate,status;

        public ProdectViewHolder(View itemView) {
            super(itemView);
            mainlayout=itemView.findViewById(R.id.mainlayout);
            cardno=itemView.findViewById(R.id.cardno);
            service_name=itemView.findViewById(R.id.service_name);
            txn_id=itemView.findViewById(R.id.txn_id);
            date=itemView.findViewById(R.id.date);
            service_charge=itemView.findViewById(R.id.service_charge);
            netamt=itemView.findViewById(R.id.netamt);
            debitdate=itemView.findViewById(R.id.debitdate);
            status=itemView.findViewById(R.id.status);
            amount=itemView.findViewById(R.id.amount);


        }
    }
}
