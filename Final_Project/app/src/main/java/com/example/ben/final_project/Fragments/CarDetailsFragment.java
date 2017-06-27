package com.example.ben.final_project.Fragments;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ben.final_project.Model.Car;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

import static android.view.View.GONE;

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

        final ImageView carPic = (ImageView) containerView.findViewById(R.id.car_details_image);
        final TextView description = (TextView) containerView.findViewById(R.id.car_details_description);
        final TextView engineVolume = (TextView) containerView.findViewById(R.id.car_details_engine_volume);
        final TextView hp = (TextView) containerView.findViewById(R.id.car_details_horse_power);
        final TextView pollution = (TextView) containerView.findViewById(R.id.car_details_pollution);
        final TextView price = (TextView) containerView.findViewById(R.id.car_details_price);
        final TextView warranty = (TextView) containerView.findViewById(R.id.car_details_warranty);
        final TextView zeroToHundrend = (TextView) containerView.findViewById(R.id.car_details_zero_to_hundrend);
        final TextView fuelConsumption = (TextView) containerView.findViewById(R.id.car_details_fuel_consumption);
        final TextView category = (TextView) containerView.findViewById(R.id.car_details_category);
        final ProgressBar progressBar = (ProgressBar) containerView.findViewById(R.id.car_details_progressBar);
        progressBar.setVisibility(GONE);

        Model.instance.getCar(companyID, carID, new Model.GetModelCallback() {
            @Override
            public void onComplete(Car car) {
                carData = car;
                description.setText(carData.description);
                engineVolume.setText(carData.engineVolume + " ליטר");
                hp.setText(carData.hp + " כוחות סוס");
                pollution.setText(carData.pollution + " מתוך 15");
                price.setText("ILS " + carData.price);
                warranty.setText(carData.warranty + " שנות אחריות");
                zeroToHundrend.setText(carData.zeroToHundred + " שניות");
                fuelConsumption.setText(carData.fuelConsumption + " ליטר ל - 100 קילומטר");
                category.setText(carData.carCategory);

                progressBar.setVisibility(View.VISIBLE);
                Model.instance.getImage(car.carPicture, new Model.GetImageListener() {
                    @Override
                    public void onSuccess(Bitmap imageLoad) {
                        carPic.setImageBitmap(imageLoad);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFail() {

                    }
                });
            }

            @Override
            public void onCancel() {

            }
        });


        return containerView;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
}
