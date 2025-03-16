package com.example.fruit_store.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fruit_store.Adapters.Bill_Info_Adapter;
import com.example.fruit_store.R;

import java.util.List;
import java.util.Map;

public class BillInfoActivity extends AppCompatActivity {

    private RecyclerView rcvBillItems;
    private Bill_Info_Adapter billInfoAdapter;
    private List<Map<String, Object>> billItems;
    private TextView txtTotalPrice;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_info);

        rcvBillItems = findViewById(R.id.rcv_bill_items);
        txtTotalPrice = findViewById(R.id.txt_total_price);
        toolbar = (Toolbar) findViewById(R.id.tb_bill_info);
        // bat nut back
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());


        // Nhận dữ liệu từ Intent
        billItems = (List<Map<String, Object>>) getIntent().getSerializableExtra("bill_items");
        double totalPrice = getIntent().getDoubleExtra("total_price", 0);

        // Hiển thị tổng tiền
        txtTotalPrice.setText("Tổng cộng: " + totalPrice + " VNĐ");

        // Cấu hình RecyclerView
        rcvBillItems.setLayoutManager(new LinearLayoutManager(this));
        billInfoAdapter = new Bill_Info_Adapter(this, billItems);
        rcvBillItems.setAdapter(billInfoAdapter);

    }
}