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

import static com.example.ben.final_project.Model.ModelLocalFiles.saveImageToFile;

/**
 * Created by Ben on 16/06/2017.
 */

public class Model {

    static final int MAX_LENGTH = 15;
    public final static Model instance = new Model();

    private CompanyModel companyModel;
    private ArticleModel articleModel;
    private UserModel userModel;
    private ModelSQL modelSql;
    private ModelArticleAndCommentFirebase modelArticleAndCommentFirebase;
    private ModelCompanyAndCarFirebase modelCompanyAndCarFirebase;
    private ModelFirebaseFiles modelFirebaseFiles;
    private ModelLocalFiles modelLocalFiles;

    private Model(){
        //articleModel = new ArticleModel();
        companyModel = new CompanyModel();
        userModel = new UserModel();

        modelSql = new ModelSQL(MyApplication.getContext());
        modelArticleAndCommentFirebase = new ModelArticleAndCommentFirebase();
        modelCompanyAndCarFirebase = new ModelCompanyAndCarFirebase();
        modelFirebaseFiles = new ModelFirebaseFiles();
        modelLocalFiles = new ModelLocalFiles();
        synchArticlesDbAndregisterArticlesUpdates();
        synchArticlesCommentsDbAndregisterArticlesCommentsUpdates();
        synchCompaniesDbAndregisterArticlesUpdates();
        synchCompaniesCarsDbAndregisterArticlesCommentsUpdates();
    }

    public interface GetArticleCommentsCallback {
        void onComplete(List<Comment> list);
        void onCancel();
    }

    public void getArticleComments(String articleId,final GetArticleCommentsCallback callback) {
        if(isNetworkAvailable()) {
            modelArticleAndCommentFirebase.getAllArticleCommentsAndObserve(articleId, new ModelArticleAndCommentFirebase.GetAllArticleCommentsAndObserveCallback() {
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

    public interface GetAllArticlesAndObserveCallback {
        void onComplete(List<Article> list);
        void onCancel();
    }

    public void getAllArticles(final GetAllArticlesAndObserveCallback callback){
        //return ArticleSQL.getAllArticles(modelSql.getReadableDatabase());
        List<Article> data = ArticleSQL.getAllArticles(modelSql.getReadableDatabase());
        callback.onComplete(data);
    }

    private void synchArticlesDbAndregisterArticlesUpdates() {
        //1. get local lastUpdateTade
        SharedPreferences pref = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        final double lastUpdateDate = pref.getFloat("ArticlesLastUpdateDate",0);
        Log.d("TAG","lastUpdateDate: " + lastUpdateDate);

        modelArticleAndCommentFirebase.registerArticlesUpdates(lastUpdateDate,new ModelArticleAndCommentFirebase.RegisterArticlesUpdatesCallback() {
            @Override
            public void onArticleUpdate(Article article) {
                //3. update the local db
                if(ArticleSQL.getArticle(modelSql.getReadableDatabase(),article.articleID) != null)
                    ArticleSQL.editArticle(modelSql.getWritableDatabase(),article);
                else
                    ArticleSQL.addNewArticle(modelSql.getWritableDatabase(),article);
                //4. update the lastUpdateTade
                SharedPreferences pref = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
                final double lastUpdateDate = pref.getFloat("ArticlesLastUpdateDate",0);
                if (lastUpdateDate < article.lastUpdateDate){
                    SharedPreferences.Editor prefEd = MyApplication.getContext().getSharedPreferences("TAG",
                            Context.MODE_PRIVATE).edit();
                    prefEd.putFloat("ArticlesLastUpdateDate", (float) article.lastUpdateDate);
                    prefEd.commit();
                    Log.d("TAG","ArticlesLastUpdateDate: " + article.lastUpdateDate);
                }

                EventBus.getDefault().post(new UpdateArticleEvent(article));
            }
        });
    }


    private void synchArticlesCommentsDbAndregisterArticlesCommentsUpdates() {
        //1. get local lastUpdateTade
        SharedPreferences pref = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        final double lastUpdateDate = pref.getFloat("ArticlesCommentsLastUpdateDate",0);
        Log.d("TAG","ArticlesCommentsLastUpdateDate: " + lastUpdateDate);

        modelArticleAndCommentFirebase.registerCommentsUpdates(lastUpdateDate,new ModelArticleAndCommentFirebase.RegisterComentsUpdatesCallback() {
            @Override
            public void onCommentUpdate(Comment comment) {
                //3. update the local db
                CommentSQL.addComment(modelSql.getWritableDatabase(),comment);
                //4. update the lastUpdateTade
                SharedPreferences pref = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
                final double lastUpdateDate = pref.getFloat("ArticlesCommentsLastUpdateDate",0);
                if (lastUpdateDate < comment.lastUpdatedDate){
                    SharedPreferences.Editor prefEd = MyApplication.getContext().getSharedPreferences("TAG",
                            Context.MODE_PRIVATE).edit();
                    prefEd.putFloat("ArticlesCommentsLastUpdateDate", (float) comment.lastUpdatedDate);
                    prefEd.commit();
                    Log.d("TAG","ArticlesCommentsLastUpdateDate: " + comment.lastUpdatedDate);
                }

                EventBus.getDefault().post(new UpdateCommentEvent(comment));
            }
        });
    }


    public class UpdateArticleEvent {
        public final Article article;
        public UpdateArticleEvent(Article article) {
            this.article = article;
        }
    }

    public void addNewArticle(Article article){
        //ArticleSQL.addNewArticle(modelSql.getWritableDatabase(),article);
        modelArticleAndCommentFirebase.addArticle(article);
    }

    public class UpdateCommentEvent {
        public final Comment comment;
        public UpdateCommentEvent(Comment comment) {
            this.comment = comment;
        }
    }

    public void addNewCommentToArticle(String articleId,Comment comment){
        Log.d("TAG","addNewCommentToArticle model");
        //CommentSQL.addComment(modelSql.getWritableDatabase(),comment);
        modelArticleAndCommentFirebase.addComment(comment);
    }

    public void removeArticle(Article article){
        modelArticleAndCommentFirebase.removeArticle(article);
        //ArticleSQL.removeArticle(modelSql.getWritableDatabase(),article);
    }

    public interface GetArticleCallback {
        void onComplete(Article article);
        void onCancel();
    }

    public void getArticle(final String articleId, final GetArticleCallback callback) {
        //return ArticleSQL.getArticle(modelSql.getReadableDatabase(),articleID);
        if(isNetworkAvailable()) {
            modelArticleAndCommentFirebase.getArticle(articleId, new ModelArticleAndCommentFirebase.GetArticleCallback() {
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

    public void editArticle(Article editedArticle) {
        modelArticleAndCommentFirebase.editArticle(editedArticle);
        //ArticleSQL.editArticle(modelSql.getWritableDatabase(),editedArticle);
    }

















    ////////////////////////////////////////////////////////////////

    public void editCompany(Company editedCompany) {
        modelCompanyAndCarFirebase.editCompany(editedCompany);
    }

    public void addNewModelToCompany(String companyId,Car car){
        Log.d("TAG","addNewModelToCompany model");
        modelCompanyAndCarFirebase.addCar(car);
    }

    public void addNewCompany(Company company){
        modelCompanyAndCarFirebase.addCompany(company);
    }




    public interface GetCompanyModelsCallback {
        void onComplete(List<Car> list);
        void onCancel();
    }

    public void getCompanyModels(String companyId,final GetCompanyModelsCallback callback) {
        if(isNetworkAvailable()) {
            modelCompanyAndCarFirebase.getAllCompanyCarsAndObserve(companyId, new ModelCompanyAndCarFirebase.GetAllCompanyCarsAndObserveCallback() {
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
            List<Car> list = CarSQL.getCompanyModels(modelSql.getReadableDatabase(),companyId);
            callback.onComplete(list);
        }
    }


    public void removeCompany(Company company){
        modelCompanyAndCarFirebase.removeCompany(company);
    }


    public interface GetCompanyCallback {
        void onComplete(Company company);
        void onCancel();
    }


    public void getCompany(final String companyID, final GetCompanyCallback callback) {
        //return ArticleSQL.getArticle(modelSql.getReadableDatabase(),articleID);
        if(isNetworkAvailable()) {
            modelCompanyAndCarFirebase.getCompany(companyID, new ModelCompanyAndCarFirebase.GetCompanyCallback() {
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

    public interface GetModelCallback {
        void onComplete(Car car);
        void onCancel();
    }


    public void getCar(final String companyId, final String carId, final GetModelCallback callback) {
        //return ArticleSQL.getArticle(modelSql.getReadableDatabase(),articleID);
        if(isNetworkAvailable()) {
            modelCompanyAndCarFirebase.getCar(carId, new ModelCompanyAndCarFirebase.GetModelCallback() {
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



    public interface GetAllCompaniesAndObserveCallback {
        void onComplete(List<Company> list);
        void onCancel();
    }

    public void getAllCompanies(final GetAllCompaniesAndObserveCallback callback){
        List<Company> data = CompanySQL.getAllCompanies(modelSql.getReadableDatabase());
        callback.onComplete(data);
    }

    public void editCar(Car editedCar) {
        modelCompanyAndCarFirebase.editCar(editedCar);
    }


    public void removeCar(Car car) {
        modelCompanyAndCarFirebase.removeCar(car);
    }


    public class UpdateCompanyEvent {
        public final Company company;
        public UpdateCompanyEvent(Company company) {
            this.company = company;
        }
    }

    public class UpdateCarEvent {
        public final Car car;
        public UpdateCarEvent(Car car) {
            this.car = car;
        }
    }



    private void synchCompaniesDbAndregisterArticlesUpdates() {
        //1. get local lastUpdateTade
        SharedPreferences pref = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        final double lastUpdateDate = pref.getFloat("CompniesLastUpdateDate",0);
        Log.d("TAG","CompniesLastUpdateDate: " + lastUpdateDate);

        modelCompanyAndCarFirebase.registerCompanysUpdates(lastUpdateDate,new ModelCompanyAndCarFirebase.RegisterCompanysUpdatesCallback() {

            @Override
            public void onCarCompanyUpdate(Company company) {
                //3. update the local db
                if(CompanySQL.getCompany(modelSql.getReadableDatabase(),company.companyId) != null)
                    CompanySQL.editCompany(modelSql.getWritableDatabase(),company);
                else
                    CompanySQL.addNewCompany(modelSql.getWritableDatabase(),company);
                //4. update the lastUpdateTade
                SharedPreferences pref = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
                final double lastUpdateDate = pref.getFloat("CompniesLastUpdateDate",0);
                if (lastUpdateDate < company.lastUpdatedDate){
                    SharedPreferences.Editor prefEd = MyApplication.getContext().getSharedPreferences("TAG",
                            Context.MODE_PRIVATE).edit();
                    prefEd.putFloat("CompniesLastUpdateDate", (float) company.lastUpdatedDate);
                    prefEd.commit();
                    Log.d("TAG","CompniesLastUpdateDate: " + company.lastUpdatedDate);
                }

                EventBus.getDefault().post(new UpdateCompanyEvent(company));
            }
        });
    }


    private void synchCompaniesCarsDbAndregisterArticlesCommentsUpdates() {
        //1. get local lastUpdateTade
        SharedPreferences pref = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        final double lastUpdateDate = pref.getFloat("CompaniesCarsLastUpdateDate",0);
        Log.d("TAG","CompaniesCarsLastUpdateDate: " + lastUpdateDate);

        modelCompanyAndCarFirebase.registerCarsUpdates(lastUpdateDate,new ModelCompanyAndCarFirebase.RegisterCarsUpdatesCallback() {
            @Override
            public void onCarUpdate(Car car) {
                //3. update the local db
                if(CarSQL.getCar(modelSql.getReadableDatabase(),car.companyID,car.carID) != null)
                    CarSQL.editCar(modelSql.getWritableDatabase(),car);
                else
                    CarSQL.addCar(modelSql.getWritableDatabase(),car);
                //4. update the lastUpdateTade
                SharedPreferences pref = MyApplication.getContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
                final double lastUpdateDate = pref.getFloat("CompaniesCarsLastUpdateDate",0);
                if (lastUpdateDate < car.lastUpdatedDate){
                    SharedPreferences.Editor prefEd = MyApplication.getContext().getSharedPreferences("TAG",
                            Context.MODE_PRIVATE).edit();
                    prefEd.putFloat("CompaniesCarsLastUpdateDate", (float) car.lastUpdatedDate);
                    prefEd.commit();
                    Log.d("TAG","CompaniesCarsLastUpdateDate: " + car.lastUpdatedDate);
                }

                EventBus.getDefault().post(new UpdateCarEvent(car));
            }
        });
    }


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




    //////////////////////////////////////////////////////////////////////////////
    public static String random() {
        Random generator = new Random();
        String template = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder randomStringBuilder = new StringBuilder();
        char tempChar;
        for (int i = 0; i < MAX_LENGTH; i++){
            tempChar = template.charAt(generator.nextInt(template.length()));
            randomStringBuilder.append(tempChar);
        }
        Log.d("TAG","random articleID = " + randomStringBuilder.toString());
        return randomStringBuilder.toString();
    }


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

        Log.d("TAG","connected = " + (haveConnectedWifi || haveConnectedMobile));

        return haveConnectedWifi || haveConnectedMobile;
    }



    //////////////////////////////////////////////////////////////////
    public interface SaveImageListener {
        void complete(String url);
        void fail();
    }

    public void saveImage(final Bitmap imageBmp, final String name, final SaveImageListener listener) {
        //modelArticleAndCommentFirebase.saveImage(imageBmp, name, new SaveImageListener() {
        modelFirebaseFiles.saveImage(imageBmp, name, new SaveImageListener() {
            @Override
            public void complete(String url) {
                String fileName = URLUtil.guessFileName(url, null, null);
                Log.d("TAG","guessed Filename = " + fileName);
                saveImageToFile(imageBmp,fileName);
                listener.complete(url);
            }

            @Override
            public void fail() {
                listener.fail();
            }
        });


    }


    public interface GetImageListener{
        void onSuccess(Bitmap image);
        void onFail();
    }
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
                            saveImageToFile(image,fileName);
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
