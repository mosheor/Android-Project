package com.example.ben.final_project.Fragments;


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

import com.example.ben.final_project.Model.Car;
import com.example.ben.final_project.Model.CarCompany;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

public class EditCarFragment extends Fragment {

    public final static int RESULT_SUCCESS_DELETE = 2;
    public final static int RESULT_SUCCESS_EDIT = 1;
    public final static int RESULT_FAIL = 0;

    private static final String ARG_PARAM1 = "param1";//company id
    private String mParam1;
    private static final String ARG_PARAM2 = "param2";//car id
    private String mParam2;
    Car car;

    private EditCarFragmentDelegate listener;

    public static EditCarFragment newInstance(String param1,String param2) {
        EditCarFragment fragment = new EditCarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void setDelegate(EditCarFragmentDelegate listener){
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TAG","EditCarFragment onCreateView");
        View containerView = inflater.inflate(R.layout.fragment_edit_car, container, false);

        final String companyID = mParam1;
        final String carID = mParam2;

        Log.d("TAG","company id = " + companyID );
        Log.d("TAG","new car id = " + carID );

        Button saveButton = (Button) containerView.findViewById(R.id.edit_car_save_button);
        Button cancelButton = (Button) containerView.findViewById(R.id.edit_car_cancel_button);
        Button deleteButton = (Button) containerView.findViewById(R.id.edit_car_delete_button);
        final EditText companyName = (EditText) containerView.findViewById(R.id.edit_car_company_name);
        final EditText carDescription = (EditText) containerView.findViewById(R.id.edit_car_description);
        final EditText fuelConsumption = (EditText) containerView.findViewById(R.id.edit_car_fuel_consumption);
        final EditText hp = (EditText) containerView.findViewById(R.id.edit_car_hp);
        final EditText picture = (EditText) containerView.findViewById(R.id.edit_car_image);
        final EditText carName = (EditText) containerView.findViewById(R.id.edit_car_name);
        final EditText pollusion = (EditText) containerView.findViewById(R.id.edit_car_pollusion);
        final EditText price = (EditText) containerView.findViewById(R.id.edit_car_price);
        final EditText warranty = (EditText) containerView.findViewById(R.id.edit_car_warranty);
        final EditText zeroToHundrend = (EditText) containerView.findViewById(R.id.edit_car_zero_to_hundrend);
        final EditText engineVolume = (EditText) containerView.findViewById(R.id.edit_car_engine_volume);
        final Spinner category = (Spinner) containerView.findViewById(R.id.edit_car_category);

        car = Model.instance.getModel(companyID,carID);

        companyName.setText(car.companName);
        companyName.setEnabled(false);
        carName.setText(car.modelName);
        carDescription.setText(car.description);
        fuelConsumption.setText(String.valueOf(car.fuelConsumption));
        hp.setText(String.valueOf(car.hp));
        picture.setText(String.valueOf(car.carPicture));
        pollusion.setText(String.valueOf(car.pollution));
        price.setText(String.valueOf(car.price));
        warranty.setText(String.valueOf(car.warranty));
        zeroToHundrend.setText(String.valueOf(car.zeroToHundrend));
        engineVolume.setText(String.valueOf(car.engineVolume));

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.car_category_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categoryAdapter);

        for(int i=0;i<categoryAdapter.getCount();i++){
            if(car.carCategory.compareTo(categoryAdapter.getItem(i).toString()) == 0)
                category.setSelection(i);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","EditCompanyFragment Btn Save click");

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
                else{
                    if(carDescription.getText().toString().compareTo(car.description) == 0)
                        if(picture.getText().toString().compareTo(car.carPicture) == 0)
                            if(carName.getText().toString().compareTo(car.modelName) == 0)
                                if(fuelConsumption.getText().toString().compareTo(String.valueOf(car.fuelConsumption)) == 0)
                                    if(hp.getText().toString().compareTo(String.valueOf(car.hp)) == 0)
                                        if(pollusion.getText().toString().compareTo(String.valueOf(car.pollution)) == 0)
                                            if(price.getText().toString().compareTo(String.valueOf(car.price)) == 0)
                                                if(warranty.getText().toString().compareTo(String.valueOf(car.warranty)) == 0)
                                                    if(zeroToHundrend.getText().toString().compareTo(String.valueOf(car.zeroToHundrend)) == 0)
                                                        if(engineVolume.getText().toString().compareTo(String.valueOf(car.engineVolume)) == 0)
                                                            if(category.getSelectedItem().toString().compareTo(String.valueOf(car.carCategory)) == 0)
                                                                save = false;

                }

                if(save == true) {
                    Car car = new Car();
                    car.carID = carID;
                    car.modelName = carName.getText().toString();
                    car.hp = Integer.parseInt(hp.getText().toString());
                    car.pollution = Integer.parseInt(pollusion.getText().toString());
                    car.fuelConsumption = Integer.parseInt(fuelConsumption.getText().toString());
                    car.zeroToHundrend = Integer.parseInt(zeroToHundrend.getText().toString());
                    car.carPicture = picture.getText().toString();
                    car.companName = companyName.getText().toString();
                    car.companyID = companyID;
                    car.description = carDescription.getText().toString();
                    car.engineVolume = Integer.parseInt(engineVolume.getText().toString());
                    car.warranty = Integer.parseInt(warranty.getText().toString());
                    car.price = Integer.parseInt(price.getText().toString());
                    car.carCategory = category.getSelectedItem().toString();

                    if(Model.instance.editModel(companyID,car))
                        listener.returnEditCarResult(RESULT_SUCCESS_EDIT);
                }
                else{
                    Log.d("TAG","EditCompanyFragment did not save edited article");
                    //listener.returnEditCompanyResult(RESULT_FAIL);
                }

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","EditCompanyFragment Btn Cancle click");
                listener.returnEditCarResult(RESULT_FAIL);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Model.instance.removeModel(companyID,carID) == true){
                    Log.d("TAG","EditCarFragment Delete car");
                    listener.returnEditCarResult(RESULT_SUCCESS_DELETE);
                }
                else{
                    Log.d("TAG","EditCarFragment Delete car did not succed");
                    listener.returnEditCarResult(RESULT_FAIL);
                }
            }
        });


        return containerView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof EditCarFragmentDelegate) {
            listener = (EditCarFragmentDelegate) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface EditCarFragmentDelegate {
        //if edit new car succeeded - result == 1, remove - result == 2, else result == 0
        void returnEditCarResult(int result);
    }

    public int valied(EditText et, String errorMessage){
        if(et.getText().toString().length() == 0){
            Log.d("TAG","12");
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
