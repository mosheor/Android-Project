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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ben.final_project.Activities.FragmentsDelegate;
import com.example.ben.final_project.Model.Car;
import com.example.ben.final_project.Model.Company;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static com.example.ben.final_project.Activities.CarCatalogActivity.CATALOG_CAR_DETAILS;

public class CarListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";//company articleID
    private String companyId;
    ListView list;
    List<Car> carsData;
    Company company;
    CarListAdapter adapter;
    FragmentsDelegate listener;

    public static CarListFragment newInstance(String param1) {
        CarListFragment fragment = new CarListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Model.UpdateCarEvent event) {
        Toast.makeText(getActivity(), "got new car event", Toast.LENGTH_SHORT).show();
        Log.d("TAG","new comment");
        boolean exist = false;
        for (Car car : carsData) {
            if (car.companyID.equals(event.car.companyID) && car.carID.equals(event.car.carID)) {
                car = event.car;
                exist = true;
                break;
            }
        }
        if (!exist && companyId.equals(event.car.companyID)) {
            carsData.add(event.car);
        }
        adapter.notifyDataSetChanged();
        list.setSelection(carsData.size()-1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            companyId = getArguments().getString(ARG_PARAM1);
        }
        //subscribe to the EventBus
        EventBus.getDefault().register(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //unsubscribe the EventBus
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("TAG","CompanyListFragment onCreateView");
        final View containerView = inflater.inflate(R.layout.fragment_cars_list, container, false);
        adapter = new CarListAdapter();
        adapter.setInflater(inflater);
        list = (ListView) containerView.findViewById(R.id.frag_cars_list);
        Model.instance.getCompany(companyId, new Model.GetCompanyCallback() {
            @Override
            public void onComplete(Company onCompleteCompany) {
                company = onCompleteCompany;
                Model.instance.getCompanyCars(companyId, new Model.GetCompanyCarsCallback() {
                    @Override
                    public void onComplete(List<Car> onCompleteList) {
                        carsData = onCompleteList;
                        list.setAdapter(adapter);
                        TextView companyNameTW = (TextView) containerView.findViewById(R.id.car_list_company_name);
                        final ImageView companyImage = (ImageView) containerView.findViewById(R.id.car_list_company_image);
                        final ProgressBar progressBar = (ProgressBar) containerView.findViewById(R.id.car_list_progressBar);

                        companyImage.setImageResource(R.drawable.bugatti);//TODO:change to current company
                        companyNameTW.setText(company.name);

                        companyImage.setTag(company.companyLogo);
                        //imageView.setImageDrawable(getDrawable(R.drawable.avatar));

                        if (company.companyLogo != null && !company.companyLogo.isEmpty() && !company.companyLogo.equals("")) {
                            progressBar.setVisibility(View.VISIBLE);
                            Model.instance.getImage(company.companyLogo, new Model.GetImageListener() {
                                @Override
                                public void onSuccess(Bitmap image) {
                                    String tagUrl = companyImage.getTag().toString();
                                    if (tagUrl.equals(company.companyLogo)) {
                                        companyImage.setImageBitmap(image);
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onFail() {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });

            }

            @Override
            public void onCancel() {

            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onAction(CATALOG_CAR_DETAILS,carsData.get(position).carID);
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

    public void setDelegate(FragmentsDelegate listener){
        this.listener = listener;
    }

    class CarListAdapter extends BaseAdapter {
        public LayoutInflater inflater;

        public void setInflater(LayoutInflater inflater) {
            this.inflater = inflater;
        }

        @Override
        public int getCount() {
            return carsData.size();
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

            final ImageView carLogo = (ImageView) convertView.findViewById(R.id.row_company_or_car_logo);
            TextView companyName = (TextView) convertView.findViewById(R.id.row_company_or_car_name);
            final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.row_company_or_car_progressBar);
            final Car car= carsData.get(position);
            companyName.setText(car.carName);
            carLogo.setTag(car.carPicture);

            if (car.carPicture != null && !car.carPicture.isEmpty() && !car.carPicture.equals("")) {
                progressBar.setVisibility(View.VISIBLE);
                Model.instance.getImage(car.carPicture, new Model.GetImageListener() {
                    @Override
                    public void onSuccess(Bitmap image) {
                        String tagUrl = carLogo.getTag().toString();
                        if (tagUrl.equals(car.carPicture)) {
                            carLogo.setImageBitmap(image);
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFail() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
            return convertView;
        }
    }
}
