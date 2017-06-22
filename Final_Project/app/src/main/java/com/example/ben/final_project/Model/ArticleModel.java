package com.example.ben.final_project.Model;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;


public class ArticleModel {

    private List<Article> articlesData = new LinkedList<Article>();

    public ArticleModel(){
        for (int i = 0; i<10;i++){
            Article article = new Article();
            article.id = ""+i;
            article.mainTitle = "Title number " + i;
            article.imageUrl = "image url";
            article.subTitle = "Sub Title number " + i;
            article.author = "Author number " + i;
            article.published_date = "15/05/1999";
            article.content = "content number1\n" +
                    "content number2\n" +
                    "content number3\n" +
                    "content number4\n";
            article.comments = new LinkedList<Comment>();
            for(int j=0;j<i;j++){
                Comment comment = new Comment();
                comment.author = "Author name number " + j;
                comment.commentContent = "Comment content number " + j;
                comment.date = "15/06/2000";
                article.comments.add(comment);
            }
            articlesData.add(i,article);
        }
    }

    public List<Article> getAllArticles(){
        return articlesData;
    }

    public void addNewCommentToArticle(String articleId,Comment comment){
        Log.d("TAG","getAllCommentForArticle article number " + articleId);
        for(Article article : articlesData){
            if (article.id.equals(articleId)) {
                Log.d("TAG","boom");
                article.comments.add(comment);
            }
        }
    }

    public void addNewArticle(Article article){
        articlesData.add(article);
    }

    public Comment getAllCommentForArticle(String articleId,int commentId){
        Log.d("TAG","getAllCommentForArticle article number " + articleId);
        for(Article article : articlesData){
            if (article.id.equals(articleId)) {
                Log.d("TAG","boom");
                return article.comments.get(commentId);
            }
        }
        return null;
    };

    public boolean removeArticle(String id){
        for (int i = 0; i < articlesData.size(); i++)
        {
            Article article = articlesData.get(i);
            if (article.id.equals(id))
            {
                articlesData.remove(i);
                return true;
            }
        }
        return false;
    }

    public Article getArticle(String id) {
        Log.d("TAG","getArticle number " + id);
        for(Article article : articlesData){
            if (article.id.equals(id)) {
                Log.d("TAG","boom");
                return article;
            }
        }
        return null;
    }

    public boolean editArticle(Article editedArticle) {
        Log.d("TAG","editArtile number " + editedArticle.id);
        for(Article article : articlesData){
            if (article.id.equals(editedArticle.id)) {
                article.content = editedArticle.content;
                article.mainTitle = editedArticle.mainTitle;
                article.subTitle = editedArticle.subTitle;
                article.author = editedArticle.author;
                article.published_date = editedArticle.published_date;
                article.imageUrl = editedArticle.imageUrl;
                return true;
            }
        }
        return false;
    }


    public int getArticleListSize() {
        return articlesData.size();
    }
}
