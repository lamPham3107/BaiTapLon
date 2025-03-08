package com.example.fruit_store.ui.MyCart;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fruit_store.Adapters.MyCartAdapter;
import com.example.fruit_store.R;
import com.example.fruit_store.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class MyCartFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore database;
    private RecyclerView rcv_my_cart;
    private MyCartAdapter myCartAdapter;
    private List<MyCartModel> myCartModelList;


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
        rcv_my_cart = (RecyclerView) root.findViewById(R.id.rcv_my_cart);
        rcv_my_cart.setLayoutManager(new LinearLayoutManager(getActivity()));

        myCartModelList = new ArrayList<>();
        myCartAdapter = new MyCartAdapter(getActivity() , myCartModelList);
        rcv_my_cart.setAdapter(myCartAdapter);

        if (auth.getCurrentUser() != null) {
            database.collection("AddToCart").document(auth.getCurrentUser().getUid())
                    .collection("CurrentUser").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                    MyCartModel cartModel = documentSnapshot.toObject(MyCartModel.class);
                                    myCartModelList.add(cartModel);
                                }
                                myCartAdapter.notifyDataSetChanged();

                            }
                        }
                    });
        } else {
            Log.e("MyCartFragment", "User not logged in!");
        }

        return root;
    }
}