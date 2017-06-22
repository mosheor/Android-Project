package com.example.ben.final_project.Activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.ben.final_project.Fragments.CarDetailsFragment;
import com.example.ben.final_project.Fragments.CarListSearchFragment;
import com.example.ben.final_project.Fragments.SearchFragment;
import com.example.ben.final_project.R;

public class SearchActivity extends Activity implements SearchFragment.SearchFragmentDelegate, CarListSearchFragment.CarListSearchFragmentDelegate {

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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);

        MenuItem addItem = menu.findItem(R.id.menu_add_icon);
        MenuItem editItem = menu.findItem(R.id.menu_edit_icon);

        addItem.setVisible(false);
        editItem.setVisible(false);

        return true;
    }

    @Override
    public void filterSelection(String category, String companyId, int engineVolumeNumberCond, int hpNumberCond) {
        Log.d("TAG","SearchActivity filterSelection");

        CarListSearchFragment carListSearchFragment = CarListSearchFragment.newInstance(
                category,companyId,String.valueOf(engineVolumeNumberCond),String.valueOf(hpNumberCond));
        carListSearchFragment.setDelegate(this);

        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.frame_fragment_search, carListSearchFragment);
        tran.commit();
        getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void selectCarDetilesClick(String companyId,String carId) {
        Log.d("TAG","SearchActivity selectCarDetilesClick");

        CarDetailsFragment carDetailsFragment = new CarDetailsFragment();
        carDetailsFragment = carDetailsFragment.newInstance(companyId,carId);
        //carDetailsFragment.setDelegate(this);

        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.frame_fragment_search, carDetailsFragment);
        tran.commit();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_about) {
            Log.d("TAG","SearchActivity menu_about");
            Intent intent = new Intent(SearchActivity.this,AboutActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId() == R.id.menu_articles){
            Log.d("TAG","SearchActivity menu_articles");
            Intent intent = new Intent(SearchActivity.this,ArticlesActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId() == R.id.menu_car_catalog){
            Log.d("TAG","SearchActivity menu_car_catalog");
            Intent intent = new Intent(SearchActivity.this,CarCatalogActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId() == R.id.menu_login){
            Log.d("TAG","SearchActivity menu_login");
            Intent intent = new Intent(SearchActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId() == R.id.menu_register){
            Log.d("TAG","SearchActivity menu_register");
            Intent intent = new Intent(SearchActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId() == R.id.menu_search){
            Log.d("TAG","SearchActivity menu_search");
        }
        else if(item.getItemId() == R.id.menu_main){
            Log.d("TAG","SearchActivity menu_main");
            finish();
        }
        return true;
    }
}
