package com.example.ben.final_project.Model;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.ben.final_project.MyApplication;
import com.example.ben.final_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by mazliachbe on 25/06/2017.
 */

public class AuthenticationUser {

    public FirebaseUser getCurrentUser(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser();
    }

    interface AuthentecationCallback{
        void succes();
        void fail();
    }

    public void createAccount(String email, String password, final AuthentecationCallback listener) {
        Log.d("TAG", "createAccount:" + email);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) MyApplication.getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            listener.succes();
                        } else {
                            Log.d("TAG", "createUserWithEmail:failure", task.getException());
                            listener.fail();
                        }
                    }
                });
    }

    public void signIn(String email, String password, final AuthentecationCallback listener) {
        Log.d("TAG", "signIn:" + email);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) MyApplication.getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            listener.succes();
                        } else {
                            Log.d("TAG", "signInWithEmail:failure", task.getException());
                            listener.fail();
                        }
                    }
                });
    }

    public void signOut() {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
    }

   /* public void getCurrentUserData(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = String.valueOf(user.getIdToken(true));
        }
    }*/
}
