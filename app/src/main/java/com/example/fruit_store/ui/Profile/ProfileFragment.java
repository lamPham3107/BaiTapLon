package com.example.fruit_store.ui.Profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fruit_store.R;
import com.example.fruit_store.models.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private CircleImageView img_profile;
    private EditText txt_name;
    private EditText txt_phone;
    private EditText txt_address;
    private Button bt_update;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container ,false);

        img_profile = (CircleImageView) root.findViewById(R.id.profile_img);
        img_profile.setVisibility(View.GONE);
        txt_name = (EditText) root.findViewById(R.id.profile_name);
        txt_name.setVisibility(View.GONE);
        txt_phone = (EditText) root.findViewById(R.id.profile_phone);
        txt_phone.setVisibility(View.GONE);
        txt_address = (EditText) root.findViewById(R.id.profile_address);
        txt_address.setVisibility(View.GONE);
        bt_update = (Button) root.findViewById(R.id.bt_update );
        progressBar = (ProgressBar) root.findViewById(R.id.prf_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                        Uri profileUri = result.getData().getData();
                        img_profile.setImageURI(profileUri);

                        final StorageReference reference = storage.getReference().child("profile_picture")
                                .child(FirebaseAuth.getInstance().getUid());

                        reference.putFile(profileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getContext() , "Uploaded" , Toast.LENGTH_SHORT).show();
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                                .child("profileImg").setValue(uri .toString());
                                        Toast.makeText(getContext(), "Profile picture uploaded" , Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
        );

        bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });
        // chon hinh anh lam avatar tu may
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                imagePickerLauncher.launch(intent);
            }
        });
        // load du lieu tu firebase
        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        if (userModel.getProfileImg() != null && !userModel.getProfileImg().isEmpty()) {
                            Glide.with(getContext()).load(userModel.getProfileImg()).into(img_profile);
                        } else {
                            img_profile.setImageResource(R.drawable.profile);
                        }
                        txt_name.setText(userModel.getName());
                        txt_phone.setText(userModel.getPhoneNumber());
                        txt_address.setText(userModel.getAddress());
                        progressBar.setVisibility(View.GONE);
                        img_profile.setVisibility(View.VISIBLE);
                        txt_name.setVisibility(View.VISIBLE);
                        txt_phone.setVisibility(View.VISIBLE);
                        txt_address.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(v);
                return false;
            }
        });

        return root;
    }

    private void uploadImageToFireBase(Uri profileUri) {
    }

    private void updateUserProfile() {
        String name = txt_name.getText().toString();
        String phone = txt_phone.getText().toString();
        String address = txt_address.getText().toString();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(getContext(), "Cần nhập đủ dữ liệu", Toast.LENGTH_SHORT).show();
            return;
        }

        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .child("name").setValue(name);
        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .child("phoneNumber").setValue(phone);
        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .child("address").setValue(address);

        Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();

    }
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }


}