package com.example.fruit_store.ui.MyCart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.fruit_store.Adapters.MyCartAdapter;
import com.example.fruit_store.R;
import com.example.fruit_store.activities.ThanksActivity;
import com.example.fruit_store.models.BillModel;
import com.example.fruit_store.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyCartFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private FirebaseDatabase database;
    private RecyclerView rcv_my_cart;
    private MyCartAdapter myCartAdapter;
    private List<MyCartModel> myCartModelList;
    private ProgressBar progressBar;
    private TextView overTotalAmount;
    private Button bt_buy;
    private String name , phone , address;
    private Double totalPrice = 0.0;

    public MyCartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_my_cart, container, false);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        bt_buy = root.findViewById(R.id.bt_buy_now);

        progressBar = (ProgressBar) root.findViewById(R.id.cart_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        rcv_my_cart = (RecyclerView) root.findViewById(R.id.rcv_my_cart);
        rcv_my_cart.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcv_my_cart.setVisibility(View.GONE);
        overTotalAmount = root.findViewById(R.id.txt_totalprice);

        myCartModelList = new ArrayList<>();
        myCartAdapter = new MyCartAdapter(getActivity() , myCartModelList);
        rcv_my_cart.setAdapter(myCartAdapter);

        if (auth.getCurrentUser() != null) {
            firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                    .collection("AddToCart").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                    String doucumentId = documentSnapshot.getId();
                                    MyCartModel cartModel = documentSnapshot.toObject(MyCartModel.class);
                                    cartModel.setDoucumentId(doucumentId);
                                    myCartModelList.add(cartModel);
                                    myCartAdapter.notifyDataSetChanged();
                                    rcv_my_cart.setVisibility(View.VISIBLE);
                                }
                                calculatorTotalAmount(myCartModelList);
                                progressBar.setVisibility(View.GONE);
                                rcv_my_cart.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        } else {
            Log.e("MyCartFragment", "User not logged in!");
        }

        bt_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getContext(), ThanksActivity.class);
                updateToBills();
                intent.putExtra("fruitList" ,(Serializable) myCartModelList);
                deleteAllCart();
                startActivity(intent);
            }
        });

        return root;
    }

    private void calculatorTotalAmount(List<MyCartModel> myCartModelList) {

        double totalAmount = 0.0;
        for (MyCartModel myCartModel: myCartModelList){
            totalAmount += myCartModel.getTotalPrice();
        }
        overTotalAmount.setText("Tổng tiền: " + totalAmount + " VNĐ");

    }
    private void deleteAllCart(){
        String userId = auth.getCurrentUser().getUid();
        firestore.collection("CurrentUser").document(userId)
                .collection("AddToCart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                        document.getReference().delete();
                    }
                    myCartModelList.clear();
                    calculatorTotalAmount(myCartModelList);
                    myCartAdapter.notifyDataSetChanged();
                });
    }
    private void updateToBills() {
        String userID = auth.getCurrentUser().getUid();
        totalPrice = 0.0; // Khởi tạo tránh lỗi null

        database.getReference("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    name = snapshot.child("name").getValue(String.class);
                    phone = snapshot.child("phoneNumber").getValue(String.class);
                    address = snapshot.child("address").getValue(String.class);

                    // Khi dữ liệu đã được tải về, ta mới tạo bill
                    createBill(name, phone, address);
                } else {
                    Log.e("updateToBills", "Không tìm thấy dữ liệu người dùng!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("updateToBills", "Lỗi truy vấn dữ liệu: " + error.getMessage());
            }
        });
    }
    private void createBill(String name, String phone, String address) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (MyCartModel cartItem : myCartModelList) {
            Map<String, Object> item = new HashMap<>();
            item.put("fruitName", cartItem.getFruitName());
            item.put("fruitPrice", cartItem.getFruitPrice());
            item.put("totalQuantity", cartItem.getTotalQuantity());
            item.put("totalPrice", cartItem.getTotalPrice());
            item.put("currentDate", cartItem.getCurrentDate());
            item.put("currentTime", cartItem.getCurrentTime());
            items.add(item);

            totalPrice += cartItem.getTotalPrice();
        }

        // Lấy bill ID cuối cùng từ Firestore
        firestore.collection("Bills")
                .orderBy("id", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String newBillId;
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String lastBillId = queryDocumentSnapshots.getDocuments().get(0).getString("id");
                        int lastNumber = Integer.parseInt(lastBillId);
                        newBillId = String.format("%06d", lastNumber + 1); // Định dạng thành 000001, 000002
                    } else {
                        newBillId = "000001"; // Nếu chưa có hóa đơn nào, bắt đầu từ 000001
                    }

                    // Tạo BillModel với ID mới
                    BillModel bill = new BillModel(newBillId, name, phone, address, totalPrice, items);

                    firestore.collection("Bills").document(newBillId)
                            .set(bill)
                            .addOnSuccessListener(unused ->{
                                Log.d("Firestore", "Bill added successfully");
                                }
                            )
                            .addOnFailureListener(e -> Log.e("Firestore", "Lỗi khi thêm bill: " + e.getMessage()));
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Lỗi lấy ID bill: " + e.getMessage()));
    }


}