package com.example.ben.final_project.Model;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static com.example.ben.final_project.Model.ArticleSQL.ARTICLE_AUTHOR;
import static com.example.ben.final_project.Model.ArticleSQL.ARTICLE_CONTENT;
import static com.example.ben.final_project.Model.ArticleSQL.ARTICLE_ID;
import static com.example.ben.final_project.Model.ArticleSQL.ARTICLE_IMAGE_URL;
import static com.example.ben.final_project.Model.ArticleSQL.ARTICLE_LAST_UPDATE;
import static com.example.ben.final_project.Model.ArticleSQL.ARTICLE_MAIN_TITLE;
import static com.example.ben.final_project.Model.ArticleSQL.ARTICLE_PUBLISH_DATE;
import static com.example.ben.final_project.Model.ArticleSQL.ARTICLE_SUB_TITLE;
import static com.example.ben.final_project.Model.ArticleSQL.ARTICLE_TABLE;
import static com.example.ben.final_project.Model.ArticleSQL.ARTICLE_WAS_DELETED;
import static com.example.ben.final_project.Model.CommentSQL.COMMENT_ARTICLE_ID;
import static com.example.ben.final_project.Model.CommentSQL.COMMENT_AUTHOR;
import static com.example.ben.final_project.Model.CommentSQL.COMMENT_CONTENT;
import static com.example.ben.final_project.Model.CommentSQL.COMMENT_ID;
import static com.example.ben.final_project.Model.CommentSQL.COMMENT_LAST_UPDATED;
import static com.example.ben.final_project.Model.CommentSQL.COMMENT_TABLE;

/**
 * Created by mazliachbe on 26/06/2017.
 */

public class ModelArticleAndCommentFirebase {

    List<ChildEventListener> listeners = new LinkedList<ChildEventListener>();

    public void addArticle(Article article) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(ARTICLE_TABLE);
        Map<String, Object> values = new HashMap<>();
        values.put(ARTICLE_ID, article.articleID);
        values.put(ARTICLE_AUTHOR, article.author);
        values.put(ARTICLE_CONTENT, article.content);
        values.put(ARTICLE_IMAGE_URL, article.imageUrl);
        values.put(ARTICLE_LAST_UPDATE, ServerValue.TIMESTAMP);
        values.put(ARTICLE_MAIN_TITLE, article.mainTitle);
        values.put(ARTICLE_PUBLISH_DATE, ServerValue.TIMESTAMP);
        values.put(ARTICLE_SUB_TITLE, article.subTitle);
        values.put(ARTICLE_WAS_DELETED, article.wasDeleted);

        myRef.child(article.articleID).setValue(values);
    }

    public void editArticle(Article article)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(ARTICLE_TABLE);
        Map<String, Object> values = new HashMap<>();
        values.put(ARTICLE_ID,article.articleID);
        values.put(ARTICLE_AUTHOR,article.author);
        values.put(ARTICLE_CONTENT,article.content);
        values.put(ARTICLE_IMAGE_URL,article.imageUrl);
        values.put(ARTICLE_LAST_UPDATE,ServerValue.TIMESTAMP);
        values.put(ARTICLE_MAIN_TITLE,article.mainTitle);
        values.put(ARTICLE_PUBLISH_DATE,article.publishDate);
        values.put(ARTICLE_SUB_TITLE,article.subTitle);
        values.put(ARTICLE_WAS_DELETED,article.wasDeleted);

        myRef.child(article.articleID).

        setValue(values);
    }

    public void removeArticle(Article article) {
        article.wasDeleted = true;
        addArticle(article);
    }

    public void addComment(Comment comment) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(COMMENT_TABLE);
        Map<String, Object> values = new HashMap<>();
        values.put(COMMENT_ID, comment.commentID);
        values.put(COMMENT_ARTICLE_ID, comment.articleID);
        values.put(COMMENT_AUTHOR, comment.author);
        values.put(COMMENT_CONTENT, comment.commentContent);
        values.put(COMMENT_LAST_UPDATED, ServerValue.TIMESTAMP);

        myRef.child(comment.commentID).setValue(values);
    }

    interface GetAllArticleCommentsAndObserveCallback {
        void onComplete(List<Comment> list);
        void onCancel();
    }

    public void getAllArticleCommentsAndObserve(final String articleId, final GetAllArticleCommentsAndObserveCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(COMMENT_TABLE);
        ValueEventListener listener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Comment> list = new LinkedList<Comment>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Comment comment = snap.getValue(Comment.class);
                    if(comment.articleID.equals(articleId))
                        list.add(comment);
                }
                callback.onComplete(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCancel();
            }
        });
    }

    interface GetArticleCallback {
        void onComplete(Article article);
        void onCancel();
    }

    public void getArticle(final String articleId, final GetArticleCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(ARTICLE_TABLE);
        myRef.child(articleId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Article article = dataSnapshot.getValue(Article.class);
                callback.onComplete(article);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCancel();
            }
        });
    }

   interface RegisterArticlesUpdatesCallback{
        void onArticleUpdate(Article article);
    }

    public void registerArticlesUpdates(double lastUpdateDate,
                                        final RegisterArticlesUpdatesCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(ARTICLE_TABLE);
        myRef.orderByChild(ARTICLE_LAST_UPDATE).startAt(lastUpdateDate);
        ChildEventListener listener = myRef.orderByChild(ARTICLE_LAST_UPDATE).startAt(lastUpdateDate)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d("TAG","onChildAdded called");
                        Article article = dataSnapshot.getValue(Article.class);
                        callback.onArticleUpdate(article);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Article article = dataSnapshot.getValue(Article.class);
                        callback.onArticleUpdate(article);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Article article = dataSnapshot.getValue(Article.class);
                        callback.onArticleUpdate(article);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        Article article = dataSnapshot.getValue(Article.class);
                        callback.onArticleUpdate(article);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        listeners.add(listener);
    }

    interface RegisterComentsUpdatesCallback{
        void onCommentUpdate(Comment comment);
    }

    public void registerCommentsUpdates(double lastUpdateDate,
                                        final RegisterComentsUpdatesCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(COMMENT_TABLE);
        myRef.orderByChild(COMMENT_LAST_UPDATED).startAt(lastUpdateDate);
        ChildEventListener listener = myRef.orderByChild(COMMENT_LAST_UPDATED).startAt(lastUpdateDate)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d("TAG","onChildAdded called");
                        Comment comment = dataSnapshot.getValue(Comment.class);
                        callback.onCommentUpdate(comment);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Comment comment = dataSnapshot.getValue(Comment.class);
                        callback.onCommentUpdate(comment);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Comment comment = dataSnapshot.getValue(Comment.class);
                        callback.onCommentUpdate(comment);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        Comment comment = dataSnapshot.getValue(Comment.class);
                        callback.onCommentUpdate(comment);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        listeners.add(listener);
    }

}
