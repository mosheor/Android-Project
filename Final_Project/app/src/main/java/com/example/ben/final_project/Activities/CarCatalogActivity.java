package com.example.ben.final_project.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.example.ben.final_project.Model.Car;
import com.example.ben.final_project.Model.Company;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class CarCatalogActivity extends Activity implements FragmentsDelegate{

    public static final int CATALOG_COMPANY_LIST = 0;
    public static final int CATALOG_COMPANY_ADD = 1;
    public static final int CATALOG_COMPANY_EDIT = 2;
    public static final int CATALOG_COMPANY_DETAILS = 3;
    public static final int CATALOG_CAR_LIST = 4;
    public static final int CATALOG_CAR_ADD = 5;
    public static final int CATALOG_CAR_EDIT = 6;
    public static final int CATALOG_CAR_DETAILS = 7;
    public static final int CATALOG_ADD_PICTURE= 8;

    private GetPicture imageDelegate;
    CompanyListFragment companyListFragment;
    String companyClickedID;
    String carClickedID;;
    MenuItem addItem;
    MenuItem editItem;
    MenuItem loginMenu;
    MenuItem logoutMenu;
    int currentFragment;
    boolean adminUser = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG","CarCatalogActivity onCreate");
        setContentView(R.layout.activity_car_catalog);

        openCompanyListFragment();
        getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);

        this.addItem = menu.findItem(R.id.menu_add_icon);
        this.editItem = menu.findItem(R.id.menu_edit_icon);
        loginMenu = menu.findItem(R.id.menu_login);
        logoutMenu = menu.findItem(R.id.menu_signout);

        if(Model.instance.isConnectedUser() && Model.instance.isNetworkAvailable()) {
            if (Model.instance.isAdmin()) {
                adminUser = true;
                this.addItem.setVisible(true);
                this.editItem.setVisible(false);
            }
        }
        if(!adminUser){
                this.addItem.setVisible(false);
                this.editItem.setVisible(false);
        }

        if(!(logoutMenu == null || loginMenu == null)){
            Log.d("TAG","connect = " + Model.instance.isConnectedUser());
            if(Model.instance.isConnectedUser()){
                logoutMenu.setVisible(true);
                loginMenu.setVisible(false);
            }
            else{
                logoutMenu.setVisible(false);
                loginMenu.setVisible(true);
            }
        }

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
                if(Model.instance.isNetworkAvailable()) {
                    if (companyClickedID == null) {//add company
                        CompanyAddFragment addCompanyFragment = new CompanyAddFragment();
                        imageDelegate = addCompanyFragment;
                        openFragment(addCompanyFragment);
                        item.setVisible(false);
                        currentFragment = CATALOG_COMPANY_ADD;
                    } else {
                        CarAddFragment addCarFragment = CarAddFragment.newInstance(companyClickedID);
                        imageDelegate = addCarFragment;
                        openFragment(addCarFragment);
                        item.setVisible(false);
                        currentFragment = CATALOG_CAR_ADD;
                    }
                }
                else
                    Toast.makeText(this, "there is not connection", Toast.LENGTH_SHORT).show();
                commitIntent = false;
                break;
            case R.id.menu_edit_icon:
                Log.d("TAG","CarCatalogActivity menu_edit_icon");
                if(Model.instance.isNetworkAvailable()) {
                    if (companyClickedID != null && carClickedID == null) {//edit company
                        Log.d("TAG", "CarCatalogActivity edit articleID " + companyClickedID);
                        CompanyEditFragment editCompanyFragment = CompanyEditFragment.newInstance(String.valueOf(companyClickedID));
                        imageDelegate = editCompanyFragment;
                        openFragment(editCompanyFragment);
                        item.setVisible(false);
                        currentFragment = CATALOG_COMPANY_ADD;
                    } else if (companyClickedID != null && carClickedID != null) {//edit car
                        Log.d("TAG", "CarCatalogActivity edit articleID " + carClickedID + " in company articleID " + companyClickedID);
                        CarEditFragment editCarFragment = CarEditFragment.newInstance(String.valueOf(companyClickedID), String.valueOf(carClickedID));
                        imageDelegate = editCarFragment;
                        openFragment(editCarFragment);
                        item.setVisible(false);
                        currentFragment = CATALOG_CAR_EDIT;
                    }
                }
                else
                    Toast.makeText(this, "there is not connection", Toast.LENGTH_SHORT).show();
                commitIntent = false;
                break;
            case R.id.menu_signout:
                commitIntent = false;
                if(Model.instance.isConnectedUser()){
                    Log.d("TAG","signout About");
                    Model.instance.signOut();
                    loginMenu.setVisible(true);
                    logoutMenu.setVisible(false);
                }
                finish();
                break;
            default:
                throw new RuntimeException("Error articleID in btn click in the menu of CarCatalogActivity");
        }

        if (commitIntent)
            commitIntentToActivityAndFinish(intentClass);
        return true;
    }

    /**
     * intent for taking a picture from camera or from gallery
     */
    private void dispatchTakePictureIntent() {
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String pickTitle = "Select or take a new Picture"; // Or get from strings.xml
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra
                (
                        Intent.EXTRA_INITIAL_INTENTS,
                        new Intent[] { takePhotoIntent }
                );

        startActivityForResult(chooserIntent, CATALOG_ADD_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CATALOG_ADD_PICTURE && resultCode == RESULT_OK) {
            if(data.getData() != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageDelegate.getPicture(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Bundle extras = data.getExtras();
                imageDelegate.getPicture((Bitmap) extras.get("data"));
            }
        }
        else
        {
            //stop the ProgressBar
            imageDelegate.getPicture(null);
        }
    }

    /**
     * Commit an intent to activity and finish this activity
     * @param to the activity class to open
     */
    private void commitIntentToActivityAndFinish(Class to)
    {
        Intent intent = new Intent(this, to);
        startActivity(intent);
        finish();
    }

    /**
     * Opens a fragment and add to addToBackStack
     * @param frag the frag to open
     */
    private void openFragment(Fragment frag){
        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.car_catalog_frame_fragment, frag);
        tran.addToBackStack("carCatalogBackFragment");
        tran.commit();
    }

    /**
     * Replace the current fragment to openCompanyListFragment fragment
     */
    private void openCompanyListFragment(){
        currentFragment = CATALOG_COMPANY_LIST;
        companyListFragment = new CompanyListFragment();
        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.car_catalog_frame_fragment, companyListFragment);
        tran.commit();
    }

    @Override
    public void onAction(int command,String id) {
        switch (command){
            case CATALOG_CAR_ADD:
                companyListFragment = new CompanyListFragment();
                openCompanyListFragment();
                if(adminUser) {
                    addItem.setVisible(true);
                    editItem.setVisible(false);
                }
                else{
                    addItem.setVisible(false);
                    editItem.setVisible(false);
                }
                companyClickedID = null;
                carClickedID = null;
                currentFragment = CATALOG_CAR_LIST;
                break;
            case CATALOG_CAR_DETAILS:
                Model.instance.getCar(companyClickedID, id, new Model.GetCarCallback() {
                    @Override
                    public void onComplete(Car car) {
                        carClickedID = car.carID;
                        CarDetailsFragment carDetailsFragment = CarDetailsFragment.newInstance(companyClickedID,carClickedID);
                        openFragment(carDetailsFragment);
                        if(adminUser) {
                            addItem.setVisible(false);
                            editItem.setVisible(true);
                        }
                        else{
                            addItem.setVisible(false);
                            editItem.setVisible(false);
                        }
                        currentFragment = CATALOG_CAR_DETAILS;
                    }

                    @Override
                    public void onCancel() {

                    }
                });

                break;
            case CATALOG_CAR_EDIT:
                if(Model.instance.isNetworkAvailable()) {
                    companyListFragment = new CompanyListFragment();
                    openCompanyListFragment();
                    if(adminUser) {
                        addItem.setVisible(true);
                        editItem.setVisible(false);
                    }
                    else{
                        addItem.setVisible(false);
                        editItem.setVisible(false);
                    }
                    companyClickedID = null;
                    carClickedID = null;
                    currentFragment = CATALOG_CAR_LIST;
                }
                else
                    Toast.makeText(this, "there is not connection", Toast.LENGTH_SHORT).show();
                break;
            case CATALOG_CAR_LIST:
                Model.instance.getCompany(id, new Model.GetCompanyCallback() {
                    @Override
                    public void onComplete(Company company) {
                        companyClickedID = company.companyId;
                        CarListFragment carListFragment = CarListFragment.newInstance(companyClickedID);
                        openFragment(carListFragment);
                        if(adminUser) {
                            addItem.setVisible(true);
                            editItem.setVisible(false);
                        }
                        else{
                            addItem.setVisible(false);
                            editItem.setVisible(false);
                        }
                        currentFragment = CATALOG_CAR_LIST;
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                break;
            case CATALOG_COMPANY_ADD:
                if(Model.instance.isNetworkAvailable()) {
                    companyListFragment = new CompanyListFragment();
                    openCompanyListFragment();
                    if(adminUser) {
                        addItem.setVisible(true);
                        editItem.setVisible(false);
                    }
                    else{
                        addItem.setVisible(false);
                        editItem.setVisible(false);
                    }
                    companyClickedID = null;
                    currentFragment = CATALOG_COMPANY_LIST;
                }
                else
                    Toast.makeText(this, "there is not connection", Toast.LENGTH_SHORT).show();
                break;
            case CATALOG_COMPANY_DETAILS:
                 Model.instance.getCompany(id, new Model.GetCompanyCallback() {
                    @Override
                    public void onComplete(Company company) {
                        companyClickedID = company.companyId;
                        CompanyDetailsFragment companyDetailsFragment = CompanyDetailsFragment.newInstance(companyClickedID);
                        openFragment(companyDetailsFragment);
                        if(adminUser) {
                            addItem.setVisible(false);
                            editItem.setVisible(true);
                        }
                        else{
                            addItem.setVisible(false);
                            editItem.setVisible(false);
                        }
                        currentFragment = CATALOG_COMPANY_DETAILS;
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                break;
            case CATALOG_COMPANY_EDIT:
                if(Model.instance.isNetworkAvailable()) {
                    companyListFragment = new CompanyListFragment();
                    openCompanyListFragment();
                    if(adminUser) {
                        addItem.setVisible(true);
                        editItem.setVisible(false);
                    }
                    else{
                        addItem.setVisible(false);
                        editItem.setVisible(false);
                    }
                    currentFragment = CATALOG_COMPANY_LIST;
                    companyClickedID = null;
                }
                else
                    Toast.makeText(this, "there is not connection", Toast.LENGTH_SHORT).show();
                break;
            case CATALOG_COMPANY_LIST:
                break;
            case CATALOG_ADD_PICTURE:
                dispatchTakePictureIntent();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        switch(currentFragment){
            case CATALOG_CAR_ADD:
                if(adminUser) {
                    editItem.setVisible(false);
                    addItem.setVisible(true);
                }
                else{
                    addItem.setVisible(false);
                    editItem.setVisible(false);
                }
                currentFragment = CATALOG_CAR_LIST;
                break;
            case CATALOG_CAR_DETAILS:
                if(adminUser) {
                    editItem.setVisible(false);
                    addItem.setVisible(true);
                }
                else{
                    addItem.setVisible(false);
                    editItem.setVisible(false);
                }
                currentFragment = CATALOG_CAR_LIST;
                break;
            case CATALOG_CAR_EDIT:
                if(adminUser) {
                    editItem.setVisible(true);
                    addItem.setVisible(false);
                }
                else{
                    addItem.setVisible(false);
                    editItem.setVisible(false);
                }
                currentFragment = CATALOG_CAR_DETAILS;
                break;
            case CATALOG_CAR_LIST:
                if(adminUser) {
                    editItem.setVisible(false);
                    addItem.setVisible(true);
                }
                else{
                    addItem.setVisible(false);
                    editItem.setVisible(false);
                }
                currentFragment = CATALOG_COMPANY_LIST;
                break;
            case CATALOG_COMPANY_ADD:
                if(adminUser) {
                    editItem.setVisible(false);
                    addItem.setVisible(true);
                }
                else{
                    addItem.setVisible(false);
                    editItem.setVisible(false);
                }
                currentFragment = CATALOG_COMPANY_LIST;
                break;
            case CATALOG_COMPANY_DETAILS:
                if(adminUser) {
                    editItem.setVisible(false);
                    addItem.setVisible(true);
                }
                else{
                    addItem.setVisible(false);
                    editItem.setVisible(false);
                }
                currentFragment = CATALOG_COMPANY_LIST;
                break;
            case CATALOG_COMPANY_EDIT:
                if(adminUser) {
                    editItem.setVisible(true);
                    addItem.setVisible(false);
                }
                else{
                    addItem.setVisible(false);
                    editItem.setVisible(false);
                }
                currentFragment = CATALOG_COMPANY_DETAILS;
                break;
            case CATALOG_COMPANY_LIST:

                finish();
                break;
        }
    }

}
