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
import com.example.ben.final_project.Model.User;
import com.example.ben.final_project.R;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText username = (EditText) findViewById(R.id.login_username);
        final EditText password = (EditText) findViewById(R.id.login_password);
        final Button connectBtn = (Button) findViewById(R.id.Login_sign_in_button);

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkUsername(username)){
                    if(checkPassword(password,username.getText().toString())){
                        finish();
                    }
                }
                else{
                    Toast.makeText(LoginActivity.this, "שם משתמש או סיסמה שגויים", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static boolean checkUsername(EditText etUsername){
        if(Model.instance.getUser(etUsername.getText().toString()) != null)
            return true;
        return false;
    }

    public static boolean checkPassword(EditText etPassword,String userName){
        User user = Model.instance.getUser(userName);
        if(user != null)
            if(user.password.compareTo(etPassword.getText().toString()) == 0)
                return true;
        return false;
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
        if (item.getItemId() == R.id.menu_about) {
            Log.d("TAG", "CarCatalogActivity menu_about");
            Intent intent = new Intent(LoginActivity.this, AboutActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.menu_articles) {
            Log.d("TAG", "CarCatalogActivity menu_articles");
            Intent intent = new Intent(LoginActivity.this, ArticlesActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.menu_car_catalog) {
            Log.d("TAG", "CarCatalogActivity menu_car_catalog");
            Intent intent = new Intent(LoginActivity.this, CarCatalogActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.menu_login) {
            Log.d("TAG", "CarCatalogActivity menu_login");
        } else if (item.getItemId() == R.id.menu_register) {
            Log.d("TAG", "CarCatalogActivity menu_register");
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.menu_search) {
            Log.d("TAG", "CarCatalogActivity menu_search");
            Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.menu_main) {
            Log.d("TAG", "CarCatalogActivity menu_main");
            finish();
        }
        return true;
    }
}
