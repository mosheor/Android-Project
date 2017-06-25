package com.example.ben.final_project.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ben.final_project.Activities.FragmentsDelegate;
import com.example.ben.final_project.Model.CarCompany;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

import java.util.List;

import static com.example.ben.final_project.Activities.CarCatalogActivity.CATALOG_CAR_LIST;
import static com.example.ben.final_project.Activities.CarCatalogActivity.CATALOG_COMPANY_DETAILS;


public class CompanyListFragment extends Fragment {

    ListView list;
    List<CarCompany> companiesData = Model.instance.getAllCompanies();
    CompanyListAdapter adapter;
    FragmentsDelegate listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TAG","CompanyListFragment onCreateView");
        View containerView = inflater.inflate(R.layout.fragment_companies_list, container, false);
        adapter = new CompanyListAdapter();
        adapter.setInflater(inflater);
        list = (ListView) containerView.findViewById(R.id.frag_companies_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onAction(CATALOG_CAR_LIST,companiesData.get(position).id);
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

            ImageView companyLogo = (ImageView) convertView.findViewById(R.id.row_company_or_car_logo);
            TextView companyName = (TextView) convertView.findViewById(R.id.row_company_or_car_name);
            CarCompany company= companiesData.get(position);
            companyLogo.setImageResource(R.drawable.audi_logo);//TODO:current company logo
            companyName.setText(company.name);
            companyLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAction(CATALOG_COMPANY_DETAILS,companiesData.get(position).id);
                }
            });
            return convertView;
        }
    }

    public void notifyAdapter(){
        adapter.notifyDataSetChanged();
    }

}
