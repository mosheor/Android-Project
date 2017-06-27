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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ben.final_project.Activities.FragmentsDelegate;
import com.example.ben.final_project.Activities.GetPicture;
import com.example.ben.final_project.Model.Car;
import com.example.ben.final_project.Model.Company;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

import java.util.LinkedList;

import static android.view.View.GONE;
import static com.example.ben.final_project.Activities.CarCatalogActivity.CATALOG_ADD_PICTURE;
import static com.example.ben.final_project.Activities.CarCatalogActivity.CATALOG_COMPANY_ADD;

public class CompanyAddFragment extends Fragment implements GetPicture {
    private static final String ARG_PARAM1 = "param1";//last company articleID
    private String lastCompanyId;
    private FragmentsDelegate listener;
    ImageView imageView;
    ProgressBar progressBar;
    Bitmap imageBitmap;

    public static CompanyAddFragment newInstance(String param1) {
        CompanyAddFragment fragment = new CompanyAddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
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
            lastCompanyId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TAG","AddNewCompanyFragment onCreateView");
        View containerView = inflater.inflate(R.layout.fragment_add_company, container, false);

        final String newCompanyID;
        /*if(Integer.parseInt(lastCompanyId) > 0)
            newCompanyID = Model.instance.getAllCompanies().get(Integer.parseInt(lastCompanyId) - 1).id + 1;
        else
            newCompanyID = "0";*/

        Button saveButton = (Button) containerView.findViewById(R.id.add_company_save_button);
        Button cancelButton = (Button) containerView.findViewById(R.id.add_company_cancel_button);
        final EditText companyName = (EditText) containerView.findViewById(R.id.add_company_name);
        imageView = (ImageView) containerView.findViewById(R.id.add_company_image);
        final EditText description = (EditText) containerView.findViewById(R.id.add_company_description);
        progressBar = (ProgressBar) containerView.findViewById(R.id.add_company_progressBar);
        progressBar.setVisibility(GONE);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Model.instance.isNetworkAvailable()) {
                    Log.d("TAG", "CompanyAddFragment Btn Save click");

                    int ok = 0;
                    boolean save = true;

                    ok += valid(companyName, "Company name is required!");//TODO:write errors in hebrew
                    ok += valid(description, "Description is required!");

                    if (ok != 2)
                        save = false;

                    if (save == true) {
                        final Company company = new Company();
                        company.companyId = Model.generateRandomId();
                        company.name = companyName.getText().toString();
                        company.companyDescription = description.getText().toString();
                        company.models = new LinkedList<Car>();

                        if (imageBitmap != null) {
                            Model.instance.saveImage(imageBitmap, Model.generateRandomId() + ".jpeg", new Model.SaveImageListener() {
                                @Override
                                public void complete(String url) {
                                    company.companyLogo = url;
                                    Model.instance.addNewCompany(company);
                                    progressBar.setVisibility(GONE);
                                    listener.onAction(CATALOG_COMPANY_ADD, null);
                                }

                                @Override
                                public void fail() {
                                    //notify operation fail,...
                                    progressBar.setVisibility(GONE);
                                    listener.onAction(CATALOG_COMPANY_ADD, null);
                                }
                            });
                        } else {
                            company.companyLogo = "";
                            Model.instance.addNewCompany(company);
                            progressBar.setVisibility(GONE);
                            listener.onAction(CATALOG_COMPANY_ADD, null);
                        }
                    } else {
                        Log.d("TAG", "CompanyAddFragment Cant save new company");
                    }
                }else {
                    Toast.makeText(getActivity(), "There is no connection", Toast.LENGTH_SHORT).show();
                    listener.onAction(CATALOG_COMPANY_ADD,null);
                }

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","ArticleAddFragment Btn Cancle click");
                listener.onAction(CATALOG_COMPANY_ADD,null);
            }
        });

        imageView = (ImageView) containerView.findViewById(R.id.add_company_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:go to galary/camera
                //dispatchTakePictureIntent();
                progressBar.setVisibility(View.VISIBLE);
                listener.onAction(CATALOG_ADD_PICTURE,null);
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

    @Override
    public void getPicture(Bitmap bitmap) {
        progressBar.setVisibility(GONE);
        imageBitmap = bitmap;
        imageView.setImageBitmap(bitmap);
    }
}
