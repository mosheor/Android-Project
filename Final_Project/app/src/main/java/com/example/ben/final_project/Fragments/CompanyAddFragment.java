package com.example.ben.final_project.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.ben.final_project.Activities.FragmentsDelegate;
import com.example.ben.final_project.Model.Car;
import com.example.ben.final_project.Model.CarCompany;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

import java.util.LinkedList;

import static com.example.ben.final_project.Activities.CarCatalogActivity.CATALOG_COMPANY_ADD;

public class CompanyAddFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";//last company id
    private String lastCompanyId;
    private FragmentsDelegate listener;

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
        if(Integer.parseInt(lastCompanyId) > 0)
            newCompanyID = Model.instance.getAllCompanies().get(Integer.parseInt(lastCompanyId) - 1).id + 1;
        else
            newCompanyID = "0";

        Button saveButton = (Button) containerView.findViewById(R.id.add_company_save_button);
        Button cancelButton = (Button) containerView.findViewById(R.id.add_company_cancel_button);
        final EditText companyName = (EditText) containerView.findViewById(R.id.add_company_name);
        final EditText companyImg = (EditText) containerView.findViewById(R.id.add_company_image);
        final EditText description = (EditText) containerView.findViewById(R.id.add_company_description);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","CompanyAddFragment Btn Save click");

                int ok = 0;
                boolean save = true;

                ok += valid(companyName,"Company name is required!");//TODO:write errors in hebrew
                ok += valid(companyImg,"Company logo is required!");
                ok += valid(description,"Description is required!");

                if(ok != 3)
                    save = false;

                if(save == true) {
                    CarCompany company = new CarCompany();
                    company.id = newCompanyID;
                    company.name = companyName.getText().toString();
                    company.companyLogo = companyImg.getText().toString();
                    company.companyDescription = description.getText().toString();
                    company.models = new LinkedList<Car>();

                    Model.instance.addNewCompany(company);

                    listener.onAction(CATALOG_COMPANY_ADD,null);
                }
                else{
                    Log.d("TAG","CompanyAddFragment Cant save new company");
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

}
