package com.training.myfapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.ScrollView;
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
    private ItemAdapter itemAdapter2;
    private RecyclerView recyclerView2;
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
        recyclerView2=binding.recyclerView2;
        currentYear=Calendar.getInstance().get(Calendar.YEAR)+1;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
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
        recyclerView2.setVisibility(View.INVISIBLE);

        if (currentYearValues.size()==0 || yearSum==0){
            setEmptyRecycler();
            recyclerView.setVisibility(View.VISIBLE);
            return;
        }

        List<Item> itemList = new ArrayList<>();
        List<Item> smallItemList = new ArrayList<>();
        usables u = new usables();
        float sumOfSmallList=0.0f;
        for (int i = currentYearValues.size()-1; i >= 0; i--) {
            if(currentYearValues.size()-11<i)
            itemList.add(new Item(currentYearValues.get(i).getKey(),
                    u.hebrewValue(currentYearValues.get(i).getValue(),'#'),
                    u.percentValue(currentYearValues.get(i).getValue(),yearSum)));
            else {
                smallItemList.add(new Item(currentYearValues.get(i).getKey(),
                        u.hebrewValue(currentYearValues.get(i).getValue(), '#'),
                        u.percentValue(currentYearValues.get(i).getValue(), yearSum)));
                sumOfSmallList +=currentYearValues.get(i).getValue();
            }
        }
        if (sumOfSmallList>0)
            itemList.add(new Item("אחר",u.hebrewValue(sumOfSmallList, '#'),
                    u.percentValue(sumOfSmallList, yearSum)));
        itemAdapter = new ItemAdapter(itemList,this::loadData,
                getResources().getIntArray(R.array.color_array),this::showElse);

        int[] greyColor = {R.color.niceGrey};
        itemAdapter2 = new ItemAdapter(smallItemList,this::loadData,greyColor, this::showElse);
        recyclerView.setAdapter(itemAdapter);
        recyclerView2.setAdapter(itemAdapter2);
    }

    private void setEmptyRecycler() {
        List<Item> itemList = new ArrayList<>();
        itemList.add(new Item("אין נתונים עבור שנה זו","0","0.0%"));
        int[] greyColor = {R.color.niceGrey};
        itemAdapter = new ItemAdapter(itemList,this::loadData,greyColor, this::showElse);
        recyclerView.setAdapter(itemAdapter);
        recyclerView2.setAdapter(itemAdapter);
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
//        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
//            @Override
//            public void onValueSelected(Entry e, Highlight h) {
//                itemAdapter.setItemFocus((int)h.getX());
//                recyclerView.scrollToPosition((int)h.getX());

//                recyclerView.post(() -> {
//                    RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition((int)h.getX());
//                    if (viewHolder != null) {
//                        View itemView = viewHolder.itemView;
//                        itemView.requestFocus();
//                    }
//                });
//                recyclerView.setAdapter(itemAdapter);

//            }

//            @Override
//            public void onNothingSelected() {
//
//            }
//        });
        if (departmentsAndValues.size()==0 || yearSum==0)
        { emptyChart();
        return ;
        }
        PieChart pieChart = binding.pieChart;
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
            if (i>departmentsAndValues.size()-11)
//            entries.add(new PieEntry(departmentsAndValues.get(i).getValue(),departmentsAndValues.get(i).getKey()));
            entries.add(new PieEntry(departmentsAndValues.get(i).getValue(),""));
            else
                elseSum += departmentsAndValues.get(i).getValue();

        }
        if(elseSum>0)
        entries.add(new PieEntry(elseSum,"אחר"));
        PieDataSet dataSet = new PieDataSet(entries,
                String.valueOf(binding.numberPicker.getValue()));
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        int[] colors = getResources().getIntArray(R.array.color_array);

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);
        Description desc = new Description();
        desc.setText("");
        pieChart.setDescription(desc);
        pieChart.setDescription(desc);
        pieChart.setVisibility(View.VISIBLE);
    }

    private void emptyChart() {
        PieChart pieChart = binding.pieChart;
        pieChart.setVisibility(View.INVISIBLE);
        pieChart.setUsePercentValues(false); // Set to true if you want to use percentages
        pieChart.getLegend().setEnabled(false); // Disable legend
        ArrayList<PieEntry> entries = new ArrayList<>();
        PieData data = new PieData(new PieDataSet(entries, "אין נתונים"));
        pieChart.setData(data);
        Description desc = new Description();
        desc.setText("אין נתונים עבור שנה זו");
        pieChart.setDescription(desc);
        pieChart.setVisibility(View.VISIBLE);
    }

    public interface MyFunction {
        void loadData(String name);

    }

    public interface showElse {
        void showElse();

    }

    public void showElse(){
        if(binding.recyclerView2.getVisibility() == View.INVISIBLE)
            binding.recyclerView2.setVisibility(View.VISIBLE);
        else
            binding.recyclerView2.setVisibility(View.INVISIBLE);
    }

    public void loadData(String name){
        currentTitles = ih.getDepartmentsFromChildren(currentTitles.get(name));
        if (currentTitles.size()==0) {
            currentTitles = ih.getDepartmentsFromChildren("00");
            Toast.makeText(getContext(),"אירעה שגיאה, חוזר לראשית", Toast.LENGTH_SHORT);
            }
        ArrayList<String> CT = new ArrayList<>();
        for (Map.Entry<String, String> entry :  new ArrayList<>(currentTitles.entrySet())) {
            CT.add(entry.getValue());
        }
        departmentsAndValues = ih.getDepartmentsAndValues(CT);
        ArrayList<Map.Entry<String,Float>> currentYearValues =
                getCurrentYearValues(String.valueOf(currentYear));

        Collections.sort(currentYearValues, Map.Entry.comparingByValue());
        yearSum = new usables().calculateSum(currentYearValues);

        setupPieChart(currentYearValues);
        setRecyclerView(currentYearValues);
        recyclerView2.scrollToPosition(0);
        recyclerView.scrollToPosition(0);

    }
}