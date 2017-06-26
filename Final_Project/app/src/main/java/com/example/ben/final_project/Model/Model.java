package com.example.ben.final_project.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.URLUtil;

import com.example.ben.final_project.MyApplication;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Random;

import static com.example.ben.final_project.Model.ModelFiles.saveImageToFile;

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

    private Model(){
        //articleModel = new ArticleModel();
        companyModel = new CompanyModel();
        userModel = new UserModel();

        modelSql = new ModelSQL(MyApplication.getContext());
        modelArticleAndCommentFirebase = new ModelArticleAndCommentFirebase();
        synchArticlesDbAndregisterArticlesUpdates();
        synchArticlesCommentsDbAndregisterArticlesCommentsUpdates();
    }

    public interface GetArticleCommentsCallback {
        void onComplete(List<Comment> list);
        void onCancel();
    }

    public void getArticleComments(String articleId,final GetArticleCommentsCallback callback) {
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
        modelArticleAndCommentFirebase.addComment(modelSql.getWritableDatabase(),comment);
    }

    public void removeArticle(Article article){
        modelArticleAndCommentFirebase.removeArticle(article);
        //ArticleSQL.removeArticle(modelSql.getWritableDatabase(),article);
    }

    public interface GetArticleCallback {
        void onComplete(Article article);
        void onCancel();
    }

    public void getArticle(String articleId,final GetArticleCallback callback) {
        //return ArticleSQL.getArticle(modelSql.getReadableDatabase(),articleID);
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

    public void editArticle(Article editedArticle) {
        modelArticleAndCommentFirebase.editArticle(editedArticle);
        //ArticleSQL.editArticle(modelSql.getWritableDatabase(),editedArticle);
    }


















    //////////////////////////////////////////////////////////////////
    public interface SaveImageListener {
        void complete(String url);
        void fail();
    }

    public void saveImage(final Bitmap imageBmp, final String name, final SaveImageListener listener) {
        modelArticleAndCommentFirebase.saveImage(imageBmp, name, new SaveImageListener() {
            @Override
            public void complete(String url) {
                String fileName = URLUtil.guessFileName(url, null, null);
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
        ModelFiles.loadImageFromFileAsynch(fileName, new ModelFiles.LoadImageFromFileAsynch() {
            @Override
            public void onComplete(Bitmap bitmap) {
                if (bitmap != null){
                    Log.d("TAG","getImage from local success " + fileName);
                    listener.onSuccess(bitmap);
                }else {
                    modelArticleAndCommentFirebase.getImage(url, new GetImageListener() {
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




    ////////////////////////////////////////////////////////////////

    public boolean editCompany(CarCompany editedCompany) {
        return companyModel.editCompany(editedCompany);
    }

    public boolean editModel(String companyId,Car car) {
        return companyModel.editModel(companyId,car);
    }

    public boolean removeModel(String companyId,String carId) {
        return companyModel.removeModel(companyId,carId);
    }





    public void addNewModel(String companyId,Car car){
        companyModel.addNewModel(companyId,car);
    }

    public void addNewCompany(CarCompany company){
        companyModel.addNewCompany(company);
    }


    public int getCompanyListSize() {
        return companyModel.getCompanyListSize();
    }

    public int getModelsListSize(String companyId) {
        return companyModel.getModelsListSize(companyId);
    }

    public Car getModel(String companyId,String carId) {
        return companyModel.getModel(companyId,carId);
    }

    public boolean removeComoany(String id){
        return companyModel.removeCompany(id);
    }

    public CarCompany getCompany(String companyID) {
        return  companyModel.getCompany(companyID);
    }

    public List<Car> getCompanyModels(String companyID) {
        return companyModel.getCompanyModels(companyID);
    }

    public Car getCar(String companyID,String carID) {
        return companyModel.getCar(companyID, carID);
    }


    public List<CarCompany> getAllCompanies(){
        return companyModel.getAllCompanies();
    }

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

}
