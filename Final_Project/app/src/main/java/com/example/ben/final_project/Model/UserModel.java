package com.example.ben.final_project.Model;

import java.util.LinkedList;
import java.util.List;

public class UserModel {

    List<User> userData = new LinkedList<User>();

    public UserModel(){
        User user = new User();
        user.birthDate = "15/12/1995";
        user.email = "bbb@gmail.com";
        user.firstName = "bobo";
        user.lastName = "obob";
        user.password = "123456";
        user.userName = "bobo";
        user.userType = "ADMIN";
        userData.add(user);
    }

    public User getUser(String username) {
        for (User user : userData) {
            if(user.userName.compareTo(username) == 0)
                return user;
        }
        return null;
    }
}
