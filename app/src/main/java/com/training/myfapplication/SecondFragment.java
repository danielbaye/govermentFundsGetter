package com.training.myfapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.training.myfapplication.databinding.FragmentSecondBinding;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SecondFragment extends Fragment {



private FragmentSecondBinding binding;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;

private Map<String,String> currentTitles;
    private InfoHandler ih;

    private int currentYear;
    private Map<String, Map<String, Float>> departmentsAndValues;

    private float yearSum;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        String url = "https://next.obudget.org/api/query?query=";
        ih = new InfoHandler(url,getContext());
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        recyclerView = binding.recyclerView;
        currentYear=Calendar.getInstance().get(Calendar.YEAR)+1;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        currentTitles = new HashMap<>();
        currentTitles.put("המדינה","00");

        NumberPicker numberPicker = binding.numberPicker;
        numberPicker.setMinValue(1997);
        numberPicker.setMaxValue(Calendar.getInstance().get(Calendar.YEAR)+1);
        numberPicker.setValue(Calendar.getInstance().get(Calendar.YEAR)+1);

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                ArrayList<Map.Entry<String,Float>> currentYearValues = getCurrentYearValues(String.valueOf(newVal));
                Collections.sort(currentYearValues, Map.Entry.comparingByValue());
                yearSum = new usables().calculateSum(currentYearValues);
                setupPieChart(currentYearValues);
                setRecyclerView(currentYearValues);
                currentYear = newVal;
            }
        });
        loadData("המדינה");

        return binding.getRoot();

    }

    private void setRecyclerView(ArrayList<Map.Entry<String, Float>> currentYearValues) {
        List<Item> itemList = new ArrayList<>();
        usables u = new usables();
        for (int i = currentYearValues.size()-1; i >= 0; i--) {

            itemList.add(new Item(currentYearValues.get(i).getKey(),
                    u.hebrewValue(currentYearValues.get(i).getValue(),'#'),
                    u.percentValue(currentYearValues.get(i).getValue(),yearSum)));
        }
        itemAdapter = new ItemAdapter(itemList,this::loadData);
        recyclerView.setAdapter(itemAdapter);
    }



    private ArrayList<Map.Entry<String, Float>> getCurrentYearValues(String currentYear) {
        ArrayList<Map.Entry<String, Float>> currentYearValues = new ArrayList<>();
        List<String> keyList = new ArrayList<>(departmentsAndValues.keySet());

        for (int i = 0; i < keyList.size(); i++) {
            String title = keyList.get(i);
            if (departmentsAndValues.get(keyList.get(i)).containsKey(currentYear)) {
                float value = departmentsAndValues.get(keyList.get(i)).get(currentYear);

                currentYearValues.add(new AbstractMap.SimpleEntry<>(title, value));
            }
        }
        return currentYearValues;
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeAsUpIndicator(null);
        }

    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupPieChart( ArrayList<Map.Entry<String, Float>> departmentsAndValues) {
        // Configure the PieChart
        PieChart pieChart = binding.pieChart;
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                itemAdapter.setItemFocus((int)h.getX());
                recyclerView.scrollToPosition((int)h.getX());

//                recyclerView.post(() -> {
//                    RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition((int)h.getX());
//                    if (viewHolder != null) {
//                        View itemView = viewHolder.itemView;
//                        itemView.requestFocus();
//                    }
//                });
//                recyclerView.setAdapter(itemAdapter);

            }

            @Override
            public void onNothingSelected() {

            }
        });
        pieChart.setVisibility(View.INVISIBLE);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setBackgroundColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setCenterTextOffset(100,100);
//        pieChart.setDrawEntryLabels(false);


        // Create entries for the PieChart
        ArrayList<PieEntry> entries = new ArrayList<>();
        float elseSum =0;
        for (int i = departmentsAndValues.size()-1; i >= 0; i--) {
            if (departmentsAndValues.get(i).getValue()/yearSum>0.015)
//            entries.add(new PieEntry(departmentsAndValues.get(i).getValue(),departmentsAndValues.get(i).getKey()));
            entries.add(new PieEntry(departmentsAndValues.get(i).getValue(),""));
            else
                elseSum += departmentsAndValues.get(i).getValue();

        }
        entries.add(new PieEntry(elseSum,"אחר"));

        PieDataSet dataSet = new PieDataSet(entries, "Pie Chart");

        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);
        pieChart.setDescription(new Description());
        pieChart.setVisibility(View.VISIBLE);
    }

    public interface MyFunction {
        void loadData(String name);
    }

    public void loadData(String name){

        currentTitles = ih.getDepartmentsFromChildren(currentTitles.get(name));
        if (currentTitles.size()==0) {
            currentTitles = ih.getDepartmentsFromChildren("00");
            Toast.makeText(getContext(),"אירעה שגיאה, חוזר לראשית", Toast.LENGTH_SHORT);
            }
        ArrayList<String> CT = new ArrayList<>();
        for (Map.Entry<String, String> entry :  new ArrayList<>(currentTitles.entrySet())) {
            CT.add(entry.getKey());
        }
        departmentsAndValues = ih.getDepartmentsAndValues(CT);
        ArrayList<Map.Entry<String,Float>> currentYearValues =
                getCurrentYearValues(String.valueOf(currentYear));

        Collections.sort(currentYearValues, Map.Entry.comparingByValue());
        yearSum = new usables().calculateSum(currentYearValues);

        setupPieChart(currentYearValues);
        setRecyclerView(currentYearValues);
    }
}