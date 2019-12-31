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

import com.squareup.picasso.Picasso;
import com.uav.autodebit.R;
import com.uav.autodebit.override.UAVTextView;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.DataAdapterVO;


import java.util.ArrayList;

public class ImageTextAdapter extends BaseAdapter implements Filterable {
    private Context context;
    public ArrayList<DataAdapterVO> dataList;
    private int design;
    private LayoutInflater layoutInflater;
    private int length;

    private ArrayList<DataAdapterVO> originalList;
    private ValueFilter filter;


    public ImageTextAdapter(Context context, ArrayList<DataAdapterVO> dataList, int design){
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
            ImageView imageView=(ImageView)convertView.findViewById(R.id.listimage);
            UAVTextView textView=(UAVTextView) convertView.findViewById(R.id.listtext);

            if(dataAdapterVO.getImagename()!=null && Utility.GetImage(context,dataAdapterVO.getImagename())!=null){
                imageView.setImageDrawable(Utility.GetImage(context,dataAdapterVO.getImagename()));
            }else if(dataAdapterVO.getImageUrl()!=null){
                Picasso.with(context)
                        .load(dataAdapterVO.getImageUrl())
                        .placeholder(R.drawable.loadimage)
                        .error(R.drawable.loadimage).into(imageView)
                        ;
            }
            textView.setTxtAssociatedValue( dataAdapterVO.getAssociatedValue());

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

   /* private class DataAdapterFilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if(constraint != null && constraint.toString().length() > 0)
            {
                ArrayList<DataAdapterVO> filteredItems = new ArrayList<DataAdapterVO>();

                for(int i = 0, l = originalList.size(); i < l; i++)
                {
                    DataAdapterVO country = originalList.get(i);
                    if(country.getText().toLowerCase().contains(constraint))
                        filteredItems.add(country);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            }
            else
            {
                synchronized(this)
                {
                    result.values = originalList;
                    result.count = originalList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            dataList=(ArrayList<DataAdapterVO>) results.values;
            notifyDataSetChanged();
        }
    }*/
}
