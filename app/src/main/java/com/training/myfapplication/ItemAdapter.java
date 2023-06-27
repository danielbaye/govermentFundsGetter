package com.training.myfapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<Item> itemList;
    private int[] colors;
    public ItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
    this.colors = ColorTemplate.PASTEL_COLORS;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.name.setText(item.getName());
        holder.value.setText(item.getValue());
        CardView cardView = holder.itemView.findViewById(R.id.card);
        int colorIndex = position % colors.length;
        int color = colors[colorIndex];
//        holder.itemView.setDrawingCacheBackgroundColor(color);
        cardView.setBackgroundColor(color);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView value;

        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            value = itemView.findViewById(R.id.value);
        }
    }
}