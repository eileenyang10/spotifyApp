package com.example.spotifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class TopArtistsFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.top_artists, container, false);

        Intent intent = requireActivity().getIntent();
        Wrapped wrapped = (Wrapped) getArguments().getSerializable("loadWrap");

        ImageView artistView = (ImageView) view.findViewById(R.id.artistImage);
        Glide.with(getActivity()).load(wrapped.getImage()).into(artistView);

        TextView artistsList = (TextView) view.findViewById(R.id.topArtistNames);
        ArrayList<String> artists = wrapped.getArtists();
        artistsList.setText(String.format("%s\n%s\n%s", artists.get(0), artists.get(1), artists.get(2)));

        return view;
    }
}
