package com.uav.autodebit.adpater;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.uav.autodebit.R;
import com.uav.autodebit.override.UAVTextView;
import com.uav.autodebit.vo.DataAdapterVO;
import com.uav.autodebit.vo.ServiceTypeVO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class EmandateServicesAdapter    extends RecyclerView.Adapter<EmandateServicesAdapter.ServiceViewHolder> {

    Context mctx;
    List<ServiceTypeVO> servicesList;
    ViewGroup vg;

    boolean imageset=true;

    public EmandateServicesAdapter(Context mctx, List<ServiceTypeVO> servicesList, boolean imageset) {
        this.mctx = mctx;
        this.servicesList = servicesList;
        this.imageset=imageset;
    }

    @Override
    public EmandateServicesAdapter.ServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        vg = parent;
        LayoutInflater layoutInflater= LayoutInflater.from(mctx);
        View view=layoutInflater.inflate(R.layout.layout_serviceslist,parent,false);
       /* ProdectViewHolder holder=new ProdectViewHolder(view);
        return holder;*/
        return new EmandateServicesAdapter.ServiceViewHolder(view);
    }



    @Override
    public void onBindViewHolder(final EmandateServicesAdapter.ServiceViewHolder holder,  int position) {

        final ServiceTypeVO serviceTypeVO =servicesList.get(position);

        holder.title.setText(serviceTypeVO.getTitle());
        holder.title.setTxtAssociatedValue(serviceTypeVO.getServiceTypeId().toString());

        int imageResource = mctx.getResources().getIdentifier( serviceTypeVO.getAppIcon(), null, mctx.getPackageName());
        Drawable res = mctx.getResources().getDrawable(imageResource);
        holder.icon.setImageDrawable(res);
    }

    @Override
    public int getItemCount() {
        return servicesList.size();
    }





    public class ServiceViewHolder extends RecyclerView.ViewHolder {
        @Override
        protected void finalize() throws Throwable {
            super.finalize();
        }
        UAVTextView title;
        ImageView icon;
        public ServiceViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            icon = itemView.findViewById(R.id.icon);

           itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // Intent intent = new Intent(mctx, PrintInvoice.class);
                  //  intent.putExtra("posSaleId", posSaleId.getText().toString());
                   // mctx.startActivity(intent);
                }
            });


        }


    }

}
