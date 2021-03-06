package com.example.ben.final_project.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mazliachbe on 26/06/2017.
 */

/**
 * CommentSQL handler class for comments table
 */
public class CommentSQL {

    //the tables and columns we use as a reference
    static final String COMMENT_TABLE = "comments";
    static final String COMMENT_ID = "commentID";
    static final String COMMENT_AUTHOR = "author";
    static final String COMMENT_CONTENT = "commentContent";
    static final String COMMENT_LAST_UPDATED = "lastUpdatedDate";
    static final String COMMENT_ARTICLE_ID = "articleID";

    /**
     * get all comments associated to an article from comments table by specifying articleId
     * @param db the SQLiteDatabase readable db
     * @param articleId the id of the article
     * @return list of all the comments if exists, else an empty list
     */
    static List<Comment> getArticleComments(SQLiteDatabase db , String articleId) {
        Log.d("TAG","get all articles comment");
        Cursor cursor = db.query(COMMENT_TABLE, null, null,null, null, null, null);
        List<Comment> list = new LinkedList<Comment>();
        if (cursor.moveToFirst()) {
            int commentIDIndex = cursor.getColumnIndex(COMMENT_ID);
            int authorIndex = cursor.getColumnIndex(COMMENT_AUTHOR);
            int commentContentIndex = cursor.getColumnIndex(COMMENT_CONTENT);
            int lastUpdatedIndex = cursor.getColumnIndex(COMMENT_LAST_UPDATED);
            int articleIDIndex = cursor.getColumnIndex(COMMENT_ARTICLE_ID);

            do {
                if(cursor.getString(articleIDIndex).equals(articleId)) {
                    Comment comment = new Comment();
                    comment.commentID = cursor.getString(commentIDIndex);
                    comment.author = cursor.getString(authorIndex);
                    comment.commentContent = cursor.getString(commentContentIndex);
                    comment.lastUpdatedDate = cursor.getDouble(lastUpdatedIndex);
                    comment.articleID = cursor.getString(articleIDIndex);
                    list.add(comment);
                }
                else
                    Log.d("TAG","can not give correct commnt for article articleID " + articleId + " from sql");
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * add a comment to the comments table
     * @param db the SQLiteDatabase writable db
     * @param comment the comment obj to be added
     */
    static void addComment(SQLiteDatabase db, Comment comment) {
        Log.d("TAG","addComment commentsql");
        ContentValues values = new ContentValues();
        values.put(COMMENT_ID, comment.commentID);
        values.put(COMMENT_AUTHOR, comment.author);
        values.put(COMMENT_CONTENT, comment.commentContent);
        values.put(COMMENT_LAST_UPDATED, comment.lastUpdatedDate);
        values.put(COMMENT_ARTICLE_ID, comment.articleID);

        try {
            db.insert(COMMENT_TABLE, COMMENT_ID, values);
        }catch (Exception e){ }
    }

    static void onCreate(SQLiteDatabase db) {
        Log.d("TAG","dddd");
        db.execSQL("create table " + COMMENT_TABLE +
                " (" +
                COMMENT_ID + " TEXT PRIMARY KEY, " +
                COMMENT_AUTHOR + " TEXT, " +
                COMMENT_CONTENT + " TEXT, " +
                COMMENT_LAST_UPDATED + " NUMBER, " +
                COMMENT_ARTICLE_ID + " TEXT);");
    }

    static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + COMMENT_TABLE + ";");
        onCreate(db);
    }

}
