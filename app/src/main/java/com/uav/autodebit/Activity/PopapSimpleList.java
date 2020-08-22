package com.uav.autodebit.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.ImageTextAdapter;
import com.uav.autodebit.adpater.SimpleTextAdapterList;
import com.uav.autodebit.override.ExpandableHeightListView;
import com.uav.autodebit.override.UAVProgressDialog;
import com.uav.autodebit.util.BackgroundAsyncService;
import com.uav.autodebit.util.BackgroundServiceInterface;
import com.uav.autodebit.util.ExceptionHandler;
import com.uav.autodebit.vo.DataAdapterVO;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PopapSimpleList extends Base_Activity {


    ExpandableHeightListView listView;
    String operatorcode,operatorname;

    SearchView searchView;
    SimpleTextAdapterList myAdapter;
    UAVProgressDialog pd;
    ArrayList<DataAdapterVO> dataAdapterVOS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popap_simple_list);



        DisplayMetrics displayMetrics =new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width=displayMetrics.widthPixels;
        int height=displayMetrics.heightPixels;

        getWindow().setLayout((int)(width*.9),(int)(height*.7));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params =getWindow().getAttributes();
        params.gravity = Gravity.CENTER;

        int x = getIntent().getIntExtra("x",0);
        int y = getIntent().getIntExtra("y",0);
        params.x=0;
        params.y=y;
        getWindow().setAttributes(params);

        searchView = findViewById(R.id.search_view);
        pd=new UAVProgressDialog(this);
        dataAdapterVOS=null;
        listView = findViewById(R.id.listview);
        listView.setExpanded(false);

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
                myAdapter = new SimpleTextAdapterList(PopapSimpleList.this, dataAdapterVOS, R.layout.activity_listtextview);
                listView.setAdapter(myAdapter);
            }
            @Override
            public void doPostExecute() {
            }
        });
        backgroundAsyncService.execute();
    }

}
