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

public class TopSongsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.top_songs, container, false);

        Intent intent = requireActivity().getIntent();
        Wrapped wrapped = (Wrapped) getArguments().getSerializable("loadWrap");

        ImageView topSongImage1 = (ImageView) view.findViewById(R.id.topSongImage1);
        ImageView topSongImage2 = (ImageView) view.findViewById(R.id.topSongImage2);
        ImageView topSongImage3 = (ImageView) view.findViewById(R.id.topSongImage3);
        Glide.with(getActivity()).load(wrapped.getTrackImages().get(0)).into(topSongImage1);
        Glide.with(getActivity()).load(wrapped.getTrackImages().get(1)).into(topSongImage2);
        Glide.with(getActivity()).load(wrapped.getTrackImages().get(2)).into(topSongImage3);

        TextView topSong1 = (TextView) view.findViewById(R.id.topSong1);
        TextView topSong2 = (TextView) view.findViewById(R.id.topSong2);
        TextView topSong3 = (TextView) view.findViewById(R.id.topSong3);
        ArrayList<String> tracks = wrapped.getTracks();
        topSong1.setText(tracks.get(0));
        topSong2.setText(tracks.get(1));
        topSong3.setText(tracks.get(2));

        return view;
    }
}
