package com.uav.autodebit.adpater;

import android.app.Activity;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uav.autodebit.R;
import com.uav.autodebit.override.UAVTextView;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.CustomerAuthServiceVO;
import com.uav.autodebit.vo.ServiceTypeVO;

import java.util.ArrayList;
import java.util.List;

public class ListViewAlertSelectListBaseAdapter extends BaseAdapter {
    private Context context;
    List<CustomerAuthServiceVO> dataList;
    private int design;
    private LayoutInflater layoutInflater;
    private int length;
    public SparseBooleanArray mCheckStates;
    private Integer selectServiceTypeId;

    public ListViewAlertSelectListBaseAdapter(Context context, ArrayList<CustomerAuthServiceVO> dataList, int design) {
        this.context = context;
        this.dataList = dataList;
        this.design = design;
        layoutInflater = ((Activity) context).getLayoutInflater();
        this.length = dataList.size();
        mCheckStates = new SparseBooleanArray(dataList.size());
        this.selectServiceTypeId=selectServiceTypeId;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return  position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CustomerAuthServiceVO customerAuthServiceVO = (CustomerAuthServiceVO) dataList.get(position);

        convertView = LayoutInflater.from(context).inflate(this.design, parent, false);

        LinearLayout mailmenu=convertView.findViewById(R.id.mainlayout);
        TextView textView1=convertView.findViewById(R.id.text1);
        TextView textView2 =convertView.findViewById(R.id.text2);
        TextView textView3 =convertView.findViewById(R.id.text3);

        textView1.setText(customerAuthServiceVO.getBankName()!=null?customerAuthServiceVO.getBankName():"");
        textView2.setText(customerAuthServiceVO.getAccountNumber()!=null?customerAuthServiceVO.getAccountNumber():"");
        textView3.setText(customerAuthServiceVO.getAnonymousString()!=null?customerAuthServiceVO.getAnonymousString()+"":"");

        if(customerAuthServiceVO.getCustomerAuthId()==0){
            textView2.setTextColor(context.getResources().getColorStateList(R.color.appbar));
        }


        return convertView;
    }

}