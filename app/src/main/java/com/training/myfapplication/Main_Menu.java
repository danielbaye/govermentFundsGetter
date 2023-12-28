package com.training.myfapplication;

import androidx.appcompat.app.ActionBar;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.training.myfapplication.databinding.FragmentMainMenuBinding;
import com.training.myfapplication.databinding.FragmentSecondBinding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Main_Menu extends Fragment {

    private FragmentMainMenuBinding binding;

    public Main_Menu() {
    }

    public static Main_Menu newInstance() {
        Main_Menu fragment = new Main_Menu();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        this.binding = FragmentMainMenuBinding.inflate(inflater, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeAsUpIndicator(null);
            actionBar.setTitle("");
        }

        binding.Button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavHostFragment.findNavController(Main_Menu.this)
                        .navigate((R.id.FirstFragment));

            }
        });

        binding.Button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavHostFragment.findNavController(Main_Menu.this)
                        .navigate((R.id.action_main_Menu_to_SecondFragment));
            }
        });
        binding.Button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavHostFragment.findNavController(Main_Menu.this)
                        .navigate((R.id.action_main_Menu_to_tax_Fragment));
            }
        });
        binding.Button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(Main_Menu.this)
                        .navigate((R.id.thirdFragment));
            }
        });

        Storage storage = Storage.getInstance();
        float timesOpened = storage.getValue("timesOpened");

        storage.addToMap("timesOpened", timesOpened + 1);

        return binding.getRoot();
        // return inflater.inflate(R.layout.fragment_main__menu, container, false);
    }

}