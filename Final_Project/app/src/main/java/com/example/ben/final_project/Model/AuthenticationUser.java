package com.example.ben.final_project.Model;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.ben.final_project.Activities.LoginActivity;
import com.example.ben.final_project.Activities.RegisterActivity;
import com.example.ben.final_project.MyApplication;
import com.example.ben.final_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

/**
 * Created by mosheor on 25/06/2017.
 */

/**
 * AuthenticationUser class to handle authentication with Firebase
 */
public class AuthenticationUser {

    FirebaseUser connectedUser;
    FirebaseAuth mAuth;

    /**
     * Default ctor
     */
    public AuthenticationUser(){
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Callback fires when Firebase responses for creating an account
     */
    public interface CreateAccountCallback{
        void onComplete();
        void onFail();
    }

    /**
     * Create an account to Firebase ASYNC.
     * @param email the users email.
     * @param password the users password.
     * @param callback see {@link CreateAccountCallback}.
     */
    void createAccount(String email, String password, final CreateAccountCallback callback) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            callback.onComplete();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            callback.onFail();
                        }
                    }
                });
        // [END create_user_with_email]
    }

    /**
     * Callback fires when Firebase responses for sign in an account
     */
    interface SignInCallback{
        void onComplete();
        void onFail();
    }

    /**
     * sign in user to Firebase ASYNC.
     * @param email the users email.
     * @param password the users password.
     * @param callback see {@link SignInCallback}.
     */
     void signIn(String email, String password, final SignInCallback callback) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            connectedUser = mAuth.getCurrentUser();
                            callback.onComplete();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            callback.onFail();
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            callback.onFail();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    /**
     * Check if there is a session with Firebase.
     * @return true if the user is connected, else false.
     */
    boolean isConnectedUser(){
        if(connectedUser!=null)
            return true;
        return false;
    }

    /**
     * Get a connected user email
     * @return the email of the connected user
     */
    String getConnectedUserEmail(){
        if(connectedUser!=null)
            return connectedUser.getEmail();
        return null;
    }

    /**
     * Get a connected user username
     * @return the username of the connected user
     */
    String getConnectedUserUsername(){
        if(connectedUser!=null)
            return connectedUser.getEmail().substring(0,connectedUser.getEmail().indexOf("@"));
        return null;
    }

    /**
     * Finish login session with Firebase.
     */
    void signOut() {
        mAuth.signOut();
        connectedUser = null;
    }
}
