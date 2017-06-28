package com.example.ben.final_project.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mazliachbe on 26/06/2017.
 */

/**
 * the super ModelSQL class which handles the SQLite db of the app
 */
public class ModelSQL extends SQLiteOpenHelper {

    ModelSQL(Context context) {
        super(context, "FinalProjectDatabase11111.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //classes which handles specific tables
        ArticleSQL.onCreate(db);//articles table
        CommentSQL.onCreate(db);//comments table
        CompanySQL.onCreate(db);//companies table
        CarSQL.onCreate(db);//cars table
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ArticleSQL.onUpgrade(db, oldVersion, newVersion);
        CommentSQL.onUpgrade(db, oldVersion, newVersion);
        CompanySQL.onUpgrade(db, oldVersion, newVersion);
        CarSQL.onUpgrade(db, oldVersion, newVersion);
    }

}