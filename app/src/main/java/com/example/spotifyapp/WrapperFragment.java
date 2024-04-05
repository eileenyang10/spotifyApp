package com.example.spotifyapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class WrapperFragment extends Fragment {

    private TextView topArtistList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        Bundle bundle = getArguments();

        topArtistList = (TextView) view.findViewById(R.id.topArtistList);
        assert bundle != null;
        ArrayList<String> aList = bundle.getStringArrayList("topArtistList");
        if (aList == null) {
            aList = new ArrayList<String>();
            System.out.println("NULL!!!!");
        }
        topArtistList.setText(((MainActivity)getActivity()).getArtists().toString());

//        TextView topTrackList = (TextView) view.findViewById(R.id.topTrackList);
//        topTrackList.setText(bundle.getStringArrayList("topTrackList").toString());
        return view;
    }

    public void updateArtist(ArrayList<String> aList) {
        topArtistList.setText(aList.toString());
    }
}
