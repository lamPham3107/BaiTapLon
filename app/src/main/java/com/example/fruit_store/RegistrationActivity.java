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

public class RegistrationActivity extends AppCompatActivity {
    private Button bt_sign_up;
    private EditText txt_Name;
    private  EditText txt_Email;
    private EditText txt_Password;
    private TextView txt_sign_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        bt_sign_up = (Button) findViewById(R.id.bt_sign_up);
        txt_Name = (EditText) findViewById(R.id.txt_name);
        txt_Email = (EditText) findViewById(R.id.txt_email);
        txt_Password = (EditText) findViewById(R.id.txt_password);
        txt_sign_in = (TextView)  findViewById(R.id.txt_sign_in);

        txt_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sign_in_intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(sign_in_intent);
            }
        });
    }
}