package com.example.ben.final_project.Model;

import android.widget.EditText;

import java.util.List;

/**
 * Created by Ben on 16/06/2017.
 */

public class Model {

    public final static Model instance = new Model();
    private CompanyModel companyModel;
    private ArticleModel articleModel;
    private UserModel userModel;

    private Model(){
        articleModel = new ArticleModel();
        companyModel = new CompanyModel();
        userModel = new UserModel();
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

    public List<CarCompany> getAllCompanies(){
        return companyModel.getAllCompanies();
    }

    public List<Article> getAllArticles(){
        return articleModel.getAllArticles();
    }

    public void addNewArticle(Article article){
        articleModel.addNewArticle(article);
    }

    public void addNewCompany(CarCompany company){
        companyModel.addNewCompany(company);
    }

    public void addNewCommentToArticle(String articleId,Comment comment){
        articleModel.addNewCommentToArticle(articleId,comment);
    }

    public void addNewModel(String companyId,Car car){
        companyModel.addNewModel(companyId,car);
    }

    public Comment getAllCommentForArticle(String articleId,int commentId){
        return articleModel.getAllCommentForArticle(articleId,commentId);
    };

    public boolean removeArticle(String id){
        return articleModel.removeArticle(id);
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

    public Article getArticle(String id) {
        return articleModel.getArticle(id);
    }

    public boolean editArticle(Article editedArticle) {
        return articleModel.editArticle(editedArticle);
    }

    public boolean editCompany(CarCompany editedCompany) {
        return companyModel.editCompany(editedCompany);
    }

    public boolean editModel(String companyId,Car car) {
        return companyModel.editModel(companyId,car);
    }

    public boolean removeModel(String companyId,String carId) {
        return companyModel.removeModel(companyId,carId);
    }

    public int getArticleListSize() {
        return articleModel.getArticleListSize();
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

    public User getUser(String username){
        return userModel.getUser(username);
    }
}
