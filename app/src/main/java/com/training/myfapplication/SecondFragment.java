package com.training.myfapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.training.myfapplication.databinding.ActivityMainBinding;
import com.training.myfapplication.databinding.FragmentSecondBinding;

import java.util.ArrayList;
import java.util.List;


public class SecondFragment extends Fragment {



private FragmentSecondBinding binding;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

      binding = FragmentSecondBinding.inflate(inflater, container, false);
      recyclerView = binding.recyclerView;
      recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Item> itemList = new ArrayList<>();

        itemList.add(new Item("Item 1", "Value 1"));
        itemList.add(new Item("Item 2", "Value 2"));
        itemList.add(new Item("Item 3", "Value 3"));

        itemAdapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);

        return binding.getRoot();

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


}