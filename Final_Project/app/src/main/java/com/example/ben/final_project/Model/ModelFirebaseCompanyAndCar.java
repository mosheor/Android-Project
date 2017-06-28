package com.example.ben.final_project.Model;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.example.ben.final_project.Model.CarSQL.CAR_NAME;
import static com.example.ben.final_project.Model.CompanySQL.COMPANY_DESCRIPTION;
import static com.example.ben.final_project.Model.CompanySQL.COMPANY_ID;
import static com.example.ben.final_project.Model.CompanySQL.COMPANY_LAST_UPDATED;
import static com.example.ben.final_project.Model.CompanySQL.COMPANY_LOGO;
import static com.example.ben.final_project.Model.CompanySQL.COMPANY_NAME;
import static com.example.ben.final_project.Model.CompanySQL.COMPANY_TABLE;
import static com.example.ben.final_project.Model.CompanySQL.COMPANY_WAS_DELETED;
import static com.example.ben.final_project.Model.CarSQL.CAR_CATEGORY;
import static com.example.ben.final_project.Model.CarSQL.CAR_COMPANY_ID;
import static com.example.ben.final_project.Model.CarSQL.CAR_COMPANY_NAME;
import static com.example.ben.final_project.Model.CarSQL.CAR_DESCRIPTION;
import static com.example.ben.final_project.Model.CarSQL.CAR_ENGINE_VOLUME;
import static com.example.ben.final_project.Model.CarSQL.CAR_FUEL_CONSUPTION;
import static com.example.ben.final_project.Model.CarSQL.CAR_HP;
import static com.example.ben.final_project.Model.CarSQL.CAR_ID;
import static com.example.ben.final_project.Model.CarSQL.CAR_LAST_UPDATED;
import static com.example.ben.final_project.Model.CarSQL.CAR_PICTURE;
import static com.example.ben.final_project.Model.CarSQL.CAR_POLLUTION;
import static com.example.ben.final_project.Model.CarSQL.CAR_PRICE;
import static com.example.ben.final_project.Model.CarSQL.CAR_TABLE;
import static com.example.ben.final_project.Model.CarSQL.CAR_WARRANTY;
import static com.example.ben.final_project.Model.CarSQL.CAR_WAS_DELETED;
import static com.example.ben.final_project.Model.CarSQL.CAR_ZERO_TO_HUNDRED;

/**
 * Created by mazliachbe on 26/06/2017.
 */

/**
 * Firebase model that manages companies and cars tables
 */
public class ModelFirebaseCompanyAndCar {

    List<ChildEventListener> listeners = new LinkedList<ChildEventListener>();

    /**
     * Add a new company in the Firebase.
     * @param company the company to be added.
     */
    public void addCompany(Company company) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(COMPANY_TABLE);
        Map<String, Object> values = new HashMap<>();
        values.put(COMPANY_ID, company.companyId);
        values.put(COMPANY_NAME, company.name);
        values.put(COMPANY_LOGO, company.companyLogo);
        values.put(COMPANY_DESCRIPTION, company.companyDescription);
        values.put(COMPANY_LAST_UPDATED, ServerValue.TIMESTAMP);
        values.put(COMPANY_WAS_DELETED, company.wasDeleted);

        myRef.child(company.companyId).setValue(values);
    }

    /**
     * Save an edited company in the Firebase.
     * @param company the edited company to be saved.
     */
    public void editCompany(Company company)
    {
        addCompany(company);
    }

    /**
     * Remove logically a company from the Firebase.
     * @param company the company to be removed.
     */
    public void removeCompany(Company company) {
        company.wasDeleted = true;
        addCompany(company);
    }

    /**
     * Add a new car associated to a company in the Firebase.
     * @param car the car to be added.
     */
    public void addCar(Car car) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(CAR_TABLE);
        Map<String, Object> values = new HashMap<>();
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
        values.put(CAR_WAS_DELETED, car.wasDeleted);

        myRef.child(car.carID).setValue(values);
    }

    /**
     * Save an edited car in Firebase.
     * @param car the edited car to be saved.
     */
    public void editCar(Car car)
    {
        addCar(car);
    }

    /**
     * Remove logically a car from Firebase.
     * @param car the car to be removed.
     */
    public void removeCar(Car car) {
        car.wasDeleted = true;
        addCar(car);
    }

    /**
     * Callback that fires when the company returns from Firebase
     */
    interface GetAllCompanyCarsAndObserveCallback {
        void onComplete(List<Car> list);
        void onCancel();
    }

    /**
     * Get the company's cars from Firebase async.
     * @param companyId the id of the company.
     * @param callback see {@link GetAllCompanyCarsAndObserveCallback}.
     */
    public void getAllCompanyCarsAndObserve(final String companyId, final GetAllCompanyCarsAndObserveCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(CAR_TABLE);
        ValueEventListener listener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Car> list = new LinkedList<Car>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    Car car = snap.getValue(Car.class);
                    if(car.companyID.equals(companyId))
                        list.add(car);
                }
                callback.onComplete(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCancel();
            }
        });
    }

    /**
     * Callback that fires when all the cars associated to a company returns from Firebase.
     */
    interface GetCompanyCallback {
        void onComplete(Company company);
        void onCancel();
    }

    /**
     * Get the company from Firebase async.
     * @param callback see {@link GetCompanyCallback}.
     */
    public void getCompany(final String companyId, final GetCompanyCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(COMPANY_TABLE);
        myRef.child(companyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Company company = dataSnapshot.getValue(Company.class);
                getAllCompanyCarsAndObserve(companyId, new GetAllCompanyCarsAndObserveCallback() {
                    @Override
                    public void onComplete(List<Car> list) {
                        company.models = list;
                        callback.onComplete(company);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCancel();
            }
        });
    }

    /**
     * Callback that fires when a car associated to a company returns from Firebase.
     */
    interface GetModelCallback {
        void onComplete(Car car);
        void onCancel();
    }

    /**
     * Get the company from Firebase async.
     * @param carId the id of the car.
     * @param callback see {@link GetModelCallback}.
     */
    public void getCar(final String carId, final GetModelCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(CAR_TABLE);
        myRef.child(carId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Car car = dataSnapshot.getValue(Car.class);
                callback.onComplete(car);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCancel();
            }
        });
    }

    /**
     * Callback fires when the Firebase return the company
     */
    interface RegisterCompanysUpdatesCallback{
        void onCarCompanyUpdate(Company company);
    }

    /**
     * Register to get all the diffs from companies table Firebase - the logic from the sync classes
     * @param lastUpdateDate get all the companies that companies.lastUpdateDate > lastUpdateDate from sharedPref
     * @param callback see {@link RegisterCompanysUpdatesCallback}
     */
    public void registerCompanysUpdates(double lastUpdateDate,
                                        final RegisterCompanysUpdatesCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(COMPANY_TABLE);
        myRef.orderByChild(COMPANY_LAST_UPDATED).startAt(lastUpdateDate);
        ChildEventListener listener = myRef.orderByChild(COMPANY_LAST_UPDATED).startAt(lastUpdateDate)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d("TAG","onChildAdded called");
                        Company company = dataSnapshot.getValue(Company.class);
                        callback.onCarCompanyUpdate(company);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Company company = dataSnapshot.getValue(Company.class);
                        callback.onCarCompanyUpdate(company);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Company company = dataSnapshot.getValue(Company.class);
                        callback.onCarCompanyUpdate(company);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        Company company = dataSnapshot.getValue(Company.class);
                        callback.onCarCompanyUpdate(company);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        listeners.add(listener);
    }

    /**
     * Callback fires when the Firebase return the car
     */
    interface RegisterCarsUpdatesCallback{
        void onCarUpdate(Car car);
    }

    /**
     * Register to get all the diffs from cars table Firebase - the logic from the sync classes
     * @param lastUpdateDate get all the cars that cars.lastUpdateDate > lastUpdateDate from sharedPref
     * @param callback see {@link RegisterCompanysUpdatesCallback}
     */
    public void registerCarsUpdates(double lastUpdateDate,
                                        final RegisterCarsUpdatesCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(CAR_TABLE);
        myRef.orderByChild(CAR_LAST_UPDATED).startAt(lastUpdateDate);
        ChildEventListener listener = myRef.orderByChild(CAR_LAST_UPDATED).startAt(lastUpdateDate)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d("TAG","onChildAdded called");
                        Car car = dataSnapshot.getValue(Car.class);
                        callback.onCarUpdate(car);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Car car = dataSnapshot.getValue(Car.class);
                        callback.onCarUpdate(car);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Car car = dataSnapshot.getValue(Car.class);
                        callback.onCarUpdate(car);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        Car car = dataSnapshot.getValue(Car.class);
                        callback.onCarUpdate(car);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        listeners.add(listener);
    }
}
