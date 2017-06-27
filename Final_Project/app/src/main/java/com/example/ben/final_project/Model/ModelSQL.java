package com.example.ben.final_project.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mazliachbe on 26/06/2017.
 */

public class ModelSQL extends SQLiteOpenHelper {

    ModelSQL(Context context) {
        super(context, "ddddddddddb.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ArticleSQL.onCreate(db);
        CommentSQL.onCreate(db);
        CompanySQL.onCreate(db);
        CarSQL.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ArticleSQL.onUpgrade(db, oldVersion, newVersion);
        CommentSQL.onUpgrade(db, oldVersion, newVersion);
        CompanySQL.onUpgrade(db, oldVersion, newVersion);
        CarSQL.onUpgrade(db, oldVersion, newVersion);
    }

}
