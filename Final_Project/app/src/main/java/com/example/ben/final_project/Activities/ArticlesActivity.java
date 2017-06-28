package com.example.ben.final_project.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ArticlesActivity extends Activity implements FragmentsDelegate {

    public static final int ARTICLE_DETAILS = 0;
    public static final int ARTICLE_ADD = 1;
    public static final int ARTICLE_EDIT = 2;
    private static final int ARTICLE_LIST = 3;
    public static final int ADD_PICTURE = 4;
    ArticlesListFragment articlesListFragment;
    MenuItem addItem;
    MenuItem editItem;
    MenuItem loginMenu;
    MenuItem logoutMenu;
    String clickedArticleID;
    int currentFragment;
    private GetPicture imageDelegate;
    boolean adminUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TAG","ArticlesActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);
        openCompanyListFragment();
        getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);

        this.addItem = menu.findItem(R.id.menu_add_icon);
        this.editItem = menu.findItem(R.id.menu_edit_icon);
        loginMenu = menu.findItem(R.id.menu_login);
        logoutMenu = menu.findItem(R.id.menu_signout);
        if(Model.instance.isConnectedUser() && Model.instance.isNetworkAvailable()) {
            if (Model.instance.isAdmin()) {
                adminUser = true;
                this.addItem.setVisible(true);
                this.editItem.setVisible(false);
            }
        }
        if(!adminUser){
            this.addItem.setVisible(false);
            this.editItem.setVisible(false);
        }

        if(!(logoutMenu == null || loginMenu == null)){
            Log.d("TAG","connect = " + Model.instance.isConnectedUser());
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
        FragmentTransaction tran;

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
                intentClass = SearchActivity.class;
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
                }
                else
                    Toast.makeText(this,"There is no connection",Toast.LENGTH_SHORT).show();
                commitIntent = false;
                break;
            case R.id.menu_add_icon:
                if(Model.instance.isNetworkAvailable()) {
                    //int articlesListSize = Model.instance.getArticleListSize();
                    ArticleAddFragment addArticleFragment = new ArticleAddFragment();
                    imageDelegate = addArticleFragment;
                    currentFragment = ARTICLE_ADD;
                    openFragment(addArticleFragment);
                    item.setVisible(false);
                }
                else
                    Toast.makeText(this, "There is no connection", Toast.LENGTH_SHORT).show();
                commitIntent = false;
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
                if(adminUser) {
                    editItem.setVisible(false);
                    addItem.setVisible(true);
                }
                else{
                    editItem.setVisible(false);
                    addItem.setVisible(false);
                }
                currentFragment = ARTICLE_LIST;
                break;
            case ARTICLE_DETAILS:
                if(adminUser) {
                    editItem.setVisible(false);
                    addItem.setVisible(true);
                }
                else{
                    editItem.setVisible(false);
                    addItem.setVisible(false);
                }
                currentFragment = ARTICLE_LIST;
                break;
            case ARTICLE_EDIT:
                if(adminUser) {
                    editItem.setVisible(true);
                    addItem.setVisible(false);
                }
                else {
                    editItem.setVisible(false);
                    addItem.setVisible(false);
                }
                currentFragment = ARTICLE_DETAILS;
                break;
            case ARTICLE_LIST:
                finish();
                break;
        }
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
                            if(adminUser) {
                                editItem.setVisible(true);
                                addItem.setVisible(false);
                            }
                            else{
                                editItem.setVisible(false);
                                addItem.setVisible(false);
                            }
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
                if(adminUser) {
                    addItem.setVisible(true);
                    editItem.setVisible(false);
                }
                else{
                    addItem.setVisible(false);
                    editItem.setVisible(false);
                }
                openCompanyListFragment();
                break;
            case ARTICLE_EDIT:
                if(adminUser) {
                    addItem.setVisible(true);
                    editItem.setVisible(false);
                }
                else{
                    addItem.setVisible(false);
                    editItem.setVisible(false);
                }
                openCompanyListFragment();
                break;
            case ADD_PICTURE:
                dispatchTakePictureIntent();
                break;
        }
    }


    /**
     * intent for taking a picture from camera or from gallery
     */
    private void dispatchTakePictureIntent() {
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String pickTitle = "Select or take a new Picture"; // Or get from strings.xml
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra
                (
                        Intent.EXTRA_INITIAL_INTENTS,
                        new Intent[] { takePhotoIntent }
                );

        startActivityForResult(chooserIntent, ADD_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_PICTURE && resultCode == RESULT_OK) {
            if(data.getData() != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageDelegate.getPicture(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Bundle extras = data.getExtras();
                imageDelegate.getPicture((Bitmap) extras.get("data"));
            }
        }
        else
        {
            //stop the ProgressBar
            imageDelegate.getPicture(null);
        }
    }

    /**
     * Opens a fragment and add to addToBackStack
     * @param frag the frag to open
     */
    private void openFragment(Fragment frag){
        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.frame_fragment_articles, frag);
        tran.addToBackStack("articleBackFragment");
        tran.commit();
    }

    /**
     * Replace the current fragment to openCompanyListFragment fragment
     */
    private void openCompanyListFragment(){
        currentFragment = ARTICLE_LIST;
        articlesListFragment = new ArticlesListFragment();
        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.frame_fragment_articles, articlesListFragment);
        tran.commit();
    }

}
