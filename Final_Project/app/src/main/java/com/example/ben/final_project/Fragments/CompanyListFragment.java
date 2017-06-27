package com.example.ben.final_project.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ben.final_project.Activities.FragmentsDelegate;
import com.example.ben.final_project.Model.Company;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.List;

import static com.example.ben.final_project.Activities.CarCatalogActivity.CATALOG_CAR_LIST;
import static com.example.ben.final_project.Activities.CarCatalogActivity.CATALOG_COMPANY_DETAILS;


public class CompanyListFragment extends Fragment {

    ListView list;
    List<Company> companiesData = new LinkedList<>();
    CompanyListAdapter adapter;
    FragmentsDelegate listener;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Model.UpdateCompanyEvent event) {
        Toast.makeText(getActivity(), "got new company event", Toast.LENGTH_SHORT).show();
        Log.d("TAG","new article");
        boolean exist = false;
        for (Company company : companiesData) {
            if (company.companyId.equals(event.company.companyId)) {
                if(event.company.wasDeleted == false)
                    company = event.company;
                else
                    companiesData.remove(companiesData.indexOf(company));
                //sarticle = event.article;
                exist = true;
                break;
            }
        }
        if (!exist && event.company.wasDeleted == false) {
            companiesData.add(event.company);
        }
        adapter.notifyDataSetChanged();
        list.setSelection(0);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TAG","CompanyListFragment onCreateView");
        View containerView = inflater.inflate(R.layout.fragment_companies_list, container, false);
        adapter = new CompanyListAdapter();
        adapter.setInflater(inflater);
        list = (ListView) containerView.findViewById(R.id.frag_companies_list);
        list.setAdapter(adapter);

        Model.instance.getAllCompanies(new Model.GetAllCompaniesAndObserveCallback() {
            @Override
            public void onComplete(List<Company> list) {
                companiesData = list;
            }

            @Override
            public void onCancel() {

            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onAction(CATALOG_CAR_LIST,companiesData.get(position).companyId);
            }
        });

        return containerView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("TAG", "on attach CompanyListFragment - context");
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
        Log.d("TAG", "on attach CompanyListFragment - activity");
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

    public void setDelegate(FragmentsDelegate listener){
        this.listener = listener;
    }

    class CompanyListAdapter extends BaseAdapter {
        public LayoutInflater inflater;

        public void setInflater(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Override
        public int getCount() {
            return companiesData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.company_and_car_list_row,null);
            }

            final ImageView companyLogo = (ImageView) convertView.findViewById(R.id.row_company_or_car_logo);
            TextView companyName = (TextView) convertView.findViewById(R.id.row_company_or_car_name);
            final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.row_company_or_car_progressBar);
            final Company company= companiesData.get(position);
            companyName.setText(company.name);
            companyLogo.setTag(company.companyLogo);

            if (company.companyLogo != null && !company.companyLogo.isEmpty() && !company.companyLogo.equals("")){
                progressBar.setVisibility(View.VISIBLE);
                Model.instance.getImage(company.companyLogo, new Model.GetImageListener() {
                    @Override
                    public void onSuccess(Bitmap image) {
                        String tagUrl = companyLogo.getTag().toString();
                        if (tagUrl.equals(company.companyLogo)) {
                            companyLogo.setImageBitmap(image);
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFail() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
            else
                companyLogo.setImageResource(R.drawable.car_icon);


            companyLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAction(CATALOG_COMPANY_DETAILS,companiesData.get(position).companyId);
                }
            });
            return convertView;
        }
    }

    public void notifyAdapter(){
        adapter.notifyDataSetChanged();
    }

}
