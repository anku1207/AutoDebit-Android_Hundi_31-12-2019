package com.uav.autodebit.adpater;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.uav.autodebit.Activity.HistorySummary;
import com.uav.autodebit.R;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.DataAdapterVO;

import org.json.JSONArray;
import org.json.JSONObject;

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

        try{

            DataAdapterVO pro=historyList.get(position);
            holder.cardno.setText(pro.getNumber());
            holder.service_name.setText(pro.getServiceName());
            holder.status.setText(pro.getStatus());
            holder.mainlayout.setTag(pro.getCustmerPassBookId());
            holder.bottom_left_status.setText(Utility.underlineTextViewtext(pro.getLink()));


            JSONArray jsonArray =new JSONArray(pro.getQuestionsData());

            if(holder.chargeslayout.getChildCount()>0) holder.chargeslayout.removeAllViews();
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject =jsonArray.getJSONObject(i);
                createChargesLayout(holder.chargeslayout,jsonObject.getString("key"),jsonObject.getString("value"));
            }

            holder.mainlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*index=position;
                    notifyDataSetChanged();*/
                    if(pro.getLink()!=null && !pro.getLink().equals("")){
                        mctx.startActivity(new Intent(mctx, HistorySummary.class).putExtra("historyId",pro.getCustmerPassBookId().toString()));
                    }
                }
            });

       /* if(index!=null && index==position){
            holder.mainlayout.setBackgroundColor(Color.parseColor("#D0D3D4"));
        }else {
            holder.mainlayout.setBackgroundColor(Color.parseColor("#ffffff"));
        }*/

        }catch (Exception e){
            Toast.makeText(mctx, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }
    public class ProdectViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mainlayout,chargeslayout;
        TextView cardno,service_name,txn_id,date,amount,service_charge,netamt,debitdate,status,bottom_left_status;


        public ProdectViewHolder(View itemView) {
            super(itemView);
            mainlayout=itemView.findViewById(R.id.mainlayout);
            cardno=itemView.findViewById(R.id.cardno);
            service_name=itemView.findViewById(R.id.service_name);
            status=itemView.findViewById(R.id.bottom_right_status);
            chargeslayout=itemView.findViewById(R.id.chargeslayout);
            bottom_left_status=itemView.findViewById(R.id.bottom_left_status);
        }
    }


    private void createChargesLayout(LinearLayout linearLayout,String key ,String Value){



        LinearLayout.LayoutParams layoutparams ;


        LinearLayout et = new LinearLayout(new ContextThemeWrapper(mctx,R.style.history_charges));
        layoutparams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1 );
        et.setLayoutParams(layoutparams);

        TextView text = new TextView(new ContextThemeWrapper(mctx, R.style.confirmation_dialog_filed));
        layoutparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0 );
        text.setLayoutParams(layoutparams);
        text.setText(key);
        text.setMaxLines(1);
        text.setEllipsize(TextUtils.TruncateAt.END);
        text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        text.setTextSize(12);
        text.setTypeface(text.getTypeface(), Typeface.BOLD);



        TextView value = new TextView(new ContextThemeWrapper(mctx, R.style.confirmation_dialog_value));
        layoutparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 5);

        value.setLayoutParams(layoutparams);
        value.setText(Value);
        value.setMaxLines(1);
        value.setEllipsize(TextUtils.TruncateAt.END);
        value.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        value.setTextSize(12);
        value.setTypeface(null);


        et.addView(text);
        et.addView(value);
        linearLayout.addView(et);
    }


}
