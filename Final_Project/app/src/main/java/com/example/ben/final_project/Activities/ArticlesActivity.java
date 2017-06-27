package com.example.ben.final_project.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.ben.final_project.Fragments.ArticleAddFragment;
import com.example.ben.final_project.Fragments.ArticleDetailsFragment;
import com.example.ben.final_project.Fragments.ArticlesListFragment;
import com.example.ben.final_project.Fragments.ArticleEditFragment;
import com.example.ben.final_project.Model.Article;
import com.example.ben.final_project.Model.Model;
import com.example.ben.final_project.R;

public class ArticlesActivity extends Activity implements FragmentsDelegate {

    public static final int ARTICLE_DETAILS = 0;
    public static final int ARTICLE_ADD = 1;
    public static final int ARTICLE_EDIT = 2;
    private static final int ARTICLE_LIST = 3;
    public static final int ADD_PICTURE = 4;
    ArticlesListFragment articlesListFragment;
    MenuItem addItem;
    MenuItem editItem;
    String clickedArticleID;
    int currentFragment;
    private GetPicture imageDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TAG","ArticlesActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);
        //articlesListFragment = new ArticlesListFragment();
        openCompanyListFragment();
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
                if(Model.instance.isNetworkAvailable()) {
                    Log.d("TAG", "ArticlesActivity edit articleID " + clickedArticleID);
                    ArticleEditFragment editArticleFragment = ArticleEditFragment.newInstance(String.valueOf(clickedArticleID));
                    imageDelegate = editArticleFragment;
                    currentFragment = ARTICLE_EDIT;
                    openFragment(editArticleFragment);
                    commitIntent = false;
                }
                else
                    Toast.makeText(this,"There is no connection",Toast.LENGTH_SHORT).show();
                commitIntent = false;
                break;
            case R.id.menu_add_icon:
                if(Model.instance.isNetworkAvailable()) {
                    //int articlesListSize = Model.instance.getArticleListSize();
                    ArticleAddFragment addArticleFragment = ArticleAddFragment.newInstance(String.valueOf(0/*articlesListSize*/));
                    imageDelegate = addArticleFragment;
                    currentFragment = ARTICLE_ADD;
                    openFragment(addArticleFragment);
                    item.setVisible(false);
                }
                else
                    Toast.makeText(this, "There is no connection", Toast.LENGTH_SHORT).show();
                commitIntent = false;
                break;
            default:
                throw new RuntimeException("Error articleID in btn click in the menu of ArticlesActivity");
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
            case ARTICLE_LIST:
                finish();
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
    public void onAction(int command, final String articleId) {
        switch (command){
            case ARTICLE_DETAILS:
                Model.instance.getArticle(articleId, new Model.GetArticleCallback() {
                    @Override
                    public void onComplete(Article article) {
                        if(article != null) {
                            clickedArticleID = article.articleID;
                            ArticleDetailsFragment articleDetailesFragment = ArticleDetailsFragment.newInstance(clickedArticleID);
                            editItem.setVisible(true);
                            addItem.setVisible(false);
                            currentFragment = ARTICLE_DETAILS;
                            openFragment(articleDetailesFragment);
                        }
                        else
                            Log.d("TAG","article not in database");
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                break;
            case ARTICLE_ADD:
                addItem.setVisible(true);
                openCompanyListFragment();
                break;
            case ARTICLE_EDIT:
                addItem.setVisible(true);
                openCompanyListFragment();
                break;
            case ADD_PICTURE:
                dispatchTakePictureIntent();
                break;
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, ADD_PICTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_PICTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageDelegate.getPicture((Bitmap) extras.get("data"));
        }
    }


    private void openFragment(Fragment frag){
        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.frame_fragment_articles, frag);
        tran.addToBackStack("articleBackFragment");
        tran.commit();
    }

    private void openCompanyListFragment(){
        currentFragment = ARTICLE_LIST;
        articlesListFragment = new ArticlesListFragment();
        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.frame_fragment_articles, articlesListFragment);
        tran.commit();
    }

}
