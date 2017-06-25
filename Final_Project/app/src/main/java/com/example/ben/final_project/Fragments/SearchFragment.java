package com.example.ben.final_project.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.ben.final_project.Model.Car;
import com.example.ben.final_project.Model.CarCompany;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SearchFragment extends Fragment {

    SearchFragmentDelegate listener;
    List<CarCompany> companiesData = Model.instance.getAllCompanies();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TAG","SearchFragment onCreateView");
        View containerView = inflater.inflate(R.layout.fragment_search, container, false);

        final Spinner category = (Spinner) containerView.findViewById(R.id.fragment_search_spinner_category);
        final Spinner companys = (Spinner) containerView.findViewById(R.id.fragment_search_spinner_manufacturer);
        final Spinner engineVolume = (Spinner) containerView.findViewById(R.id.fragment_search_spinner_engine_volume);
        final Spinner hp = (Spinner) containerView.findViewById(R.id.fragment_search_spinner_hp);
        Button searchButton = (Button) containerView.findViewById(R.id.search_button);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.car_category_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categoryAdapter);

        final List<String> companiesId = new ArrayList<String>();
        final List<String> companiesName = new ArrayList<String>();
        companiesName.add("כל היצרנים");
        companiesId.add("-1");
        for(CarCompany company:companiesData) {
            companiesId.add(company.id);
            companiesName.add(company.name);
        }

        ArrayAdapter<String> companyAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, companiesName);
        companyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        companys.setAdapter(companyAdapter);

        ArrayAdapter<CharSequence> hpAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.car_hp_array, android.R.layout.simple_spinner_item);
        hpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hp.setAdapter(hpAdapter);

        ArrayAdapter<CharSequence> engineVolumeAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.car_engine_volume_array, android.R.layout.simple_spinner_item);
        engineVolumeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        engineVolume.setAdapter(engineVolumeAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("TAG","SearchFragment search btm");

                String companyFilterId = null;
                String categoryFilterId = null;
                int hpFilterId = hp.getSelectedItemPosition();
                int engineFilterId = engineVolume.getSelectedItemPosition();
                if(companys.getSelectedItemPosition() != 0)
                    companyFilterId = companiesId.get(companys.getSelectedItemPosition());
                if(category.getSelectedItemPosition() != 0)
                    categoryFilterId = category.getSelectedItem().toString();

                listener.filterSelection(categoryFilterId,companyFilterId,engineFilterId,hpFilterId);
            }
        });

        return  containerView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchFragmentDelegate) {
            listener = (SearchFragmentDelegate) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        if (context instanceof SearchFragmentDelegate) {
            listener = (SearchFragmentDelegate) context;
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

    public interface SearchFragmentDelegate{
        void filterSelection(String category,String companyId,int engineVolumeNumberCond,int hpNumberCond);
    }

    public void setDelegate(SearchFragmentDelegate listener){
        this.listener = listener;
    }

}
