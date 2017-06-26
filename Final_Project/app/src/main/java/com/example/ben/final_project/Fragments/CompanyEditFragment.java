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
import com.example.ben.final_project.Model.CarCompany;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

import static com.example.ben.final_project.Activities.CarCatalogActivity.CATALOG_COMPANY_EDIT;

public class CompanyEditFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";//company articleID
    private String companyId;
    CarCompany company;
    private FragmentsDelegate listener;

    public static CompanyEditFragment newInstance(String param1) {
        CompanyEditFragment fragment = new CompanyEditFragment();
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
            companyId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TAG","CompanyEditFragment onCreateView");
        View containerView =  inflater.inflate(R.layout.fragment_edit_company, container, false);

        Button saveButton = (Button) containerView.findViewById(R.id.edit_company_save_button);
        Button cancelButton = (Button) containerView.findViewById(R.id.edit_company_cancel_button);
        Button deleteButton = (Button) containerView.findViewById(R.id.edit_company_delete_button);
        final EditText companyName = (EditText) containerView.findViewById(R.id.edit_company_name);
        final EditText companyImg = (EditText) containerView.findViewById(R.id.edit_company_image);
        final EditText description = (EditText) containerView.findViewById(R.id.edit_company_description);

        company = new CarCompany();
        company = Model.instance.getCompany(companyId);
        companyName.setText(company.name);
        companyImg.setText(company.companyLogo);
        description.setText(company.companyDescription);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","CompanyEditFragment Btn Save click");

                int ok = 0;
                boolean save = true;

                ok += valid(companyName,"Main title is required!");//TODO:write errors in hebrew
                ok += valid(companyImg,"Author name is required!");
                ok += valid(description,"Content is required!");

                if(ok != 3)
                    save = false;
                else{
                    if(companyName.getText().toString().compareTo(company.name) == 0)
                        if(companyImg.getText().toString().compareTo(company.companyLogo) == 0)
                            if(description.getText().toString().compareTo(company.companyDescription) == 0)
                                        save = false;

                }

                if(save == true) {
                    company.name = companyName.getText().toString();
                    company.companyLogo = companyImg.getText().toString();
                    company.companyDescription = description.getText().toString();

                    if(Model.instance.editCompany(company))
                        listener.onAction(CATALOG_COMPANY_EDIT,null);
                }
                else{
                    Log.d("TAG","CompanyEditFragment did not save edited article");
                    //listener.returnEditCompanyResult(RESULT_FAIL);
                }

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","CompanyEditFragment Btn Cancle click");
                listener.onAction(CATALOG_COMPANY_EDIT,null);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Model.instance.removeComoany(company.id) == true){
                    Log.d("TAG","CompanyEditFragment Delete article");
                    listener.onAction(CATALOG_COMPANY_EDIT,null);
                }
                else{
                    Log.d("TAG","CompanyEditFragment Delete article did not succed");
                    listener.onAction(CATALOG_COMPANY_EDIT,null);
                }
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
            et.setError(errorMessage);
            return 0;
        }
        else
            return 1;
    }

}
