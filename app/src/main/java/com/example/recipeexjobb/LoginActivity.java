package com.example.recipeexjobb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class LoginActivity extends AppCompatActivity {

    //FireBase instances
    private FirebaseAuth mAuth;

    //Edit texts
    private EditText emailView;
    private EditText passwordView;
    private EditText userNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //instantiates fireBase auth
        mAuth = FirebaseAuth.getInstance();

        //check if user is logged in, if so then start main activity.
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            intentMainActivity();
        }

        //instantiate edit text and buttons for login and sign up
        emailView = findViewById(R.id.email);
        passwordView = findViewById(R.id.password);
        userNameView = findViewById(R.id.username);

        //Buttons
        Button signUpButton = findViewById(R.id.signUpButton);
        Button loginButton = findViewById(R.id.loginButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = emailView.getText().toString();
                String userPassword = passwordView.getText().toString();
                String username = userNameView.getText().toString();

                if(userEmail.length() != 0 && userPassword.length() != 0 && username.length() != 0){
                    createUser(userEmail, userPassword, username);
                }
                else {
                    Toast.makeText(LoginActivity.this, "Please fill in username and password", Toast.LENGTH_LONG).show();
                }
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = emailView.getText().toString();
                String userPassword = passwordView.getText().toString();

                if(userEmail.length() != 0 && userPassword.length() != 0){
                    login(userEmail, userPassword);
                }
                else {
                    Toast.makeText(LoginActivity.this, "Please fill in username and password", Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    private void intentMainActivity(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void createUser(final String userEmail, String userPassword, final String username) {
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username).build();

                            user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        intentMainActivity();
                                    }
                                }
                            });

                        }
                        else {
                            Log.d("sign up", "sign up failed");
                        }
                    }
                });
    }


    private void login(String userEmail, String userPassword) {
        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    intentMainActivity();
                }
                else {
                    Log.d("Login", "login unsuccessful");
                }
            }
        });
    }

}
