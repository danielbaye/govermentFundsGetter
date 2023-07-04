package com.training.myfapplication;

import android.app.SearchManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.BaseColumns;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.training.myfapplication.databinding.ActivityMainBinding;
import com.training.myfapplication.databinding.FragmentFirstBinding;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONException;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;


public class FirstFragment extends Fragment {



private FragmentFirstBinding binding;


//    private String [] currentYear;



    private ArrayList<String> titles;

    private InfoHandler ih;
    private LineChart lineChart;
    private String [] currentMoney;
    private String [] currentQuery;
    private Float userInput;
    private Map<String,Float> yearlySum;
    private Map<String,String> departmentsMap;
    private boolean initializationFlag = false;
    private Map<String,Float> currentYearlyAmount;
    private String url = "https://next.obudget.org/api/query?query=";
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        String[] moneyArray = getResources().getStringArray(R.array.money);
        lineChart = binding.linechart;


        this.currentMoney = new String[]{moneyArray[0]};

        currentQuery = new String[]{""};
        currentYearlyAmount = new HashMap<>();
        ih = new InfoHandler(url,getContext());
        userInput = Storage.getInstance().getValue("taxes");
        if (userInput<=0)
            userInput = 1000.0f;
        yearlySum = ih.getJustBudgetByYear();
        currentYearlyAmount = yearlySum;

        initializeDepartmentsMap();

        Spinner spinner = binding.spinner2;
        Context context = getContext();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,getDepartmentKeys());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (initializationFlag) {
                    String item = parent.getItemAtPosition(position).toString();
                    ih.addDepartment(new AbstractMap.SimpleEntry<>(departmentsMap.get(item), item));

                    setDepartmentsMap(ih.peekDepartment().getKey(), item);
                    adapter.clear();
                    adapter.addAll(getDepartmentKeys());
                    adapter.notifyDataSetChanged();
                    currentYearlyAmount = getSumMap();
                    spinner.setSelection(0);
                    initializationFlag = false;
                } else
                    initializationFlag = true;
                drawChart();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

       });
        NumberPicker moneyPicker = binding.money;
        moneyPicker.setMinValue(0);
        moneyPicker.setMaxValue(moneyArray.length-1);
        moneyPicker.setDisplayedValues(moneyArray);
        moneyPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Handler handler = new Handler(Looper.getMainLooper());
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        currentMoney[0] = moneyArray[newVal];
                        drawChart();
                    }
                }, 500);

            }
        });

        SearchView searchView = binding.query;
        searchView.setQueryRefinementEnabled(true);
        SimpleCursorAdapter suggestionAdapter;
        String[] from = {SearchManager.SUGGEST_COLUMN_TEXT_1};
        int[] to = {android.R.id.text1};
        suggestionAdapter = new SimpleCursorAdapter(getContext(),
                android.R.layout.simple_list_item_1,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        searchView.setSuggestionsAdapter(suggestionAdapter);

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                currentQuery[0] = "";
                currentYearlyAmount = getSumMap();
                drawChart();
                searchView.clearFocus();
                return false;
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = searchView.getSuggestionsAdapter().getCursor();
                String s = cursor.getString(1);
                searchView.setQuery(s,true);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentYearlyAmount = getSumMap();
                drawChart();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery[0] = query;
                currentYearlyAmount = getSumMap();
                drawChart();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length()<3) {
                    return false;
                }
                if (newText.length() == 3) {

                    titles = ih.getTitles(newText);
                }
                ArrayList<String> searchTitles = titles;
                if (newText.length() >= 3) {
                    searchTitles.removeIf(title->!title.contains(newText));
                    Set<String> uniqueSet = new HashSet<>(searchTitles);
                    searchTitles =  new ArrayList<>(uniqueSet);
                    }
                currentQuery[0] = newText;
                String[] columnNames =  {BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1};
                MatrixCursor cursor = new MatrixCursor(columnNames);
                int len = searchTitles.size()>7?7:searchTitles.size();
                for (int i=0;i<len;i++)
                        cursor.addRow(new Object[]{i, searchTitles.get(i)});
                suggestionAdapter.changeCursor(cursor);
                return true;
            }
        });




//        backgroundDrawable.setStroke(20, ColorStateList.valueOf(Color.BLACK));

        binding.Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.constrainedLayout.setVisibility(View.INVISIBLE);
                binding.taxLayout.setVisibility(View.VISIBLE);
//                binding.childFragmentContainer.setVisibility(View.VISIBLE);
//                binding.backButton.setVisibility(View.VISIBLE);
            }
        });
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Tax_Fragment tax_fragment = new Tax_Fragment();
        fragmentTransaction.replace(R.id.child_fragment_container, tax_fragment);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.constrainedLayout.setVisibility(View.VISIBLE);
                binding.childFragmentContainer.setVisibility(View.INVISIBLE);
                binding.backButton.setVisibility(View.INVISIBLE);
                Storage storage = Storage.getInstance();
                userInput = storage.getValue("taxes");
                drawChart();
            }
        });


        binding.orderList.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if (ih.sizeDepartment()>2) {
                    ih.popDepartment();
                    setDepartmentsMap(ih.peekDepartment().getKey(),
                            ih.peekDepartment().getValue());
                    currentYearlyAmount = getSumMap();
                }
                else {
                    initializeDepartmentsMap();
                    ih.clearDepartment();
                    ih.addDepartment(new AbstractMap.SimpleEntry<>("00","הכל"));

                    currentYearlyAmount = yearlySum;
                    searchView.setIconified(true);
                    currentQuery[0]="";
                }
                adapter.clear();
                adapter.addAll(getDepartmentKeys());
                adapter.notifyDataSetChanged();
                spinner.setSelection(0);
                drawChart();
            }
        });



      return binding.getRoot();
    }

    private List<String> getDepartmentKeys() {
        List<Map.Entry<String, String>> entryList = new ArrayList<>(this.departmentsMap.entrySet());
        Collections.sort(entryList, Map.Entry.comparingByValue());

        List<String> departmentKeys = new ArrayList<>();
        for (Map.Entry<String, String> entry : entryList) {
            departmentKeys.add(entry.getKey());
        }
        return departmentKeys;
    }

    private void initializeDepartmentsMap() {

       this.departmentsMap = ih.getDepartments();
        this.departmentsMap.put("הכל","00");
    }
    private void setDepartmentsMap(String newDepartmentCode,String name) {

        this.departmentsMap= ih.getDepartmentsFromChildren(newDepartmentCode);

        this.departmentsMap.put(name,newDepartmentCode);
    }


    private Map<String, Float> getSumMap() {
        Map<String,Float> sumMap = new HashMap<>();

        sumMap = ih.getSumByYear(currentQuery[0]);
        return sumMap;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeAsUpIndicator(null);
        }

        drawNoInfo();
    }

    private void drawNoInfo() {
        ArrayList Y = new ArrayList();
        for (int j=0;j<1000;j++){
            Y.add(new Entry(j,0f));
        }
        lineChart.setData(new LineData(new LineDataSet(Y,"כלום")));
        Description dsc = new Description();
        dsc.setText("אין מידע");
        lineChart.setDescription(dsc);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    class EntryComparator implements Comparator<Entry> {
        @Override
        public int compare(Entry entry1, Entry entry2) {
            return entry1.getX() >entry2.getX()? 1:-1;
        }
    }

    public Map<String,Float> viewAdapter(Map<String,Float> amount){
        String[] keys = amount.keySet().toArray(new String[0]);
        Map<String,Float> newAmount = new HashMap<>();
        if(this.currentMoney[0].equals("%"))
        {
            for (int i = 0; i < amount.size(); i++) {
                if(this.yearlySum.containsKey(keys[i]))
                    if(this.yearlySum.get(keys[i])!=0)
                newAmount.put(keys[i], (amount.get(keys[i])/this.yearlySum.get(keys[i]))*100);
            }
        }
        else if(this.currentMoney[0].equals("₪%"))
        {
            for (int i = 0; i < amount.size(); i++) {
                if(this.yearlySum.containsKey(keys[i]))
                    if(this.yearlySum.get(keys[i])!=0)
                newAmount.put(keys[i], (amount.get(keys[i])/(this.yearlySum.get(keys[i])))*this.userInput);
            }
        }
        else
            for (int i = 0; i < amount.size(); i++) {
                newAmount.put(keys[i], amount.get(keys[i]));
            }

        return newAmount;
    }

    public void drawChart(){
        Map<String,Float> M = this.currentYearlyAmount;

        String [] names = ih.getDepartmentNames();
        String orderlistText = names[0];
        for (int i = 1; i < names.length; i++) {
            orderlistText+= ">"+names[i];
        }
        binding.orderListText.setText(orderlistText);
        lineChart.setVisibility(View.INVISIBLE);
        String newTitle = selectTitle();

        List<Entry> valuesList = new ArrayList<>();
        Map<String,Float> adaptedMap = this.viewAdapter(M);
        for (Map.Entry<String, Float> entry : adaptedMap.entrySet()) {
            if (entry.getValue()!=0)
                valuesList.add(new Entry(Float.parseFloat(entry.getKey()),entry.getValue()));
        }
        if (adaptedMap.size()==0){
            drawNoInfo();
            newTitle+= " - אין מידע";
        }
        else
            drawLineChart(valuesList);
        binding.chartTitle.setText(newTitle);
        binding.chartTitle.setAutoSizeTextTypeUniformWithConfiguration(10, 100, 1, TypedValue.COMPLEX_UNIT_SP);

        Description dsc = new Description();
        dsc.setText(newTitle);
        lineChart.setDescription(dsc);
        lineChart.setVisibility(View.VISIBLE);
        lineChart.setDrawMarkers(false);
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Toast.makeText(getContext(), new usables().hebrewValue(e.getY(),currentMoney[0].charAt(0)) +" ,"+String.valueOf((int)e.getX()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void drawLineChart(List<Entry> valuesList) {
        Collections.sort(valuesList,new EntryComparator());
        LineDataSet LDS = new LineDataSet(valuesList,currentMoney[0]);
        LDS.setDrawValues(false);

        LDS.setCircleColor(Color.parseColor("#e27c7c"));
//
//        lineChart.setGridBackgroundColor(Color.parseColor("#fd7f6f"));
//        lineChart.setBorderColor(Color.parseColor("#beb9db"));
//        LDS.setColor(Color.parseColor("a86464"));
//        lineChart.setBackgroundColor( Color.parseColor("#333333"));
        lineChart.getAxisLeft().setTextColor(Color.GRAY);
        lineChart.getXAxis().setTextColor(Color.GRAY);
        LDS.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value <=1)
                    return String.format("%.1f", value*100);
                float newValue = value;
                if(value >=1000000000)
                    newValue = value /1000000000;
                else if(value >=1000000)
                    newValue = value /1000000;
                else if(value >=1000)
                    newValue = value /1000;
                int decimalPoint = Float.toString(newValue).indexOf(".");
                String format  = decimalPoint==1? "%.2f":decimalPoint==2?
                        "%.1f":"%.0f";
                return String.format(format, newValue);
            }
        });
        lineChart.setData(new LineData(LDS));
        lineChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return new usables().hebrewValue(value,currentMoney[0].charAt(0));
            }
        });

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.0f", value);
            }
        });
    }

    private String selectTitle() {
        String chartTitle = new String();
        if(this.currentMoney[0].equals("%"))
        {
            chartTitle = "אחוז ההוצאות מתוך כל הוצאות המדינה";
        }
        else if(this.currentMoney[0].equals("₪%"))
        {
            chartTitle = "סכום משוערך ששולם על ידך";
        }
        else
            chartTitle = "ס\"כ ההוצאות";

        if (currentQuery[0].length()>0)
            chartTitle+= " על "+currentQuery[0];
        if (ih.sizeDepartment()>1)
            chartTitle+= " מתוך סעיף "+ih.peekDepartment().getValue();
        else
            chartTitle+= " מתוך הוצאות המדינה";

        return chartTitle;
    }

    public Float parseFloatOrNull(String input) {
        try {
            return Float.parseFloat(input);
        } catch (NumberFormatException e) {
            return null; // Return null if the input string is not a valid float
        }
    }



}