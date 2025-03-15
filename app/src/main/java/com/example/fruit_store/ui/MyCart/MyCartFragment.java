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
import com.example.fruit_store.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MyCartFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore database;
    private RecyclerView rcv_my_cart;
    private MyCartAdapter myCartAdapter;
    private List<MyCartModel> myCartModelList;
    private ProgressBar progressBar;
    private TextView overTotalAmount;
    private Button bt_buy;


    public MyCartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_my_cart, container, false);
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
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
            database.collection("CurrentUser").document(auth.getCurrentUser().getUid())
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
        database.collection("CurrentUser").document(userId)
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

}