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
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.uav.autodebit.R;
import com.uav.autodebit.override.UAVTextView;

import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ServiceTypeVO;

import java.util.List;

public class ListViewItemCheckboxBaseAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener{
    private Context context;
    List<ServiceTypeVO> dataList;
    private int design;
    private LayoutInflater layoutInflater;
    private int length;
    public SparseBooleanArray mCheckStates;


    public ListViewItemCheckboxBaseAdapter(Context context, List<ServiceTypeVO> dataList, int design) {
        this.context = context;
        this.dataList = dataList;
        this.design = design;
        layoutInflater = ((Activity) context).getLayoutInflater();
        this.length = dataList.size();
        mCheckStates = new SparseBooleanArray(dataList.size());
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

        ServiceTypeVO serviceTypeVO = (ServiceTypeVO) dataList.get(position);


        convertView = LayoutInflater.from(context).inflate(this.design, parent, false);

        CardView maincard=convertView.findViewById(R.id.maincard);
        LinearLayout mailmenu =convertView.findViewById(R.id.mailmenu);
        CheckBox checkBox=convertView.findViewById(R.id.list_view_item_checkbox);
        UAVTextView textView =convertView.findViewById(R.id.list_view_item_text);
        ImageView imageView =convertView.findViewById(R.id.imageview);

        textView.setText(serviceTypeVO.getTitle());
        textView.setTxtAssociatedValue(serviceTypeVO.getServiceTypeId().toString());
        textView.setText(serviceTypeVO.getTitle());

        imageView.setImageDrawable(Utility.GetImage(context,serviceTypeVO.getAppIcon()));

        checkBox.setTag(serviceTypeVO.getServiceTypeId());
        checkBox.setChecked( serviceTypeVO.getAdopted()==1 ?true:false);
        if (checkBox.isChecked()){
            checkBox.setEnabled(false); // disable checkbox
            mCheckStates.put(serviceTypeVO.getServiceTypeId(), true);
            textView.setTextColor(Utility.getColorWrapper(context,R.color.disabledTextColor));
        }else {
            mCheckStates.put(serviceTypeVO.getServiceTypeId(), false);
            textView.setTextColor(Utility.getColorWrapper(context,R.color.defaultTextColor));
        }
        checkBox.setOnCheckedChangeListener(this);

        if(serviceTypeVO.getServiceTypeId()==15) mailmenu.removeAllViewsInLayout();
        return convertView;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
      //  Toast.makeText(context, ""+compoundButton.getTag().toString(), Toast.LENGTH_SHORT).show();
        mCheckStates.put(Integer.parseInt(compoundButton.getTag().toString()), isChecked);

    }
}