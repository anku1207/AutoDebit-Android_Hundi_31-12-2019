package com.uav.autodebit.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.uav.autodebit.R;
import com.uav.autodebit.adpater.ImageTextAdapter;
import com.uav.autodebit.override.ExpandableHeightListView;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.ExceptionHandler;
import com.uav.autodebit.vo.DataAdapterVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Landline_Operator_List extends Base_Activity {
    TextView title;
    ImageView back_activity_button;
    ExpandableHeightListView listView;
    String operatorcode,operatorname;

    ArrayList<DataAdapterVO> dataAdapterVOS =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landline__operator__list);
        setContentView(R.layout.activity_listview_with_image);
        getSupportActionBar().hide();

        title=findViewById(R.id.title);
        listView=findViewById(R.id.listview);



        listView.setExpanded(true);
        title.setText("Operator");


        back_activity_button=findViewById(R.id.back_activity_button1);

        back_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        dataAdapterVOS=getDataList();
        ImageTextAdapter myAdapter=new ImageTextAdapter(this, dataAdapterVOS, R.layout.round_image_with_text);
        listView.setAdapter(myAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // String s=(String)parent.getItemAtPosition(position);
                int i=position;
                DataAdapterVO dataAdapterVO = (DataAdapterVO)dataAdapterVOS.get(position);

                operatorcode=dataAdapterVO.getAssociatedValue();
                operatorname=dataAdapterVO.getText();

                Intent intent;
                if(operatorcode.equals("BILLPayAirtellandline")){
                    intent=new Intent(Landline_Operator_List.this,LandlineBill.class);
                    intent.putExtra("key",operatorname);
                    intent.putExtra("value",operatorcode);
                    startActivity(intent);
                }else if(operatorcode.equals("BSNLLandlineCorporate")){
                    intent=new Intent(Landline_Operator_List.this,Bsnl_Landline_Corporate.class);
                    intent.putExtra("key",operatorname);
                    intent.putExtra("value",operatorcode);
                    startActivity(intent);
                }else if(operatorcode.equals("BSNLLandlineIndividual")){
                    intent=new Intent(Landline_Operator_List.this,Bsnl_Landline_Individual.class);
                    intent.putExtra("key",operatorname);
                    intent.putExtra("value",operatorcode);
                    startActivity(intent);
                }else if(operatorcode.equals("MTNLDelhi")){
                    intent=new Intent(Landline_Operator_List.this,MTNL_Landline_Delhi.class);
                    intent.putExtra("key",operatorname);
                    intent.putExtra("value",operatorcode);
                    startActivity(intent);
                }


            }
        });

    }


    public ArrayList<DataAdapterVO> getDataList(){
        ArrayList<DataAdapterVO> datalist = new ArrayList<>();
        String operator= Session.getSessionByKey(Landline_Operator_List.this,Session.CACHE_LANDLINE_OPERATOR);
        try {
            JSONArray jsonArray =new JSONArray(operator);

            Log.w("dataoperator",jsonArray.toString());
            for(int i=0;i<jsonArray.length();i++){
                DataAdapterVO dataAdapterVO = new DataAdapterVO();
                JSONObject object =jsonArray.getJSONObject(i);
                dataAdapterVO.setText(object.getString("name"));
                // dataAdapterVO.setImagename(object.getString("serviceName").toLowerCase());
                dataAdapterVO.setAssociatedValue(object.getString("serviceName"));
                datalist.add(dataAdapterVO);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  datalist;
    }


}
