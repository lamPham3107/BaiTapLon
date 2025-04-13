package com.example.fruit_store.ui.Warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fruit_store.Adapters.FruitAdapters;
import com.example.fruit_store.Adapters.WarehouseAdapter;
import com.example.fruit_store.R;
import com.example.fruit_store.models.FruitModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class WarehouseFragment extends Fragment {
    private static final int REQUEST_CODE_ADD_PRODUCT = 100;
    private static final int REQUEST_CODE_UPDATE = 100;

    private RecyclerView recyclerView_Warehouse;
    private WarehouseAdapter warehouseAdapter;
    private List<FruitModel> listWare,filteredList;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Button btnAdd;
    private ProgressBar progressBar;
    private SearchView searchBox;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_warehouse, container, false);

        progressBar = (ProgressBar) root.findViewById(R.id.warehouse_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        // Khởi tạo RecyclerView
        recyclerView_Warehouse = root.findViewById(R.id.ware_recylerview);
        recyclerView_Warehouse.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView_Warehouse.setVisibility(View.GONE);

        // Khởi tạo danh sách & adapter
        listWare = new ArrayList<>();
        filteredList = new ArrayList<>();

        warehouseAdapter = new WarehouseAdapter(getActivity(), filteredList, updateLauncher);
        recyclerView_Warehouse.setAdapter(warehouseAdapter);

        searchBox = root.findViewById(R.id.search);
        // Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Button thêm sản phẩm
        btnAdd = root.findViewById(R.id.btnadd);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), addFruit.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_PRODUCT);
        });


        // lay du lieu tu firebase truyen vao listware , filterList de loc tim kiem
        db.collection("Fruits")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            listWare.clear();
                            filteredList.clear();
                            for(QueryDocumentSnapshot document : task.getResult()){
                                FruitModel fruitModel = document.toObject(FruitModel.class);
                                listWare.add(fruitModel);

                            }
                            filteredList.addAll(listWare);
                            warehouseAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            recyclerView_Warehouse.setVisibility(View.VISIBLE);
                        }
                        else {
                            Toast.makeText(getActivity() , "Error" + task.getException(),Toast.LENGTH_SHORT);
                        }
                    }
                });

        //bat su kien tim kiem
        searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
        // Load danh sách sản phẩm từ Firestore
        loadProductList();
        return root;
    }

    // Bắt sự kiện nhập vào SearchView
    private void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(listWare);
        } else {
            for (FruitModel fruit : listWare) {
                if (fruit.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(fruit);
                }
            }
        }
        warehouseAdapter.notifyDataSetChanged();
    }

    // Phương thức tải danh sách sản phẩm từ Firestore
    private void loadProductList() {
        db.collection("Fruits")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        filteredList.clear();// Xóa dữ liệu cũ
                        listWare.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            FruitModel warelist = document.toObject(FruitModel.class);
                            listWare.add(warelist);
                        }
                        filteredList.addAll(listWare);
                        warehouseAdapter.notifyDataSetChanged(); // Chỉ cập nhật sau khi load xong
                        progressBar.setVisibility(View.GONE);
                        recyclerView_Warehouse.setVisibility(View.VISIBLE);
                    } else {
                        Log.e("WarehouseFetch", "Lỗi truy cập dữ liệu ", task.getException());
                        Toast.makeText(getActivity(), "Lỗi: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Nhận kết quả từ AddProduct để làm mới danh sách
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == REQUEST_CODE_ADD_PRODUCT )
                && resultCode == getActivity().RESULT_OK) {
            loadProductList(); // Tải lại danh sách sản phẩm sau khi cập nhật
        }
    }

    //Cap nhat
    private final ActivityResultLauncher<Intent> updateLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    loadProductList(); // Tải lại danh sách sau khi cập nhật sản phẩm
                }
            }
    );

}
