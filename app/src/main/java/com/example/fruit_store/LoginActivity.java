package com.example.fruit_store;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {
    private Button bt_sign_up;
    private EditText txt_email_login;
    private  EditText txt_password_login;
    private TextView txt_sign_up;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        bt_sign_up = (Button) findViewById(R.id.bt_sign_in);
        txt_email_login = (EditText)  findViewById(R.id.txt_email_login);
        txt_password_login =(EditText) findViewById(R.id.txt_password_login);
        txt_sign_up =(TextView) findViewById(R.id.txt_sign_up);

        txt_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sign_up_intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(sign_up_intent);
            }
        });
    }
}