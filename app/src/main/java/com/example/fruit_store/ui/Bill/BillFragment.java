package com.example.fruit_store.ui.Bill;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fruit_store.Adapters.BillsAdapter;
import com.example.fruit_store.R;
import com.example.fruit_store.models.BillModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class BillFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private RecyclerView rcv_bill;
    private BillsAdapter billsAdapter;
    private List<BillModel> list_bill;

    private ProgressBar progressBar;

    public BillFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_bill, container, false);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        progressBar = (ProgressBar) root.findViewById(R.id.bill_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        rcv_bill = (RecyclerView) root.findViewById(R.id.rcv_bill);
        rcv_bill.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcv_bill.setVisibility(View.GONE);
        list_bill = new ArrayList<>();
        billsAdapter = new BillsAdapter(getActivity() , list_bill);
        rcv_bill.setAdapter(billsAdapter);

        if(auth.getCurrentUser() != null){
            loadBills();
        }

        return root;
    }

    public void loadBills() {
        firestore.collection("Bills")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        list_bill.clear();
                        if (value != null) {
                            for (QueryDocumentSnapshot doc : value) {
                                try {
                                    // do cu bao loi khong convert duoc sang Map nen phai the nay
                                    String id = doc.getString("id");
                                    String name = doc.getString("name");
                                    String phone = doc.getString("phone");
                                    String address = doc.getString("address");
                                    Double totalPrice = doc.getDouble("totalPrice");
                                    List<Map<String, Object>> items = (List<Map<String, Object>>) doc.get("items");

                                    if (items == null) {
                                        items = new ArrayList<>();
                                    }

                                    BillModel bill_info = new BillModel(id, name, phone, address, totalPrice, items);
                                    list_bill.add(bill_info);
                                } catch (Exception e) {
                                    Log.e("Firestore", "Lỗi khi chuyển đổi dữ liệu hóa đơn: " + e.getMessage());
                                }
                            }
                            billsAdapter.notifyDataSetChanged();
                        }
                        progressBar.setVisibility(View.GONE);
                        rcv_bill.setVisibility(View.VISIBLE);
                    }
                });
    }
}