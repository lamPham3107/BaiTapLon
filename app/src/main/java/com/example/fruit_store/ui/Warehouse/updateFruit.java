package com.example.fruit_store.ui.Warehouse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fruit_store.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class updateFruit extends AppCompatActivity {

    private FirebaseFirestore db;
    private StorageReference storageRef;
    private ImageView imgUpdate;
    private EditText edtName, edtPrice, edtQuantity, edtDescription;
    private Button btnUpdate;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private String productName;
    private androidx.appcompat.widget.Toolbar toolbar;

    private Spinner spinnerUnit;


    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    imgUpdate.setImageURI(imageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_fruit);

        toolbar = findViewById(R.id.add_toolbar);

        // bat nut back
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        // Nhận dữ liệu từ Intent
        productName = getIntent().getStringExtra("name");
        String productPrice = getIntent().getStringExtra("price");
        String productQuantity = getIntent().getStringExtra("quantity");
        String productDescription = getIntent().getStringExtra("description");
        String productImage = getIntent().getStringExtra("img_url");

        if (productName == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy sản phẩm!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("Fruits_img").child(productName + ".jpg");

        imgUpdate = findViewById(R.id.imgud);
        edtName = findViewById(R.id.editname);
        edtPrice = findViewById(R.id.editprice);
        edtQuantity = findViewById(R.id.editquantity);
        edtDescription = findViewById(R.id.editreview);
        btnUpdate = findViewById(R.id.btnud);

        spinnerUnit = findViewById(R.id.spinner_unit);

        String productUnit = getIntent().getStringExtra("unit");
        if (productUnit != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.unit_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerUnit.setAdapter(adapter);

            int spinnerPosition = adapter.getPosition(productUnit);
            spinnerUnit.setSelection(spinnerPosition);
        }


        edtName.setText(productName);
        edtPrice.setText(productPrice);
        edtQuantity.setText(productQuantity);
        edtDescription.setText(productDescription);

        if (productImage != null && !productImage.isEmpty()) {
            Glide.with(updateFruit.this).load(productImage).into(imgUpdate);
        } else {
            imgUpdate.setImageResource(R.drawable.image);
        }

        imgUpdate.setOnClickListener(v -> openImagePicker());
        btnUpdate.setOnClickListener(v -> updateProduct());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void updateProduct() {
        String name = edtName.getText().toString().trim();
        String price = edtPrice.getText().toString().trim();
        int quantity = Integer.parseInt(edtQuantity.getText().toString().trim());
        String description = edtDescription.getText().toString().trim();

        if (name.isEmpty() || price.isEmpty() || edtQuantity.getText().toString().trim().isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang cập nhật...");
        progressDialog.show();

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("price", price);
        updates.put("quantity", quantity);
        updates.put("description", description);
        updates.put("unit", spinnerUnit.getSelectedItem().toString());

        db.collection("Fruits").whereEqualTo("name", productName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        db.collection("Fruits").document(documentId)
                                .update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    if (imageUri == null) {
                                        progressDialog.dismiss();
                                        Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();

                                        // Gửi kết quả về Fragment trước đó
                                        Intent resultIntent = new Intent();
                                        setResult(Activity.RESULT_OK, resultIntent);
                                        finish();
                                    } else {
                                        uploadImage(documentId);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(updateFruit.this, "Lỗi khi cập nhật dữ liệu!", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(updateFruit.this, "Lỗi: Không tìm thấy sản phẩm trong cơ sở dữ liệu!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(updateFruit.this, "Lỗi truy vấn dữ liệu!", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadImage(String documentId) {
        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    db.collection("Fruits_img").document(documentId)
                            .update("img_url", uri.toString())
                            .addOnSuccessListener(aVoid -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                // Gửi kết quả về Fragment trước đó
                                Intent resultIntent = new Intent();
                                setResult(Activity.RESULT_OK, resultIntent);
                                finish();
                            });
                })
        ).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(updateFruit.this, "Lỗi khi tải ảnh!", Toast.LENGTH_SHORT).show();
        });
    }
}
