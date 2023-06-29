package com.training.myfapplication;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private int clickedPosition;
    private List<Item> itemList;

    private SecondFragment.MyFunction loadData;
    private int[] colors;
    public ItemAdapter(List<Item> itemList,SecondFragment.MyFunction loadData) {
        this.itemList = itemList;
    this.colors = ColorTemplate.PASTEL_COLORS;
    this.clickedPosition = -1;
        this.loadData = loadData;

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
        holder.percent.setText(item.getPercent());

        setCardColor(holder, position);
        setCardListener(holder,position);
    }

    private void setCardListener(ViewHolder holder, int position) {

        CardView cardView = holder.itemView.findViewById(R.id.card);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(clickedPosition!=position) {
                clickedPosition = position;

            }
            else {
                clickedPosition =-1;
                loadData.loadData(holder.name.getText().toString());
            }
                notifyDataSetChanged();
            }
        });
    }

    private void setCardColor(ViewHolder holder, int position) {
        CardView cardView = holder.itemView.findViewById(R.id.card);
        int colorIndex = position % colors.length;
        int color = colors[colorIndex];
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(30f);
        gradientDrawable.setColor(color); // Set desired background color
        if (position == clickedPosition){
            int StrokeIndex = (position+2) % colors.length;
            int Strokecolor = colors[colorIndex];

            gradientDrawable.setStroke(20,Color.parseColor("#7096A2"));

        }
        cardView.setBackground(gradientDrawable);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView value;

        TextView percent;
        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            value = itemView.findViewById(R.id.value);
            percent = itemView.findViewById(R.id.percent);
        }
    }
}