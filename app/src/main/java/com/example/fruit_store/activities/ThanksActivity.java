package com.example.fruit_store.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fruit_store.R;
import com.example.fruit_store.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ThanksActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thanks);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        toolbar = (Toolbar) findViewById(R.id.thanks_toolbar);
        // bat nut back
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        List<MyCartModel> list = (ArrayList<MyCartModel>)getIntent().getSerializableExtra("fruitList");
        if(list != null && list.size() > 0){
            for (MyCartModel model : list){
                // bang bam chua du lieu cac thuoc tinh cua fruitModel
                final HashMap<String , Object> cartMap = new HashMap<>();

                cartMap.put("fruitName", model.getFruitName());
                cartMap.put("fruitPrice", model.getFruitPrice());
                cartMap.put("currentDate", model.getCurrentDate());
                cartMap.put("currentTime", model.getCurrentTime());
                cartMap.put("totalQuantity", model.getTotalQuantity());
                cartMap.put("totalPrice", model.getTotalPrice());

                // day du lieu cua bang bam len firebase firestore
                firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                        .collection("MyOrder").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                // cap nhat trang thai kho khi bam thanh toan
//                        Map<String, Object> update_quantity = new HashMap<>();
//                        update_quantity.put("quantity" , String.valueOf(Max_quantity - total_quantity));
//                        firestore.collection("Fruits").whereEqualTo("name" , fruitModel.getName())
//                                    .get()
//                                        .addOnSuccessListener(queryDocumentSnapshots -> {
//                                            if(!queryDocumentSnapshots.isEmpty()){
//                                                String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
//                                                firestore.collection("Fruits").document(documentId)
//                                                        .update(update_quantity)
//                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                            @Override
//                                                            public void onSuccess(Void unused) {
//                                                                Toast.makeText(DetailActivity.this , "da tru trong kho " , Toast.LENGTH_SHORT  ).show();
//                                                            }
//                                                        });
//                                            }
//                                        });
                                Toast.makeText(ThanksActivity.this , "My order added" , Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }
}