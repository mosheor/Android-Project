package com.example.ben.final_project.Model;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ben on 20/06/2017.
 */

public class CompanyModel {

    private List<Company> companysData = new LinkedList<Company>();

    public CompanyModel(){
        for (int i = 0; i<10;i++){
            Company company = new Company();
            company.companyId = ""+i;
            company.name = "Company " + i;
            company.companyDescription = "Company " + i + " is the best cars company " +
                    "in the world";
            company.companyLogo = "";
            company.models = new LinkedList<Car>();
            for(int j=0;j<i;j++){
                Car car = new Car();
                car.carID = ""+j;
                car.companyID = ""+i;
                car.companyName = company.name;
                car.carName = "Model number " + j;
                car.carPicture = "";
                car.description = "car " + j + "in company " + i;
                car.engineVolume = i * 200 + j * 10;
                car.fuelConsumption = i * j;
                car.hp = i * j * 10;
                car.pollution = i;
                car.carCategory = "מיני";
                company.models.add(car);
            }
            companysData.add(i,company);
        }
    }

    public List<Company> getAllCompanies(){
        return companysData;
    }

    public Company getCompany(String companyID) {
        Log.d("TAG","getCompany number " + companyID);
        for(Company company : companysData){
            if (company.companyId.equals(companyID)) {
                Log.d("TAG","boom");
                return company;
            }
        }
        return null;
    }

    public List<Car> getCompanyModels(String companyID) {
        Log.d("TAG","getCompany number " + companyID);
        for(Company company : companysData){
            if (company.companyId.equals(companyID)) {
                Log.d("TAG","boom");
                return company.models;
            }
        }
        return null;
    }

    public Car getCar(String companyID,String carID) {
        Log.d("TAG","getCar company number " + companyID + " and car number " + carID);
        for(Company company : companysData){
            if (company.companyId.equals(companyID)) {
                for(Car car : company.models) {
                    if(car.carID.equals(carID)) {
                        Log.d("TAG", "boom");
                        return car;
                    }
                }
            }
        }
        return null;
    }

    public void addNewCompany(Company company){
        companysData.add(company);
    }

    public int getCompanyListSize() {
        return companysData.size();
    }

    public boolean editCompany(Company editedCompany) {
        Log.d("TAG","editedCompany number " + editedCompany.companyId);
        for(Company company : companysData){
            if (company.companyId.equals(editedCompany.companyId)) {
                company.name = editedCompany.name;
                company.companyLogo = editedCompany.companyLogo;
                company.companyDescription = editedCompany.companyDescription;
                return true;
            }
        }
        return false;
    }

    public boolean removeCompany(String id){
        for (int i = 0; i < companysData.size(); i++)
        {
            Company company = companysData.get(i);
            if (company.companyId.equals(id))
            {
                companysData.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean removeModel(String companyId,String modelId){
        for (int i = 0; i < companysData.size(); i++)
        {
            Company company = companysData.get(i);
            if (company.companyId.equals(companyId))
            {
                for(int j=0;j<company.models.size();j++) {
                    Car car = company.models.get(j);
                    if(car.carID.equals(modelId)) {
                        company.models.remove(j);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void addNewModel(String companyId,Car car){
        Log.d("TAG","addNewModel company number " + companyId);
        for(Company company : companysData){
            if (company.companyId.equals(companyId)) {
                Log.d("TAG","boom");
                company.models.add(car);
            }
        }
    }

    public int getModelsListSize(String companyId) {
        return getCompany(companyId).models.size();
    }

    public Car getModel(String companyId,String carId) {
        for(Company company : companysData){
            if (company.companyId.equals(companyId)) {
                for(Car car : company.models) {
                    if (car.carID.equals(carId)){
                        Log.d("TAG", "boom");
                        return car;
                    }
                }
            }
        }
        return null;
    }

    public boolean editModel(String companyId,Car editCar) {
        Log.d("TAG","editModel number " + editCar.carID);
        for(Company company : companysData){
            if (companyId.equals(company.companyId)) {
                for(Car car : company.models){
                    if (car.carID.equals(editCar.carID)) {
                        //car = editCar; // todo check if this line can change all the lines down here??
                        car.carName = editCar.carName;
                        car.hp = editCar.hp;
                        car.pollution = editCar.pollution;
                        car.fuelConsumption = editCar.fuelConsumption;
                        car.zeroToHundred = editCar.zeroToHundred;
                        car.carPicture = editCar.carPicture;
                        car.description = editCar.description;
                        car.engineVolume = editCar.engineVolume;
                        car.warranty = editCar.warranty;
                        car.price = editCar.price;
                        car.carCategory = editCar.carCategory;
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
