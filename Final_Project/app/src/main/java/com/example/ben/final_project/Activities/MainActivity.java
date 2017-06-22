package com.example.ben.final_project.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.ben.final_project.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button articles_btn = (Button) findViewById(R.id.main_articles);
        Button about_btn = (Button) findViewById(R.id.main_about);
        Button catalog_btn = (Button) findViewById(R.id.main_car_catalog);
        Button search_btn = (Button) findViewById(R.id.main_search);

        articles_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","MainActivity articles_btn");
                Intent intent = new Intent(MainActivity.this,ArticlesActivity.class);
                startActivity(intent);
            }
        });
        about_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","MainActivity about_btn");
                Intent intent = new Intent(MainActivity.this,AboutActivity.class);
                startActivity(intent);
            }
        });
        catalog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","MainActivity catalog_btn");
                Intent intent = new Intent(MainActivity.this,CarCatalogActivity.class);
                startActivity(intent);
            }
        });
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","MainActivity search_btn");
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });
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
            Log.d("TAG","MainActivity menu_about");
            Intent intent = new Intent(MainActivity.this,AboutActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.menu_articles){
            Log.d("TAG","MainActivity menu_articles");
            Intent intent = new Intent(MainActivity.this,ArticlesActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.menu_car_catalog){
            Log.d("TAG","MainActivity menu_car_catalog");
            Intent intent = new Intent(MainActivity.this,CarCatalogActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.menu_login){
            Log.d("TAG","MainActivity menu_login");
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.menu_register){
            Log.d("TAG","MainActivity menu_register");
            Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.menu_search){
            Log.d("TAG","MainActivity menu_search");
            Intent intent = new Intent(MainActivity.this,SearchActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.menu_main){
            Log.d("TAG","MainActivity menu_main");
        }
        return true;
    }

}
