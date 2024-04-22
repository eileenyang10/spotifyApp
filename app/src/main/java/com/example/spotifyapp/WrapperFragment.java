package com.example.spotifyapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.spotifyapp.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.Request;;

import com.bumptech.glide.Glide;

public class WrapperFragment extends Fragment {

    FirebaseAuth auth;
    TextView textView;
    FirebaseUser user;
    public static final String CLIENT_ID = "7e3c827957d04767abeb48474576a013";
    public static final String REDIRECT_URI = "spotifyApp://auth";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken, mAccessCode;
    private TextView topArtistList, topTrackList, topGenreList;
    private String time;
    private ImageView album;
    private Wrapped newWrap;
    private DatabaseReference mDatabase;

    private int currentGroup = 0; // Counter for the current group
    private Button nextButton; // Button to go to the next group

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.home_fragment, container, false);


        nextButton = view.findViewById(R.id.nextButton); // Assuming you have a Button with id "nextButton" in your layout


        mAccessToken = ((MainActivity)getActivity()).getmAccessToken();

        time = getArguments().getString("time");

        newWrap = (Wrapped) getArguments().getSerializable("loadWrap");

        topArtistList = (TextView) view.findViewById(R.id.topArtistList);
        topTrackList = (TextView) view.findViewById(R.id.topTrackList);
        topGenreList = (TextView) view.findViewById(R.id.topGenreList);
        album = (ImageView) view.findViewById(R.id.albumImage);

        if (newWrap != null && newWrap.getName() != null) {
            Log.d("wrap is not null", newWrap.toString());
            ArrayList<String> artists = newWrap.getArtists();
            topArtistList.setText(String.format("%s\n%s\n%s", artists.get(0), artists.get(1), artists.get(2)));
            ArrayList<String> tracks = newWrap.getTracks();
            topTrackList.setText(String.format("%s\n%s\n%s", tracks.get(0), tracks.get(1), tracks.get(2)));
            ArrayList<String> genres = newWrap.getGenres();
            topGenreList.setText(genres.get(0));

            Glide.with(getActivity()).load(newWrap.getImage()).into(album);
        } else {
            newWrap = new Wrapped();
            getArtists();
            getTracks();
        }


        setHasOptionsMenu(true);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("wrapped");

        view.findViewById(R.id.albumImage).setVisibility(View.GONE);
        view.findViewById(R.id.textView).setVisibility(View.GONE);

        view.findViewById(R.id.topSong).setVisibility(View.GONE);
        view.findViewById(R.id.topTrackList).setVisibility(View.GONE);

        view.findViewById(R.id.topArtists).setVisibility(View.GONE);
        view.findViewById(R.id.topArtistList).setVisibility(View.GONE);

        view.findViewById(R.id.topGenre).setVisibility(View.GONE);
        view.findViewById(R.id.topGenreList).setVisibility(View.GONE);


        nextButton.setOnClickListener(v -> {
            currentGroup++;
            switch (currentGroup) {
                case 1:
                    view.findViewById(R.id.topGenre).setVisibility(View.GONE);
                    view.findViewById(R.id.topGenreList).setVisibility(View.GONE);
                    view.findViewById(R.id.albumImage).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.textView).setVisibility(View.VISIBLE);
                    System.out.println("currentGroup: " + currentGroup);
                    break;
                case 2:
                    view.findViewById(R.id.albumImage).setVisibility(View.GONE);
                    view.findViewById(R.id.textView).setVisibility(View.GONE);
                    view.findViewById(R.id.topSong).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.topTrackList).setVisibility(View.VISIBLE);
                    System.out.println("currentGroup: " + currentGroup);

                    break;
                case 3:
                    view.findViewById(R.id.topSong).setVisibility(View.GONE);
                    view.findViewById(R.id.topTrackList).setVisibility(View.GONE);
                    view.findViewById(R.id.topArtists).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.topArtistList).setVisibility(View.VISIBLE);
                    System.out.println("currentGroup: " + currentGroup);
                    break;

                case 4:
                    view.findViewById(R.id.topArtists).setVisibility(View.GONE);
                    view.findViewById(R.id.topArtistList).setVisibility(View.GONE);
                    view.findViewById(R.id.topGenre).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.topGenreList).setVisibility(View.VISIBLE);
                    System.out.println("currentGroup: " + currentGroup);
                    break;
                default:
                    view.findViewById(R.id.topGenre).setVisibility(View.GONE);
                    view.findViewById(R.id.topGenreList).setVisibility(View.GONE);

                    currentGroup = 0;
                    break;
            }
        });


        return view;
    }

    private String getTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        int year = currentDateTime.getYear();
        int month = currentDateTime.getMonthValue();
        int day = currentDateTime.getDayOfMonth();
        int hour = currentDateTime.getHour();
        int minute = currentDateTime.getMinute();
        int second = currentDateTime.getSecond();

        return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.wrapped_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.saveButton){
            newWrap.setName(getTime());
            mDatabase.child(newWrap.getName()).setValue(newWrap);
            return true;
        } else if (item.getItemId() == R.id.regenerate) {
            NavHostFragment.findNavController(WrapperFragment.this)
                    .navigate(R.id.action_navigation_home_to_navigation_home_generate);
            return true;
        } else if (item.getItemId() == R.id.pastWrapped) {
            NavHostFragment.findNavController(WrapperFragment.this)
                    .navigate(R.id.action_navigation_home_to_savedWrapped);
            return true;
        }
        return false;
    }

    /**
     * Creates a UI thread to update a TextView in the background
     * Reduces UI latency and makes the system perform more consistently
     *
     * @param text     the text to set
     * @param textView TextView object to update
     */
    private void setTextAsync(final String text, TextView textView) {
        getActivity().runOnUiThread(() -> {
            if (isAdded()) {
                textView.setText(text);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public ArrayList<String> getArtists() {

        ArrayList<String> topArtists = new ArrayList<>();

        if (mAccessToken == null) {
            // If access token is null, return empty list
            return topArtists;
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


                    // Loop through each item (artist)
                    for (int i = 0; i < items.length(); i++) {
                        // Get the artist object
                        JSONObject artist = items.getJSONObject(i);

                        // Get the name of the artist
                        String artistName = artist.getString("name");

                        // Add the artist name to the list
                        topArtists.add(artistName);
                    }

                    // Now, you can use the topArtists list as needed
                    // For now, let's just log the list
                    Log.d("Top Artists", topArtists.toString());
                    String top = topArtists.get(0) + "\n" + topArtists.get(1) + "\n" + topArtists.get(2);
                    setTextAsync(top, topArtistList);

                    newWrap.setArtists(topArtists);

                    JSONObject artist = items.getJSONObject(0);
                    JSONArray genres = artist.getJSONArray("genres");
                    ArrayList<String> topGenres = new ArrayList<>();
                    for (int i = 0; i < genres.length(); i++) {
                        topGenres.add(genres.get(i).toString());
                    }

                    newWrap.setGenres(topGenres);

                    setTextAsync(genres.get(0).toString(), topGenreList);

                    JSONArray images = artist.getJSONArray("images");
                    String imageUrl = images.getJSONObject(0).getString("url");
                    newWrap.setImage(imageUrl);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(getActivity())
                                    .load(imageUrl)
                                    .into(album);                                    }
                    });
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

        if (mAccessToken == null) {
            // If access token is null, return empty list
            return topTracks;
        }

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

                    // Loop through each item (track)
                    for (int i = 0; i < items.length(); i++) {
                        // Get the track object
                        JSONObject track = items.getJSONObject(i);

                        // Get the name of the track
                        String trackName = track.getString("name");

                        // Add the track name to the list
                        topTracks.add(trackName);
                    }

                    newWrap.setTracks(topTracks);

                    // Now, you can use the topTracks list as needed
                    // For now, let's just log the list
                    Log.d("Top Tracks", topTracks.toString());
                    String top = topTracks.get(0) + "\n" + topTracks.get(1) + "\n" + topTracks.get(2);
                    setTextAsync(top, topTrackList);

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


