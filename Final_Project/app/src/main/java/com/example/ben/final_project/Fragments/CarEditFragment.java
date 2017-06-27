package com.example.ben.final_project.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ben.final_project.Activities.FragmentsDelegate;
import com.example.ben.final_project.Activities.GetPicture;
import com.example.ben.final_project.Model.Car;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

import static android.view.View.GONE;
import static com.example.ben.final_project.Activities.ArticlesActivity.ADD_PICTURE;
import static com.example.ben.final_project.Activities.ArticlesActivity.ARTICLE_ADD;
import static com.example.ben.final_project.Activities.ArticlesActivity.ARTICLE_EDIT;
import static com.example.ben.final_project.Activities.CarCatalogActivity.CATALOG_ADD_PICTURE;
import static com.example.ben.final_project.Activities.CarCatalogActivity.CATALOG_CAR_EDIT;

public class CarEditFragment extends Fragment implements GetPicture {
    private static final String ARG_PARAM1 = "param1";//company articleID
    private String companyId;
    private static final String ARG_PARAM2 = "param2";//car articleID
    private String carId;
    Car car;
    private FragmentsDelegate listener;
    ImageView imageUrl;
    ProgressBar progressBar;
    Bitmap imageBitmap;

    public static CarEditFragment newInstance(String param1, String param2) {
        CarEditFragment fragment = new CarEditFragment();
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
        Log.d("TAG","CarEditFragment onCreateView");
        View containerView = inflater.inflate(R.layout.fragment_edit_car, container, false);

        final String companyID = companyId;
        final String carID = carId;

        Log.d("TAG","company articleID = " + companyID );
        Log.d("TAG","new car articleID = " + carID );

        Button saveButton = (Button) containerView.findViewById(R.id.edit_car_save_button);
        Button cancelButton = (Button) containerView.findViewById(R.id.edit_car_cancel_button);
        Button deleteButton = (Button) containerView.findViewById(R.id.edit_car_delete_button);
        final EditText companyName = (EditText) containerView.findViewById(R.id.edit_car_company_name);
        final EditText carDescription = (EditText) containerView.findViewById(R.id.edit_car_description);
        final EditText fuelConsumption = (EditText) containerView.findViewById(R.id.edit_car_fuel_consumption);
        final EditText hp = (EditText) containerView.findViewById(R.id.edit_car_hp);
        final EditText carName = (EditText) containerView.findViewById(R.id.edit_car_name);
        final EditText pollusion = (EditText) containerView.findViewById(R.id.edit_car_pollusion);
        final EditText price = (EditText) containerView.findViewById(R.id.edit_car_price);
        final EditText warranty = (EditText) containerView.findViewById(R.id.edit_car_warranty);
        final EditText zeroToHundrend = (EditText) containerView.findViewById(R.id.edit_car_zero_to_hundrend);
        final EditText engineVolume = (EditText) containerView.findViewById(R.id.edit_car_engine_volume);
        final Spinner category = (Spinner) containerView.findViewById(R.id.edit_car_category);
        imageUrl = (ImageView) containerView.findViewById(R.id.edit_car_image);
        progressBar = (ProgressBar) containerView.findViewById(R.id.edit_car_progressBar);
        progressBar.setVisibility(GONE);

        final ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.car_category_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categoryAdapter);

        //TODO:spinner
        Model.instance.getCar(companyID, carID, new Model.GetModelCallback() {
            @Override
            public void onComplete(Car onCompletecars) {
                car = onCompletecars;
                companyName.setText(car.companyName);
                companyName.setEnabled(false);
                carName.setText(car.carName);
                carDescription.setText(car.description);
                fuelConsumption.setText(String.valueOf(car.fuelConsumption));
                hp.setText(String.valueOf(car.hp));
                pollusion.setText(String.valueOf(car.pollution));
                price.setText(String.valueOf(car.price));
                warranty.setText(String.valueOf(car.warranty));
                zeroToHundrend.setText(String.valueOf(car.zeroToHundred));
                engineVolume.setText(String.valueOf(car.engineVolume));

                progressBar.setVisibility(View.VISIBLE);
                Model.instance.getImage(car.carPicture, new Model.GetImageListener() {
                    @Override
                    public void onSuccess(Bitmap image) {
                        imageUrl.setImageBitmap(image);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFail() {

                    }
                });

                for(int i=0;i<categoryAdapter.getCount();i++){
                    if(car.carCategory.compareTo(categoryAdapter.getItem(i).toString()) == 0)
                        category.setSelection(i);
                }
            }

            @Override
            public void onCancel() {

            }
        });



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Model.instance.isNetworkAvailable()) {
                    Log.d("TAG", "CompanyEditFragment Btn Save click");

                    int ok = 0;
                    boolean save = true;

                    ok += valid(carDescription, "Description is required!");//TODO:write errors in hebrew
                    ok += valid(carName, "Car name is required!");
                    ok += valid(fuelConsumption, "Fuel consumption is required!");
                    ok += valid(hp, "Horse powers is required!");
                    ok += valid(pollusion, "Pollusion is required!");
                    ok += valid(price, "Car price is required!");
                    ok += valid(warranty, "Car warranty is required!");
                    ok += valid(zeroToHundrend, "0-100 is required!");
                    ok += valid(engineVolume, "Engine volume is required!");
                    ok += checkIfInteger(fuelConsumption);
                    ok += checkIfInteger(hp);
                    ok += checkIfInteger(pollusion);
                    ok += checkIfInteger(price);
                    ok += checkIfInteger(warranty);
                    ok += checkIfFloat(zeroToHundrend);
                    ok += checkIfInteger(engineVolume);

                    if (ok != 16)
                        save = false;
                    else {
                        if (carDescription.getText().toString().compareTo(car.description) == 0)
                            if (carName.getText().toString().compareTo(car.carName) == 0)
                                if (fuelConsumption.getText().toString().compareTo(String.valueOf(car.fuelConsumption)) == 0)
                                    if (hp.getText().toString().compareTo(String.valueOf(car.hp)) == 0)
                                        if (pollusion.getText().toString().compareTo(String.valueOf(car.pollution)) == 0)
                                            if (price.getText().toString().compareTo(String.valueOf(car.price)) == 0)
                                                if (warranty.getText().toString().compareTo(String.valueOf(car.warranty)) == 0)
                                                    if (zeroToHundrend.getText().toString().compareTo(String.valueOf(car.zeroToHundred)) == 0)
                                                        if (engineVolume.getText().toString().compareTo(String.valueOf(car.engineVolume)) == 0)
                                                            if (category.getSelectedItem().toString().compareTo(String.valueOf(car.carCategory)) == 0)
                                                                if (imageBitmap != null)
                                                                    save = false;

                    }

                    if (save == true) {
                        final Car car = new Car();
                        car.carID = carID;
                        car.carName = carName.getText().toString();
                        car.hp = Integer.parseInt(hp.getText().toString());
                        car.pollution = Integer.parseInt(pollusion.getText().toString());
                        car.fuelConsumption = Integer.parseInt(fuelConsumption.getText().toString());
                        car.zeroToHundred = Integer.parseInt(zeroToHundrend.getText().toString());
                        car.companyName = companyName.getText().toString();
                        car.companyID = companyID;
                        car.description = carDescription.getText().toString();
                        car.engineVolume = Integer.parseInt(engineVolume.getText().toString());
                        car.warranty = Integer.parseInt(warranty.getText().toString());
                        car.price = Integer.parseInt(price.getText().toString());
                        car.carCategory = category.getSelectedItem().toString();

                        if (imageBitmap != null) {
                            Model.instance.saveImage(imageBitmap, Model.random() + ".jpeg", new Model.SaveImageListener() {
                                @Override
                                public void complete(String url) {
                                    car.carPicture = url;
                                    Model.instance.editCar(car);
                                    progressBar.setVisibility(GONE);
                                    listener.onAction(CATALOG_CAR_EDIT, null);
                                }

                                @Override
                                public void fail() {
                                    //notify operation fail,...
                                    progressBar.setVisibility(GONE);
                                    listener.onAction(CATALOG_CAR_EDIT, null);
                                }
                            });
                        } else {
                            car.carPicture = "";
                            Model.instance.editCar(car);
                            progressBar.setVisibility(GONE);
                            listener.onAction(CATALOG_CAR_EDIT, null);
                        }
                    } else {
                        Log.d("TAG", "CompanyEditFragment did not save edited article");
                    }
                }
                else {
                    Toast.makeText(getActivity(), "There is no connection", Toast.LENGTH_SHORT);
                    listener.onAction(CATALOG_CAR_EDIT,null);
                }

            }
        });

        imageUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:go to galary/camera
                //dispatchTakePictureIntent();
                progressBar.setVisibility(View.VISIBLE);
                listener.onAction(CATALOG_ADD_PICTURE,null);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","CompanyEditFragment Btn Cancle click");
                listener.onAction(CATALOG_CAR_EDIT,null);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Model.instance.isNetworkAvailable()) {
                    Model.instance.removeCar(car);
                }
                else
                    Toast.makeText(getActivity(), "There is no connection", Toast.LENGTH_SHORT);
                listener.onAction(CATALOG_CAR_EDIT,null);
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

    public int valid(EditText et, String errorMessage){
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

    @Override
    public void getPicture(Bitmap bitmap) {
        progressBar.setVisibility(GONE);
        imageBitmap = bitmap;
        imageUrl.setImageBitmap(bitmap);
    }
}
