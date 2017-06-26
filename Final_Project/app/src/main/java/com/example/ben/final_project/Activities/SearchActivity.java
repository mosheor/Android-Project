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

import com.example.ben.final_project.Fragments.CarDetailsFragment;
import com.example.ben.final_project.Fragments.SearchCarListResultFragment;
import com.example.ben.final_project.Fragments.SearchFragment;
import com.example.ben.final_project.R;

public class SearchActivity extends Activity implements SearchFragment.SearchFragmentDelegate, SearchCarListResultFragment.CarListSearchFragmentDelegate {

    SearchFragment searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d("TAG","SearchActivity onCreate");

        searchFragment = new SearchFragment();
        searchFragment.setDelegate(this);

        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.add(R.id.frame_fragment_search, searchFragment);
        tran.commit();
        getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);

        menu.findItem(R.id.menu_add_icon).setVisible(false);
        menu.findItem(R.id.menu_edit_icon).setVisible(false);

        return true;
    }

    @Override
    public void filterSelection(String category, String companyId, int engineVolumeNumberCond, int hpNumberCond) {
        Log.d("TAG","SearchActivity filterSelection");

        SearchCarListResultFragment carListSearchFragment = SearchCarListResultFragment.newInstance(
                category,companyId,String.valueOf(engineVolumeNumberCond),String.valueOf(hpNumberCond));

        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.frame_fragment_search, carListSearchFragment);
        tran.addToBackStack("searchToSearchResults");
        tran.commit();
        getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void selectCarDetailsClick(String companyId, String carId) {
        Log.d("TAG","SearchActivity selectCarDetailsClick");

        CarDetailsFragment carDetailsFragment = new CarDetailsFragment();
        carDetailsFragment = carDetailsFragment.newInstance(companyId,carId);

        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.frame_fragment_search, carDetailsFragment);
        tran.addToBackStack("SearchResultsToCarDetails");
        tran.commit();
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
                intentClass = CarCatalogActivity.class;
                break;
            case R.id.menu_login:
                intentClass = LoginActivity.class;
                break;
            case R.id.menu_register:
                intentClass = RegisterActivity.class;
                break;
            case R.id.menu_search:
                commitIntent = false;
                Toast.makeText(this, "you are already here", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_main:
                commitIntent = false;
                finish();
                break;
            default:
                throw new RuntimeException("Error articleID in btn click in the menu of SearchActivity");
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
}
