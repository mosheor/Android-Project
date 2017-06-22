package com.example.ben.final_project.Fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ben.final_project.Model.Car;
import com.example.ben.final_project.Model.CarCompany;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

import java.util.LinkedList;
import java.util.List;

public class CarListSearchFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";//category
    private String mParam1;
    private static final String ARG_PARAM2 = "param2";//company id
    private String mParam2;
    private static final String ARG_PARAM3 = "param3";//engine volume condition
    private String mParam3;
    private static final String ARG_PARAM4 = "param4";//hp condition
    private String mParam4;

    ListView list;
    List<Car> carsData = new LinkedList<Car>();
    CarListAdapter adapter;
    CarListSearchFragmentDelegate listener;

    public static CarListSearchFragment newInstance(String param1,String param2,String param3,String param4) {
        CarListSearchFragment fragment = new CarListSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getString(ARG_PARAM4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TAG","CarListSearchFragment onCreateView");
        View containerView = inflater.inflate(R.layout.fragment_cars_list, container, false);

        List<Car> temp = new LinkedList<Car>();
        if(mParam2 !=null) {//companyid !=null ==> specefic company
            for (CarCompany company : Model.instance.getAllCompanies())
                if (company.id.compareTo(mParam2) == 0)
                    temp = Model.instance.getCompanyModels(mParam2);
        }
        else {//All companies
            for (CarCompany company : Model.instance.getAllCompanies())
                for (Car car : Model.instance.getCompanyModels(company.id))
                    temp.add(car);
        }

        for(int i=0; i<temp.size();i++){
            if(checkCategoryCondition(temp.get(i).carCategory,mParam1))
                if(checkEngineVolumeCondition(temp.get(i).engineVolume,mParam3))
                    if(checkHpCondition(temp.get(i).hp,mParam4))
                        carsData.add(temp.get(i));
        }

        TextView companyNameTW = (TextView) containerView.findViewById(R.id.car_list_company_name);
        ImageView companyImage = (ImageView) containerView.findViewById(R.id.car_list_company_image);
        companyNameTW.setText("תוצאות חיפוש");
        companyNameTW.setTextSize(25);
        companyImage.setVisibility(View.GONE);

        //TODO: if there is no cars do popup

        adapter = new CarListAdapter();
        adapter.setInflater(inflater);
        list = (ListView) containerView.findViewById(R.id.frag_cars_list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.selectCarDetilesClick(carsData.get(position).companyID,carsData.get(position).carID);
            }
        });

        return containerView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CarListSearchFragmentDelegate) {
            listener = (CarListSearchFragmentDelegate) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface CarListSearchFragmentDelegate{
        void selectCarDetilesClick(String companyId,String carId);
    }

    public void setDelegate(CarListSearchFragmentDelegate listener){
        this.listener = listener;
    }

    class CarListAdapter extends BaseAdapter {
        public LayoutInflater inflater;

        public void setInflater(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Override
        public int getCount() {
            return carsData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.company_and_car_list_row,null);
            }

            ImageView companyLogo = (ImageView) convertView.findViewById(R.id.row_company_or_car_logo);
            TextView companyName = (TextView) convertView.findViewById(R.id.row_company_or_car_name);

            Car car= carsData.get(position);
            companyLogo.setImageResource(R.drawable.car);
            companyName.setText(car.companName + " - " + car.modelName);
            companyName.setTextSize(20);

            Log.d("TAG","car num" + position);
            return convertView;
        }
    }

    public void notifyAdapter(){
        adapter.notifyDataSetChanged();
    }

    boolean checkHpCondition(int carHp,String condition){
        if(condition.compareTo("0") == 0) {
            return true;
        }
        else if(condition.compareTo("1") == 0) {
            if (carHp < 100)
                return true;
        }
        else if(condition.compareTo("2") == 0) {
            if (carHp >= 100 && carHp < 150)
                return true;
        }
        else if(condition.compareTo("3") == 0) {
            if (carHp >= 150 && carHp < 250)
                return true;
        }
        else if(condition.compareTo("4") == 0) {
            if (carHp >= 250)
                return true;
        }
        return false;
    }

    boolean checkEngineVolumeCondition(int carEngineVolume,String condition){
        if(condition.compareTo("0") == 0) {
            return true;
        }
        else if(condition.compareTo("1") == 0) {
            if (carEngineVolume < 1000)
                return true;
        }
        else if(condition.compareTo("2") == 0) {
            if (carEngineVolume >= 1000 && carEngineVolume < 1500)
                return true;
        }
        else if(condition.compareTo("3") == 0) {
            if (carEngineVolume >= 1500 && carEngineVolume < 2000)
                return true;
        }
        else if(condition.compareTo("4") == 0) {
            if (carEngineVolume >= 2500)
                return true;
        }
        return false;
    }

    boolean checkCategoryCondition(String category,String filterCategory){
       if(filterCategory == null)
           return true;
        else if(filterCategory.compareTo(category) == 0)
            return true;
        return false;
    }
}
