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
import android.widget.Toast;

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

        menu.findItem(R.id.menu_add_icon).setVisible(false);;
        menu.findItem(R.id.menu_edit_icon).setVisible(false);;

        //// TODO: not in use yet
        /*final EditText firstNameET = (EditText) findViewById(R.articleID.register_first_name);
        final EditText lastNameET = (EditText) findViewById(R.articleID.register_last_name);
        final EditText birthDateET = (EditText) findViewById(R.articleID.register_birth_date);
        final EditText usernameET = (EditText) findViewById(R.articleID.register_username);
        final EditText emailET = (EditText) findViewById(R.articleID.register_email);
        final EditText passwordET = (EditText) findViewById(R.articleID.register_password);
        final EditText passwordValidationET = (EditText) findViewById(R.articleID.register_password_validation);*/
        final Button registerBTN = (Button)findViewById(R.id.register_save_button);

        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return true;
    }

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
                commitIntent = false;
                Toast.makeText(this, "you are already here", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_search:
                intentClass = AboutActivity.class;
                break;
            case R.id.menu_main:
                commitIntent = false;
                finish();
                break;
            default:
                throw new RuntimeException("Error articleID in btn click in the menu of RegisterActivity");
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
