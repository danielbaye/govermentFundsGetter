package com.training.myfapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.training.myfapplication.databinding.FragmentFirstBinding;
import com.training.myfapplication.databinding.FragmentTaxBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Tax_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Tax_Fragment extends Fragment {
    private FragmentTaxBinding binding;    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final double[][] taxArray = {
            {81480 , 0.1},
            {35280, 0.14},
            {70679, 0.2},
            {73079, 0.31},
            {70679, 0.2},
            {281639, 0.35},
            {156119, 0.47}
    };
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Tax_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Tax_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Tax_Fragment newInstance(String param1, String param2) {
        Tax_Fragment fragment = new Tax_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTaxBinding.inflate(inflater, container, false);

        binding.button.setVisibility(View.INVISIBLE);
        String fragment_first_title = getResources().getString(R.string.fragment_first_title);
        binding.button.setText("מעבר אל "+fragment_first_title);
        TextView textviewMoney = binding.textviewMoney;

        String[] spinnerArray = {"שנה", "חודש"};


        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(adapter);
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Storage storage = Storage.getInstance();
                if(!textviewMoney.getText().toString().isEmpty())
                    storage.addToMap("taxes", Float.parseFloat(textviewMoney.getText().toString()));
                NavHostFragment.findNavController(Tax_Fragment.this)
                        .navigate((R.id.FirstFragment));
            }
        });
        binding.buttonCalcule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tax;
                if (binding.editTextNumber.getText().toString().equals(""))
                    tax =0;
                else
                    tax = getTaxCalculation(binding.editTextNumber.getText());
                textviewMoney.setText(String.valueOf(tax));
                binding.button.setVisibility(View.VISIBLE);
            }

        });

        double minValue = 2.25;
        double maxValue = 10.0;
        double stepSize = 0.25;
        int numSteps = (int) ((maxValue - minValue) / stepSize) + 1;
        String[] displayedValues = new String[numSteps];
        for (int i = 0; i < numSteps; i++) {
            double value = minValue + (stepSize * i);
            displayedValues[i] = String.format("%.2f", value); // Format the double value as desired
        }
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                binding.textview.setText("הזן את הכנסתך ל"+spinnerArray[position]+" לשיערוך המס ששילמת בשנה זו");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.textview.setText("הזן את הכנסתך ל"+binding.spinner.getSelectedItem().toString()+" לשיערוך המס ששילמת בשנה זו");
        binding.nekudotZhut.setMinValue(0);
        binding.nekudotZhut.setMaxValue(numSteps - 1);
        binding.nekudotZhut.setDisplayedValues(displayedValues);
        binding.nekudotZhut.setValue(1);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private int getTaxCalculation(CharSequence text) {
        String str = text.toString();
        int amount = Integer.parseInt(str);
        if (binding.spinner.getSelectedItem().toString().equals("חודש"))
            amount *=12;
        int tax = amount;
        int taxSum =0;

        int i=0;
        while (tax>0 && i<taxArray.length){
            if (tax - taxArray[i][0]>0){
                taxSum+= (int) Math.round( taxArray[i][0]*taxArray[i][1]);
            }
            else
                taxSum+= (int) Math.round( tax*taxArray[i][1]);
            tax-= taxArray[i][0];
            i++;
        }
        double nekudotMas = 2.25 + (0.25 * binding.nekudotZhut.getValue());
        taxSum -= (int) nekudotMas * 2820;
        int maAm = (int)((amount - taxSum)*0.17);

        return taxSum + maAm;
    }
}