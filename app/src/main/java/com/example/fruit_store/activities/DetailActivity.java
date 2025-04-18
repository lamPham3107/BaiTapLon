package com.example.fruit_store.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fruit_store.R;
import com.example.fruit_store.models.FruitModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    private ImageView detailImg;
    private TextView txt_price;
    private TextView txt_description;
    private Button bt_addToCart;
    private ImageView img_addFruit;
    private ImageView img_removeFruit;
    private Toolbar toolbar;
    private FruitModel fruitModel = null;

    private TextView txt_quantity;
    private int total_quantity = 1;
    private int total_price = 0;
    private int Max_quantity;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    @Override
    //Hàm khởi tạo
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //Khởi tạo các biến và liên kết với XML
        setContentView(R.layout.activity_detail);

        detailImg = (ImageView) findViewById(R.id.detail_img);
        txt_price = (TextView) findViewById(R.id.detail_price);
        txt_description  = (TextView) findViewById(R.id.content_description);
        bt_addToCart = (Button) findViewById(R.id.add_to_cart);
        img_addFruit =(ImageView) findViewById(R.id.add_fruit);
        img_removeFruit = (ImageView) findViewById(R.id.remove_fruit);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        toolbar = (Toolbar) findViewById(R.id.detail_toolbar);

        // bat nut back
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        txt_quantity = (TextView) findViewById(R.id.quantity);

        // lay du lieu tu intent = key detail
        final Object object = getIntent().getSerializableExtra("detail");
        if(object instanceof FruitModel){
            fruitModel = (FruitModel) object;
        }
        //Hiển thị thông tin sản phẩm lên giao diện
        if(fruitModel != null){
            Glide.with(getApplicationContext()).load(fruitModel.getImg_url()).into(detailImg);
            txt_description.setText(fruitModel.getDescription());
            txt_price.setText(fruitModel.getPrice() + " VNĐ/" + fruitModel.getUnit());
            Max_quantity =fruitModel.getQuantity();
        }
        //Thay đổi giá trị số lượng sản phẩm khi nhấn nút cộng hoặc trừ
        img_addFruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total_quantity++;
                txt_quantity.setText(String.valueOf(total_quantity));
            }
        });
        img_removeFruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(total_quantity > 1){
                    total_quantity--;
                    txt_quantity.setText(String.valueOf(total_quantity));
                }
            }
        });
        //Xử lí nút Thêm sản phẩm vào giỏ hàng
        bt_addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(total_quantity < Max_quantity){
                    total_price = Integer.parseInt(fruitModel.getPrice()) * total_quantity;
                    added_to_cart();
                }
                else{
                    cant_buy(fruitModel.getName(), Max_quantity,fruitModel.getUnit());
                }

            }
        });

    }
    //Hàm thêm sản phẩm vào giỏ hàng
    private void added_to_cart() {
        String saveCurrentDate, saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();

        // dd la ngay trong thang , MM la thang , DD la ngay trong nam , mm la phut
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy" , Locale.getDefault());
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        // bang bam chua du lieu cac thuoc tinh cua fruitModel
        final HashMap<String , Object> cartMap = new HashMap<>();

        //Thêm dữ liệu vào Firestore
        cartMap.put("fruitName", fruitModel.getName());
        cartMap.put("fruitPrice", txt_price.getText().toString());
        cartMap.put("currentDate", saveCurrentDate);
        cartMap.put("currentTime", saveCurrentTime);
        cartMap.put("totalQuantity", total_quantity);
        cartMap.put("totalPrice", total_price);

        // day du lieu cua bang bam len firebase firestore
        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("AddToCart").add(cartMap).addOnCompleteListener(
                        new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                finish();
                            }
                        });

    }
    //Hàm hiển thị thông báo khi không đủ số lượng sản phẩm trong kho
    private void cant_buy(String fruit_name , int fruit_quantity, String fruit_unit) {
        new AlertDialog.Builder(this)
                .setTitle("Xin lỗi quý khách")
                .setMessage("Sản phẩm " + fruit_name + " không đủ số lượng trong kho. " +
                        "Số lượng hiện có là " + fruit_quantity + " " + fruit_unit + ".")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void test() {
        // Test code here
        Toast.makeText(this, "Test", Toast.LENGTH_SHORT).show();
    }


}