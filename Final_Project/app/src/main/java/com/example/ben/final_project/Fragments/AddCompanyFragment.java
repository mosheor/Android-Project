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

import com.example.ben.final_project.Model.Article;
import com.example.ben.final_project.Model.Car;
import com.example.ben.final_project.Model.CarCompany;
import com.example.ben.final_project.Model.Comment;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

import java.util.LinkedList;

public class AddCompanyFragment extends Fragment {

    public final static int RESULT_SUCCESS = 1;
    public final static int RESULT_FAIL = 0;

    private static final String ARG_PARAM1 = "param1";//last company id
    private String mParam1;

    private AddCompanyFragmentDelegate listener;

    public static AddCompanyFragment newInstance(String param1) {
        AddCompanyFragment fragment = new AddCompanyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public void setDelegate(AddCompanyFragmentDelegate listener){
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
        Log.d("TAG","AddNewCompanyFragment onCreateView");
        View containerView = inflater.inflate(R.layout.fragment_add_company, container, false);

        final String newCompanyID;
        if(Integer.parseInt(mParam1) > 0)
            newCompanyID = Model.instance.getAllCompanies().get(Integer.parseInt(mParam1) - 1).id + 1;
        else
            newCompanyID = "0";
        Log.d("TAG","new company id = " + newCompanyID );

        Button saveButton = (Button) containerView.findViewById(R.id.add_company_save_button);
        Button cancelButton = (Button) containerView.findViewById(R.id.add_company_cancel_button);
        final EditText companyName = (EditText) containerView.findViewById(R.id.add_company_name);
        final EditText companyImg = (EditText) containerView.findViewById(R.id.add_company_image);
        final EditText description = (EditText) containerView.findViewById(R.id.add_company_description);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","AddCompanyFragment Btn Save click");

                int ok = 0;
                boolean save = true;

                ok += valied(companyName,"Company name is required!");//TODO:write errors in hebrew
                ok += valied(companyImg,"Company logo is required!");
                ok += valied(description,"Description is required!");

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

                    listener.returnAddCompanyResult(RESULT_SUCCESS);
                }
                else{
                    Log.d("TAG","AddCompanyFragment Cant save new company");
                    //listener.returnResult(RESULT_FAIL);
                }

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","AddArticleFragment Btn Cancle click");
                listener.returnAddCompanyResult(RESULT_FAIL);
            }
        });

        return containerView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AddCompanyFragmentDelegate) {
            listener = (AddCompanyFragmentDelegate) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface AddCompanyFragmentDelegate {
        void returnAddCompanyResult(int result);//if add new company succeeded - result == 1, else result == 0
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
