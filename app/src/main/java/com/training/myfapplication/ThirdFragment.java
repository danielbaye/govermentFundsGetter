package com.training.myfapplication;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.training.myfapplication.databinding.FragmentThirdBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;

public class ThirdFragment extends Fragment {

    private FragmentThirdBinding binding;
    float govMoney, meMoney;
    Float yearSum;

    int currentPosition;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentThirdBinding.inflate(inflater, container, false);
        InfoHandler ih = new InfoHandler("https://next.obudget.org/api/query?query=",
                getContext());

        govMoney = 0f;
        meMoney = 0f;
        currentPosition = 0;
        Map<String, Float> budgetByYear = ih.getJustBudgetByYear();
        int year = Calendar.getInstance().get(Calendar.YEAR) + 1;
        yearSum = budgetByYear.get(String.valueOf(year));

        binding.govMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                govMoney = Float.parseFloat(s.toString());
                changeOut();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.meMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                meMoney = Float.parseFloat(s.toString());
                changeOut();
            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("whats this");
                System.out.println(s);

            }
        });

        binding.Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.constrainedLayout.setVisibility(View.INVISIBLE);
                binding.taxLayout.setVisibility(View.VISIBLE);

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
                binding.taxLayout.setVisibility(View.INVISIBLE);

                Storage storage = Storage.getInstance();
                binding.meMoney.setText(String.format("%.0f", storage.getValue("taxes")));
                meMoney = storage.getValue("taxes");
                changeOut();
            }
        });
        binding.taxLayout.setVisibility(View.INVISIBLE);

        String[] spinnerArray = { "שקלים", "אלפים", "מליונים", "מיליאדרים" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(adapter);
        binding.spinner.setSelection(0);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentPosition = position;
                changeOut();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (Storage.getInstance().getValue("timesOpened") > 8)
            binding.textviewExplnaiton.setText("");
        else
            binding.constrainedLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.textviewExplnaiton.setText("");
                }
            });

        return binding.getRoot();

    }

    private void changeOut() {
        if (govMoney > 0 && yearSum > 0 && meMoney > 0) {
            if (govMoney > yearSum) {
                Toast.makeText(getContext(),
                        "הוצאת הממשלה החדשה לא יכולה" +
                                " להיות גדולה מהתקציב שלה!",
                        Toast.LENGTH_LONG);
                return;
            }
            float tens = (float) (Math.pow(10, 3 * currentPosition));
            float govSpends = govMoney * tens / (govMoney * tens + yearSum);
            int outMoney = (int) (govSpends * meMoney);
            DecimalFormat decimalFormat = new DecimalFormat("#,###");

            binding.actualMoney.setText(decimalFormat.format(outMoney) + "  שקלים ");
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}