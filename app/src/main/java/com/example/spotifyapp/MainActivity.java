package com.example.spotifyapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    TextView textView;
    FirebaseUser user;
    private ActivityMainBinding binding;
    public static final String CLIENT_ID = "7e3c827957d04767abeb48474576a013";
    public static final String REDIRECT_URI = "spotifyApp://auth";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken, mAccessCode;
    private Call mCall;
    private TextView tokenTextView, codeTextView, profileTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top-level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_games, R.id.navigation_GPT, R.id.navigation_profile)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        // Setup action bar with NavController
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Setup bottom navigation with NavController
        NavigationUI.setupWithNavController(binding.navView, navController);

        auth = FirebaseAuth.getInstance();
        textView = findViewById(R.id.user_details);
        user = auth.getCurrentUser();

        // Check if user returns null, meaning user is not logged in
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        // Initialize the views
        tokenTextView = (TextView) findViewById(R.id.token_text_view);
        codeTextView = (TextView) findViewById(R.id.code_text_view);

        // Initialize the buttons
        Button tokenBtn = (Button) findViewById(R.id.token_btn);
        //Button codeBtn = (Button) findViewById(R.id.code_btn);
        // Button profileBtn = (Button) findViewById(R.id.profile_btn);
        Button artistsBtn = (Button) findViewById(R.id.artist_btn);
        Button tracksBtn = (Button) findViewById(R.id.tracks_btn);

        // Set the click listeners for the buttons

        tokenBtn.setOnClickListener((v) -> {
            getToken();

            if (mAccessToken != null) {
                getCode();
            }
        });

        artistsBtn.setOnClickListener((v) -> {
            for (String artists : getArtists()) {
                System.out.println(artists);
            }
        });

        tracksBtn.setOnClickListener((v) -> {
            for (String tracks : getTracks()) {
                System.out.println(tracks);
            }
        });
    }

    // Override onBackPressed to handle fragment navigation
    @Override
    public void onBackPressed() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        if (!navController.popBackStack()) {
            super.onBackPressed();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.account_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection.
        if(item.getItemId() == R.id.logOutButton){
            logOut();
            return true;
        } else if(item.getItemId() == R.id.deleteAccountButton) {
            delete();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }

    private void logOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }

    private void delete(){
        String password = "123456";

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Reauthentication successful, now delete the account
                            user.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Account deleted successfully, proceed to logout
                                                FirebaseAuth.getInstance().signOut();
                                                // Redirect to login page
                                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                // Account deletion failed
                                                Toast.makeText(MainActivity.this, "Failed to delete account. Please try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            // Reauthentication failed
                            Toast.makeText(MainActivity.this, "Reauthentication failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    /**
     * Get token from Spotify
     * This method will open the Spotify login activity and get the token
     * What is token?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(MainActivity.this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    /**
     * Get code from Spotify
     * This method will open the Spotify login activity and get the code
     * What is code?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getCode() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(MainActivity.this, AUTH_CODE_REQUEST_CODE, request);
    }


    /**
     * When the app leaves this activity to momentarily get a token/code, this function
     * fetches the result of that external activity to get the response from Spotify
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
            setTextAsync(mAccessToken, tokenTextView);

        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
            setTextAsync(mAccessCode, codeTextView);
        }
    }

    /**
     * Get user profile
     * This method will get the user profile using the token
     */
    public void onGetUserProfileClicked() {
        if (mAccessToken == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a request to get the user profile
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?time_range=long_term")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(MainActivity.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    setTextAsync(jsonObject.toString(3), profileTextView);
                    System.out.println(jsonObject.toString(3));
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(MainActivity.this, "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Creates a UI thread to update a TextView in the background
     * Reduces UI latency and makes the system perform more consistently
     *
     * @param text     the text to set
     * @param textView TextView object to update
     */
    private void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

    /**
     * Get authentication request
     *
     * @param type the type of the request
     * @return the authentication request
     */
    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-email", "user-top-read"}) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }

    /**
     * Gets the redirect Uri for Spotify
     *
     * @return redirect Uri object
     */
    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        cancelCall();
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
        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
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
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    // Handle failure to parse data
                    // For now, let's just print a log message
                }
            }
        });

        // Return the list of top artists (this will likely be empty initially)
        System.out.println(topArtists);
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
        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
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


