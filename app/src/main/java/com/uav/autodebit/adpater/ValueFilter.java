package com.uav.autodebit.adpater;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Filter;
import android.widget.Toast;

import com.uav.autodebit.vo.DataAdapterVO;

import java.util.ArrayList;

public class ValueFilter extends Filter {

    ArrayList<DataAdapterVO> dataList;
    ArrayList<DataAdapterVO> originalList;
    searchInterface searchInterface;




    public ValueFilter(ArrayList<DataAdapterVO> dataList,searchInterface searchInterface) {
        this.dataList=dataList;
        this.originalList=dataList;
        this.searchInterface=searchInterface;
    }

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

        searchInterface.newList(dataList);
        Log.w("sdfs","sdfsdf");
        //notifyDataSetChanged();
    }

    interface searchInterface{
        void newList(ArrayList<DataAdapterVO> dataAdapterVOS);
    }
}
