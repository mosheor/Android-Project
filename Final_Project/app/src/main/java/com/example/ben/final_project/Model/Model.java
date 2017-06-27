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

    private UserModel userModel;
    private ModelSQL modelSql;
    private ModelFirebaseArticleAndComment modelFirebaseArticleAndComment;
    private ModelFirebaseCompanyAndCar modelFirebaseCompanyAndCar;
    private ModelFirebaseFiles modelFirebaseFiles;

    private Model(){
        userModel = new UserModel();

        modelSql = new ModelSQL(MyApplication.getContext());
        modelFirebaseArticleAndComment = new ModelFirebaseArticleAndComment();
        modelFirebaseCompanyAndCar = new ModelFirebaseCompanyAndCar();
        modelFirebaseFiles = new ModelFirebaseFiles();

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
     * GetAllArticlesAndObserveCallback interface for the async Firebase
     * function to get all the articles (diffs) and observe
     */
    public interface GetAllArticlesAndObserveCallback {
        void onComplete(List<Article> list);
        void onCancel();
    }

    /**
     * get all the articles from SQLite //todo -async?!
     * @param callback see {@link GetAllArticlesAndObserveCallback}
     */
    //todo - wtf thy callback??, need from firebase or sql??
    public void getAllArticles(final GetAllArticlesAndObserveCallback callback){
        //return ArticleSQL.getAllArticles(modelSql.getReadableDatabase());
        List<Article> data = ArticleSQL.getAllArticles(modelSql.getReadableDatabase());
        callback.onComplete(data);
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
        //ArticleSQL.addNewArticle(modelSql.getWritableDatabase(),article);
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
        //CommentSQL.addComment(modelSql.getWritableDatabase(),comment);
        modelFirebaseArticleAndComment.addComment(comment);
    }

    /**
     * Logically delete an article remotely
     * (then Firebase notifies and it deleted logically locally).
     * @param article the article to be removed.
     */
    public void removeArticle(Article article){
        modelFirebaseArticleAndComment.removeArticle(article);
        //ArticleSQL.removeArticle(modelSql.getWritableDatabase(),article);
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
        //return ArticleSQL.getArticle(modelSql.getReadableDatabase(),articleID);
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
        //ArticleSQL.editArticle(modelSql.getWritableDatabase(),editedArticle);
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
        //return ArticleSQL.getArticle(modelSql.getReadableDatabase(),articleID);
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
        //return ArticleSQL.getArticle(modelSql.getReadableDatabase(),articleID);
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

    //todo - wtf why callback??
    public interface GetAllCompaniesAndObserveCallback {
        void onComplete(List<Company> list);
        void onCancel();
    }
    //todo - wtf why callback??
    public void getAllCompanies(final GetAllCompaniesAndObserveCallback callback){
        List<Company> data = CompanySQL.getAllCompanies(modelSql.getReadableDatabase());
        callback.onComplete(data);
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

    //todo javadoc after complete users
    /////////////////////////////////////////////////////////////////////////////////

    public User getUser(String username){
        return userModel.getUser(username);
    }

    public boolean isValidNewUser(User user) {
        User user1 = getUser(user.userName); //todo chane the param name
        //todo check if the new user is valid
        return false;
    }

    public boolean isValidUser(String username, String password){
        User user = getUser(username);
        if(user != null){
            if(user.password.compareTo(password) == 0){
                return true;
            }
        }
        return false;
    }
    //////////////////////////////////////////////////////////////////////////////////
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
        //modelFirebaseArticleAndComment.saveImage(imageBmp, name, new SaveImageListener() {
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
                if (bitmap != null){
                    Log.d("TAG","getImage from local success " + fileName);
                    listener.onSuccess(bitmap);
                }else {
                    modelFirebaseFiles.getImage(url, new GetImageListener() {
                        @Override
                        public void onSuccess(Bitmap image) {
                            String fileName = URLUtil.guessFileName(url, null, null);
                            Log.d("TAG","getImage from FB success " + fileName);
                            saveImageToFileAsynch(image,fileName);
                            listener.onSuccess(image);
                        }

                        @Override
                        public void onFail() {
                            Log.d("TAG","getImage from FB fail ");
                            listener.onFail();
                        }
                    });

                }
            }
        });
    }

}
