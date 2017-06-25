package com.example.ben.final_project.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.ben.final_project.Fragments.ArticleAddFragment;
import com.example.ben.final_project.Fragments.ArticleDetailsFragment;
import com.example.ben.final_project.Fragments.ArticlesListFragment;
import com.example.ben.final_project.Fragments.ArticleEditFragment;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

public class ArticlesActivity extends Activity implements FragmentsDelegate {

    public static final int ARTICLE_DETAILS = 0;
    public static final int ARTICLE_ADD = 1;
    public static final int ARTICLE_EDIT = 2;
    private static final int ARTICLE_LIST = 3;
    ArticlesListFragment articlesListFragment;
    MenuItem addItem;
    MenuItem editItem;
    String clickedArticleID;
    int currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TAG","ArticlesActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);
        articlesListFragment = new ArticlesListFragment();
        currentFragment = ARTICLE_LIST;
        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.add(R.id.frame_fragment_articles, articlesListFragment);
        tran.commit();
        getActionBar().setDisplayHomeAsUpEnabled(false);
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
                Log.d("TAG","ArticlesActivity edit id " + clickedArticleID);
                ArticleEditFragment editArticleFragment = ArticleEditFragment.newInstance(String.valueOf(clickedArticleID));
                currentFragment = ARTICLE_EDIT;
                openFragment(editArticleFragment);
                commitIntent = false;
                item.setVisible(false);
                break;
            case R.id.menu_add_icon:
                int articlesListSize = Model.instance.getArticleListSize();
                ArticleAddFragment addArticleFragment = ArticleAddFragment.newInstance(String.valueOf(articlesListSize));
                currentFragment = ARTICLE_ADD;
                openFragment(addArticleFragment);
                item.setVisible(false);
                commitIntent = false;
                break;
            default:
                throw new RuntimeException("Error id in btn click in the menu of ArticlesActivity");
        }

        if (commitIntent)
            commitIntentToActivityAndFinish(intentClass);
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        switch(currentFragment){
            case ARTICLE_ADD:
                editItem.setVisible(false);
                addItem.setVisible(true);
                currentFragment = ARTICLE_LIST;
                break;
            case ARTICLE_DETAILS:
                editItem.setVisible(false);
                addItem.setVisible(true);
                currentFragment = ARTICLE_LIST;
                break;
            case ARTICLE_EDIT:
                editItem.setVisible(true);
                addItem.setVisible(false);
                currentFragment = ARTICLE_DETAILS;
                break;
        }
    }

    private void commitIntentToActivityAndFinish(Class to)
    {
        Intent intent = new Intent(this, to);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAction(int command,String articleId) {
        switch (command){
            case ARTICLE_DETAILS:
                clickedArticleID = Model.instance.getArticle(articleId).id;
                ArticleDetailsFragment articleDetailesFragment = ArticleDetailsFragment.newInstance(clickedArticleID);
                editItem.setVisible(true);
                addItem.setVisible(false);
                currentFragment = ARTICLE_DETAILS;
                openFragment(articleDetailesFragment);
                break;
            case ARTICLE_ADD:
                articlesListFragment = new ArticlesListFragment();
                addItem.setVisible(true);
                currentFragment = ARTICLE_LIST;
                openFragment(articlesListFragment);
                break;
            case ARTICLE_EDIT:
                articlesListFragment = new ArticlesListFragment();
                addItem.setVisible(true);
                currentFragment = ARTICLE_LIST;
                openFragment(articlesListFragment);
                break;
        }
    }


    private void openFragment(Fragment frag){
        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.frame_fragment_articles, frag);
        tran.addToBackStack("articleBackFragment");
        tran.commit();
    }

}
