package com.example.ben.final_project.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ben.final_project.Activities.FragmentsDelegate;
import com.example.ben.final_project.Model.Car;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

public class CarDetailsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";//companyID
    private String companyID;
    private static final String ARG_PARAM2 = "param2";//carID
    private String carID;
    Car carData;

    public static CarDetailsFragment newInstance(String param1,String param2) {
        CarDetailsFragment fragment = new CarDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            companyID = getArguments().getString(ARG_PARAM1);
            carID = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TAG","CarDetailsFragment onCreateView");
        View containerView = inflater.inflate(R.layout.fragment_car_details, container, false);

        carData = Model.instance.getCar(companyID, carID);

        ImageView carPic = (ImageView) containerView.findViewById(R.id.car_details_image);
        TextView description = (TextView) containerView.findViewById(R.id.car_details_description);
        TextView engineVolume = (TextView) containerView.findViewById(R.id.car_details_engine_volume);
        TextView hp = (TextView) containerView.findViewById(R.id.car_details_horse_power);
        TextView pollution = (TextView) containerView.findViewById(R.id.car_details_pollution);
        TextView price = (TextView) containerView.findViewById(R.id.car_details_price);
        TextView warranty = (TextView) containerView.findViewById(R.id.car_details_warranty);
        TextView zeroToHundrend = (TextView) containerView.findViewById(R.id.car_details_zero_to_hundrend);
        TextView fuelConsumption = (TextView) containerView.findViewById(R.id.car_details_fuel_consumption);
        TextView category = (TextView) containerView.findViewById(R.id.car_details_category);

        carPic.setImageResource(R.drawable.car);//TODO:change to current car
        description.setText(carData.description);
        engineVolume.setText(carData.engineVolume + " ליטר");
        hp.setText(carData.hp + " כוחות סוס");
        pollution.setText(carData.pollution + " מתוך 15");
        price.setText("ILS " + carData.price);
        warranty.setText(carData.warranty + " שנות אחריות");
        zeroToHundrend.setText(carData.zeroToHundrend + " שניות");
        fuelConsumption.setText(carData.fuelConsumption + " ליטר ל - 100 קילומטר");
        category.setText(carData.carCategory);

        return containerView;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
}
