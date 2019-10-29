package com.example.recipeexjobb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    //All fireBase instances
    private FirebaseAuth mAuth;

    //temp text view and log out button
    private TextView tempTextView;
    private Button logOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instantiates fireBase auth
        mAuth = FirebaseAuth.getInstance();

        //set temp button and text
        tempTextView = findViewById(R.id.textView);
        logOutButton = findViewById(R.id.logOutButton);

        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            //go to login screen
            intentLoginScreen();
        }

        tempTextView.setText(user.getEmail());

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                intentLoginScreen();
            }
        });

    }

    private void intentLoginScreen(){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }


}
