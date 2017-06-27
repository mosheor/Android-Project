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

public class ModelCompanyAndCarFirebase {

    List<ChildEventListener> listeners = new LinkedList<ChildEventListener>();

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

    public void editCompany(Company company)
    {
        addCompany(company);
    }

    public void removeCompany(Company company) {
        company.wasDeleted = true;
        addCompany(company);
    }

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

    public void editCar(Car car)
    {
        addCar(car);
    }

    public void removeCar(Car car) {
        car.wasDeleted = true;
        addCar(car);
    }

    interface GetAllCompanyCarsAndObserveCallback {
        void onComplete(List<Car> list);
        void onCancel();
    }

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

    interface GetCompanyCallback {
        void onComplete(Company company);
        void onCancel();
    }

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

    interface GetModelCallback {
        void onComplete(Car car);
        void onCancel();
    }

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


    interface RegisterCompanysUpdatesCallback{
        void onCarCompanyUpdate(Company company);
    }

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

    interface RegisterCarsUpdatesCallback{
        void onCarUpdate(Car car);
    }

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
