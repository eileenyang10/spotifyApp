package com.example.spotifyapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SavedWrapped extends Fragment {
    private Spinner spinner;

    private Button button;
    private DatabaseReference mDatabase;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.generate_saved_wrap, container, false);

        spinner = view.findViewById(R.id.savedWraps);
        button = view.findViewById(R.id.button);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("wrapped");

        List<Wrapped> items = new ArrayList<>();

            Bundle args = new Bundle();

            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the dataSnapshot exists and has children
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    // Iterate over the children to retrieve the array elements
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Wrapped element = snapshot.getValue(Wrapped.class);
                        items.add(element);
                    }
                } else {
                    // Handle the case where the dataSnapshot is empty or doesn't exist
                    Log.d("FirebaseArray", "No data found");
                }

                ArrayAdapter<Wrapped> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        args.putSerializable("loadWrap", items.get(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // Another interface callback
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Log.e("FirebaseArray", "Error reading data", databaseError.toException());
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(SavedWrapped.this)
                        .navigate(R.id.action_savedWrapped_to_navigation_home, args);
            }
        });
        return view;
    }


}


