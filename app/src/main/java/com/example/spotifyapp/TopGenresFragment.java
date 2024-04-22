package com.example.spotifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

public class TopGenresFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.top_genres, container, false);

        Intent intent = requireActivity().getIntent();
        Wrapped wrapped = (Wrapped) getArguments().getSerializable("loadWrap");

        TextView topGenre1 = (TextView) view.findViewById(R.id.topGenre1);
        TextView topGenre2 = (TextView) view.findViewById(R.id.topGenre2);
        TextView topGenre3 = (TextView) view.findViewById(R.id.topGenre3);

        topGenre1.setText(wrapped.getGenres().get(0));
        topGenre2.setText(wrapped.getGenres().get(1));
        topGenre3.setText(wrapped.getGenres().get(2));

        return view;
    }
}