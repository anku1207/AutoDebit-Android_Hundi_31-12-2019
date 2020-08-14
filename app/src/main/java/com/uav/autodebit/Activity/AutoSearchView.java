package com.uav.autodebit.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AutoSearchView extends Base_Activity {
    private static int REQ_AUTOSEARCH = 100;


    ArrayAdapter<String> adapter;
    ArrayList<String> entityText;
    ArrayList<Integer> entityId;
    ProgressBar progressBar;
    EditText searchEditText;
    HashMap<String, Object> params;
    TextView noresult;
    android.widget.ListView listView;
    Intent activityIntent;
    ConnectionVO connectionVO;
    ImageView back_activity_button;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_search_view);
        activityIntent = getIntent();
        connectionVO = (ConnectionVO) activityIntent.getSerializableExtra(ApplicationConstant.INTENT_EXTRA_CONNECTION);


        back_activity_button=(ImageView)findViewById(R.id.back_activity_button);
        title=(TextView)findViewById(R.id.title);

        getSupportActionBar().hide();

       /* getSupportActionBar().setTitle(connectionVO.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
*/

        title.setText(connectionVO.getTitle());
        back_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView=(ListView)findViewById(R.id.autosearch);
        params=new HashMap<>();

        searchEditText = (EditText) findViewById(R.id.searchEditText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        noresult=(TextView)findViewById(R.id.noresult);
        entityText = new ArrayList<String>();
        entityId=new ArrayList<>();
        adapter = new ArrayAdapter(this, R.layout.activity_listtextview, R.id.textdata, entityText);
        listView.setAdapter(adapter);
        if(entityText.size()==0){
            noresult.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Toast.makeText(Customersearch.this, "before", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (searchEditText.length() > 2) {
                    noresult.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    searchAction();
                    //progressBar.setVisibility(View.VISIBLE);
                }else {
                    listView.setVisibility(View.GONE);
                    noresult.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                searchEditText.setText(listView.getItemAtPosition(position).toString());
                Intent intent12 = new Intent();
                intent12.putExtra("text",listView.getItemAtPosition(position).toString());
                intent12.putExtra("id",entityId.get(position).toString());
                setResult(RESULT_OK,intent12);
                finish() ;



            }

        });


    }

    public void searchAction() {

        connectionVO  = (ConnectionVO) getIntent().getSerializableExtra(ApplicationConstant.INTENT_EXTRA_CONNECTION);
        HashMap<String , Object>  params = new HashMap<String, Object>();
        params.put(connectionVO.getSearchKeyName(), searchEditText.getText().toString() );
        connectionVO.setParams(params);

        VolleyUtils.makeJsonObjectRequest(this, connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {

            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;

                if(response.get("status").equals("fail")){
                    Utility.furnishErrorMsg(  connectionVO.getTitle() ,response, AutoSearchView.this);
                    return;
                }
                searchResponseHandler(response.toString());
                progressBar.setVisibility(View.GONE);

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
        }

    }


    public void searchResponseHandler(String resp ){
        try {
            entityText.clear();
            entityId.clear();

            JSONObject response = new JSONObject(resp);
            String x= (String) response.get("data");

            JSONArray jsonArray = new JSONArray(x);
            int len = jsonArray.length();
            for(int i=0;i<len;i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                entityText.add(jsonObject.getString("value"));
                entityId.add(jsonObject.getInt("id"));
            }

        } catch (JSONException e) {

            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
