package com.training.myfapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.training.myfapplication.databinding.FragmentMainMenuBinding;
import com.training.myfapplication.databinding.FragmentSecondBinding;

public class MainMenu extends Fragment {
    private FragmentMainMenuBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentMainMenuBinding.inflate(inflater, container, false);

        binding.Button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Navigation.findNavController(v).navigate(R.id.action_main_Menu_to_FirstFragment);
            }
        });
        binding.Button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_main_Menu_to_SecondFragment);
            }
        });
        return binding.getRoot();

    }
}
