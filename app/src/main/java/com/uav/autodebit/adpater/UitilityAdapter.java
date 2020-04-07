package com.uav.autodebit.adpater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.uav.autodebit.Activity.Home;
import com.uav.autodebit.R;
import com.uav.autodebit.override.UAVProgressDialog;
import com.uav.autodebit.override.UAVTextView;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.BackgroundAsyncService;
import com.uav.autodebit.util.BackgroundServiceInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ServiceTypeVO;

import java.util.List;

import static java.lang.Class.forName;


public class UitilityAdapter extends RecyclerView.Adapter<UitilityAdapter.ProdectViewHolder>  {
    Context mctx ;
    List<ServiceTypeVO> productslist;
    int Activityname;
    Activity activity;
    UAVProgressDialog pd;

    Integer level;




    public UitilityAdapter(Context mctx, List<ServiceTypeVO> productslist, int Activityname , UAVProgressDialog pd) {
        this.mctx = mctx;
        this.activity=(Activity) mctx;
        this.productslist = productslist;
        this.Activityname=Activityname;
        this.pd=pd;
    }

    @Override
    public UitilityAdapter.ProdectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(mctx);
        View view=layoutInflater.inflate(Activityname,null);
       /* ProdectViewHolder holder=new ProdectViewHolder(view);
        return holder;*/
        return new UitilityAdapter.ProdectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UitilityAdapter.ProdectViewHolder holder, int position) {

        final ServiceTypeVO pro=productslist.get(position);
        holder.textViewTitle.setText(pro.getTitle());
        holder.imageView.setImageDrawable(Utility.GetImage(mctx,pro.getAppIcon()));

        if(pro.getAdopted()==1 && pro.getServiceAdopteBMA()!=null ){
            holder.serviceactive.setVisibility(View.VISIBLE);
        }else {
            holder.serviceactive.setVisibility(View.GONE);
        }

        holder.mailmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                level=null;
                Utility.enableDisableView(v,false);
                BackgroundAsyncService backgroundAsyncService = new BackgroundAsyncService(pd,true, new BackgroundServiceInterface() {
                    @Override
                    public void doInBackGround() {
                         level= Session.getCustomerLevel(mctx);
                    }
                    @Override
                    public void doPostExecute() {
                                Intent intent;
                               /* switch (pro.getServiceTypeId()){
                                    case 5 :*/

                                        ((Home)mctx).startUserClickService(pro.getServiceTypeId().toString(),v);

                                       /* ((Home)mctx).serviceClick(pro.getServiceTypeId(),new ServiceClick((ServiceClick.OnSuccess)(s)->{
                                            CustomerVO customerVO =(CustomerVO) s;
                                            ((Home)mctx).startActivityServiceClick(5,Mobile_Prepaid_Recharge_Service.class,s,pro.getMandateAmount());
                                        },(ServiceClick.OnError)(e)->{

                                        }));*/
                                  /*      break;
                                    case 13 :
                                        ((Home)mctx).serviceClick(pro.getServiceTypeId(),new ServiceClick((ServiceClick.OnSuccess)(s)->{
                                            ((Home)mctx).startActivityServiceClick(13,DTH_Recharge_Service.class,s,pro.getMandateAmount());
                                        },(ServiceClick.OnError)(e)->{

                                        }));
                                        break;
                                    case 7 :
                                        ((Home)mctx).serviceClick(pro.getServiceTypeId(),new ServiceClick((ServiceClick.OnSuccess)(s)->{
                                            ((Home)mctx).startActivityServiceClick(7,LandlineBill.class,s,pro.getMandateAmount());
                                        },(ServiceClick.OnError)(e)->{

                                        }));
                                        break;
                                    case 8 :
                                        ((Home)mctx).serviceClick(pro.getServiceTypeId(),new ServiceClick((ServiceClick.OnSuccess)(s)->{
                                            ((Home)mctx).startActivityServiceClick(8,Broadband.class,s,pro.getMandateAmount());
                                        },(ServiceClick.OnError)(e)->{

                                        }));

                                        break;
                                    case 14 :
                                        ((Home)mctx).serviceClick(pro.getServiceTypeId(),new ServiceClick((ServiceClick.OnSuccess)(s)->{
                                            ((Home)mctx).startActivityServiceClick(14,Mobile_Postpaid.class,s,pro.getMandateAmount());
                                        },(ServiceClick.OnError)(e)->{

                                        }));

                                        break;
                                    case 12 :
                                        ((Home)mctx).serviceClick(pro.getServiceTypeId(),new ServiceClick((ServiceClick.OnSuccess)(s)->{
                                            ((Home)mctx).startActivityServiceClick(12,Water.class,s,pro.getMandateAmount());
                                        },(ServiceClick.OnError)(e)->{

                                        }));

                                        break;
                                    case 11 :
                                        ((Home)mctx).serviceClick(pro.getServiceTypeId(),new ServiceClick((ServiceClick.OnSuccess)(s)->{
                                            ((Home)mctx).startActivityServiceClick(11,Gas_Bill.class,s,pro.getMandateAmount());
                                        },(ServiceClick.OnError)(e)->{

                                        }));
                                        break;
                                    case 10:
                                        ((Home)mctx).serviceClick(pro.getServiceTypeId(),new ServiceClick((ServiceClick.OnSuccess)(s)->{
                                            ((Home)mctx).startActivityServiceClick(10,Electricity_Bill.class,s,pro.getMandateAmount());
                                        },(ServiceClick.OnError)(e)->{

                                        }));
                                        break;
                                    case 15:
                                        intent =new Intent(mctx, All_Service.class);
                                        intent.putExtra("serviceid",pro.getServiceTypeId().toString());
                                        mctx.startActivity(intent);
                                        break;
                                    default:
                                        pd.dismiss();
                                }*/
                    }
                });
                backgroundAsyncService.execute();

            }
        });
    }






    @Override
    public int getItemCount() {
        return productslist.size();
    }



    public class ProdectViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mailmenu;
        ImageView serviceactive,imageView;
        UAVTextView textViewTitle;


        public ProdectViewHolder(View itemView) {
            super(itemView);
            serviceactive=itemView.findViewById(R.id.serviceactive);
            imageView=itemView.findViewById(R.id.imageView);
            textViewTitle=itemView.findViewById(R.id.textViewTitle);
            mailmenu=itemView.findViewById(R.id.mailmenu);

        }
    }



}
