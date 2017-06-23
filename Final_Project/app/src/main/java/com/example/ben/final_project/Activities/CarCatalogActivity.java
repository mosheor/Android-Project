package com.example.ben.final_project.Activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.ben.final_project.Fragments.AddCarFragment;
import com.example.ben.final_project.Fragments.AddCompanyFragment;
import com.example.ben.final_project.Fragments.CarDetailsFragment;
import com.example.ben.final_project.Fragments.CarListFragment;
import com.example.ben.final_project.Fragments.CompanyDetailsFragment;
import com.example.ben.final_project.Fragments.CompanyListFragment;
import com.example.ben.final_project.Fragments.EditCarFragment;
import com.example.ben.final_project.Fragments.EditCompanyFragment;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

public class CarCatalogActivity extends Activity implements CompanyListFragment.CompanyListFragmentDelegate,
    CarListFragment.CarListFragmentDelegate, AddCompanyFragment.AddCompanyFragmentDelegate, EditCompanyFragment.EditCompanyFragmentDelegate, AddCarFragment.AddCarFragmentDelegate, EditCarFragment.EditCarFragmentDelegate {

    CompanyListFragment companyListFragment;
    String companyClickedID;
    String carClickedID;;
    MenuItem addItem;
    MenuItem editItem;
    String currentFragmentName;
    /*TODO:This string is if I clicked on add/edit icon. Sign what we need -
    TODO: - add/edit car or company*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG","CarCatalogActivity onCreate");
        setContentView(R.layout.activity_car_catalog);

        companyListFragment = new CompanyListFragment();
        companyListFragment.setDelegate(this);

        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.add(R.id.car_catalog_frame_fragment, companyListFragment);
        tran.commit();
        getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void selectModelDetailsClick(String id) {
        Log.d("TAG","CarCatalogActivity selectModelDetailsClick");
        Log.d("TAG","Company number " + id);

        //CarListFragment carListFragment = new CarListFragment();
        companyClickedID = Model.instance.getCompany(id).id;
        Log.d("TAG","companyClickedID = " + companyClickedID);
        CarListFragment carListFragment = CarListFragment.newInstance(companyClickedID); // todo big C or small c in CarListFragment.new... ?!?!?!?!?!?!
        carListFragment.setDelegate(this); // todo - set the delegate or not like in the function below ?!?!!??!?

        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.car_catalog_frame_fragment, carListFragment);
        tran.commit();

        addItem.setVisible(true);
        editItem.setVisible(false);
    }

    @Override
    public void selectCompanyDetailsClick(String id) {
        Log.d("TAG","CarCatalogActivity selectCompanyDetailsClick");
        Log.d("TAG","Company number " + id);

        companyClickedID = Model.instance.getCompany(id).id;
        Log.d("TAG","companyClickedID = " + companyClickedID);
        CompanyDetailsFragment companyDetailsFragment = CompanyDetailsFragment.newInstance(companyClickedID);
        //companyDetailsFragment.setDelegate(this);

        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.car_catalog_frame_fragment, companyDetailsFragment);
        tran.commit();

        addItem.setVisible(false);
        editItem.setVisible(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);

        this.addItem = menu.findItem(R.id.menu_add_icon);
        this.addItem.setVisible(true);

        this.editItem = menu.findItem(R.id.menu_edit_icon);
        this.editItem.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Class intentClass = null;
        boolean commitIntent = true;

        //which btn from the menu was pressed?
        switch (item.getItemId()){
            case R.id.menu_about:
                intentClass = AboutActivity.class;
                break;
            case R.id.menu_articles:
                intentClass = ArticlesActivity.class;
                break;
            case R.id.menu_car_catalog:
                commitIntent = false;
                Toast.makeText(this, "you are already here", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_login:
                intentClass = LoginActivity.class;
                break;
            case R.id.menu_register:
                intentClass = RegisterActivity.class;
                break;
            case R.id.menu_search:
                intentClass = SearchActivity.class;
                break;
            case R.id.menu_main:
                commitIntent = false;
                finish();
                break;
            case R.id.menu_add_icon:
                if(companyClickedID == null) {//add company
                    AddCompanyFragment addCompanyFragment = new AddCompanyFragment();
                    int companyListSize = Model.instance.getCompanyListSize();
                    addCompanyFragment = addCompanyFragment.newInstance(String.valueOf(companyListSize));
                    addCompanyFragment.setDelegate(this);

                    FragmentTransaction tran = getFragmentManager().beginTransaction();
                    tran.replace(R.id.car_catalog_frame_fragment, addCompanyFragment);
                    tran.commit();

                    item.setVisible(false);
                }
                else{
                    AddCarFragment addCarFragment = new AddCarFragment();
                    int modelsListSize = Model.instance.getModelsListSize(companyClickedID);
                    addCarFragment = addCarFragment.newInstance(companyClickedID,String.valueOf(modelsListSize));
                    Log.d("TAG","models size = " + modelsListSize);
                    Log.d("TAG","company id = " + companyClickedID);
                    addCarFragment.setDelegate(this);

                    FragmentTransaction tran = getFragmentManager().beginTransaction();
                    tran.replace(R.id.car_catalog_frame_fragment, addCarFragment);
                    tran.commit();

                    item.setVisible(false);
                }
                break;
            case R.id.menu_edit_icon:
                Log.d("TAG","CarCatalogActivity menu_edit_icon");
                if(companyClickedID != null && carClickedID == null) {//edit company
                    EditCompanyFragment editCompanyFragment = new EditCompanyFragment();
                    Log.d("TAG","CarCatalogActivity edit id " + companyClickedID);
                    editCompanyFragment = editCompanyFragment.newInstance(String.valueOf(companyClickedID));
                    editCompanyFragment.setDelegate(this);

                    FragmentTransaction tran = getFragmentManager().beginTransaction();
                    tran.replace(R.id.car_catalog_frame_fragment, editCompanyFragment);
                    tran.addToBackStack("tran");
                    tran.commit();

                    item.setVisible(false);
                }
                else if(companyClickedID != null && carClickedID != null){//edit car
                    Log.d("TAG","CarCatalogActivity menu_edit_icon");
                    EditCarFragment editCarFragment = new EditCarFragment();
                    Log.d("TAG","CarCatalogActivity edit id " + carClickedID + " in company id " + companyClickedID);
                    editCarFragment = editCarFragment.newInstance(String.valueOf(companyClickedID),String.valueOf(carClickedID));
                    editCarFragment.setDelegate(this);

                    FragmentTransaction tran = getFragmentManager().beginTransaction();
                    tran.replace(R.id.car_catalog_frame_fragment, editCarFragment);
                    tran.commit();

                    item.setVisible(false);
                }
                break;
            default:
                throw new RuntimeException("Error id in btn click in the menu of CarCatalogActivity");
        }

        if (commitIntent)
            commitIntentToActivityAndFinish(intentClass);
        return true;
    }

    private void commitIntentToActivityAndFinish(Class to)
    {
        Intent intent = new Intent(this, to);
        startActivity(intent);
        finish();
    }

    @Override
    public void selectCarDetilesClick(String id) {
        Log.d("TAG","CarCatalogActivity selectCarDetilesClick");
        Log.d("TAG","Car number " + id);

        CarDetailsFragment carDetailsFragment = new CarDetailsFragment();
        carClickedID = Model.instance.getCar(companyClickedID,id).carID;
        Log.d("TAG","carClickedID = " + carClickedID);
        carDetailsFragment = carDetailsFragment.newInstance(companyClickedID,carClickedID);
        //carDetailsFragment.setDelegate(this);

        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.car_catalog_frame_fragment, carDetailsFragment);
        tran.commit();

        addItem.setVisible(false);
        editItem.setVisible(true);

    }

    @Override
    public void returnAddCompanyResult(int result) {
        if(result == 1)
            Log.d("TAG", "add new company succeded");
        else
            Log.d("TAG","add new company did not succed - cancle or faild");

        companyListFragment = new CompanyListFragment();
        companyListFragment.setDelegate(this);

        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.car_catalog_frame_fragment, companyListFragment);
        tran.commit();
        addItem.setVisible(true);
        companyClickedID = null;
    }

    @Override
    public void returnEditCompanyResult(int result) {
        if(result == 1)
            Log.d("TAG", "edit company succeded");
        else if(result == 2)
            Log.d("TAG", "delete company succeded");
        else
            Log.d("TAG","edit company or delete did not succed - cancle or faild");

        companyListFragment = new CompanyListFragment();
        companyListFragment.setDelegate(this);

        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.car_catalog_frame_fragment, companyListFragment);
        tran.commit();
        addItem.setVisible(true);
        companyClickedID = null;
    }

    @Override
    public void returnAddCarResult(int result) {
        if(result == 1)
            Log.d("TAG", "add new car succeded");
        else
            Log.d("TAG","add new car did not succeed - cancle or faild");

        companyListFragment = new CompanyListFragment();
        companyListFragment.setDelegate(this);

        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.car_catalog_frame_fragment, companyListFragment);
        tran.commit();
        addItem.setVisible(true);
        companyClickedID = null;
        carClickedID = null;
    }

    @Override
    public void returnEditCarResult(int result) {
        if(result == 1)
            Log.d("TAG", "add new car succeeded");
        else if(result == 2)
            Log.d("TAG", "delete car succeeded");
        else
            Log.d("TAG","add new car did not succeed - cancle or faild");

        companyListFragment = new CompanyListFragment();
        companyListFragment.setDelegate(this);

        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.car_catalog_frame_fragment, companyListFragment);
        tran.commit();
        addItem.setVisible(true);
        companyClickedID = null;
        carClickedID = null;
    }
}
