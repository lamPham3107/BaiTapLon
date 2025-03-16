package com.example.fruit_store.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fruit_store.R;
import com.example.fruit_store.activities.BillInfoActivity;
import com.example.fruit_store.models.BillModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.List;
import java.util.zip.Inflater;

public class BillsAdapter extends RecyclerView.Adapter<BillsAdapter.BillsViewHolder> {
    private List<BillModel> list_bill;
    private Context context;
    private FirebaseFirestore firestore;
    public BillsAdapter(Context context, List<BillModel> list_bill) {
        this.context = context;
        this.list_bill = list_bill;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public BillsAdapter.BillsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill, parent , false);
        return  new BillsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillsAdapter.BillsViewHolder holder, int position) {
        BillModel bill = list_bill.get(position);

        holder.txtBillId.setText("#" + bill.getId());
        holder.txtBillName.setText("Tên Khách Hàng: " + bill.getName());
        holder.txtBillPhone.setText("SĐT: " + bill.getPhone());
        holder.txtBillAddress.setText("Địa chỉ giao hàng: " + bill.getAddress());
        holder.txtBillPrice.setText("Tổng tiền: " + bill.getTotalPrice() + " VNĐ");

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BillInfoActivity.class);
            intent.putExtra("bill_items", (Serializable) bill.getItems());
            intent.putExtra("total_price", bill.getTotalPrice());
            context.startActivity(intent);
        });
        holder.checkBoxPayment.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                String billId = bill.getId();
                firestore.collection("Bills").document(billId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            // Xóa trên RecyclerView sau khi xóa trên Firestore thành công
                            int removedPosition = holder.getAdapterPosition();
                            if (removedPosition != RecyclerView.NO_POSITION) {
                                list_bill.remove(removedPosition);
                                notifyItemRemoved(removedPosition);
                                notifyItemRangeChanged(removedPosition, list_bill.size());
                                Toast.makeText(context, "Đã xóa hóa đơn!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Lỗi khi xóa hóa đơn!", Toast.LENGTH_SHORT).show();
                            holder.checkBoxPayment.setChecked(false); // Bỏ chọn nếu xóa thất bại
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_bill.size();
    }

    public class BillsViewHolder extends RecyclerView.ViewHolder{
        TextView txtBillId, txtBillName, txtBillPhone, txtBillAddress, txtBillPrice, txtInfo;
        CheckBox checkBoxPayment;
        public BillsViewHolder(@NonNull View itemView) {
            super(itemView);
            txtBillId = itemView.findViewById(R.id.txt_bill_id);
            txtBillName = itemView.findViewById(R.id.txt_bill_name_customer);
            txtBillPhone = itemView.findViewById(R.id.txt_bill_phone);
            txtBillAddress = itemView.findViewById(R.id.txt_bill_address);
            txtBillPrice = itemView.findViewById(R.id.txt_bill_price);
            txtInfo = itemView.findViewById(R.id.txt_info);
            checkBoxPayment = itemView.findViewById(R.id.chb_payment);
        }
    }
}
