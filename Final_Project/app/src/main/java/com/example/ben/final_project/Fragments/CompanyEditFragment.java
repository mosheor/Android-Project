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
import com.example.ben.final_project.Model.Company;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

import static android.view.View.GONE;
import static com.example.ben.final_project.Activities.ArticlesActivity.ARTICLE_EDIT;
import static com.example.ben.final_project.Activities.CarCatalogActivity.CATALOG_ADD_PICTURE;
import static com.example.ben.final_project.Activities.CarCatalogActivity.CATALOG_CAR_EDIT;
import static com.example.ben.final_project.Activities.CarCatalogActivity.CATALOG_COMPANY_EDIT;

public class CompanyEditFragment extends Fragment implements GetPicture {
    private static final String ARG_PARAM1 = "param1";//company articleID
    private String companyId;
    Company company;
    private FragmentsDelegate listener;
    ImageView imageUrl;
    ProgressBar progressBar;
    Bitmap imageBitmap;

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
        final EditText description = (EditText) containerView.findViewById(R.id.edit_company_description);
        imageUrl = (ImageView) containerView.findViewById(R.id.edit_article_image);
        progressBar = (ProgressBar) containerView.findViewById(R.id.edit_company_progressBar);
        progressBar.setVisibility(GONE);

        company = new Company();
        Model.instance.getCompany(companyId, new Model.GetCompanyCallback() {
            @Override
            public void onComplete(Company onCompleteCompany) {
                company = onCompleteCompany;
                companyName.setText(company.name);
                description.setText(company.companyDescription);
                progressBar.setVisibility(View.VISIBLE);
                Model.instance.getImage(company.companyLogo, new Model.GetImageListener() {
                    @Override
                    public void onSuccess(Bitmap image) {
                        imageUrl.setImageBitmap(image);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFail() {

                    }
                });
            }

            @Override
            public void onCancel() {

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Model.instance.isNetworkAvailable()) {
                    Log.d("TAG", "CompanyEditFragment Btn Save click");

                    int ok = 0;
                    boolean save = true;

                    ok += valid(companyName, "Main title is required!");//TODO:write errors in hebrew
                    ok += valid(description, "Content is required!");

                    if (ok != 2)
                        save = false;
                    else {
                        if (companyName.getText().toString().compareTo(company.name) == 0)
                            if (imageBitmap != null)
                                if (description.getText().toString().compareTo(company.companyDescription) == 0)
                                    save = false;

                    }

                    if (save == true) {
                        company.name = companyName.getText().toString();
                        company.companyDescription = description.getText().toString();


                        if (imageBitmap != null) {
                            Model.instance.saveImage(imageBitmap, Model.random() + ".jpeg", new Model.SaveImageListener() {
                                @Override
                                public void complete(String url) {
                                    company.companyLogo = url;
                                    Model.instance.editCompany(company);
                                    progressBar.setVisibility(GONE);
                                    listener.onAction(CATALOG_COMPANY_EDIT, null);
                                }

                                @Override
                                public void fail() {
                                    //notify operation fail,...
                                    Model.instance.editCompany(company);
                                    listener.onAction(CATALOG_COMPANY_EDIT, null);
                                }
                            });
                        } else {
                            company.companyLogo = "";
                            Model.instance.editCompany(company);
                            progressBar.setVisibility(GONE);
                            listener.onAction(CATALOG_COMPANY_EDIT, null);
                        }

                    } else {
                        Log.d("TAG", "CompanyEditFragment did not save edited article");
                        //listener.returnEditCompanyResult(RESULT_FAIL);
                    }
                }
                else {
                    Toast.makeText(getActivity(), "There is no connection", Toast.LENGTH_SHORT);
                    listener.onAction(CATALOG_COMPANY_EDIT,null);
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
                if (Model.instance.isNetworkAvailable())
                    Model.instance.removeCompany(company);
                else
                    Toast.makeText(getActivity(), "There is no connection", Toast.LENGTH_SHORT);
                listener.onAction(CATALOG_COMPANY_EDIT,null);
            }
        });

        imageUrl = (ImageView) containerView.findViewById(R.id.edit_company_image);
        imageUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            throw new RuntimeException(context.toString()
                    + " must implement FragmentsDelegate");
        }
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof FragmentsDelegate) {
            listener = (FragmentsDelegate) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentsDelegate");
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

    @Override
    public void getPicture(Bitmap bitmap) {
        progressBar.setVisibility(GONE);
        imageBitmap = bitmap;
        imageUrl.setImageBitmap(bitmap);
    }
}
