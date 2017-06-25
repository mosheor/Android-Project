package com.example.ben.final_project.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.ben.final_project.Fragments.CarAddFragment;
import com.example.ben.final_project.Fragments.CompanyAddFragment;
import com.example.ben.final_project.Fragments.CarDetailsFragment;
import com.example.ben.final_project.Fragments.CarListFragment;
import com.example.ben.final_project.Fragments.CompanyDetailsFragment;
import com.example.ben.final_project.Fragments.CompanyListFragment;
import com.example.ben.final_project.Fragments.CarEditFragment;
import com.example.ben.final_project.Fragments.CompanyEditFragment;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;


public class CarCatalogActivity extends Activity implements FragmentsDelegate{

    public static final int CATALOG_COMPANY_LIST = 0;
    public static final int CATALOG_COMPANY_ADD = 1;
    public static final int CATALOG_COMPANY_EDIT = 2;
    public static final int CATALOG_COMPANY_DETAILS = 3;
    public static final int CATALOG_CAR_LIST = 4;
    public static final int CATALOG_CAR_ADD = 5;
    public static final int CATALOG_CAR_EDIT = 6;
    public static final int CATALOG_CAR_DETAILS = 7;

    CompanyListFragment companyListFragment;
    String companyClickedID;
    String carClickedID;;
    MenuItem addItem;
    MenuItem editItem;
    int currentFragment;
    /*TODO:This string is if I clicked on add/edit icon. Sign what we need -
    TODO: - add/edit car or company*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG","CarCatalogActivity onCreate");
        setContentView(R.layout.activity_car_catalog);

        companyListFragment = new CompanyListFragment();
        openFragment(companyListFragment);
        getActionBar().setDisplayHomeAsUpEnabled(false);
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
                    int companyListSize = Model.instance.getCompanyListSize();
                    CompanyAddFragment addCompanyFragment = CompanyAddFragment.newInstance(String.valueOf(companyListSize));
                    openFragment(addCompanyFragment);
                    item.setVisible(false);
                    currentFragment = CATALOG_COMPANY_ADD;
                }
                else{
                    int modelsListSize = Model.instance.getModelsListSize(companyClickedID);
                    CarAddFragment addCarFragment = CarAddFragment.newInstance(companyClickedID,String.valueOf(modelsListSize));
                    openFragment(addCarFragment);
                    item.setVisible(false);
                    currentFragment = CATALOG_CAR_ADD;
                }
                commitIntent = false;
                break;
            case R.id.menu_edit_icon:
                Log.d("TAG","CarCatalogActivity menu_edit_icon");
                if(companyClickedID != null && carClickedID == null) {//edit company
                    Log.d("TAG","CarCatalogActivity edit id " + companyClickedID);
                    CompanyEditFragment editCompanyFragment = CompanyEditFragment.newInstance(String.valueOf(companyClickedID));//todo in line
                    openFragment(editCompanyFragment);
                    item.setVisible(false);
                    currentFragment = CATALOG_COMPANY_ADD;
                }
                else if(companyClickedID != null && carClickedID != null){//edit car
                    Log.d("TAG","CarCatalogActivity edit id " + carClickedID + " in company id " + companyClickedID);
                    CarEditFragment editCarFragment = CarEditFragment.newInstance(String.valueOf(companyClickedID),String.valueOf(carClickedID));
                    openFragment(editCarFragment);
                    item.setVisible(false);
                    currentFragment = CATALOG_CAR_EDIT;
                }
                commitIntent = false;
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

    private void openFragment(Fragment frag){
        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.car_catalog_frame_fragment, frag);
        tran.addToBackStack("carCatalogBackFragment");
        tran.commit();
    }

    @Override
    public void onAction(int command,String id) {
        switch (command){
            case CATALOG_CAR_ADD:
                companyListFragment = new CompanyListFragment();
                openFragment(companyListFragment);
                addItem.setVisible(true);
                editItem.setVisible(false);
                companyClickedID = null;
                carClickedID = null;
                currentFragment = CATALOG_COMPANY_LIST;
                break;
            case CATALOG_CAR_DETAILS:
                carClickedID = Model.instance.getCar(companyClickedID,id).carID;
                CarDetailsFragment carDetailsFragment = CarDetailsFragment.newInstance(companyClickedID,carClickedID);// todo carDetailsFragment OR CarDetailsFragment ???
                openFragment(carDetailsFragment);
                addItem.setVisible(false);
                editItem.setVisible(true);
                currentFragment = CATALOG_CAR_DETAILS;
                break;
            case CATALOG_CAR_EDIT:
                companyListFragment = new CompanyListFragment();
                openFragment(companyListFragment);
                addItem.setVisible(true);
                editItem.setVisible(false);
                companyClickedID = null;
                carClickedID = null;
                currentFragment = CATALOG_COMPANY_LIST;
                break;
            case CATALOG_CAR_LIST:
                companyClickedID = Model.instance.getCompany(id).id;
                CarListFragment carListFragment = CarListFragment.newInstance(companyClickedID);
                openFragment(carListFragment);
                addItem.setVisible(true);
                editItem.setVisible(false);
                currentFragment = CATALOG_CAR_LIST;
                break;
            case CATALOG_COMPANY_ADD:
                companyListFragment = new CompanyListFragment();
                openFragment(companyListFragment);
                addItem.setVisible(true);
                editItem.setVisible(false);
                companyClickedID = null;
                currentFragment = CATALOG_COMPANY_LIST;
                break;
            case CATALOG_COMPANY_DETAILS:
                companyClickedID = Model.instance.getCompany(id).id;
                CompanyDetailsFragment companyDetailsFragment = CompanyDetailsFragment.newInstance(companyClickedID);
                openFragment(companyDetailsFragment);
                addItem.setVisible(false);
                editItem.setVisible(true);
                currentFragment = CATALOG_COMPANY_DETAILS;
                break;
            case CATALOG_COMPANY_EDIT:
                companyListFragment = new CompanyListFragment();
                openFragment(companyListFragment);
                addItem.setVisible(true);
                editItem.setVisible(false);
                currentFragment = CATALOG_COMPANY_LIST;
                companyClickedID = null;
                break;
            case CATALOG_COMPANY_LIST:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        switch(currentFragment){
            case CATALOG_CAR_ADD:
                editItem.setVisible(false);
                addItem.setVisible(true);
                currentFragment = CATALOG_CAR_LIST;
                break;
            case CATALOG_CAR_DETAILS:
                editItem.setVisible(false);
                addItem.setVisible(true);
                currentFragment = CATALOG_CAR_LIST;
                break;
            case CATALOG_CAR_EDIT:
                editItem.setVisible(true);
                addItem.setVisible(false);
                currentFragment = CATALOG_CAR_DETAILS;
                break;
            case CATALOG_CAR_LIST:
                editItem.setVisible(false);
                addItem.setVisible(true);
                currentFragment = CATALOG_COMPANY_LIST;
                break;
            case CATALOG_COMPANY_ADD:
                editItem.setVisible(false);
                addItem.setVisible(true);
                currentFragment = CATALOG_COMPANY_LIST;
                break;
            case CATALOG_COMPANY_DETAILS:
                editItem.setVisible(false);
                addItem.setVisible(true);
                currentFragment = CATALOG_COMPANY_LIST;
                break;
            case CATALOG_COMPANY_EDIT:
                editItem.setVisible(true);
                addItem.setVisible(false);
                currentFragment = CATALOG_COMPANY_DETAILS;
                break;
            case CATALOG_COMPANY_LIST:
                break;
        }
    }

}
