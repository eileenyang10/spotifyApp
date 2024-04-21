package com.example.spotifyapp;

import android.text.Html;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import android.text.Html;
import com.example.spotifyapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.view.Gravity;

public class GameFragment extends Fragment {

    private TextView songTextView;
    private TextView scoreTextView;

    private Button option1Button;
    private Button option2Button;
    private Button option3Button;
    private Button option4Button;
    private String correctAnswer;
    private ImageView gameImageView;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private int score = 0;
    private Call fetchSongsCall;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_fragment, container, false);

        mAccessToken = ((MainActivity)getActivity()).getmAccessToken();

        // Initialize views
        songTextView = view.findViewById(R.id.songTextView);
        scoreTextView = view.findViewById(R.id.scoreTextView);
        option1Button = view.findViewById(R.id.option1Button);
        option2Button = view.findViewById(R.id.option2Button);
        option3Button = view.findViewById(R.id.option3Button);
        option4Button = view.findViewById(R.id.option4Button);
        gameImageView = view.findViewById(R.id.gameImageView);

        // Fetch song for the selected playlist and display it
        fetchAndDisplaySong();

        return view;
    }

    private void fetchAndDisplaySong() {
        // Fetch playlists for the user using Spotify API
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/playlists")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        fetchSongsCall = mOkHttpClient.newCall(request);
        fetchSongsCall.enqueue(new Callback()  {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Handle failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                try {
                    JSONObject jsonResponse = new JSONObject(response.body().string());
                    JSONArray playlists = jsonResponse.getJSONArray("items");

                    // Randomly select a playlist
                    int playlistIndex = new Random().nextInt(playlists.length());
                    JSONObject selectedPlaylist = playlists.getJSONObject(playlistIndex);
                    correctAnswer = selectedPlaylist.getString("name");

                    // Fetch the songs from the selected playlist
                    String playlistId = selectedPlaylist.getString("id");
                    Request songRequest = new Request.Builder()
                            .url("https://api.spotify.com/v1/playlists/" + playlistId + "/tracks")
                            .addHeader("Authorization", "Bearer " + mAccessToken)
                            .build();

                    mOkHttpClient.newCall(songRequest).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            // Handle failure
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                throw new IOException("Unexpected code " + response);
                            }

                            try {
                                JSONObject songResponse = new JSONObject(response.body().string());
                                JSONArray songs = songResponse.getJSONArray("items");

                                // Randomly select a song
                                int songIndex = new Random().nextInt(songs.length());
                                JSONObject song = songs.getJSONObject(songIndex).getJSONObject("track");
                                String songName = song.getString("name");

                                // Get the artist of the song
                                JSONArray artists = song.getJSONArray("artists");
                                String artistName = artists.getJSONObject(0).getString("name");

                                // Get the album cover image URL
                                JSONObject album = song.getJSONObject("album");
                                JSONArray images = album.getJSONArray("images");
                                String imageUrl = images.getJSONObject(0).getString("url");

                                String finalSongDisplay = songName + " by " + artistName;
                                // Display the song name
                                String finalfinalSongDisplay = "What playlist is the song <b>" + finalSongDisplay + "</b> in?";
                                // Update the songTextView in the UI thread
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isAdded()) {
                                            songTextView.setText(Html.fromHtml(finalfinalSongDisplay, Html.FROM_HTML_MODE_COMPACT));
                                            Glide.with(getActivity())
                                                    .load(imageUrl)
                                                    .into(gameImageView);

                                        }
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    // Create a list of incorrect answers
                    List<String> incorrectAnswers = new ArrayList<>();
                    for (int i = 0; i < playlists.length(); i++) {
                        if (i != playlistIndex) {
                            incorrectAnswers.add(playlists.getJSONObject(i).getString("name"));
                        }
                    }

                    // Randomly select three incorrect answers
                    Collections.shuffle(incorrectAnswers);
                    incorrectAnswers = incorrectAnswers.subList(0, 3);

                    // Add the correct answer to the list and shuffle it
                    incorrectAnswers.add(correctAnswer);
                    Collections.shuffle(incorrectAnswers);

                    // Set the answers to the buttons in the UI thread
                    List<String> finalIncorrectAnswers = incorrectAnswers;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isAdded()) {
                                option1Button.setText(finalIncorrectAnswers.get(0));
                                option2Button.setText(finalIncorrectAnswers.get(1));
                                option3Button.setText(finalIncorrectAnswers.get(2));
                                option4Button.setText(finalIncorrectAnswers.get(3));

                                // Set click listeners for the buttons
                                option1Button.setOnClickListener(v -> checkAnswer(finalIncorrectAnswers.get(0), option1Button));
                                option2Button.setOnClickListener(v -> checkAnswer(finalIncorrectAnswers.get(1), option2Button));
                                option3Button.setOnClickListener(v -> checkAnswer(finalIncorrectAnswers.get(2), option3Button));
                                option4Button.setOnClickListener(v -> checkAnswer(finalIncorrectAnswers.get(3), option4Button));
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void checkAnswer(String selectedAnswer, Button selectedButton) {
        if (selectedAnswer.equals(correctAnswer)) {
            // Correct answer
            Animation correctAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.correct_answer);
            selectedButton.startAnimation(correctAnimation);
            // Toast animation
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_correct, (ViewGroup) getView().findViewById(R.id.toast_root));
            Toast toast = new Toast(getActivity());
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            // Load the fade-out animation
            Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.toast_fade);

            // Apply the animation to the Toast
            layout.startAnimation(fadeOut);
            toast.show();

            score++;
            fetchAndDisplaySong();

        } else {
            // Incorrect answer
            Animation shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
            selectedButton.startAnimation(shakeAnimation);

            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_wrong, (ViewGroup) getView().findViewById(R.id.toast_root));
            Toast toast = new Toast(getActivity());
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
            score = 0;
        }
        scoreTextView.setText("Score: " + String.valueOf(score));
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (fetchSongsCall != null) {
            fetchSongsCall.cancel();
        }
    }
}
