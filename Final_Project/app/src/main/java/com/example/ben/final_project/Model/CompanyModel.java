package com.example.ben.final_project.Model;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ben on 20/06/2017.
 */

public class CompanyModel {

    private List<CarCompany> companysData = new LinkedList<CarCompany>();

    public CompanyModel(){
        for (int i = 0; i<10;i++){
            CarCompany company = new CarCompany();
            company.id = ""+i;
            company.name = "Company " + i;
            company.companyDescription = "Company " + i + " is the best cars company " +
                    "in the world";
            company.companyLogo = "";
            company.models = new LinkedList<Car>();
            for(int j=0;j<i;j++){
                Car car = new Car();
                car.carID = ""+j;
                car.companyID = ""+i;
                car.companName = company.name;
                car.modelName = "Model number " + j;
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

    public List<CarCompany> getAllCompanies(){
        return companysData;
    }

    public CarCompany getCompany(String companyID) {
        Log.d("TAG","getCompany number " + companyID);
        for(CarCompany company : companysData){
            if (company.id.equals(companyID)) {
                Log.d("TAG","boom");
                return company;
            }
        }
        return null;
    }

    public List<Car> getCompanyModels(String companyID) {
        Log.d("TAG","getCompany number " + companyID);
        for(CarCompany company : companysData){
            if (company.id.equals(companyID)) {
                Log.d("TAG","boom");
                return company.models;
            }
        }
        return null;
    }

    public Car getCar(String companyID,String carID) {
        Log.d("TAG","getCar company number " + companyID + " and car number " + carID);
        for(CarCompany company : companysData){
            if (company.id.equals(companyID)) {
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

    public void addNewCompany(CarCompany company){
        companysData.add(company);
    }

    public int getCompanyListSize() {
        return companysData.size();
    }

    public boolean editCompany(CarCompany editedCompany) {
        Log.d("TAG","editedCompany number " + editedCompany.id);
        for(CarCompany company : companysData){
            if (company.id.equals(editedCompany.id)) {
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
            CarCompany company = companysData.get(i);
            if (company.id.equals(id))
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
            CarCompany company = companysData.get(i);
            if (company.id.equals(companyId))
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
        for(CarCompany company : companysData){
            if (company.id.equals(companyId)) {
                Log.d("TAG","boom");
                company.models.add(car);
            }
        }
    }

    public int getModelsListSize(String companyId) {
        return getCompany(companyId).models.size();
    }

    public Car getModel(String companyId,String carId) {
        for(CarCompany company : companysData){
            if (company.id.equals(companyId)) {
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
        for(CarCompany company : companysData){
            if (companyId.equals(company.id)) {
                for(Car car : company.models){
                    if (car.carID.equals(editCar.carID)) {
                        car.modelName = editCar.modelName;
                        car.hp = editCar.hp;
                        car.pollution = editCar.pollution;
                        car.fuelConsumption = editCar.fuelConsumption;
                        car.zeroToHundrend = editCar.zeroToHundrend;
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
