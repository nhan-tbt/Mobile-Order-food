package com.example.masterchef.ui.manager.stock;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.masterchef.Ingredient;
import com.example.masterchef.R;
import com.example.masterchef.Tables;
import com.example.masterchef.ui.manager.statistic.ThongKe;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.List;

import static com.example.masterchef.MainActivity.server;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    List<Ingredient> LI;
    LayoutInflater inflater;
    StorageReference storeImage;
    Context context;

    public Adapter(Context context, List<Ingredient> li){
        this.LI = li;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_stock_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!LI.get(position).getFlagName().equals("")) {
            storeImage = FirebaseStorage.getInstance()
                    .getReferenceFromUrl("gs://orderdoan-a172f.appspot.com/")
                    .child(LI.get(position).getFlagName());
            Glide.with(context.getApplicationContext()).using(new FirebaseImageLoader()).load(storeImage).into(holder.img);
        }
        holder.title.setText(LI.get(position).getTen().toString());
        holder.quantity.setText("Số lượng: " + Integer.toString(LI.get(position).getSoluongtonkho()));
        holder.choose_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.increase_ingredient_dialog);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                EditText number = (EditText) dialog.findViewById(R.id.edv_input_number_of);
                Button ok = dialog.findViewById(R.id.btn_OK_number_of);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int tmp = LI.get(position).getSoluongtonkho() + Integer.parseInt(number.getText().toString());
                        LI.get(position).getRef().child("soluongtonkho").setValue(tmp);
                        Calendar cal = Calendar.getInstance();
                        int i = cal.get(Calendar.MONTH)+1;
                        String query1;
                        if(i<10)  query1 = "0"+i+cal.get(Calendar.YEAR);
                        else query1 =""+i+cal.get(Calendar.YEAR);

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference dataref = database.getReference(server.getText().toString());
                        Query query = dataref.child("ThongKe").orderByKey().equalTo(query1);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    ThongKe thongKe = null;
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        thongKe = dataSnapshot.getValue(ThongKe.class);
                                    }
                                    thongKe.Chiphi += LI.get(position).getGia()*Integer.parseInt(number.getText().toString());

                                    dataref.child("ThongKe").child(query1).child("Chiphi").setValue(thongKe.Chiphi);
                                }
                                else dataref.child("ThongKe").child(query1).child("Chiphi").setValue(LI.get(position).getGia()*Integer.parseInt(number.getText().toString()));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        dialog.dismiss();
                    }
                });
                Button cancel = dialog.findViewById(R.id.btn_Cancel_number_of);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount(){
        return LI.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title;
        TextView quantity;
        Button choose_btn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.stock_title);
            img = itemView.findViewById(R.id.stock_image);
            quantity = itemView.findViewById(R.id.stock_quantity);
            choose_btn = itemView.findViewById(R.id.stock_choose_btn);
        }
    }
}