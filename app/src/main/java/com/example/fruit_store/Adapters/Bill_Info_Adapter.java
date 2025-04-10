package com.example.fruit_store.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fruit_store.R;
import java.util.List;
import java.util.Map;

public class Bill_Info_Adapter extends RecyclerView.Adapter<Bill_Info_Adapter.BillItemViewHolder> {
    private Context context;
    private List<Map<String, Object>> billItems;


    public Bill_Info_Adapter(Context context, List<Map<String, Object>> billItems) {
        this.context = context;
        this.billItems = billItems;
    }

    @NonNull
    @Override
    public BillItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bill_info, parent, false);
        return new BillItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillItemViewHolder holder, int position) {
        Map<String, Object> item = billItems.get(position);

        String name = (String) item.get("fruitName");
        Number totalQuantityObj = (Number) item.get("totalQuantity");
        int totalQuantity = totalQuantityObj != null ? totalQuantityObj.intValue() : 0;
        String price = (String) item.get("fruitPrice");
        Number totalPriceObj = (Number) item.get("totalPrice");
        int totalPrice = totalPriceObj != null ? totalPriceObj.intValue() : 0;

        holder.txtName.setText(name);
        holder.txtQuantity.setText(String.valueOf(totalQuantity));
        holder.txtPrice.setText( price);
        holder.txtTotal.setText(String.format("%d VNĐ", totalPrice));
    }

    @Override
    public int getItemCount() {
        return billItems.size();
    }

    public static class BillItemViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtQuantity, txtPrice, txtTotal ;

        public BillItemViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_name_info);
            txtQuantity = itemView.findViewById(R.id.txt_quantity_info);
            txtPrice = itemView.findViewById(R.id.txt_price_info);
            txtTotal = itemView.findViewById(R.id.txt_total_info);
        }
    }
}
