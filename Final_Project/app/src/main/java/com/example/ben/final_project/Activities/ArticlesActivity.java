package com.example.ben.final_project.Activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.ben.final_project.Fragments.AddArticleFragment;
import com.example.ben.final_project.Fragments.ArticleDetailesFragment;
import com.example.ben.final_project.Fragments.ArticlesListFragment;
import com.example.ben.final_project.Fragments.EditArticleFragment;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

public class ArticlesActivity extends Activity implements ArticlesListFragment.ArticlesListFragmentDelegate,
        AddArticleFragment.AddArticleFragmentDelegate,EditArticleFragment.EditArticleFragmentDelegate{

    ArticlesListFragment articlesListFragment;
    MenuItem addItem;
    MenuItem editItem;
    String clickedArticleID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TAG","ArticlesActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);

        articlesListFragment = new ArticlesListFragment();
        articlesListFragment.setDelegate(this);

        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.add(R.id.frame_fragment_articles, articlesListFragment);
        tran.commit();
        getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void selectDetilesClick(String id) {
        Log.d("TAG","Article number " + id);
        ArticleDetailesFragment articleDetailesFragment = new ArticleDetailesFragment();
        clickedArticleID = Model.instance.getArticle(id).id;
        articleDetailesFragment = articleDetailesFragment.newInstance(clickedArticleID);
        //articleDetailesFragment.setDelegate(this);

        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.frame_fragment_articles, articleDetailesFragment);
        tran.commit();

        editItem.setVisible(true);
        addItem.setVisible(false);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);

        MenuItem addItem = menu.findItem(R.id.menu_add_icon);
        this.addItem = addItem;
        MenuItem editItem = menu.findItem(R.id.menu_edit_icon);
        this.editItem = editItem;

        addItem.setVisible(true);
        editItem.setVisible(false);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_about) {
            Log.d("TAG","ArticlesActivity menu_about");
            Intent intent = new Intent(ArticlesActivity.this,AboutActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId() == R.id.menu_articles){
            Log.d("TAG","ArticlesActivity menu_articles");
            articlesListFragment = new ArticlesListFragment();
            articlesListFragment.setDelegate(this);

            FragmentTransaction tran = getFragmentManager().beginTransaction();
            tran.replace(R.id.frame_fragment_articles, articlesListFragment);
            tran.commit();
        }
        else if(item.getItemId() == R.id.menu_car_catalog){
            Log.d("TAG","ArticlesActivity menu_car_catalog");
            Intent intent = new Intent(ArticlesActivity.this,CarCatalogActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId() == R.id.menu_login){
            Log.d("TAG","ArticlesActivity menu_login");
            Intent intent = new Intent(ArticlesActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId() == R.id.menu_register){
            Log.d("TAG","ArticlesActivity menu_register");
            Intent intent = new Intent(ArticlesActivity.this,RegisterActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId() == R.id.menu_search){
            Log.d("TAG","ArticlesActivity menu_search");
            Intent intent = new Intent(ArticlesActivity.this,SearchActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId() == R.id.menu_main){
            Log.d("TAG","ArticlesActivity menu_main");
            finish();
        }
        else if(item.getItemId() == R.id.menu_add_icon){
            Log.d("TAG","ArticlesActivity menu_add_icon");
            AddArticleFragment addArticleFragment = new AddArticleFragment();
            int articlesListSize = Model.instance.getArticleListSize();
            addArticleFragment = addArticleFragment.newInstance(String.valueOf(articlesListSize));
            addArticleFragment.setDelegate(this);

            FragmentTransaction tran = getFragmentManager().beginTransaction();
            tran.replace(R.id.frame_fragment_articles, addArticleFragment);
            tran.commit();

            item.setVisible(false);
        }
        else if(item.getItemId() == R.id.menu_edit_icon){
            Log.d("TAG","ArticlesActivity menu_add_icon");
            EditArticleFragment editArticleFragment = new EditArticleFragment();
            Log.d("TAG","ArticlesActivity edit id " + clickedArticleID);
            editArticleFragment = editArticleFragment.newInstance(String.valueOf(clickedArticleID));
            editArticleFragment.setDelegate(this);

            FragmentTransaction tran = getFragmentManager().beginTransaction();
            tran.replace(R.id.frame_fragment_articles, editArticleFragment);
            tran.commit();

            item.setVisible(false);
        }
        return true;
    }

    @Override
    public void returnAddResult(int result) {
        if(result == 1)
            Log.d("TAG", "add new article succeded");
        else
            Log.d("TAG","add new article did not succed - cancle or faild");

        articlesListFragment = new ArticlesListFragment();
        articlesListFragment.setDelegate(this);

        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.frame_fragment_articles, articlesListFragment);
        tran.commit();
        MenuInflater inflater = getMenuInflater();
        addItem.setVisible(true);
    }

    @Override
    public void returnEditResult(int result) {
        if(result == 1)
            Log.d("TAG", "edit article succeded");
        else if(result == 2)
            Log.d("TAG", "delete article succeded");
        else
            Log.d("TAG","edit article or delete did not succed - cancle or faild");

        articlesListFragment = new ArticlesListFragment();
        articlesListFragment.setDelegate(this);

        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.frame_fragment_articles, articlesListFragment);
        tran.commit();
        MenuInflater inflater = getMenuInflater();
        addItem.setVisible(true);
    }
}
