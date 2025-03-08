package com.example.fruit_store.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fruit_store.Adapters.FruitAdapters;
import com.example.fruit_store.models.FruitModel;
import com.example.fruit_store.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView_fruit;
    private FruitAdapters fruitAdapter;
    private List<FruitModel> fruitModelList , filteredList;
    private FirebaseFirestore db;
    private SearchView searchBox;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        db = FirebaseFirestore.getInstance();
        searchBox = root.findViewById(R.id.search_box);
        filteredList = new ArrayList<>();
        recyclerView_fruit = root.findViewById(R.id.fruit_recyclerView);
        recyclerView_fruit.setLayoutManager(new GridLayoutManager(getContext(), 2));
        fruitModelList = new ArrayList<>();
        fruitAdapter = new FruitAdapters(getActivity(), filteredList);
        recyclerView_fruit.setAdapter(fruitAdapter);

        db.collection("Fruit")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            fruitModelList.clear();
                            filteredList.clear();
                            for(QueryDocumentSnapshot document : task.getResult()){
                                FruitModel fruitModel = document.toObject(FruitModel.class);
                                fruitModelList.add(fruitModel);

                            }
                            filteredList.addAll(fruitModelList);
                            fruitAdapter.notifyDataSetChanged();
                        }
                        else {
                            Toast.makeText(getActivity() , "Error" + task.getException(),Toast.LENGTH_SHORT);
                        }
                    }
                });
        // Bắt sự kiện nhập vào SearchView
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

        return root;
    }
    private void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(fruitModelList);
        } else {
            for (FruitModel fruit : fruitModelList) {
                if (fruit.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(fruit);
                }
            }
        }
        fruitAdapter.notifyDataSetChanged();
    }
}