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
import com.uav.autodebit.R;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DMRC_List_Adpater extends RecyclerView.Adapter<DMRC_List_Adpater.ProdectViewHolder>{
    Context mctx ;
    List<DMRC_Customer_CardVO> productslist;
    int Activityname;

    public DMRC_List_Adpater(Context mctx, List<DMRC_Customer_CardVO> productslist, int Activityname) {
        this.mctx = mctx;
        this.productslist = productslist;
        this.Activityname=Activityname;
    }
    @Override
    public DMRC_List_Adpater.ProdectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.design_dmrc_card_list, parent, false);
        return new DMRC_List_Adpater.ProdectViewHolder(itemView);

       /* View rootView = LayoutInflater.from(mctx).inflate(R.layout.design_dmrc_card_list, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(lp);
        return new ProdectViewHolder(rootView);
*/
    }
    @Override
    public void onBindViewHolder(DMRC_List_Adpater.ProdectViewHolder holder, int position) {

        final DMRC_Customer_CardVO pro=productslist.get(position);
        holder.name.setText("  "+pro.getCustomerName());
        holder.status.setText("  "+pro.getDmrccardStaus().getStatusName());
        holder.cardnumber.setText("Card No. \n"+pro.getCardNo());

        if(pro.getIssueDate()!=null){
            Date date =new Date(pro.getIssueDate());
            holder.issuedate.setText("Issue Date \n"+new SimpleDateFormat("dd-MM-yyyy").format(date));
        }else {
            holder.issuedate.setText("");
        }


        if(pro.getImage()!=null){
            Picasso.with(mctx).load(pro.getImage()).fit()
                    .error(R.drawable.dmrc)
                    .placeholder(R.drawable.dmrc)
                    .into(holder.imageview);
        }else {
            holder.imageview.setImageURI(null);
        }


    }

    @Override
    public int getItemCount() {
        return productslist.size();
    }
    public class ProdectViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mainlayout;
        TextView name,cardnumber,status,issuedate;
        ImageView imageview;

        public ProdectViewHolder(View itemView) {
            super(itemView);
            mainlayout=itemView.findViewById(R.id.mainlayout);
            name=itemView.findViewById(R.id.name);
            cardnumber=itemView.findViewById(R.id.cardnumber);
            status=itemView.findViewById(R.id.status);
            issuedate=itemView.findViewById(R.id.issuedate);
            imageview=itemView.findViewById(R.id.imageview);


        }
    }
}
