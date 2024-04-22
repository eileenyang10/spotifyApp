package com.example.spotifyapp;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Html;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GameFragment2 extends Fragment {

    private TextView questionTextView;
    private Button option1Button;
    private Button option2Button;
    private Button option3Button;
    private Button option4Button;
    private TextView scoreTextView;

    private List<String[]> playlistData = new ArrayList<>();
    private OkHttpClient client = new OkHttpClient();
    private String mAccessToken;
    private int score = 0;

    private Call fetchSongsCall;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game2, container, false);
        mAccessToken = ((MainActivity)getActivity()).getmAccessToken();
        questionTextView = view.findViewById(R.id.questionTextView);
        option1Button = view.findViewById(R.id.option1Button);
        option2Button = view.findViewById(R.id.option2Button);
        option3Button = view.findViewById(R.id.option3Button);
        option4Button = view.findViewById(R.id.option4Button);
        scoreTextView = view.findViewById(R.id.scoreTextView);

        fetchAndDisplaySong();

        return view;
    }

    private void fetchAndDisplaySong() {
        // Fetch playlists for the user using Spotify API
        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/playlists")
                .addHeader("Authorization", "Bearer " + mAccessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
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
                    int playlistIndex = (int) (Math.random() * playlists.length());
                    JSONObject selectedPlaylist = playlists.getJSONObject(playlistIndex);

                    // Fetch songs from the selected playlist
                    String playlistId = selectedPlaylist.getString("id");
                    Request songRequest = new Request.Builder()
                            .url("https://api.spotify.com/v1/playlists/" + playlistId + "/tracks")
                            .addHeader("Authorization", "Bearer " + mAccessToken)
                            .build();

                    fetchSongsCall = client.newCall(songRequest);
                    fetchSongsCall.enqueue(new Callback() {
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


                                // Parse the tracks
                                for (int i = 0; i < songs.length(); i++) {
                                    JSONObject track = songs.getJSONObject(i).getJSONObject("track");

                                    String songName = track.getString("name");
                                    String artistName = track.getJSONArray("artists").getJSONObject(0).getString("name");
                                    playlistData.add(new String[]{songName, artistName});
                                }

                                // Start the game on the UI thread
                                getActivity().runOnUiThread(() ->{
                                        if (isAdded()) {
                                            // Start the game on the UI thread
                                            startNewQuestion();
                                        }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startNewQuestion() {
        if (isAdded()) {
            // Randomly select a song from the playlist
            int questionIndex = (int) (Math.random() * playlistData.size());
            String questionSong = playlistData.get(questionIndex)[0];
            String correctAnswer = playlistData.get(questionIndex)[1];

            // Display the song name
            String songName = "Who is the artist of the song <b>" + questionSong + "</b>?";
            questionTextView.setText(Html.fromHtml(songName, Html.FROM_HTML_MODE_COMPACT));

            // Randomly select other artists from the playlist to be the wrong answers
            // Note: This is a simplified version and doesn't guarantee that the wrong answers are unique or different from the correct answer
            String[] answers = new String[4];
            answers[0] = correctAnswer;

            // Use a Set to store the artists that have already been used
            Set<String> usedArtists = new HashSet<>();
            usedArtists.add(correctAnswer);

            for (int i = 1; i < 4; i++) {
                int wrongAnswerIndex;
                String wrongAnswer;
                do {
                    wrongAnswerIndex = (int) (Math.random() * playlistData.size());
                    wrongAnswer = playlistData.get(wrongAnswerIndex)[1];
                } while (usedArtists.contains(wrongAnswer));
                answers[i] = wrongAnswer;
                usedArtists.add(wrongAnswer);
            }

            // Randomly shuffle the answers
            for (int i = 0; i < answers.length; i++) {
                int j = (int) (Math.random() * answers.length);
                String temp = answers[i];
                answers[i] = answers[j];
                answers[j] = temp;
            }

            // Display the multiple choice options
            option1Button.setText(answers[0]);
            option2Button.setText(answers[1]);
            option3Button.setText(answers[2]);
            option4Button.setText(answers[3]);

            // Set click listeners for the buttons
            option1Button.setOnClickListener(v -> checkAnswer(answers[0], correctAnswer, option1Button));
            option2Button.setOnClickListener(v -> checkAnswer(answers[1], correctAnswer, option2Button));
            option3Button.setOnClickListener(v -> checkAnswer(answers[2], correctAnswer, option3Button));
            option4Button.setOnClickListener(v -> checkAnswer(answers[3], correctAnswer, option4Button));
        }
    }

    private void checkAnswer(String selectedAnswer, String correctAnswer, Button selectedButton) {
            if (isAdded()) {
                if (selectedAnswer.equals(correctAnswer)) {
                    // The answer is correct
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
                    startNewQuestion();
                } else {
                    // The answer is wrong
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
                // Start a new question
                scoreTextView.setText("Score: " + String.valueOf(score));
            }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (fetchSongsCall != null) {
            fetchSongsCall.cancel();
        }
    }
}