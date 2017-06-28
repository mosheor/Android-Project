package com.example.ben.final_project.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

public class MainActivity extends Activity{

    static final int REQUEST_WRITE_STORAGE = 11;
    private int i;

    MenuItem loginMenu;
    MenuItem logoutMenu;
    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button articles_btn = (Button) findViewById(R.id.main_articles);
        Button about_btn = (Button) findViewById(R.id.main_about);
        Button catalog_btn = (Button) findViewById(R.id.main_car_catalog);
        Button search_btn = (Button) findViewById(R.id.main_search);
        name = (TextView) findViewById(R.id.main_welcome);

        //init the onclick listener - one listener for all buttons
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class intentClass = null;

                //which button was pressed?
                switch (v.getId()){
                    case R.id.main_articles:
                        intentClass = ArticlesActivity.class;
                        break;
                    case R.id.main_about:
                        intentClass = AboutActivity.class;
                        break;
                    case R.id.main_car_catalog:
                        intentClass = CarCatalogActivity.class;
                        break;
                    case R.id.main_search:
                        intentClass = SearchActivity.class;
                        break;
                    default:
                        throw new RuntimeException("Error articleID in btn click in MainActivity");
                }

                commitIntentToActivity(intentClass);
            }
        };

        //set the listener for all the buttons
        articles_btn.setOnClickListener(onClickListener);
        about_btn.setOnClickListener(onClickListener);
        catalog_btn.setOnClickListener(onClickListener);
        search_btn.setOnClickListener(onClickListener);

        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!(logoutMenu == null || loginMenu == null)){
            Log.d("TAG","connect = " + Model.instance.isConnectedUser());
            if(Model.instance.isConnectedUser()){
                logoutMenu.setVisible(true);
                loginMenu.setVisible(false);
                name.setText("Welcome " + Model.instance.getConnectedUserUsername());
                name.setVisibility(View.VISIBLE);
            }
            else{
                logoutMenu.setVisible(false);
                loginMenu.setVisible(true);
                name.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Commit an intent to activity
     * @param to the activity class to open
     */
    private void commitIntentToActivity(Class to)
    {
        Intent intent = new Intent(this, to);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);

        menu.findItem(R.id.menu_add_icon).setVisible(false);
        menu.findItem(R.id.menu_edit_icon).setVisible(false);

        loginMenu = menu.findItem(R.id.menu_login);
        logoutMenu = menu.findItem(R.id.menu_signout);
        logoutMenu.setVisible(false);


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
                Toast.makeText(this, "you are already here", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_signout:
                commitIntent = false;
                if(Model.instance.isConnectedUser()){
                    Model.instance.signOut();
                    loginMenu.setVisible(true);
                    logoutMenu.setVisible(false);
                    name.setVisibility(View.GONE);
                }
                break;
            default:
                throw new RuntimeException("Error articleID in btn click in the menu of MainActivity");
        }

        if (commitIntent)
            commitIntentToActivity(intentClass);

        return true;
    }
}
