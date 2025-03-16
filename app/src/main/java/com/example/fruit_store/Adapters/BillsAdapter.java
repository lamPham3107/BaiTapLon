package com.example.fruit_store.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import java.util.Map;
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
                updateFruitStock(bill.getItems() , () ->{
                    firestore.collection("Bills").document(billId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                int removedPosition = holder.getAdapterPosition();
                                if (removedPosition != RecyclerView.NO_POSITION) {
                                    list_bill.remove(removedPosition);
                                    notifyItemRemoved(removedPosition);
                                    notifyItemRangeChanged(removedPosition, list_bill.size());
                                    Toast.makeText(context, "Đã thanh toán và cập nhật kho!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Lỗi khi xóa hóa đơn!", Toast.LENGTH_SHORT).show();
                                holder.checkBoxPayment.setChecked(false); // Bỏ chọn nếu xóa thất bại
                            });
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
    private void updateFruitStock(List<Map<String, Object>> items, Runnable onSuccess) {
        for (Map<String, Object> item : items) {
            String fruitName = (String) item.get("fruitName");
            Object totalQuantityObj = item.get("totalQuantity");
            int totalQuantity;
            if (totalQuantityObj instanceof Number) {
                totalQuantity = ((Number) totalQuantityObj).intValue();
            } else {
                Log.e("UpdateStock", "Lỗi: totalQuantity không hợp lệ!");
                return;
            }

            firestore.collection("Fruits").whereEqualTo("name", fruitName)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                            Long maxQuantity = queryDocumentSnapshots.getDocuments().get(0).getLong("quantity");

                            if (maxQuantity != null && maxQuantity >= totalQuantity) {
                                int newQuantity = maxQuantity.intValue() - totalQuantity;

                                firestore.collection("Fruits").document(documentId)
                                        .update("quantity", newQuantity)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("UpdateStock", "Cập nhật tồn kho thành công cho " + fruitName);
                                            onSuccess.run();  // 🛠 Chạy tiếp khi cập nhật kho thành công
                                        })
                                        .addOnFailureListener(e ->
                                                Log.e("UpdateStock", "Lỗi cập nhật tồn kho: " + e.getMessage()));
                            } else {
                                Log.e("UpdateStock", "Số lượng tồn kho không hợp lệ cho " + fruitName);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e("UpdateStock", "Lỗi truy vấn sản phẩm: " + e.getMessage()));
        }
    }

}
