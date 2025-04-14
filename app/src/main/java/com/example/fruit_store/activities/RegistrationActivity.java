package com.example.fruit_store.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fruit_store.R;
import com.example.fruit_store.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {
    private Button bt_sign_up;
    private EditText txt_Name;
    private  EditText txt_Email;
    private EditText txt_Password;
    private EditText txt_Phone_Number;
    private EditText txt_Address;
    private TextView txt_sign_in;

    private FirebaseAuth auth;
    private FirebaseDatabase database;

    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressBar = (ProgressBar) findViewById(R.id.processbar);
        progressBar.setVisibility(View.GONE);

        bt_sign_up = (Button) findViewById(R.id.bt_sign_up);
        txt_Name = (EditText) findViewById(R.id.txt_name);
        txt_Email = (EditText) findViewById(R.id.txt_email);
        txt_Password = (EditText) findViewById(R.id.txt_password);
        txt_Phone_Number = (EditText) findViewById(R.id.txt_phone_number);
        txt_Address = (EditText) findViewById(R.id.txt_address);

        txt_sign_in = (TextView)  findViewById(R.id.txt_sign_in);
        txt_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sign_in_intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(sign_in_intent);
            }
        });
        
        bt_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                creatUser();

            }
        });
        View rootView = findViewById(android.R.id.content);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

    }

    private void creatUser() {
        String userName = txt_Name.getText().toString();
        String userEmail = txt_Email.getText().toString();
        String userPassword = txt_Password.getText().toString();
        String userPhoneNumber = txt_Phone_Number.getText().toString();
        String userAddress = txt_Address.getText().toString();
        String userRole = "user";

        if(TextUtils.isEmpty(userName)){
            Toast.makeText(this, "Họ tên đang rỗng", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userEmail)){
            Toast.makeText(this, "Email đang rỗng", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userPassword)){
            Toast.makeText(this, "Password đang rỗng", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userPhoneNumber)){
            Toast.makeText(this, "SĐT đang rỗng", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userAddress)){
            Toast.makeText(this, "Địa chỉ đang rỗng", Toast.LENGTH_SHORT).show();
            return;
        }
        if(userPassword.length() < 6){
            Toast.makeText(this, "Password phải nhiều hơn 5 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            UserModel userModel = new UserModel(userName,userEmail,userPassword,userPhoneNumber,userAddress,userRole);
                            String id = task.getResult().getUser().getUid();
                            database.getReference().child("Users").child(id).setValue(userModel);
                            Toast.makeText(RegistrationActivity.this, "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            String errorMessage = "Lỗi: ";
                            if (task.getException() != null) {
                                String exceptionMessage = task.getException().getMessage();
                                if (exceptionMessage != null && exceptionMessage.contains("The email address is already in use")) {
                                    errorMessage = "Email này đã được đăng ký.";
                                } else {
                                    errorMessage += exceptionMessage;
                                }
                            }
                            Toast.makeText(RegistrationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void showKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}