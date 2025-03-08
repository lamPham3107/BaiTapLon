package com.example.fruit_store.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fruit_store.R;
import com.example.fruit_store.models.MyCartModel;

import java.util.List;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {
    private Context context;
    private List<MyCartModel> myCartModelList;

    public MyCartAdapter(Context context, List<MyCartModel> myCartModelList) {
        this.context = context;
        this.myCartModelList = myCartModelList;
    }

    @NonNull

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_cart_item , parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.name.setText(myCartModelList.get(position).getFruitName());
        holder.price.setText(myCartModelList.get(position).getFruitPrice());
        holder.date.setText(myCartModelList.get(position).getCurrentDate());
        holder.time.setText(myCartModelList.get(position).getCurrentTime());
        holder.quantity.setText(myCartModelList.get(position).getTotalQuantity());
        holder.total_price.setText(String.valueOf(myCartModelList.get(position).getTotalPrice()));



    }

    @Override
    public int getItemCount() {
        return myCartModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name , price , date , time , quantity , total_price;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.txt_cart_fruitName);
            price = (TextView) itemView.findViewById(R.id.txt_cart_price);
            date = (TextView) itemView.findViewById(R.id.txt_cart_current_date);
            time = (TextView) itemView.findViewById(R.id.txt_cart_current_time);
            quantity = (TextView) itemView.findViewById(R.id.txt_cart_total_quantity);
            total_price = (TextView) itemView.findViewById(R.id.txt_cart_total_price);
        }
    }
}
