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

/**
 * CarSQL handler class for cars table
 */
public class CarSQL {

    //the tables and columns we use as a reference
    static final String CAR_TABLE = "cars";
    static final String CAR_ID = "carID";
    static final String CAR_COMPANY_ID = "companyID";
    static final String CAR_NAME = "carName";
    static final String CAR_COMPANY_NAME = "companyName";
    static final String CAR_PICTURE= "carPicture";
    static final String CAR_DESCRIPTION = "description";
    static final String CAR_CATEGORY = "carCategory";
    static final String CAR_ENGINE_VOLUME = "engineVolume";
    static final String CAR_HP = "hp";
    static final String CAR_POLLUTION = "pollution";
    static final String CAR_PRICE = "price";
    static final String CAR_WARRANTY = "warranty";
    static final String CAR_ZERO_TO_HUNDRED = "zeroToHundred";
    static final String CAR_FUEL_CONSUPTION = "fuelConsumption";
    static final String CAR_LAST_UPDATED = "lastUpdatedDate";
    static final String CAR_WAS_DELETED = "wasDeleted";

    /**
     * get all specific company's cars from cars table by specifying the companyId
     * @param db the SQLiteDatabase readable db
     * @param companyId id of the company
     * @return if there are cars associated to the company,
     *         returns all the company's cars, else - an empty list
     */
    static List<Car> getCompanyCars(SQLiteDatabase db , String companyId) {
        Log.d("TAG","get all company models");
        Cursor cursor = db.query(CAR_TABLE, null, null,null, null, null, null);
        List<Car> list = new LinkedList<Car>();
        if (cursor.moveToFirst()) {
            int carIDIndex = cursor.getColumnIndex(CAR_ID);
            int companyIDIndex = cursor.getColumnIndex(CAR_COMPANY_ID);
            int carNameIndex = cursor.getColumnIndex(CAR_NAME);
            int companyNameIndex = cursor.getColumnIndex(CAR_COMPANY_NAME);
            int carPictureIndex = cursor.getColumnIndex(CAR_PICTURE);
            int carDescriptionIndex = cursor.getColumnIndex(CAR_DESCRIPTION);
            int categoryIndex = cursor.getColumnIndex(CAR_CATEGORY);
            int engineVolumeIndex = cursor.getColumnIndex(CAR_ENGINE_VOLUME);
            int hpIndex = cursor.getColumnIndex(CAR_HP);
            int pollustionIndex = cursor.getColumnIndex(CAR_POLLUTION);
            int priceIndex = cursor.getColumnIndex(CAR_PRICE);
            int warrantyIndex = cursor.getColumnIndex(CAR_WARRANTY);
            int zeroToHundredIndex = cursor.getColumnIndex(CAR_ZERO_TO_HUNDRED);
            int fuelConsuptionIndex = cursor.getColumnIndex(CAR_FUEL_CONSUPTION);
            int lastUpdatedIndex = cursor.getColumnIndex(CAR_LAST_UPDATED);
            int wasDeletedIndex = cursor.getColumnIndex(CAR_WAS_DELETED);

            do {
                if(cursor.getString(companyIDIndex).equals(companyId)) {
                    Car car = new Car();
                    car.carID = cursor.getString(carIDIndex);
                    car.companyID = cursor.getString(companyIDIndex);
                    car.carName = cursor.getString(carNameIndex);
                    car.companyName = cursor.getString(companyNameIndex);
                    car.carPicture = cursor.getString(carPictureIndex);
                    car.description = cursor.getString(carDescriptionIndex);
                    car.carCategory = cursor.getString(categoryIndex);
                    car.engineVolume = cursor.getInt(engineVolumeIndex);
                    car.hp = cursor.getInt(hpIndex);
                    car.pollution = cursor.getInt(pollustionIndex);
                    car.price = cursor.getFloat(priceIndex);
                    car.warranty = cursor.getInt(warrantyIndex);
                    car.zeroToHundred = cursor.getInt(zeroToHundredIndex);
                    car.fuelConsumption = cursor.getFloat(fuelConsuptionIndex);
                    car.lastUpdatedDate = cursor.getDouble(lastUpdatedIndex);
                    car.wasDeleted = (cursor.getDouble(wasDeletedIndex) == 1);
                    list.add(car);
                }
                else
                    Log.d("TAG","can not give correct commnt for company cpmpanyID " + companyId + " from sql");
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * get a car associated to a company from cars table by specifying companyId,carId
     * @param db the SQLiteDatabase readable db
     * @param companyId id of the company
     * @param carId id of the car
     * @return the car fassociated to the company if exists, else null
     */
    static Car getCar(SQLiteDatabase db , String companyId, String carId) {
        Log.d("TAG","get specific model");
        Cursor cursor = db.query(CAR_TABLE, null, null,null, null, null, null);
        if (cursor.moveToFirst()) {
            int carIDIndex = cursor.getColumnIndex(CAR_ID);
            int companyIDIndex = cursor.getColumnIndex(CAR_COMPANY_ID);
            int carNameIndex = cursor.getColumnIndex(CAR_NAME);
            int companyNameIndex = cursor.getColumnIndex(CAR_COMPANY_NAME);
            int carPictureIndex = cursor.getColumnIndex(CAR_PICTURE);
            int carDescriptionIndex = cursor.getColumnIndex(CAR_DESCRIPTION);
            int categoryIndex = cursor.getColumnIndex(CAR_CATEGORY);
            int engineVolumeIndex = cursor.getColumnIndex(CAR_ENGINE_VOLUME);
            int hpIndex = cursor.getColumnIndex(CAR_HP);
            int pollustionIndex = cursor.getColumnIndex(CAR_POLLUTION);
            int priceIndex = cursor.getColumnIndex(CAR_PRICE);
            int warrantyIndex = cursor.getColumnIndex(CAR_WARRANTY);
            int zeroToHundredIndex = cursor.getColumnIndex(CAR_ZERO_TO_HUNDRED);
            int fuelConsuptionIndex = cursor.getColumnIndex(CAR_FUEL_CONSUPTION);
            int lastUpdatedIndex = cursor.getColumnIndex(CAR_LAST_UPDATED);
            int wasDeletedIndex = cursor.getColumnIndex(CAR_WAS_DELETED);

            do {
                if(cursor.getString(companyIDIndex).equals(companyId) &&
                        cursor.getString(carIDIndex).equals(carId)) {
                    Car car = new Car();
                    car.carID = cursor.getString(carIDIndex);
                    car.companyID = cursor.getString(companyIDIndex);
                    car.carName = cursor.getString(carNameIndex);
                    car.companyName = cursor.getString(companyNameIndex);
                    car.carPicture = cursor.getString(carPictureIndex);
                    car.description = cursor.getString(carDescriptionIndex);
                    car.carCategory = cursor.getString(categoryIndex);
                    car.engineVolume = cursor.getInt(engineVolumeIndex);
                    car.hp = cursor.getInt(hpIndex);
                    car.pollution = cursor.getInt(pollustionIndex);
                    car.price = cursor.getFloat(priceIndex);
                    car.warranty = cursor.getInt(warrantyIndex);
                    car.zeroToHundred = cursor.getInt(zeroToHundredIndex);
                    car.fuelConsumption = cursor.getFloat(fuelConsuptionIndex);
                    car.lastUpdatedDate = cursor.getDouble(lastUpdatedIndex);
                    car.wasDeleted = (cursor.getDouble(wasDeletedIndex) == 1);
                    return car;
                }
                else
                    Log.d("TAG","can not give correct commnt for company cpmpanyID " + companyId + " from sql");
            } while (cursor.moveToNext());
        }
        return null;
    }

    /**
     * add a car to cars table
     * @param db the SQLiteDatabase writable db
     * @param car the car obj
     */
    static void addCar(SQLiteDatabase db, Car car) {
        Log.d("TAG","addCar carSql");
        ContentValues values = new ContentValues();
        values.put(CAR_ID, car.carID);
        values.put(CAR_COMPANY_ID, car.companyID);
        values.put(CAR_NAME, car.carName);
        values.put(CAR_COMPANY_NAME, car.companyName);
        values.put(CAR_PICTURE, car.carPicture);
        values.put(CAR_DESCRIPTION, car.description);
        values.put(CAR_CATEGORY, car.carCategory);
        values.put(CAR_ENGINE_VOLUME, car.engineVolume);
        values.put(CAR_HP, car.hp);
        values.put(CAR_POLLUTION, car.pollution);
        values.put(CAR_PRICE, car.price);
        values.put(CAR_WARRANTY, car.warranty);
        values.put(CAR_ZERO_TO_HUNDRED, car.zeroToHundred);
        values.put(CAR_FUEL_CONSUPTION, car.fuelConsumption);
        values.put(CAR_LAST_UPDATED, car.lastUpdatedDate);
        if(car.wasDeleted)
            values.put(CAR_WAS_DELETED, 1);
        else
            values.put(CAR_WAS_DELETED, 0);

        db.insert(CAR_TABLE, CAR_ID, values);
    }

    /**
     * edit a car from cars table
     * @param db the SQLiteDatabase writable db
     * @param car the edited car obj to be saved
     */
    public static void editCar(SQLiteDatabase db, Car car) {
        ContentValues values = new ContentValues();
        values.put(CAR_ID, car.carID);
        values.put(CAR_COMPANY_ID, car.companyID);
        values.put(CAR_NAME, car.carName);
        values.put(CAR_COMPANY_NAME, car.companyName);
        values.put(CAR_PICTURE, car.carPicture);
        values.put(CAR_DESCRIPTION, car.description);
        values.put(CAR_CATEGORY, car.carCategory);
        values.put(CAR_ENGINE_VOLUME, car.engineVolume);
        values.put(CAR_HP, car.hp);
        values.put(CAR_POLLUTION, car.pollution);
        values.put(CAR_PRICE, car.price);
        values.put(CAR_WARRANTY, car.warranty);
        values.put(CAR_ZERO_TO_HUNDRED, car.zeroToHundred);
        values.put(CAR_FUEL_CONSUPTION, car.fuelConsumption);
        values.put(CAR_LAST_UPDATED, car.lastUpdatedDate);
        if(car.wasDeleted)
            values.put(CAR_WAS_DELETED, 1);
        else
            values.put(CAR_WAS_DELETED, 0);

        db.update(CAR_TABLE,values, CAR_ID + " = ?", new String[] { car.carID});
    }

    static void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CAR_TABLE +
                " (" +
                CAR_ID + " TEXT PRIMARY KEY, " +
                CAR_COMPANY_ID + " TEXT, " +
                CAR_NAME + " TEXT, " +
                CAR_COMPANY_NAME + " TEXT, " +
                CAR_PICTURE + " TEXT, " +
                CAR_DESCRIPTION + " TEXT, " +
                CAR_CATEGORY + " TEXT, " +
                CAR_ENGINE_VOLUME + " NUMBER, " +
                CAR_HP + " NUMBER, " +
                CAR_POLLUTION + " NUMBER, " +
                CAR_PRICE + " FLOAT, " +
                CAR_WARRANTY + " NUMBER, " +
                CAR_ZERO_TO_HUNDRED + " FLOAT, " +
                CAR_FUEL_CONSUPTION + " FLOAT, " +
                CAR_LAST_UPDATED + " DOUBLE, " +
                CAR_WAS_DELETED + " NUMBER);");
    }

    static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + CAR_TABLE + ";");
        onCreate(db);
    }


}
