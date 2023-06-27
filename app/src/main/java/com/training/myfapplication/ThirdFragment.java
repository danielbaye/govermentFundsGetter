package com.training.myfapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.training.myfapplication.databinding.FragmentThirdBinding;


public class ThirdFragment extends Fragment {



private FragmentThirdBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

      binding = FragmentThirdBinding.inflate(inflater, container, false);

      return binding.getRoot();

    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String URL="https://next.obudget.org/datapackages/entities/all/datapackage.json";


        binding.actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "clicked", Toast.LENGTH_SHORT).show();


            }
        });

//
//        binding.buttonPrev.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                NavHostFragment.findNavController(ThirdFragment.this)
//                        .navigate((R.id.action_thirdFragment_to_FirstFragment));
//            }
//        });
    }



@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}