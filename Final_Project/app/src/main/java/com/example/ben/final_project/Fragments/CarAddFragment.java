package com.example.ben.final_project.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.ben.final_project.Activities.FragmentsDelegate;
import com.example.ben.final_project.Model.Car;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

import static com.example.ben.final_project.Activities.CarCatalogActivity.CATALOG_CAR_ADD;

public class CarAddFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";//company id
    private static final String ARG_PARAM2 = "param2";//last car id
    private String companyId;
    private String carId;
    private FragmentsDelegate listener;

    public static CarAddFragment newInstance(String param1, String param2) {
        CarAddFragment fragment = new CarAddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void setDelegate(FragmentsDelegate listener){
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            companyId = getArguments().getString(ARG_PARAM1);
            carId = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TAG","CarAddFragment onCreateView");
        View containerView = inflater.inflate(R.layout.fragment_add_car, container, false);
        final String companyID = Model.instance.getAllCompanies().get(Integer.parseInt(companyId)).id;
        final String newCarID;
        if(Integer.parseInt(carId) > 0)
            newCarID = Model.instance.getAllCompanies().get(Integer.parseInt(carId) - 1).id + 1;
        else
            newCarID = "0";

        Button saveButton = (Button) containerView.findViewById(R.id.add_model_save_button);
        Button cancelButton = (Button) containerView.findViewById(R.id.add_model_cancel_button);
        final EditText companyName = (EditText) containerView.findViewById(R.id.add_car_company_name);
        final EditText carDescription = (EditText) containerView.findViewById(R.id.add_car_description);
        final EditText fuelConsumption = (EditText) containerView.findViewById(R.id.add_car_fuel_consumption);
        final EditText hp = (EditText) containerView.findViewById(R.id.add_car_hp);
        final EditText picture = (EditText) containerView.findViewById(R.id.add_car_image);
        final EditText carName = (EditText) containerView.findViewById(R.id.add_car_name);
        final EditText pollusion = (EditText) containerView.findViewById(R.id.add_car_pollusion);
        final EditText price = (EditText) containerView.findViewById(R.id.add_car_price);
        final EditText warranty = (EditText) containerView.findViewById(R.id.add_car_warranty);
        final EditText zeroToHundrend = (EditText) containerView.findViewById(R.id.add_car_zero_to_hundrend);
        final EditText engineVolume = (EditText) containerView.findViewById(R.id.add_car_engine_volume);
        final Spinner category = (Spinner) containerView.findViewById(R.id.add_car_category);

        companyName.setText(Model.instance.getCompany(companyID).name);
        companyName.setEnabled(false);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.car_category_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categoryAdapter);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","CarAddFragment Btn Save click");

                int ok = 0;
                boolean save = true;

                ok += valied(carDescription,"Description is required!");//TODO:write errors in hebrew
                ok += valied(picture,"Car picture logo is required!");
                ok += valied(carName,"Car name is required!");
                ok += valied(fuelConsumption,"Fuel consumption is required!");
                ok += valied(hp,"Horse powers is required!");
                ok += valied(pollusion,"Pollusion is required!");
                ok += valied(price,"Car price is required!");
                ok += valied(warranty,"Car warranty is required!");
                ok += valied(zeroToHundrend,"0-100 is required!");
                ok += valied(engineVolume,"Engine volume is required!");
                ok += checkIfInteger(fuelConsumption);
                ok += checkIfInteger(hp);
                ok += checkIfInteger(pollusion);
                ok += checkIfInteger(price);
                ok += checkIfInteger(warranty);
                ok += checkIfFloat(zeroToHundrend);
                ok += checkIfInteger(engineVolume);

                if(ok != 17)
                    save = false;

                if(save == true) {
                    Car car = new Car();
                    car.carID = newCarID;
                    car.modelName = carName.getText().toString();
                    car.hp = Integer.parseInt(hp.getText().toString());
                    car.pollution = Integer.parseInt(pollusion.getText().toString());
                    car.fuelConsumption = Float.parseFloat(fuelConsumption.getText().toString());
                    car.zeroToHundrend = Float.parseFloat(zeroToHundrend.getText().toString());
                    car.carPicture = picture.getText().toString();
                    car.companyName = companyName.getText().toString();
                    car.companyID = companyID;
                    car.description = carDescription.getText().toString();
                    car.engineVolume = Integer.parseInt(engineVolume.getText().toString());
                    car.warranty = Integer.parseInt(warranty.getText().toString());
                    car.price = Float.parseFloat(price.getText().toString());
                    car.carCategory = category.getSelectedItem().toString();
                    Model.instance.addNewModel(car.companyID,car);

                    listener.onAction(CATALOG_CAR_ADD,null);
                }
                else{
                    Log.d("TAG","CarAddFragment Cant save new company");
                }

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","AddACarFragment Btn Cancle click");
                listener.onAction(CATALOG_CAR_ADD,null);
            }
        });

        return containerView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FragmentsDelegate) {
            listener = (FragmentsDelegate) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);

        if (context instanceof FragmentsDelegate) {
            listener = (FragmentsDelegate) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public int valied(EditText et, String errorMessage){
        if(et.getText().toString().length() == 0){
            et.setError(errorMessage);
            return 0;
        }
        else
            return 1;
    }

    public int checkIfInteger(EditText et){
        try{
            if(Integer.parseInt(et.getText().toString()) > 0)
                return 1;
            else {
                et.setError("Please insert positive integer");
                et.setText("");
                return 0;
            }
        } catch(NumberFormatException s){
            et.setError("Please insert integer");
            return 0;
        }
    }

    public int checkIfFloat(EditText et){
        try{
            if(Float.parseFloat(et.getText().toString()) > 0)
                return 1;
            else {
                et.setError("Please insert positive integer");
                et.setText("");
                return 0;
            }
        } catch(NumberFormatException s){
            et.setError("Please insert integer");
            return 0;
        }
    }
}