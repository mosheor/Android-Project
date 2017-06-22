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
import android.widget.EditText;

import com.example.ben.final_project.R;

public class RegisterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);

        MenuItem addItem = menu.findItem(R.id.menu_add_icon);
        MenuItem editItem = menu.findItem(R.id.menu_edit_icon);

        final EditText firstNameET = (EditText) findViewById(R.id.register_first_name);
        final EditText lastNameET = (EditText) findViewById(R.id.register_last_name);
        final EditText birthDateET = (EditText) findViewById(R.id.register_birth_date);
        final EditText usernameET = (EditText) findViewById(R.id.register_username);
        final EditText emailET = (EditText) findViewById(R.id.register_email);
        final EditText passwordET = (EditText) findViewById(R.id.register_password);
        final EditText passwordValidationET = (EditText) findViewById(R.id.register_password_validation);
        final Button registerBTN = (Button)findViewById(R.id.register_save_button);

        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        addItem.setVisible(false);
        editItem.setVisible(false);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_about) {
            Log.d("TAG", "CarCatalogActivity menu_about");
            Intent intent = new Intent(RegisterActivity.this, AboutActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.menu_articles) {
            Log.d("TAG", "CarCatalogActivity menu_articles");
            Intent intent = new Intent(RegisterActivity.this, ArticlesActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.menu_car_catalog) {
            Log.d("TAG", "CarCatalogActivity menu_car_catalog");
            Intent intent = new Intent(RegisterActivity.this, CarCatalogActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.menu_login) {
            Log.d("TAG", "CarCatalogActivity menu_login");
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.menu_register) {
            Log.d("TAG", "CarCatalogActivity menu_register");
        } else if (item.getItemId() == R.id.menu_search) {
            Log.d("TAG", "CarCatalogActivity menu_search");
            Intent intent = new Intent(RegisterActivity.this, SearchActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.menu_main) {
            Log.d("TAG", "CarCatalogActivity menu_main");
            finish();
        }
        return true;
    }
}
