package com.uav.autodebit.androidFragment;


import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;




import com.uav.autodebit.R;
import com.uav.autodebit.adpater.RecyclerViewAdapterMenu;
import com.uav.autodebit.vo.DataAdapterVO;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {


    RecyclerView bankrecyclerView,servicesrecy;
    RecyclerViewAdapterMenu recyclerViewAdapterMenu;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        /*ImageView imageView1=view.findViewById(R.id.imageView1);
        ImageView imgFloating=view.findViewById(R.id.imgFloating);*/


        ArrayList<DataAdapterVO> dataList =  getDataList();
        ArrayList<DataAdapterVO> servicestypelist =  getservicetype();
        bankrecyclerView= (RecyclerView) view.findViewById(R.id.bankrecycler);
        servicesrecy=view.findViewById(R.id.servicerecycler);



        bankrecyclerView.setHasFixedSize(true);



        /*bankrecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.HORIZONTAL));
        bankrecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        */
      //  bankrecyclerView.addItemDecoration(new DividerItemDecoration(bankrecyclerView.getContext(), DividerItemDecoration.VERTICAL));


        bankrecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        bankrecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        bankrecyclerView.setNestedScrollingEnabled(false);


       /* LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);

*/
        servicesrecy.setHasFixedSize(true);

        DividerItemDecoration dividerItemDecoration_horizontal=new DividerItemDecoration(getContext(),
                DividerItemDecoration.HORIZONTAL);

        Drawable drawable_horizontal= ContextCompat.getDrawable(getActivity(),R.drawable.horizontal_divider);
        dividerItemDecoration_horizontal.setDrawable(drawable_horizontal);

        servicesrecy.addItemDecoration(dividerItemDecoration_horizontal);

        DividerItemDecoration dividerItemDecoration_vertical=new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable drawable_vertical= ContextCompat.getDrawable(getActivity(),R.drawable.vertical_divider);
        dividerItemDecoration_vertical.setDrawable(drawable_vertical);


        servicesrecy.addItemDecoration(dividerItemDecoration_vertical);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);

        servicesrecy.setLayoutManager(new GridLayoutManager(getContext(), 3));

       /* servicesrecy.addItemDecoration(
                new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation()) {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        int position = parent.getChildAdapterPosition(view);
                        // hide the divider for the last child
                        if (position == parent.getAdapter().getItemCount() - 1) {
                            outRect.setEmpty();
                        } else {
                            super.getItemOffsets(outRect, view, parent, state);
                        }
                    }
                }
        );*/




        //servicesrecy.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        servicesrecy.setNestedScrollingEnabled(false);

        //creating recyclerview adapter
      /*  recyclerViewAdapterMenu = new RecyclerViewAdapterMenu(getActivity(), dataList,R.layout.two_tailes);
        bankrecyclerView.setAdapter(recyclerViewAdapterMenu);
*/

  /*      RecyclerViewAdapterMenu recyclerViewAdapter=new RecyclerViewAdapterMenu(getActivity(), servicestypelist,R.layout.activity_singleimage);

        servicesrecy.setAdapter(recyclerViewAdapter);
*/

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public  ArrayList<DataAdapterVO> getDataList(){
        ArrayList<DataAdapterVO> datalist = new ArrayList<>();

        DataAdapterVO dataAdapterVO = new DataAdapterVO();

        dataAdapterVO=new DataAdapterVO();
        dataAdapterVO.setText("Aviation Sales");
        dataAdapterVO.setImage(R.drawable.yesbank);
        datalist.add(dataAdapterVO);

        dataAdapterVO=new DataAdapterVO();
        dataAdapterVO.setText("Aviation Sales");
        dataAdapterVO.setAssociatedValue("ssfdsdf");
        dataAdapterVO.setImage(R.drawable.yesbank);
        datalist.add(dataAdapterVO);

        dataAdapterVO=new DataAdapterVO();
        dataAdapterVO.setText("Aviation Sales");
        dataAdapterVO.setAssociatedValue("ssfdsdf");
        dataAdapterVO.setImage(R.drawable.yesbank);
        datalist.add(dataAdapterVO);

        dataAdapterVO=new DataAdapterVO();
        dataAdapterVO.setText("Aviation Sales");
        dataAdapterVO.setAssociatedValue("ssfdsdf");
        dataAdapterVO.setImage(R.drawable.yesbank);
        datalist.add(dataAdapterVO);
        return  datalist;
    }

    public  ArrayList<DataAdapterVO> getservicetype(){
        ArrayList<DataAdapterVO> datalist = new ArrayList<>();

        DataAdapterVO dataAdapterVO = new DataAdapterVO();

        dataAdapterVO=new DataAdapterVO();
        dataAdapterVO.setImage(R.drawable.common_google_signin_btn_icon_light);
        dataAdapterVO.setText("");
        datalist.add(dataAdapterVO);

        dataAdapterVO=new DataAdapterVO();
        dataAdapterVO.setImage(R.drawable.gas_service);
        dataAdapterVO.setText("");
        datalist.add(dataAdapterVO);


        dataAdapterVO=new DataAdapterVO();
        dataAdapterVO.setImage(R.drawable.gas_service);
        dataAdapterVO.setText("");
        datalist.add(dataAdapterVO)

        ;
        dataAdapterVO=new DataAdapterVO();
        dataAdapterVO.setImage(R.drawable.gas_service);
        dataAdapterVO.setText("");;
        datalist.add(dataAdapterVO);

        dataAdapterVO=new DataAdapterVO();
        dataAdapterVO.setImage(R.drawable.gas_service);
        dataAdapterVO.setText("");
        datalist.add(dataAdapterVO);



        dataAdapterVO=new DataAdapterVO();
        dataAdapterVO.setImage(R.drawable.gas_service);
        dataAdapterVO.setText("");
        datalist.add(dataAdapterVO);




        dataAdapterVO=new DataAdapterVO();
       dataAdapterVO.setImage(R.drawable.gauge);
        dataAdapterVO.setText("");
        datalist.add(dataAdapterVO);

        dataAdapterVO=new DataAdapterVO();
        dataAdapterVO.setImage(R.drawable.telephone_service);
        dataAdapterVO.setText("");
        datalist.add(dataAdapterVO);


        dataAdapterVO=new DataAdapterVO();
        dataAdapterVO.setImage(R.drawable.mobile_service);
        dataAdapterVO.setText("");
        datalist.add(dataAdapterVO);

        dataAdapterVO=new DataAdapterVO();
        dataAdapterVO.setImage(R.drawable.wirelesssignal_service);
        dataAdapterVO.setText("");
        datalist.add(dataAdapterVO);

        return  datalist;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
