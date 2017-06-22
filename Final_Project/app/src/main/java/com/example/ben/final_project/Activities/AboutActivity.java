package com.example.ben.final_project.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.ben.final_project.R;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
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

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_about) {
            Log.d("TAG","AboutActivity menu_about");
        }
        else if(item.getItemId() == R.id.menu_articles){
            Log.d("TAG","AboutActivity menu_articles");
            Intent intent = new Intent(AboutActivity.this,ArticlesActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId() == R.id.menu_car_catalog){
            Log.d("TAG","AboutActivity menu_car_catalog");
            Intent intent = new Intent(AboutActivity.this,CarCatalogActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId() == R.id.menu_login){
            Log.d("TAG","AboutActivity menu_login");
            Intent intent = new Intent(AboutActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId() == R.id.menu_register){
            Log.d("TAG","AboutActivity menu_register");
            Intent intent = new Intent(AboutActivity.this,RegisterActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId() == R.id.menu_search){
            Log.d("TAG","AboutActivity menu_search");
            Intent intent = new Intent(AboutActivity.this,SearchActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId() == R.id.menu_main){
            Log.d("TAG","AboutActivity menu_main");
            finish();
        }
        return true;
    }
}
