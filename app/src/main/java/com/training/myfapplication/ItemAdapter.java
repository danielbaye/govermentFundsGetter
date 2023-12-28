package com.training.myfapplication;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
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
    private int focusedItemPosition;
    private List<Item> itemList;

    private SecondFragment.showElse showElse;
    private SecondFragment.MyFunction loadData;

    private boolean earlyUser;
    private int[] colors;

    public ItemAdapter(List<Item> itemList, SecondFragment.MyFunction loadData,
            int[] colors, SecondFragment.showElse showElse) {
        this.itemList = itemList;
        this.colors = colors;
        this.clickedPosition = -1;
        this.loadData = loadData;
        this.focusedItemPosition = -1;
        this.showElse = showElse;
        Storage storage = Storage.getInstance();
        this.earlyUser = storage.getValue("timesOpened") < 4;
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
        setCardListener(holder, position);
        if (position == focusedItemPosition) {

        }
    }

    public void setItemFocus(int position) {
        this.focusedItemPosition = position;
    }

    private void setCardListener(ViewHolder holder, int position) {

        CardView cardView = holder.itemView.findViewById(R.id.card);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 10 && itemList.get(10).getName().equals("אחר")) {
                    showElse.showElse();
                    clickedPosition = -1;
                } else if (position == 0 && holder.value.getText().equals("0")) {
                    if (earlyUser)
                        itemList.remove(0);
                } else if (clickedPosition != position) {
                    clickedPosition = position;

                } else {
                    clickedPosition = -1;
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
        if (itemList.get(0).getValue().equals("0") && position == 0) {
            color = colors[10];
            holder.name.setTextSize(20f);
        }
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(30f);
        gradientDrawable.setColor(color); // Set desired background color
        if (position == clickedPosition && !holder.name.equals("אחר")) {
            // int StrokeIndex = (position+2) % colors.length;
            // int Strokecolor = colors[colorIndex];

            gradientDrawable.setStroke(20, Color.parseColor("#7096A2"));

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