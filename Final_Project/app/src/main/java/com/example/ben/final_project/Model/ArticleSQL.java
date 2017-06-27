package com.example.ben.final_project.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mazliachbe on 26/06/2017.
 */

/**
 * ArticleSQL handler class for articles table
 */
public class ArticleSQL {

    //the tables and columns we use as a reference
    static final String ARTICLE_TABLE = "articles";
    static final String ARTICLE_ID = "articleID";
    static final String ARTICLE_IMAGE_URL = "imageUrl";
    static final String ARTICLE_MAIN_TITLE = "mainTitle";
    static final String ARTICLE_SUB_TITLE = "subTitle";
    static final String ARTICLE_AUTHOR = "author";
    static final String ARTICLE_PUBLISH_DATE = "publishDate";
    static final String ARTICLE_CONTENT = "content";
    static final String ARTICLE_LAST_UPDATE = "lastUpdateDate";
    static final String ARTICLE_WAS_DELETED = "wasDeleted";

    /**
     * get all the articles from articles table
     * @param db the SQLiteDatabase readable db
     * @return list of all the articles if exists, else an empty list
     */
    static List<Article> getAllArticles(SQLiteDatabase db) {
        Cursor cursor = db.query(ARTICLE_TABLE, null, ARTICLE_WAS_DELETED + " = ?", new String[]    {"0"}, null, null, null);
        List<Article> list = new LinkedList<Article>();
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(ARTICLE_ID);
            int imageUrlIndex = cursor.getColumnIndex(ARTICLE_IMAGE_URL);
            int mainTitleIndex = cursor.getColumnIndex(ARTICLE_MAIN_TITLE);
            int subTitleIndex = cursor.getColumnIndex(ARTICLE_SUB_TITLE);
            int authorIndex = cursor.getColumnIndex(ARTICLE_AUTHOR);
            int publishedDateIndex = cursor.getColumnIndex(ARTICLE_PUBLISH_DATE);
            int contentIndex = cursor.getColumnIndex(ARTICLE_CONTENT);
            int lastUpdateIndex = cursor.getColumnIndex(ARTICLE_LAST_UPDATE);
            int wasDeletedIndex = cursor.getColumnIndex(ARTICLE_WAS_DELETED);

            do {
                Article article = new Article();
                article.articleID = cursor.getString(idIndex);
                article.author = cursor.getString(authorIndex);
                article.content = cursor.getString(contentIndex);
                article.imageUrl = cursor.getString(imageUrlIndex);
                article.lastUpdateDate = cursor.getDouble(lastUpdateIndex);
                article.mainTitle = cursor.getString(mainTitleIndex);
                article.publishDate = cursor.getDouble(publishedDateIndex);
                article.subTitle = cursor.getString(subTitleIndex);
                article.wasDeleted = (cursor.getInt(wasDeletedIndex) == 1);
                if(article.wasDeleted == false) {
                    article.comments = CommentSQL.getArticleComments(db, article.articleID);
                    list.add(article);
                }
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * add a new article to articles table
     * @param db the SQLiteDatabase writable db
     * @param article the article to be added
     */
    static void addNewArticle(SQLiteDatabase db, Article article) {
        ContentValues values = new ContentValues();
        values.put(ARTICLE_ID, article.articleID);
        values.put(ARTICLE_AUTHOR, article.author);
        values.put(ARTICLE_CONTENT, article.content);
        values.put(ARTICLE_IMAGE_URL, article.imageUrl);
        values.put(ARTICLE_LAST_UPDATE, article.lastUpdateDate);
        values.put(ARTICLE_MAIN_TITLE, article.mainTitle);
        values.put(ARTICLE_PUBLISH_DATE, article.publishDate);
        values.put(ARTICLE_SUB_TITLE, article.subTitle);
        if (article.wasDeleted)
            values.put(ARTICLE_WAS_DELETED, 1);
        else
            values.put(ARTICLE_WAS_DELETED, 0);
        //commentsSql.addArticleComments(article.comments);

        db.insert(ARTICLE_TABLE, ARTICLE_ID, values);
    }

    /**
     * get a specific article from articles table
     * @param db the SQLiteDatabase readable db
     * @param articleId the id of the article
     * @return the article if exists, else null
     */
    static Article getArticle(SQLiteDatabase db, String articleId) {
        Cursor cursor = db.query(ARTICLE_TABLE, null, ARTICLE_ID + " = ?", new String[] { articleId }, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(ARTICLE_ID);
            int imageUrlIndex = cursor.getColumnIndex(ARTICLE_IMAGE_URL);
            int mainTitleIndex = cursor.getColumnIndex(ARTICLE_MAIN_TITLE);
            int subTitleIndex = cursor.getColumnIndex(ARTICLE_SUB_TITLE);
            int authorIndex = cursor.getColumnIndex(ARTICLE_AUTHOR);
            int publishedDateIndex = cursor.getColumnIndex(ARTICLE_PUBLISH_DATE);
            int contentIndex = cursor.getColumnIndex(ARTICLE_CONTENT);
            int lastUpdateIndex = cursor.getColumnIndex(ARTICLE_LAST_UPDATE);
            int wasDeletedIndex = cursor.getColumnIndex(ARTICLE_WAS_DELETED);

            do {
                if(cursor.getString(idIndex).equals(articleId)) {
                    Article article = new Article();
                    article.articleID = cursor.getString(idIndex);
                    article.author = cursor.getString(authorIndex);
                    article.content = cursor.getString(contentIndex);
                    article.imageUrl = cursor.getString(imageUrlIndex);
                    article.lastUpdateDate = cursor.getDouble(lastUpdateIndex);
                    article.mainTitle = cursor.getString(mainTitleIndex);
                    article.publishDate = cursor.getDouble(publishedDateIndex);
                    article.subTitle = cursor.getString(subTitleIndex);
                    article.wasDeleted = (cursor.getInt(wasDeletedIndex) == 1);
                    article.comments = CommentSQL.getArticleComments(db,article.articleID);
                    return article;
                }
                else
                    Log.d("TAG","can not give correct article articleID " + articleId + " from sql");
            } while (cursor.moveToNext());
        }
        return null;
    }

    /**
     * edit a specific article from articles table
     * @param db the SQLiteDatabase readable db
     * @param article the edited article to be saved
     */
    static public void editArticle(SQLiteDatabase db, Article article){
        ContentValues values = new ContentValues();
        values.put(ARTICLE_ID, article.articleID);
        values.put(ARTICLE_AUTHOR, article.author);
        values.put(ARTICLE_CONTENT, article.content);
        values.put(ARTICLE_IMAGE_URL, article.imageUrl);
        values.put(ARTICLE_LAST_UPDATE, article.lastUpdateDate);
        values.put(ARTICLE_MAIN_TITLE, article.mainTitle);
        values.put(ARTICLE_PUBLISH_DATE, article.publishDate);
        values.put(ARTICLE_SUB_TITLE, article.subTitle);
        if (article.wasDeleted)
            values.put(ARTICLE_WAS_DELETED, 1);
        else
            values.put(ARTICLE_WAS_DELETED, 0);

        db.update(ARTICLE_TABLE,values, ARTICLE_ID + " = ?", new String[] { article.articleID});
    }

    static void onCreate(SQLiteDatabase db) {
        String sql = "create table " + ARTICLE_TABLE +
                " (" +
                ARTICLE_ID + " TEXT PRIMARY KEY, " +
                ARTICLE_AUTHOR + " TEXT, " +
                ARTICLE_CONTENT + " TEXT, " +
                ARTICLE_IMAGE_URL + " TEXT, " +
                ARTICLE_LAST_UPDATE + " NUMBER, " +
                ARTICLE_MAIN_TITLE + " TEXT, " +
                ARTICLE_PUBLISH_DATE + " NUMBER, " +
                ARTICLE_SUB_TITLE + " TEXT, " +
                ARTICLE_WAS_DELETED + " NUMBER);";
        db.execSQL(sql);
    }

    static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + ARTICLE_TABLE + ";");
        onCreate(db);
    }


}
