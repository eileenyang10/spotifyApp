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
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GenerateWrapped extends Fragment {

    private Spinner spinner;

    private Button button;

    private String time;
    private Wrapped newWrap;
    private String mAccessToken;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private Bundle args;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.generate_wrapped, container, false);

        spinner = view.findViewById(R.id.savedWraps);
        button = view.findViewById(R.id.button);
        mAccessToken = ((MainActivity)requireActivity()).getmAccessToken();

        List<String> items = new ArrayList<>();
        items.add("Past Year");
        items.add("Past 6 Months");
        items.add("Past Month");

        args = new Bundle();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, items);        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
                getArtists();
            }
        });

        newWrap = new Wrapped();
        return view;
    }

    public ArrayList<String> getArtists() {

        ArrayList<String> topArtists = new ArrayList<>();

        if (mAccessToken == null) {
            mAccessToken = ((MainActivity)getActivity()).getmAccessToken();
            // If access token is null, return empty list
        }

        // Create a request to get the user's top artists
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/artists?time_range="+time)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        // Execute the request asynchronously
        //cancelCall();
        Call mCallArtist = mOkHttpClient.newCall(request);

        mCallArtist.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                // Handle failure to fetch data
                // For now, let's just print a log message
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    // Parse the response body as JSON
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    // Get the array of items (artists)
                    JSONArray items = jsonObject.getJSONArray("items");

                    ArrayList<String> topGenres = new ArrayList<>();
                    // Loop through each item (artist)
                    for (int i = 0; i < items.length(); i++) {
                        // Get the artist object
                        JSONObject artist = items.getJSONObject(i);

                        // Get the name of the artist
                        String artistName = artist.getString("name");

                        JSONArray genres = artist.getJSONArray("genres");

                        for (int j = 0; j < genres.length(); j++) {
                            if (!topGenres.contains(genres.get(j).toString())) {
                                topGenres.add(genres.get(j).toString());
                            }
                        }

                        // Add the artist name to the list
                        topArtists.add(artistName);
                    }

                    // Now, you can use the topArtists list as needed
                    // For now, let's just log the list
                    Log.d("Top Artists", topArtists.toString());
                    String top = topArtists.get(0) + "\n" + topArtists.get(1) + "\n" + topArtists.get(2);

                    newWrap.setArtists(topArtists);

                    JSONObject artist = items.getJSONObject(0);

                    newWrap.setGenres(topGenres);

                    JSONArray images = artist.getJSONArray("images");
                    String imageUrl = images.getJSONObject(0).getString("url");
                    newWrap.setImage(imageUrl);

                    getTracks();

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    // Handle failure to parse data
                    // For now, let's just print a log message
                }
            }
        });

        // Return the list of top artists (this will likely be empty initially)
        System.out.println("artists: " + topArtists);

        return topArtists;
    }


    public ArrayList<String> getTracks() {
        ArrayList<String> topTracks = new ArrayList<>();

        // Create a request to get the user's top tracks
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?time_range="+time)
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        // Execute the request asynchronously
        //cancelCall();
        Call mCallTrack = mOkHttpClient.newCall(request);

        mCallTrack.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                // Handle failure to fetch data
                // For now, let's just print a log message
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    // Parse the response body as JSON
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    // Get the array of items (tracks)
                    JSONArray items = jsonObject.getJSONArray("items");

                    ArrayList<String> topTrackImages = new ArrayList<>();

                    // Loop through each item (track)
                    for (int i = 0; i < items.length(); i++) {
                        // Get the track object
                        JSONObject track = items.getJSONObject(i);

                        // Get the name of the track
                        String trackName = track.getString("name");
                        String imageUrl = (track.getJSONObject("album")).getJSONArray("images").getJSONObject(0).getString("url");

                        // Add the track name to the list
                        topTracks.add(trackName);
                        topTrackImages.add(imageUrl);
                    }

                    newWrap.setTracks(topTracks);
                    newWrap.setTrackImages(topTrackImages);

                    // Now, you can use the topTracks list as needed
                    // For now, let's just log the list
                    Log.d("Top Tracks", topTracks.toString());
                    String top = topTracks.get(0) + "\n" + topTracks.get(1) + "\n" + topTracks.get(2);
                    //setTextAsync(top, topTrackList);
                    Log.d("Wrapped", newWrap.getImage());
                    newWrap.setName("hi");

                    getActivity().runOnUiThread(() -> {
                        //Log.d("HELLO", "HELLO");
                        args.putSerializable("loadWrap", newWrap);
                        Log.d("Args", args.toString());
                        Log.d("Wrapped", newWrap.getTracks().toString());
                        NavHostFragment.findNavController(GenerateWrapped.this)
                                .navigate(R.id.action_generateWrapped_to_navigation_home, args);
                    });

                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    // Handle failure to parse data
                    // For now, let's just print a log message
                }


            }
        });

        // Return the list of top tracks (this will likely be empty initially)
        System.out.println(topTracks);
        return topTracks;
    }

}
