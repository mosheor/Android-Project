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

import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText username = (EditText) findViewById(R.id.login_username);
        final EditText password = (EditText) findViewById(R.id.login_password);
        final Button signInButtonBtn = (Button) findViewById(R.id.Login_sign_in_button);

        signInButtonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Model.instance.isValidUser(username.getText().toString(),password.getText().toString())){
                    //todo set the user loggrd in!!!
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this, "שם משתמש או סיסמה שגויים", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                commitIntent = false;
                Toast.makeText(this, "you are already here", Toast.LENGTH_SHORT).show();
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
            default:
                throw new RuntimeException("Error id in btn click in the menu of LoginActivity");
        }

        if (commitIntent)
            commitIntentToActivityAndFinish(intentClass);
        return true;
    }

    private void commitIntentToActivityAndFinish(Class to)
    {
        Intent intent = new Intent(LoginActivity.this, to);
        startActivity(intent);
        finish();
    }
}
