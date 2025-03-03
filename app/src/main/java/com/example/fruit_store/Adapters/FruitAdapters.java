package com.example.fruit_store.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fruit_store.R;
import com.example.fruit_store.models.Fruit;

import java.util.List;
public class FruitAdapters extends RecyclerView.Adapter<FruitAdapters.FruitViewHolder>{
    private List<Fruit> fruitsList;

    public FruitAdapters(List<Fruit> fruitsList) {
        this.fruitsList = fruitsList;
    }

    public static class FruitViewHolder extends RecyclerView.ViewHolder {
        ImageView fruitImage;
        TextView fruitName, fruitPrice;

        public FruitViewHolder(View itemView) {
            super(itemView);
            fruitImage = itemView.findViewById(R.id.fruitImage);
            fruitName = itemView.findViewById(R.id.fruitName);
            fruitPrice = itemView.findViewById(R.id.fruitPrice);
        }
    }

    @NonNull
    @Override
    public FruitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fruit, parent, false);
        return new FruitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FruitViewHolder holder, int position) {
        Fruit fruit = fruitsList.get(position);
        holder.fruitName.setText(fruit.getFruit_Name());
        holder.fruitPrice.setText("$" + fruit.getFruit_Price());
        holder.fruitImage.setImageResource(fruit.getImage_Res());
    }

    @Override
    public int getItemCount() {
        return fruitsList.size();
    }
}
