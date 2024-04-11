package com.example.spotifyapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.List;

public class GenerateWrapped extends Fragment {

    private Spinner spinner;

    private Button button;

    private String time;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.generate_wrapped, container, false);

        spinner = view.findViewById(R.id.timeFrame);
        button = view.findViewById(R.id.button);

        List<String> items = new ArrayList<>();
        items.add("Past Year");
        items.add("Past 6 Months");
        items.add("Past Month");

        Bundle args = new Bundle();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if (position == 0) {
                    time =  "long_term";
                } else if (position == 1) {
                    time = "medium_term";
                } else {
                    time = "short_term";
                }

                args.putString("time", time);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Another interface callback
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(GenerateWrapped.this)
                        .navigate(R.id.action_generateWrapped_to_navigation_home, args);
            }
        });
        return view;
    }

}
