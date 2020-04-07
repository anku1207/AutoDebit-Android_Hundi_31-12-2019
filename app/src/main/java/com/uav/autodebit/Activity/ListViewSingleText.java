package com.uav.autodebit.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.vo.ConnectionVO;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class ListViewSingleText extends Base_Activity {

    ArrayList<String> entityText;
    ArrayList<Object> entityId;
    ProgressBar progressBar;
    ArrayAdapter<String> adapter;
    Intent activityIntent;
    ConnectionVO connectionVO;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        activityIntent = getIntent();
        connectionVO = (ConnectionVO) activityIntent.getSerializableExtra(ApplicationConstant.INTENT_EXTRA_CONNECTION);



        ImageView back_activity_button=(ImageView)findViewById(R.id.back_activity_button);
        TextView title=(TextView)findViewById(R.id.title);
        getSupportActionBar().hide();

        title.setText(connectionVO.getTitle());
        back_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        sharedPreferences = getSharedPreferences(ApplicationConstant.SHAREDPREFENCE,  Context.MODE_PRIVATE);


        String jsonString= (String)sharedPreferences.getString( connectionVO.getSharedPreferenceKey(),null);



        entityText = new ArrayList<String>();
        entityId = new ArrayList<Object>();



        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray= jsonObject.getJSONArray("dataList");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                entityText.add(jsonObject1.getString(connectionVO.getEntityTextKey() ));
                entityId.add(jsonObject1.get(connectionVO.getEntityIdKey()  ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collections.sort(entityText);

        final ListView listView=(ListView)findViewById(R.id.single_listview);
        adapter = new ArrayAdapter(this, R.layout.activity_listtextview, R.id.textdata, entityText);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent();
                intent.putExtra("valueName",listView.getItemAtPosition(i).toString());
                intent.putExtra("valueId",entityId.get(i).toString());
                setResult(RESULT_OK,intent);
                finish() ;

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
