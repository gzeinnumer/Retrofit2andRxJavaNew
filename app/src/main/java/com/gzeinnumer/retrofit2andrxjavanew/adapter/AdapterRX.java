package com.gzeinnumer.retrofit2andrxjavanew.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gzeinnumer.retrofit2andrxjavanew.R;
import com.gzeinnumer.retrofit2andrxjavanew.model.ArticlesItem;

import java.util.List;

public class AdapterRX extends RecyclerView.Adapter<AdapterRX.MyHolder> {

    private Context contex;
    private List<ArticlesItem> list;

    public AdapterRX(Context contex, List<ArticlesItem> list) {
        this.contex = contex;
        this.list = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contex).inflate(R.layout.item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.tvTitle.setText(list.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }
}
