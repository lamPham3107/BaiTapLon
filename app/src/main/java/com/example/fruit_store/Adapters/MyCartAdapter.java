package com.example.fruit_store.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fruit_store.R;
import com.example.fruit_store.models.FruitModel;
import com.example.fruit_store.models.MyCartModel;
import com.example.fruit_store.ui.MyCart.MyCartFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {
    private Context context;
    private List<MyCartModel> myCartModelList;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private MyCartFragment myCartFragment;
    private TextView overTotalAmount;


    public MyCartAdapter(Context context, List<MyCartModel> myCartModelList , TextView overTotalAmount) {
        this.context = context;
        this.myCartModelList = myCartModelList;
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        this.overTotalAmount = overTotalAmount;
    }

    @NonNull

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_cart_item , parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.name.setText(myCartModelList.get(position).getFruitName());
        holder.price.setText(myCartModelList.get(position).getFruitPrice() );

        holder.quantity.setText(String.valueOf(myCartModelList.get(position).getTotalQuantity()));
        holder.total_price.setText(String.valueOf(myCartModelList.get(position).getTotalPrice()));

        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                        .collection("AddToCart")
                        .document(myCartModelList.get(pos).getDoucumentId())
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    myCartModelList.remove(myCartModelList.get(pos));
                                    notifyDataSetChanged();
                                    updateTotalAmount();
                                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                                    MyCartFragment myCartFragment = (MyCartFragment) fragmentManager.findFragmentByTag("MY_CART_FRAGMENT_TAG");
                                    if (myCartFragment != null) {
                                        myCartFragment.calculatorTotalAmount(myCartModelList);
                                    } else {
                                        Log.e("MyCartAdapter", "myCartFragment is null");
                                    }
                                    Toast.makeText(context , "Đã xóa" , Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(context , "Error: " + task.getException() , Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


    }

    @Override
    public int getItemCount() {
        return myCartModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name , price , date , time , quantity , total_price;
        ImageView img_delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.txt_cart_fruitName);
            price = (TextView) itemView.findViewById(R.id.txt_cart_price);
            quantity = (TextView) itemView.findViewById(R.id.txt_cart_total_quantity);
            total_price = (TextView) itemView.findViewById(R.id.txt_cart_total_price);
            img_delete = (ImageView) itemView.findViewById(R.id.img_delete);
        }
    }

    private void updateTotalAmount() {
        double totalAmount = 0.0;
        for (MyCartModel myCartModel : myCartModelList) {
            totalAmount += myCartModel.getTotalPrice();
        }
        overTotalAmount.setText("Tổng tiền: " + totalAmount + " VNĐ");
    }
    private void sendDataToFragment() {
        Intent intent = new Intent("MyCartFragment");
        intent.putExtra("totalAmount", overTotalAmount.getText().toString());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
