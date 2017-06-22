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

import java.util.List;

public class CarListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";//company id
    private String mParam1;

    ListView list;
    List<Car> carsData;
    CarCompany company;
    CarListAdapter adapter;
    CarListFragmentDelegate listener;

    public static CarListFragment newInstance(String param1) {
        CarListFragment fragment = new CarListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TAG","CompanyListFragment onCreateView");
        View containerView = inflater.inflate(R.layout.fragment_cars_list, container, false);

        company = Model.instance.getCompany(mParam1);
        carsData = Model.instance.getCompanyModels(mParam1);

        Log.d("TAG","cars size = " + carsData.size());

        adapter = new CarListAdapter();
        adapter.setInflater(inflater);

        TextView companyNameTW = (TextView) containerView.findViewById(R.id.car_list_company_name);
        ImageView companyImage = (ImageView) containerView.findViewById(R.id.car_list_company_image);
        list = (ListView) containerView.findViewById(R.id.frag_cars_list);
        list.setAdapter(adapter);

        companyNameTW.setText(company.name);
        companyImage.setImageResource(R.drawable.bugatti);//TODO:change to current company

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.selectCarDetilesClick(carsData.get(position).carID);
            }
        });

        return containerView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CarListFragmentDelegate) {
            listener = (CarListFragmentDelegate) context;
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

    public interface CarListFragmentDelegate{
        void selectCarDetilesClick(String id);
    }

    public void setDelegate(CarListFragmentDelegate listener){
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
            companyName.setText(car.modelName);

            Log.d("TAG","car num" + position);
            return convertView;
        }
    }

    public void notifyAdapter(){
        adapter.notifyDataSetChanged();
    }


}
