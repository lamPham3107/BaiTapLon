package com.example.fruit_store.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fruit_store.Adapters.FruitAdapters;
import com.example.fruit_store.models.Fruit;
import com.example.fruit_store.R;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private FruitAdapters fruitAdapter;
    private List<Fruit> fruitList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = root.findViewById(R.id.fruit_recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        fruitList = new ArrayList<>();
        fruitList.add(new Fruit("Sweet Marian", "2.45", R.drawable.account));
        fruitList.add(new Fruit("Strawberries", "1.52", R.drawable.account));
        fruitList.add(new Fruit("Grapes", "2.15", R.drawable.account));
        fruitList.add(new Fruit("Orange", "1.50", R.drawable.account));
        fruitList.add(new Fruit("Mango", "2.55", R.drawable.account));

        fruitAdapter = new FruitAdapters(fruitList);
        fruitAdapter = new FruitAdapters(fruitList);
        recyclerView.setAdapter(fruitAdapter);

        return root;
    }

}