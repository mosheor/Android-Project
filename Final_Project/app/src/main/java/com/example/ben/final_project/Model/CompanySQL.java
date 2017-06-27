package com.example.ben.final_project.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mazliachbe on 27/06/2017.
 */

public class CompanySQL {

    static final String COMPANY_TABLE = "companies";
    static final String COMPANY_ID = "companyId";
    static final String COMPANY_NAME = "name";
    static final String COMPANY_LOGO = "companyLogo";
    static final String COMPANY_DESCRIPTION = "companyDescription";
    static final String COMPANY_LAST_UPDATED = "lastUpdatedDate";
    static final String COMPANY_WAS_DELETED = "wasDeleted";

    static List<Company> getAllCompanies(SQLiteDatabase db) {
        Cursor cursor = db.query(COMPANY_TABLE, null, COMPANY_WAS_DELETED + " = ?", new String[] {"0"}, null, null, null);
        List<Company> list = new LinkedList<Company>();
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COMPANY_ID);
            int nameIndex = cursor.getColumnIndex(COMPANY_NAME);
            int logoIndex = cursor.getColumnIndex(COMPANY_LOGO);
            int descriptionIndex = cursor.getColumnIndex(COMPANY_DESCRIPTION);
            int lastUpdatedIndex = cursor.getColumnIndex(COMPANY_LAST_UPDATED);

            do {
                Company company = new Company();
                company.companyId = cursor.getString(idIndex);
                company.name = cursor.getString(nameIndex);
                company.companyLogo = cursor.getString(logoIndex);
                company.companyDescription = cursor.getString(descriptionIndex);
                company.lastUpdatedDate = cursor.getDouble(lastUpdatedIndex);
                if(company.wasDeleted == false) {
                    company.models = CarSQL.getCompanyModels(db, company.companyId);
                    list.add(company);
                }
            } while (cursor.moveToNext());
        }
        return list;
    }

    static void addNewCompany(SQLiteDatabase db, Company company) {
        ContentValues values = new ContentValues();
        values.put(COMPANY_ID, company.companyId);
        values.put(COMPANY_NAME, company.name);
        values.put(COMPANY_LOGO, company.companyLogo);
        values.put(COMPANY_DESCRIPTION, company.companyDescription);
        values.put(COMPANY_LAST_UPDATED, company.lastUpdatedDate);
        if (company.wasDeleted)
            values.put(COMPANY_WAS_DELETED, 1);
        else
            values.put(COMPANY_WAS_DELETED, 0);

        db.insert(COMPANY_TABLE, COMPANY_ID, values);
    }

    static Company getCompany(SQLiteDatabase db, String companyId) {
        Cursor cursor = db.query(COMPANY_TABLE, null, COMPANY_ID + " = ?", new String[] { companyId }, null, null, null);
        List<Company> list = new LinkedList<Company>();
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COMPANY_ID);
            int nameIndex = cursor.getColumnIndex(COMPANY_NAME);
            int logoIndex = cursor.getColumnIndex(COMPANY_LOGO);
            int descriptionIndex = cursor.getColumnIndex(COMPANY_DESCRIPTION);
            int lastUpdatedIndex = cursor.getColumnIndex(COMPANY_LAST_UPDATED);
            int wasDeletedIndex = cursor.getColumnIndex(COMPANY_WAS_DELETED);

            do {
                if(cursor.getString(idIndex).equals(companyId)) {
                    Company company = new Company();
                    company.companyId = cursor.getString(idIndex);
                    company.name = cursor.getString(nameIndex);
                    company.companyLogo = cursor.getString(logoIndex);
                    company.companyDescription = cursor.getString(descriptionIndex);
                    company.lastUpdatedDate = cursor.getDouble(lastUpdatedIndex);
                    company.wasDeleted = (cursor.getInt(wasDeletedIndex) == 1);
                    company.models = CarSQL.getCompanyModels(db,company.companyId);
                    return company;
                }
                else
                    Log.d("TAG","can not give correct company companyID " + companyId + " from sql");
            } while (cursor.moveToNext());
        }
        return null;
    }

    static public void editCompany(SQLiteDatabase db, Company company){
        ContentValues values = new ContentValues();
        values.put(COMPANY_ID, company.companyId);
        values.put(COMPANY_NAME, company.name);
        values.put(COMPANY_LOGO, company.companyLogo);
        values.put(COMPANY_DESCRIPTION, company.companyDescription);
        values.put(COMPANY_LAST_UPDATED, company.lastUpdatedDate);
        if (company.wasDeleted)
            values.put(COMPANY_WAS_DELETED, 1);
        else
            values.put(COMPANY_WAS_DELETED, 0);

        db.update(COMPANY_TABLE,values, COMPANY_ID + " = ?", new String[] { company.companyId});
    }

    static void onCreate(SQLiteDatabase db) {
        String sql = "create table " + COMPANY_TABLE +
                " (" +
                COMPANY_ID + " TEXT PRIMARY KEY, " +
                COMPANY_NAME + " TEXT, " +
                COMPANY_LOGO + " TEXT, " +
                COMPANY_DESCRIPTION + " TEXT, " +
                COMPANY_LAST_UPDATED + " NUMBER, " +
                COMPANY_WAS_DELETED + " NUMBER);";
        Log.d("TAG","create company table - " + sql);
        db.execSQL(sql);
    }

    static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + COMPANY_TABLE + ";");
        onCreate(db);
    }

}
