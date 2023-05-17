package com.fptu.android.financialmanagement.Category;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.android.financialmanagement.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
    private Context context;
    private Activity activity;
    private ArrayList cId, categoryName, category_PID, level;

    MyAdapter(Activity activity, Context context, ArrayList cId, ArrayList categoryName, ArrayList category_PID, ArrayList level){
        this.context=context;
        this.cId =cId ;
        this.categoryName =categoryName;
        this.category_PID=category_PID;
        this.level=level;
        this.activity = activity;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyler_layout, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,  int position) {
        holder.txt_cId.setText("Category ID: " +String.valueOf(cId.get(position)));
        holder.txt_cName.setText("Category Name: " +String.valueOf(categoryName.get(position)));
        holder.txt_ParentId.setText("Category Type: " +String.valueOf(category_PID.get(position)));
        holder.txt_level.setText("Category Note: " +String.valueOf(level.get(position)));
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateActivity.class);
                intent.putExtra("Cid", String.valueOf(cId.get(position)));
                intent.putExtra("cName", String.valueOf(categoryName.get(position)));
                intent.putExtra("PId", String.valueOf(category_PID.get(position)));
                intent.putExtra("level", String.valueOf(level.get(position)));
                System.out.println(position);
                activity.startActivityForResult(intent, 1);
//             context.startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return cId.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_cId,txt_cName,txt_ParentId,txt_level ;
        LinearLayout mainLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_cId = itemView.findViewById(R.id.txt_cId);
            txt_cName = itemView.findViewById(R.id.txt_cName);
            txt_ParentId = itemView.findViewById(R.id.txt_ParentId);
            txt_level = itemView.findViewById(R.id.txt_level);
            mainLayout = itemView.findViewById(R.id.crudPage);

        }
    }


}

