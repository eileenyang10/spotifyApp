package com.example.spotifyapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.Request;;

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
    //private Call mCall;
    private TextView tokenTextView, codeTextView, profileTextView;

    private Bundle bundle;

    private WrapperFragment wrapped;
    private TextView topArtistList;
    private TextView topTrackList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        mAccessToken = ((MainActivity)getActivity()).getmAccessToken();

        topArtistList = (TextView) view.findViewById(R.id.topArtistList);
        getArtists();

        topTrackList = (TextView) view.findViewById(R.id.topTrackList);
        getTracks();
        return view;
    }

    /**
     * Creates a UI thread to update a TextView in the background
     * Reduces UI latency and makes the system perform more consistently
     *
     * @param text     the text to set
     * @param textView TextView object to update
     */
    private void setTextAsync(final String text, TextView textView) {
        getActivity().runOnUiThread(() -> textView.setText(text));
    }

//    private void cancelCall() {
//        if (mCall != null) {
//            mCall.cancel();
//        }
//    }

    @Override
    public void onDestroy() {
        //cancelCall();
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
                .url("https://api.spotify.com/v1/me/top/artists")
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
                    setTextAsync(topArtists.toString(), topArtistList);
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
                .url("https://api.spotify.com/v1/me/top/tracks")
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

                    // Now, you can use the topTracks list as needed
                    // For now, let's just log the list
                    Log.d("Top Tracks", topTracks.toString());
                    setTextAsync(topTracks.toString(), topTrackList);
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


