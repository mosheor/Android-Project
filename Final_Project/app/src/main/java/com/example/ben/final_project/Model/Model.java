package com.example.ben.final_project.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.webkit.URLUtil;

import com.example.ben.final_project.MyApplication;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Random;

import static com.example.ben.final_project.Model.ModelLocalFiles.saveImageToFileAsynch;

/**
 * Created by Ben on 16/06/2017.
 */

/**
 * Model super class that manages all the other model classes -
 * sync b/w Firebase and SQLite, save images locally and remotely,
 * and manages cache.
 * Implements Singleton pattern
 */
public class Model {

    static final int RANDOM_STRING_LENGTH = 15;
    public final static Model instance = new Model(); //Singleton

    private ModelSQL modelSql;
    private ModelFirebaseArticleAndComment modelFirebaseArticleAndComment;
    private ModelFirebaseCompanyAndCar modelFirebaseCompanyAndCar;
    private ModelFirebaseFiles modelFirebaseFiles;
    private AuthenticationUser authenticationUser;

    private Model(){

        modelSql = new ModelSQL(MyApplication.getContext());
        modelFirebaseArticleAndComment = new ModelFirebaseArticleAndComment();
        modelFirebaseCompanyAndCar = new ModelFirebaseCompanyAndCar();
        modelFirebaseFiles = new ModelFirebaseFiles();
        authenticationUser = new AuthenticationUser();

        //Register to get all the diffs from Firebase - the logic from the sync classes
        syncArticlesDbAndregisterArticlesUpdates();
        syncArticlesCommentsDbAndregisterArticlesCommentsUpdates();
        syncCompaniesDbAndregisterArticlesUpdates();
        syncCompaniesCarsDbAndregisterArticlesCommentsUpdates();
    }

    /**
     * GetArticleCommentsAndObserveCallback interface for the async Firebase.
     * function to get list of all the comments of an article (diffs) and observe.
     */
    public interface GetArticleCommentsAndObserveCallback {
        void onComplete(List<Comment> list);
        void onCancel();
    }

    /**
     * get all comments associated to an article async from Firebase,
     * if there is no internet connection search locally.
     * @param articleId the id of the article.
     * @param callback see {@link GetArticleCommentsAndObserveCallback}
     */
    public void getArticleComments(String articleId, final GetArticleCommentsAndObserveCallback callback) {
        if(isNetworkAvailable()) {
            modelFirebaseArticleAndComment.getAllArticleCommentsAndObserve(articleId, new ModelFirebaseArticleAndComment.GetAllArticleCommentsAndObserveCallback() {
                @Override
                public void onComplete(List<Comment> list) {
                    callback.onComplete(list);
                }

                @Override
                public void onCancel() {
                    callback.onCancel();
                }
            });
        }
        else{
            List<Comment> list = CommentSQL.getArticleComments(modelSql.getReadableDatabase(),articleId);
            callback.onComplete(list);
        }
    }

    /**
     * get all the articles from SQLite
     */
    public List<Article> getAllArticles(){
        return ArticleSQL.getAllArticles(modelSql.getReadableDatabase());
    }

    /**
     * Register to get all the diffs from articles table Firebase - the logic from the sync classes
     * and update the lastUpdateDate.
     */
    private void syncArticlesDbAndregisterArticlesUpdates() {
        //1. get local lastUpdateDate
        SharedPreferences pref = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        final double lastUpdateDate = pref.getFloat("ArticlesLastUpdateDate",0);
        Log.d("TAG","lastUpdateDate: " + lastUpdateDate);

        modelFirebaseArticleAndComment.registerArticlesUpdates(lastUpdateDate,new ModelFirebaseArticleAndComment.RegisterArticlesUpdatesCallback() {
            @Override
            public void onArticleUpdate(Article article) {
                //3. update the local db
                if(ArticleSQL.getArticle(modelSql.getReadableDatabase(),article.articleID) != null)
                    ArticleSQL.editArticle(modelSql.getWritableDatabase(),article);
                else
                    ArticleSQL.addNewArticle(modelSql.getWritableDatabase(),article);
                //4. update the lastUpdateDate
                SharedPreferences pref = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
                final double lastUpdateDate = pref.getFloat("ArticlesLastUpdateDate",0);
                if (lastUpdateDate < article.lastUpdateDate){
                    SharedPreferences.Editor prefEd = MyApplication.getContext().getSharedPreferences("TAG",
                            Context.MODE_PRIVATE).edit();
                    prefEd.putFloat("ArticlesLastUpdateDate", (float) article.lastUpdateDate);
                    prefEd.commit();
                    Log.d("TAG","ArticlesLastUpdateDate: " + article.lastUpdateDate);
                }

                //notify who listens that got new data from Firebase
                EventBus.getDefault().post(new UpdateArticleEvent(article));
            }
        });
    }

    /**
     * Register to get all the diffs from comments table Firebase - the logic from the sync classes
     * and update the lastUpdateDate.
     */
    private void syncArticlesCommentsDbAndregisterArticlesCommentsUpdates() {
        //1. get local lastUpdateDate
        SharedPreferences pref = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        final double lastUpdateDate = pref.getFloat("ArticlesCommentsLastUpdateDate",0);
        Log.d("TAG","ArticlesCommentsLastUpdateDate: " + lastUpdateDate);

        modelFirebaseArticleAndComment.registerCommentsUpdates(lastUpdateDate,new ModelFirebaseArticleAndComment.RegisterComentsUpdatesCallback() {
            @Override
            public void onCommentUpdate(Comment comment) {
                //3. update the local db
                CommentSQL.addComment(modelSql.getWritableDatabase(),comment);
                //4. update the lastUpdateDate
                SharedPreferences pref = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
                final double lastUpdateDate = pref.getFloat("ArticlesCommentsLastUpdateDate",0);
                if (lastUpdateDate < comment.lastUpdatedDate){
                    SharedPreferences.Editor prefEd = MyApplication.getContext().getSharedPreferences("TAG",
                            Context.MODE_PRIVATE).edit();
                    prefEd.putFloat("ArticlesCommentsLastUpdateDate", (float) comment.lastUpdatedDate);
                    prefEd.commit();
                    Log.d("TAG","ArticlesCommentsLastUpdateDate: " + comment.lastUpdatedDate);
                }

                //notify who listens that got new data from Firebase
                EventBus.getDefault().post(new UpdateCommentEvent(comment));
            }
        });
    }

    /**
     * Article event class for EventBus
     */
    public class UpdateArticleEvent {
        public final Article article;
        public UpdateArticleEvent(Article article) {
            this.article = article;
        }
    }

    /**
     * Add new article and save it remotely
     * (then Firebase notifies and it saved locally).
     * @param article the article to be saved
     */
    public void addNewArticle(Article article){
        modelFirebaseArticleAndComment.addArticle(article);
    }

    /**
     * Comment event class for EventBus.
     */
    public class UpdateCommentEvent {
        public final Comment comment;
        public UpdateCommentEvent(Comment comment) {
            this.comment = comment;
        }
    }

    /**
     * Add new comment associated to an article and save it remotely
     * (then Firebase notifies and it saved locally).
     * @param comment the comment to be saved.
     */
    public void addNewCommentToArticle(Comment comment){
        Log.d("TAG","addNewCommentToArticle model");
        modelFirebaseArticleAndComment.addComment(comment);
    }

    /**
     * Logically delete an article remotely
     * (then Firebase notifies and it deleted logically locally).
     * @param article the article to be removed.
     */
    public void removeArticle(Article article){
        modelFirebaseArticleAndComment.removeArticle(article);
    }

    /**
     * A callback that fires when the firebase returns
     * the article that we are looking for.
     */
    public interface GetArticleCallback {
        void onComplete(Article article);
        void onCancel();
    }

    /**
     * Get an article from Firebase async
     * if there is no internet connection - try to grt from local storage.
     * @param articleId the id of the article.
     * @param callback see {@link GetArticleCallback}.
     */
    public void getArticle(final String articleId, final GetArticleCallback callback) {
        if(isNetworkAvailable()) {
            modelFirebaseArticleAndComment.getArticle(articleId, new GetArticleCallback() {
                @Override
                public void onComplete(Article article) {
                    callback.onComplete(article);
                }

                @Override
                public void onCancel() {
                    callback.onCancel();
                }
            });
        }
        else {
            Article article = ArticleSQL.getArticle(modelSql.getReadableDatabase(), articleId);
            callback.onComplete(article);
        }
    }

    /**
     * Save an edited article in the Firebase.
     * @param editedArticle the edited article to be saved.
     */
    public void editArticle(Article editedArticle) {
        modelFirebaseArticleAndComment.editArticle(editedArticle);
    }

    ////////////////////////////////////////////////////////////////
    //   Articles and Comments - up, Companies and Cars - down    //
    ////////////////////////////////////////////////////////////////

    /**
     * Save an edited company in the Firebase.
     * @param editedCompany the edited company to be saved.
     */
    public void editCompany(Company editedCompany) {
        modelFirebaseCompanyAndCar.editCompany(editedCompany);
    }

    /**
     * Add a new car associated to a company in the Firebase.
     * @param car the car to be added.
     */
    public void addNewCarToCompany(Car car){
        Log.d("TAG","addNewCarToCompany model");
        modelFirebaseCompanyAndCar.addCar(car);
    }

    /**
     * Add a new company in the Firebase.
     * @param company the company to be added.
     */
    public void addNewCompany(Company company){
        modelFirebaseCompanyAndCar.addCompany(company);
    }


    /**
     * Callback that fires when the company returns
     */
    public interface GetCompanyCarsCallback {
        void onComplete(List<Car> list);
        void onCancel();
    }

    /**
     * Get the company's cars from Firebase async.
     * if there is no internet connection - try to grt from local storage.
     * @param companyId the id of the company.
     * @param callback see {@link GetCompanyCarsCallback}.
     */
    public void getCompanyCars(String companyId, final GetCompanyCarsCallback callback) {
        if(isNetworkAvailable()) {
            modelFirebaseCompanyAndCar.getAllCompanyCarsAndObserve(companyId, new ModelFirebaseCompanyAndCar.GetAllCompanyCarsAndObserveCallback() {
                @Override
                public void onComplete(List<Car> list) {
                    callback.onComplete(list);
                }

                @Override
                public void onCancel() {
                    callback.onCancel();
                }
            });
        }
        else{
            List<Car> list = CarSQL.getCompanyCars(modelSql.getReadableDatabase(),companyId);
            callback.onComplete(list);
        }
    }

    /**
     * Remove logically a company from the Firebase.
     * @param company the company to be removed.
     */
    public void removeCompany(Company company){
        modelFirebaseCompanyAndCar.removeCompany(company);
    }

    /**
     * Callback that fires when all the cars associated to a company returns.
     */
    public interface GetCompanyCallback {
        void onComplete(Company company);
        void onCancel();
    }

    /**
     * Get the company from Firebase async.
     * If there is no network connection, search in SQLite.
     * @param companyID the id of the company.
     * @param callback see {@link GetCompanyCallback}.
     */
    public void getCompany(final String companyID, final GetCompanyCallback callback) {
        if(isNetworkAvailable()) {
            modelFirebaseCompanyAndCar.getCompany(companyID, new ModelFirebaseCompanyAndCar.GetCompanyCallback() {
                @Override
                public void onComplete(Company company) {
                    callback.onComplete(company);
                }

                @Override
                public void onCancel() {
                    callback.onCancel();
                }
            });
        }
        else {
            Company company = CompanySQL.getCompany(modelSql.getReadableDatabase(), companyID);
            callback.onComplete(company);
        }
    }

    /**
     * Callback that fires when a car associated to a company returns.
     */
    public interface GetCarCallback {
        void onComplete(Car car);
        void onCancel();
    }

    /**
     * Get the company from Firebase async.
     * If there is no network connection, search in SQLite.
     * @param companyId the id of the company.
     * @param carId the id of the car.
     * @param callback see {@link GetCarCallback}.
     */
    public void getCar(final String companyId, final String carId, final GetCarCallback callback) {
        if(isNetworkAvailable()) {
            modelFirebaseCompanyAndCar.getCar(carId, new ModelFirebaseCompanyAndCar.GetModelCallback() {
                @Override
                public void onComplete(Car car) {
                    callback.onComplete(car);
                }

                @Override
                public void onCancel() {
                    callback.onCancel();
                }
            });
        }
        else {
            Car car = CarSQL.getCar(modelSql.getReadableDatabase(), companyId,carId);
            callback.onComplete(car);
        }
    }

    public List<Company> getAllCompanies(){
        return CompanySQL.getAllCompanies(modelSql.getReadableDatabase());
    }

    /**
     * Save an edited car in Firebase.
     * @param editedCar the edited car to be saved.
     */
    public void editCar(Car editedCar) {
        modelFirebaseCompanyAndCar.editCar(editedCar);
    }

    /**
     * Remove logically a car from Firebase.
     * @param car the car to be removed.
     */
    public void removeCar(Car car) {
        modelFirebaseCompanyAndCar.removeCar(car);
    }


    /**
     * Company event class for EventBus.
     */
    public class UpdateCompanyEvent {
        public final Company company;
        public UpdateCompanyEvent(Company company) {
            this.company = company;
        }
    }

    /**
     * Car event class for EventBus.
     */
    public class UpdateCarEvent {
        public final Car car;
        public UpdateCarEvent(Car car) {
            this.car = car;
        }
    }

    /**
     * Register to get all the diffs from companies table Firebase - the logic from the sync classes
     * and update the lastUpdateDate.
     */
    private void syncCompaniesDbAndregisterArticlesUpdates() {
        //1. get local lastUpdateDate
        SharedPreferences pref = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        final double lastUpdateDate = pref.getFloat("CompniesLastUpdateDate",0);
        Log.d("TAG","CompniesLastUpdateDate: " + lastUpdateDate);

        modelFirebaseCompanyAndCar.registerCompanysUpdates(lastUpdateDate,new ModelFirebaseCompanyAndCar.RegisterCompanysUpdatesCallback() {

            @Override
            public void onCarCompanyUpdate(Company company) {
                //3. update the local db
                if(CompanySQL.getCompany(modelSql.getReadableDatabase(),company.companyId) != null)
                    CompanySQL.editCompany(modelSql.getWritableDatabase(),company);
                else
                    CompanySQL.addNewCompany(modelSql.getWritableDatabase(),company);
                //4. update the lastUpdateDate
                SharedPreferences pref = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
                final double lastUpdateDate = pref.getFloat("CompniesLastUpdateDate",0);
                if (lastUpdateDate < company.lastUpdatedDate){
                    SharedPreferences.Editor prefEd = MyApplication.getContext().getSharedPreferences("TAG",
                            Context.MODE_PRIVATE).edit();
                    prefEd.putFloat("CompniesLastUpdateDate", (float) company.lastUpdatedDate);
                    prefEd.commit();
                    Log.d("TAG","CompniesLastUpdateDate: " + company.lastUpdatedDate);
                }

                //notify who listens that got new data from Firebase
                EventBus.getDefault().post(new UpdateCompanyEvent(company));
            }
        });
    }

    /**
     * Register to get all the diffs from cars table Firebase - the logic from the sync classes
     * and update the lastUpdateDate.
     */
    private void syncCompaniesCarsDbAndregisterArticlesCommentsUpdates() {
        //1. get local lastUpdateDate
        SharedPreferences pref = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        final double lastUpdateDate = pref.getFloat("CompaniesCarsLastUpdateDate",0);
        Log.d("TAG","CompaniesCarsLastUpdateDate: " + lastUpdateDate);

        modelFirebaseCompanyAndCar.registerCarsUpdates(lastUpdateDate,new ModelFirebaseCompanyAndCar.RegisterCarsUpdatesCallback() {
            @Override
            public void onCarUpdate(Car car) {
                //3. update the local db
                if(CarSQL.getCar(modelSql.getReadableDatabase(),car.companyID,car.carID) != null)
                    CarSQL.editCar(modelSql.getWritableDatabase(),car);
                else
                    CarSQL.addCar(modelSql.getWritableDatabase(),car);
                //4. update the lastUpdateDate
                SharedPreferences pref = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
                final double lastUpdateDate = pref.getFloat("CompaniesCarsLastUpdateDate",0);
                if (lastUpdateDate < car.lastUpdatedDate){
                    SharedPreferences.Editor prefEd = MyApplication.getContext().getSharedPreferences("TAG",
                            Context.MODE_PRIVATE).edit();
                    prefEd.putFloat("CompaniesCarsLastUpdateDate", (float) car.lastUpdatedDate);
                    prefEd.commit();
                    Log.d("TAG","CompaniesCarsLastUpdateDate: " + car.lastUpdatedDate);
                }

                //notify who listens that got new data from Firebase
                EventBus.getDefault().post(new UpdateCarEvent(car));
            }
        });
    }


    /**
     * Generate a random unique id that contains {a-z}U{A-Z}U{0-9}
     * @return the random-generated id
     */
    public static String generateRandomId() {
        Random generator = new Random();
        String template = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder randomStringBuilder = new StringBuilder();
        char tempChar;
        for (int i = 0; i < RANDOM_STRING_LENGTH; i++){
            tempChar = template.charAt(generator.nextInt(template.length()));
            randomStringBuilder.append(tempChar);
        }
        Log.d("TAG","generateRandomId articleID = " + randomStringBuilder.toString());
        return randomStringBuilder.toString();
    }

    /**
     * check if there is a network connection
     * @return true if there is a network connection, else false
     */
    public boolean isNetworkAvailable() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    /**
     * Callback that fires when the Firebase returns the url of the saved image.
     */
    public interface SaveImageListener {
        void complete(String url);
        void fail();
    }

    /**
     * Save an image async in Firebase storage and get back its url.
     * After the image is returned, save it locally.
     * @param imageBmp the Bitmap image to be saved.
     * @param name the name of the generated save file.
     * @param listener see {@link SaveImageListener}
     */
    public void saveImage(final Bitmap imageBmp, final String name, final SaveImageListener listener) {
        modelFirebaseFiles.saveImage(imageBmp, name, new SaveImageListener() {
            @Override
            public void complete(String url) {
                String fileName = URLUtil.guessFileName(url, null, null);
                Log.d("TAG","guessed Filename = " + fileName);
                saveImageToFileAsynch(imageBmp,fileName);
                listener.complete(url);
            }

            @Override
            public void fail() {
                listener.fail();
            }
        });
    }

    /**
     * Callback that fires when the image returns from local storage or from Firebase if not exists locally.
     */
    public interface GetImageListener{
        void onSuccess(Bitmap image);
        void onFail();
    }

    /**
     * Get an image locally if exists in storage,
     * if not get if from Firebase storage async.
     * @param url url of the image
     * @param listener see {@link GetImageListener}
     */
    public void getImage(final String url, final GetImageListener listener) {
        //check if image exsist localy
        final String fileName = URLUtil.guessFileName(url, null, null);
        ModelLocalFiles.loadImageFromFileAsynch(fileName, new ModelLocalFiles.LoadImageFromFileAsynch() {
            @Override
            public void onComplete(Bitmap bitmap) {
                if (bitmap != null) {
                    Log.d("TAG", "getImage from local success " + fileName);
                    listener.onSuccess(bitmap);
                } else {
                    modelFirebaseFiles.getImage(url, new GetImageListener() {
                        @Override
                        public void onSuccess(Bitmap image) {
                            String fileName = URLUtil.guessFileName(url, null, null);
                            Log.d("TAG", "getImage from FB success " + fileName);
                            saveImageToFileAsynch(image, fileName);
                            listener.onSuccess(image);
                        }

                        @Override
                        public void onFail() {
                            Log.d("TAG", "getImage from FB fail ");
                            listener.onFail();
                        }
                    });

                }
            }
        });
    }



    /**
     * Create an account to Firebase ASYNC.
     * @param email the users email.
     * @param password the users password.
     * @param callback see {@link AuthenticationUser.CreateAccountCallback}.
     */
    public void createAccount(String email, String password, final AuthenticationUser.CreateAccountCallback callback){
        authenticationUser.createAccount(email, password, new AuthenticationUser.CreateAccountCallback() {
            @Override
            public void onComplete() {
                callback.onComplete();
            }

            @Override
            public void onFail() {
                callback.onFail();
            }
        });
    }

    /**
     * sign in user to Firebase ASYNC.
     * @param email the users email.
     * @param password the users password.
     * @param callback see {@link AuthenticationUser.SignInCallback}.
     */
    public void signIn(String email, String password, final AuthenticationUser.CreateAccountCallback callback){
        authenticationUser.signIn(email, password, new AuthenticationUser.SignInCallback() {
            @Override
            public void onComplete() {
                callback.onComplete();
            }

            @Override
            public void onFail() {
                callback.onFail();
            }
        });
    }

    /**
     * Check if there is a session with Firebase.
     * @return true if the user is connected, else false.
     */
    public boolean isConnectedUser(){
        return authenticationUser.isConnectedUser();
    }

    public String getConnectedUserEmail(){
        return authenticationUser.getConnectedUserEmail();
    }

    /**
     * Get a connected user username
     * @return the username of the connected user
     */
    public String getConnectedUserUsername(){
        return authenticationUser.getConnectedUserUsername();
    }

    /**
     * Finish login session with Firebase.
     */
    public void signOut() {
        authenticationUser.signOut();
    }

    /**
     * Check if the connected account is admin
     * @return
     */
    public boolean isAdmin(){
        return Model.instance.getConnectedUserEmail().equals("benmazliach@gmail.com");
    }


}




