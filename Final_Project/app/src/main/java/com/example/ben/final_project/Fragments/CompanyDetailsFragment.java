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
import com.example.ben.final_project.Model.CarCompany;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

public class CompanyDetailsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";//companyID
    private String companyID;
    CarCompany companyData;
    private FragmentsDelegate listener;

    public void setDelegate(FragmentsDelegate l){
        this.listener = l;
    }

    public static CompanyDetailsFragment newInstance(String param1) {
        CompanyDetailsFragment fragment = new CompanyDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            companyID = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TAG","CompanyDetailsFragment onCreateView");
        View containerView = inflater.inflate(R.layout.fragment_company_details, container, false);

        companyData = Model.instance.getCompany(companyID);

        ImageView companyPic = (ImageView) containerView.findViewById(R.id.company_details_company_pic);
        TextView description = (TextView) containerView.findViewById(R.id.company_details_description);
        TextView companyName = (TextView) containerView.findViewById(R.id.company_details_company_name);
        TextView numOfModels = (TextView) containerView.findViewById(R.id.company_details_num_of_models);

        companyPic.setImageResource(R.drawable.bugatti);//TODO:change to current car
        description.setText(companyData.companyDescription);
        companyName.setText(companyData.name);
        numOfModels.setText("" + companyData.models.size());

        return containerView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentsDelegate) {
            listener = (FragmentsDelegate) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof FragmentsDelegate) {
            listener = (FragmentsDelegate) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
