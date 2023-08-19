package com.example.myservices.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myservices.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterMostServices extends RecyclerView.Adapter<AdapterMostServices.MyViewHolder> {
    ArrayList<MostSarvices> M;

    public AdapterMostServices(ArrayList<MostSarvices> mostSarvices) {
        this.M=mostSarvices;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_most_servies,null,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    MostSarvices mostSarvices =M.get(position);
    holder.name.setText(mostSarvices.getName());
    holder.image.setImageResource(mostSarvices.getIamge());
    holder.Job.setText(mostSarvices.getJob());
    holder.sall.setText(mostSarvices.getSall());
    }


    @Override
    public int getItemCount() {
        return M.size();
    }
public static class MyViewHolder extends RecyclerView.ViewHolder{
    ImageView image;
    TextView name,Job,sall;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        name=(TextView)itemView.findViewById(R.id.name);
        image=(ImageView) itemView.findViewById(R.id.IV_profile_most);
        Job=(TextView)itemView.findViewById(R.id.job);
        sall=(TextView)itemView.findViewById(R.id.sall);
    }
}




}

