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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ben.final_project.Activities.FragmentsDelegate;
import com.example.ben.final_project.Model.Company;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

import static android.view.View.GONE;

public class CompanyDetailsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";//companyID
    private String companyID;
    Company companyData;
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

        final ImageView companyPic = (ImageView) containerView.findViewById(R.id.company_details_company_pic);
        final TextView description = (TextView) containerView.findViewById(R.id.company_details_description);
        final TextView companyName = (TextView) containerView.findViewById(R.id.company_details_company_name);
        final TextView numOfModels = (TextView) containerView.findViewById(R.id.company_details_num_of_models);
        final ProgressBar progressBar = (ProgressBar) containerView.findViewById(R.id.company_details_progressBar);
        progressBar.setVisibility(GONE);

        Model.instance.getCompany(companyID, new Model.GetCompanyCallback() {
            @Override
            public void onComplete(Company company) {
                companyData = company;
                description.setText(companyData.companyDescription);
                companyName.setText(companyData.name);
                numOfModels.setText("" + companyData.models.size());

                if(!company.companyLogo.equals("")) {
                    progressBar.setVisibility(View.VISIBLE);
                    Model.instance.getImage(company.companyLogo, new Model.GetImageListener() {
                        @Override
                        public void onSuccess(Bitmap imageLoad) {
                            companyPic.setImageBitmap(imageLoad);
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFail() {

                        }
                    });
                }
                else
                    companyPic.setImageResource(R.drawable.car_icon);
            }

            @Override
            public void onCancel() {

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
