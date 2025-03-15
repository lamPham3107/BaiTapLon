package com.example.fruit_store.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fruit_store.R;
import com.example.fruit_store.activities.DetailActivity;
import com.example.fruit_store.models.FruitModel;

import java.util.List;
import java.util.Random;

public class FruitAdapters extends RecyclerView.Adapter<FruitAdapters.FruitViewHolder>{
    private List<FruitModel> fruitsList;
    private Context context;
    private int[] colors = {
            Color.parseColor("#FFCDD2"), // Đỏ nhạt
            Color.parseColor("#C8E6C9"), // Xanh lá nhạt
            Color.parseColor("#BBDEFB"), // Xanh dương nhạt
            Color.parseColor("#FFECB3"), // Vàng nhạt
            Color.parseColor("#D1C4E9")  // Tím nhạt
    };
    private Random random = new Random();

    public FruitAdapters(Context context, List<FruitModel> fruitsList) {
        this.context = context;
        this.fruitsList = fruitsList;
    }

    public static class FruitViewHolder extends RecyclerView.ViewHolder {
        ImageView fruitImage;
        TextView fruitName, fruitPrice;
        View fruit_item_background;

        public FruitViewHolder(View itemView) {
            super(itemView);
            fruitImage = itemView.findViewById(R.id.fruitImage);
            fruitName = itemView.findViewById(R.id.fruitName);
            fruitPrice = itemView.findViewById(R.id.fruitPrice);
            fruit_item_background = itemView.findViewById(R.id.fruit_linearlayout);
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
        Glide.with(context).load(fruitsList.get(position).getImg_url()).into(holder.fruitImage);
        holder.fruitName.setText(fruitsList.get(position).getName());
        holder.fruitPrice.setText( fruitsList.get(position).getPrice() + " VND");
        int colorIndex = position % colors.length;
        holder.fruit_item_background.setBackgroundColor(colors[colorIndex]);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                Intent intent = new Intent(context , DetailActivity.class);
                intent.putExtra("detail" , fruitsList.get(pos));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return fruitsList.size();
    }
}
