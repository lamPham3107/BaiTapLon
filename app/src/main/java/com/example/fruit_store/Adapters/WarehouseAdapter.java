package com.example.fruit_store.Adapters;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fruit_store.R;
import com.example.fruit_store.models.FruitModel;
import com.example.fruit_store.ui.Warehouse.updateFruit;
import com.example.fruit_store.ui.Warehouse.WarehouseFragment;
import com.example.fruit_store.ui.Warehouse.updateFruit;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.List;

public class WarehouseAdapter extends RecyclerView.Adapter<WarehouseAdapter.WarehouseViewHolder> {
    private final ActivityResultLauncher<Intent> updateLauncher;
    Context context;
    List<FruitModel> listWare;

    private int[] colors = {
            Color.parseColor("#FFCDD2"), // Đỏ nhạt
            Color.parseColor("#C8E6C9"), // Xanh lá nhạt
            Color.parseColor("#BBDEFB"), // Xanh dương nhạt
            Color.parseColor("#FFECB3"), // Vàng nhạt
            Color.parseColor("#D1C4E9")  // Tím nhạt
    };
    private static final int REQUEST_CODE_UPDATE_PRODUCT = 101;
    public WarehouseAdapter(Context context, List<FruitModel> listWare, ActivityResultLauncher<Intent> updateLauncher) {
        this.context = context;
        this.listWare = listWare;
        this.updateLauncher = updateLauncher;
    }
    public static class WarehouseViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView txtname,txtprice,txtquantity,txtdescrip;
        View ware_item_background;

        ImageView imgUpdate,imgDelete;

        public WarehouseViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgName);
            txtname = itemView.findViewById(R.id.productName);
            txtprice = itemView.findViewById(R.id.price);
            txtquantity = itemView.findViewById(R.id.quantity);
            ware_item_background = itemView.findViewById(R.id.warelayout);
            imgUpdate = itemView.findViewById(R.id.imgUpdate);
            imgDelete = itemView.findViewById(R.id.imgDelete);

        }
    }

    @NonNull
    @Override
    public WarehouseAdapter.WarehouseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_warehouse, parent, false);
        return new WarehouseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WarehouseAdapter.WarehouseViewHolder holder, int position) {
        Glide.with(context).load(listWare.get(position).getImg_url()).into(holder.img);
        holder.txtname.setText("Tên : " +listWare.get(position).getName());
        holder.txtprice.setText("Giá : " +listWare.get(position).getPrice()+ "VND");
        holder.txtquantity.setText("Số lượng : " + listWare.get(position).getQuantity());


        int colorIndex = position % colors.length;
        holder.ware_item_background.setBackgroundColor(colors[colorIndex]);
        holder.imgUpdate.setBackgroundColor(colors[colorIndex]);
        holder.imgDelete.setBackgroundColor(colors[colorIndex]);
        FruitModel fruit = listWare.get(position);

        // Bắt sự kiện xoá
        holder.imgDelete.setOnClickListener(v -> deleteProduct(fruit.getName()));


        // Bắt sự kiện cập nhật
        holder.imgUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(context, updateFruit.class);
            intent.putExtra("name", fruit.getName());
            intent.putExtra("price", fruit.getPrice());
            intent.putExtra("quantity", fruit.getQuantity());
            intent.putExtra("description", fruit.getDescription());
            intent.putExtra("img_url", fruit.getImg_url());

            //
            updateLauncher.launch(intent);

        });

    }



    private void deleteProduct(String productName) {
        new AlertDialog.Builder(context)
                .setTitle("Xóa Sản Phẩm")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Đang xóa sản phẩm...");
                    progressDialog.show();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseStorage storage = FirebaseStorage.getInstance();

                    // Tìm sản phẩm theo tên
                    db.collection("Fruits")
                            .whereEqualTo("name", productName)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        String foundProductId = document.getId();
                                        String imageUrl = document.getString("img_url"); // Lấy đường dẫn ảnh từ Firestore

                                        // Xóa sản phẩm từ Firestore
                                        db.collection("Fruits").document(foundProductId)
                                                .delete()
                                                .addOnSuccessListener(aVoid -> {
                                                    if (imageUrl != null && !imageUrl.isEmpty()) {
                                                        // Tạo reference từ URL để xóa ảnh trong Storage
                                                        StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);
                                                        imageRef.delete()
                                                                .addOnSuccessListener(unused -> {
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(context, "Sản phẩm đã bị xóa!", Toast.LENGTH_SHORT).show();
                                                                    //
                                                                    removeItemFromList(productName);
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(context, "Lỗi khi xóa ảnh!", Toast.LENGTH_SHORT).show();
                                                                });
                                                    } else {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(context, "Sản phẩm đã bị xóa nhưng không có ảnh!", Toast.LENGTH_SHORT).show();
                                                        //
                                                        removeItemFromList(productName);
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(context, "Lỗi khi xóa sản phẩm!", Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Không tìm thấy sản phẩm!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(context, "Lỗi khi tìm sản phẩm!", Toast.LENGTH_SHORT).show();
                            });

                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    @Override
    public int getItemCount() {
        return listWare.size();
    }

    //cap nhat danh sach sau xoa
    private void removeItemFromList(String productName) {
        for (int i = 0; i < listWare.size(); i++) {
            if (listWare.get(i).getName().equals(productName)) {
                listWare.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, listWare.size()); // Cập nhật lại danh sách sau khi xóa
                Toast.makeText(context, "Sản phẩm đã bị xóa!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }


}
