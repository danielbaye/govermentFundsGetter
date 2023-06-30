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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Main_Menu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Main_Menu extends Fragment {

    private FragmentMainMenuBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Main_Menu() {
        // Required empty public constructor
    }
    public static Main_Menu newInstance(String param1, String param2) {
        Main_Menu fragment = new Main_Menu();
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
//                binding.editText.setVisibility(View.VISIBLE);
//                binding.submitButton.setVisibility(View.VISIBLE);
                 NavHostFragment.findNavController(Main_Menu.this)
                                .navigate((R.id.FirstFragment));
//                binding.Button1.setVisibility(View.INVISIBLE);
//                binding.Button2.setVisibility(View.INVISIBLE);
//                binding.Button3.setVisibility(View.INVISIBLE);
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
//        EditText editText = binding.editText;
//        binding.submitButton.setVisibility(View.INVISIBLE);
//        binding.editText.setVisibility(View.INVISIBLE);

//        editText.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
//                    binding.submitButton.setVisibility(View.INVISIBLE);
//                    binding.editText.setVisibility(View.INVISIBLE);
//                    binding.Button1.setVisibility(View.VISIBLE);
//                    binding.Button2.setVisibility(View.VISIBLE);
//                    binding.Button3.setVisibility(View.VISIBLE);
//                    return true;
//                }
//                return false;
//            }
//        });


//        binding.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                   @Override
//                   public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                       if (actionId == EditorInfo.IME_ACTION_DONE){
//                           Storage storage = Storage.getInstance();
//                           if(!editText.getText().toString().isEmpty())
//                               storage.addToMap("taxes", Float.parseFloat(editText.getText().toString()));
//                           NavHostFragment.findNavController(Main_Menu.this)
//                                   .navigate((R.id.FirstFragment));
//                           return true;
//                       }
//                       return false;
//                   }
//               });

//                binding.submitButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Storage storage = Storage.getInstance();
//                        if(!editText.getText().toString().isEmpty())
//                            storage.addToMap("taxes", Float.parseFloat(editText.getText().toString()));
////                        storage.addToMap("taxes", Float.parseFloat(editText.getText().toString()));
//                        NavHostFragment.findNavController(Main_Menu.this)
//                                .navigate((R.id.FirstFragment));
//                    }
//                });

                binding.Button4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NavHostFragment.findNavController(Main_Menu.this)
                                .navigate((R.id.thirdFragment));
                    }
                });



        return binding.getRoot();
//        return inflater.inflate(R.layout.fragment_main__menu, container, false);
    }
}