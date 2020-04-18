package com.uav.autodebit.adpater;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uav.autodebit.R;
import com.uav.autodebit.override.UAVTextView;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.DataAdapterVO;

import java.util.ArrayList;

public class SimpleTextAdapterList  extends BaseAdapter implements Filterable {

    private Context context;
    public ArrayList<DataAdapterVO> dataList;
    private int design;
    private LayoutInflater layoutInflater;
    private int length;

    private ArrayList<DataAdapterVO> originalList;
    private ValueFilter filter;


    public SimpleTextAdapterList(Context context, ArrayList<DataAdapterVO> dataList, int design){
        this.context=context;
        this.dataList = dataList;
        this.design = design;
        layoutInflater=((Activity)context).getLayoutInflater();
        this.length = dataList.size();

        this.originalList =dataList;

    }
    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(context).
                inflate(this.design, parent, false);
        DataAdapterVO  dataAdapterVO = (DataAdapterVO)dataList.get(position);
        if(dataAdapterVO!=null){
            TextView textView=(TextView) convertView.findViewById(R.id.textdata);
            textView.setText( dataAdapterVO.getText());
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {

        if (filter == null){
            filter  = new ValueFilter(originalList, new ValueFilter.searchInterface() {
                @Override
                public void newList(ArrayList<DataAdapterVO> dataAdapterVOS) {
                    dataList=dataAdapterVOS;
                    notifyDataSetChanged();
                }
            });
        }
        return filter;
    }
}


