package com.example.ben.final_project.Fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.ben.final_project.Model.CarCompany;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

public class EditCompanyFragment extends Fragment {

    public final static int RESULT_SUCCESS_DELETE = 2;
    public final static int RESULT_SUCCESS_EDIT = 1;
    public final static int RESULT_FAIL = 0;

    private static final String ARG_PARAM1 = "param1";//company id
    private String mParam1;
    CarCompany company;

    private EditCompanyFragmentDelegate listener;

    public static EditCompanyFragment newInstance(String param1) {
        EditCompanyFragment fragment = new EditCompanyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public void setDelegate(EditCompanyFragmentDelegate listener){
        this.listener = listener;
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
        Log.d("TAG","EditCompanyFragment onCreateView");
        View containerView =  inflater.inflate(R.layout.fragment_edit_company, container, false);

        Log.d("TAG","company id = " + mParam1);

        Button saveButton = (Button) containerView.findViewById(R.id.edit_company_save_button);
        Button cancelButton = (Button) containerView.findViewById(R.id.edit_company_cancel_button);
        Button deleteButton = (Button) containerView.findViewById(R.id.edit_company_delete_button);

        final EditText companyName = (EditText) containerView.findViewById(R.id.edit_company_name);
        final EditText companyImg = (EditText) containerView.findViewById(R.id.edit_company_image);
        final EditText description = (EditText) containerView.findViewById(R.id.edit_company_description);

        company = new CarCompany();
        company = Model.instance.getCompany(mParam1);

        companyName.setText(company.name);
        companyImg.setText(company.companyLogo);
        description.setText(company.companyDescription);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","EditCompanyFragment Btn Save click");

                int ok = 0;
                boolean save = true;

                ok += valied(companyName,"Main title is required!");//TODO:write errors in hebrew
                ok += valied(companyImg,"Author name is required!");
                ok += valied(description,"Content is required!");

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
                        listener.returnEditCompanyResult(RESULT_SUCCESS_EDIT);
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
                listener.returnEditCompanyResult(RESULT_FAIL);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Model.instance.removeComoany(company.id) == true){
                    Log.d("TAG","EditCompanyFragment Delete article");
                    listener.returnEditCompanyResult(RESULT_SUCCESS_DELETE);
                }
                else{
                    Log.d("TAG","EditCompanyFragment Delete article did not succed");
                    listener.returnEditCompanyResult(RESULT_FAIL);
                }
            }
        });

        return containerView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof EditCompanyFragmentDelegate) {
            listener = (EditCompanyFragmentDelegate) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface EditCompanyFragmentDelegate {
        //if edit new company succeeded - result == 1, remove - result == 2, else result == 0
        void returnEditCompanyResult(int result);
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

}
