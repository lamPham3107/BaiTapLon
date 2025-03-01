package com.example.fruit_store.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fruit_store.MainActivity;
import com.example.fruit_store.R;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        }
    }
    public void login(View view) {
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
    }

    public void registration(View view) {
        startActivity(new Intent(HomeActivity.this, RegistrationActivity.class));
    }
}