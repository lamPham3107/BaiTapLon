package com.example.fruit_store.ui.Warehouse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fruit_store.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class addFruit extends AppCompatActivity {

    private ImageView imgProduct;
    private EditText edtName, edtPrice, edtQuantity, edtDescription;
    private Button btnAddProduct;
    private Uri imageUri;
    private Toolbar toolbar;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private static final int PICK_IMAGE_REQUEST = 1;

    private Spinner spinnerUnit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_fruit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ View
        imgProduct = findViewById(R.id.imgadd);
        edtName = findViewById(R.id.editname);
        edtPrice = findViewById(R.id.editprice);
        edtQuantity = findViewById(R.id.editquantity);
        edtDescription = findViewById(R.id.editreview);
        btnAddProduct = findViewById(R.id.btnupdate);
        toolbar = findViewById(R.id.add_toolbar);
        // bat nut back
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("Fruits_img");
        progressDialog = new ProgressDialog(this);

        spinnerUnit = findViewById(R.id.spinner_unit);


        // Chọn ảnh từ thư viện
        imgProduct.setOnClickListener(view -> openFileChooser());

        // Xử lý thêm sản phẩm
        btnAddProduct.setOnClickListener(view -> {
            if (validateInputs()) {
                uploadImageToFirebase();
            }
        });
    }

    // Kiểm tra thông tin đầu vào
    private boolean validateInputs() {
        if (edtName.getText().toString().trim().isEmpty() ||
                edtPrice.getText().toString().trim().isEmpty() ||
                edtQuantity.getText().toString().trim().isEmpty() ||
                edtDescription.getText().toString().trim().isEmpty()) {

            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (imageUri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Chọn ảnh từ thư viện
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Nhận ảnh sau khi chọn
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgProduct.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // Upload ảnh lên Firebase Storage
    private void uploadImageToFirebase() {
        progressDialog.setMessage("Đang tải ảnh...");
        progressDialog.show();

        String imageName = UUID.randomUUID().toString();
        StorageReference fileReference = storageReference.child(imageName);

        fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    addProductToFirestore(imageUrl);
                })
        ).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(addFruit.this, "Lỗi tải ảnh!", Toast.LENGTH_SHORT).show();
        });
    }

    // Thêm sản phẩm vào Firestore
    private void addProductToFirestore(String imageUrl) {
        progressDialog.setMessage("Đang thêm sản phẩm...");
        progressDialog.show();

        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("img_url", imageUrl);
        productMap.put("name", edtName.getText().toString().trim());
        productMap.put("price", edtPrice.getText().toString().trim());
        productMap.put("quantity", Integer.parseInt(edtQuantity.getText().toString().trim()));
        productMap.put("description", edtDescription.getText().toString().trim());
        productMap.put("unit", spinnerUnit.getSelectedItem().toString());

        firestore.collection("Fruits").document().set(productMap)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(addFruit.this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();

                        // Gửi kết quả về Fragment trước đó
                        Intent resultIntent = new Intent();
                        setResult(Activity.RESULT_OK, resultIntent);

                        // Đóng activity
                        finish();
                    } else {
                        Toast.makeText(addFruit.this, "Lỗi khi thêm sản phẩm!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
