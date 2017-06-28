package com.example.ben.final_project.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

public class AboutActivity extends Activity {

    MenuItem loginMenu;
    MenuItem logoutMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);

        menu.findItem(R.id.menu_add_icon).setVisible(false);
        menu.findItem(R.id.menu_edit_icon).setVisible(false);
        loginMenu = menu.findItem(R.id.menu_login);
        logoutMenu = menu.findItem(R.id.menu_signout);
        if(!(logoutMenu == null || loginMenu == null)){
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
        Log.d("TAG","about menu");

        //which btn from the menu was pressed?
        switch (item.getItemId()){
            case R.id.menu_about:
                commitIntent = false;
                Toast.makeText(this, "you are already here", Toast.LENGTH_SHORT).show();
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
                finish();
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
                throw new RuntimeException("Error articleID in btn click in the menu of AboutActivity");
        }

        if (commitIntent)
            commitIntentToActivityAndFinish(intentClass);
        return true;
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
}
