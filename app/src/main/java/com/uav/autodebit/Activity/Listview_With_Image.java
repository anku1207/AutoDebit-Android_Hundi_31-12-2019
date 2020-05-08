package com.uav.autodebit.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.ImageTextAdapter;
import com.uav.autodebit.override.ExpandableHeightListView;
import com.uav.autodebit.override.UAVProgressDialog;
import com.uav.autodebit.util.BackgroundAsyncService;
import com.uav.autodebit.util.BackgroundServiceInterface;
import com.uav.autodebit.util.ExceptionHandler;
import com.uav.autodebit.vo.DataAdapterVO;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Listview_With_Image extends Base_Activity {

    ImageView back_activity_button;
    ExpandableHeightListView listView;
    String operatorcode,operatorname;

    SearchView searchView;
    ImageTextAdapter myAdapter;
    UAVProgressDialog pd;
    ArrayList<DataAdapterVO> dataAdapterVOS;
    TextView title;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_listview_with_image);
        getSupportActionBar().hide();

        title = findViewById(R.id.title);
        searchView = findViewById(R.id.search_view);

        pd=new UAVProgressDialog(this);
        dataAdapterVOS=null;

        listView = findViewById(R.id.listview);
        listView.setExpanded(false);


        back_activity_button = findViewById(R.id.back_activity_button1);

        back_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

       setDateOnListview();

        //  listView.setNestedScrollingEnabled(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // String s=(String)parent.getItemAtPosition(position);

                int i = position;
                DataAdapterVO dataAdapterVO = (DataAdapterVO)myAdapter.dataList.get(position);

                operatorcode = dataAdapterVO.getAssociatedValue();
                operatorname = dataAdapterVO.getText();

                Intent intent = new Intent();
                intent.putExtra("operator", operatorcode);
                intent.putExtra("operatorname", operatorname);
                intent.putExtra("datavo", dataAdapterVO);

                setResult(RESULT_OK, intent);
                finish();


            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // collapse the view ?
                //menu.findItem(R.id.menu_search).collapseActionView();
                Log.e("queryText", query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // search goes here !!
                // listAdapter.getFilter().filter(query);
                Log.e("queryText", newText);
                myAdapter.getFilter().filter(newText.toString());
                return false;
            }


        });
    }

    private void setDateOnListview(){
        BackgroundAsyncService backgroundAsyncService = new BackgroundAsyncService(pd,true, new BackgroundServiceInterface() {
            @Override
            public void doInBackGround() {

                Intent intent = getIntent();
                Gson gson = new Gson();

                Type type = new TypeToken<List<DataAdapterVO>>() {}.getType();
                dataAdapterVOS = gson.fromJson(intent.getStringExtra("datalist"), type);

                title.setText(intent.getStringExtra("title"));
                myAdapter = new ImageTextAdapter(Listview_With_Image.this, dataAdapterVOS, R.layout.round_image_with_text);
                listView.setAdapter(myAdapter);


            }
            @Override
            public void doPostExecute() {

            }
        });
        backgroundAsyncService.execute();
    }

}
