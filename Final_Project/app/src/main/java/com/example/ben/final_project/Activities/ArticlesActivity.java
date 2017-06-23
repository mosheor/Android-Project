package com.example.ben.final_project.Activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);

        this.addItem = menu.findItem(R.id.menu_add_icon);
        this.addItem.setVisible(true);
        this.editItem = menu.findItem(R.id.menu_edit_icon);
        this.editItem.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Class intentClass = null;
        boolean commitIntent = true;
        FragmentTransaction tran;

        //which btn from the menu was pressed?
        switch (item.getItemId()){
            case R.id.menu_about:
                intentClass = AboutActivity.class;
                break;
            case R.id.menu_articles:
                commitIntent = false;
                articlesListFragment = new ArticlesListFragment();
                articlesListFragment.setDelegate(this);
                tran = getFragmentManager().beginTransaction();
                tran.replace(R.id.frame_fragment_articles, articlesListFragment);
                tran.commit();
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
                intentClass = AboutActivity.class;
                break;
            case R.id.menu_main:
                commitIntent = false;
                finish();
                break;
            case R.id.menu_edit_icon:
                EditArticleFragment editArticleFragment = new EditArticleFragment();
                Log.d("TAG","ArticlesActivity edit id " + clickedArticleID);
                editArticleFragment = editArticleFragment.newInstance(String.valueOf(clickedArticleID));
                editArticleFragment.setDelegate(this);

                tran = getFragmentManager().beginTransaction();
                tran.replace(R.id.frame_fragment_articles, editArticleFragment);
                tran.commit();

                item.setVisible(false);
                break;
            case R.id.menu_add_icon:
                AddArticleFragment addArticleFragment = new AddArticleFragment();
                int articlesListSize = Model.instance.getArticleListSize();
                addArticleFragment = addArticleFragment.newInstance(String.valueOf(articlesListSize));
                addArticleFragment.setDelegate(this);

                tran = getFragmentManager().beginTransaction();
                tran.replace(R.id.frame_fragment_articles, addArticleFragment);
                tran.commit();

                item.setVisible(false);
                break;
            default:
                throw new RuntimeException("Error id in btn click in the menu of ArticlesActivity");
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

    @Override
    public void returnAddResult(int result) {
        if(result == 1) // todo ???
            Log.d("TAG", "add new article succeded");
        else
            Log.d("TAG","add new article did not succed - cancle or faild");

        articlesListFragment = new ArticlesListFragment();
        articlesListFragment.setDelegate(this);

        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.frame_fragment_articles, articlesListFragment);
        tran.commit();
        //MenuInflater inflater = getMenuInflater(); // todo not in use ???
        addItem.setVisible(true);
    }

    @Override
    public void returnEditResult(int result) {
        if(result == 1) // todo ???
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
