package com.example.spotifyapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GPTFragment extends Fragment {

    private EditText questionField;
    private Button submitButton;
    private TextView responseText;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken, mAccessCode;


    private static final String API_KEY = "AIzaSyDjLda0_6ZOaxsL_4AXH8WIpteyM7oZVDE";

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gpt_fragment, container, false);
        mAccessToken = ((MainActivity)getActivity()).getmAccessToken();


        questionField = view.findViewById(R.id.question_field);
        submitButton = view.findViewById(R.id.submit_button);
        responseText = view.findViewById(R.id.response_text);
        ArrayList<String> artistList = getArtists();
        ArrayList<String> trackList = getTracks();


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = questionField.getText().toString();
                sendQuestionToAPI("I want to ask about my music taste. Here are my top artists and tracks: Artists: " + artistList + " Tracks: " + trackList + ". My question is: " + question + " Make your response very conversational and short and easy to read. I just want you to provide the response in a short/concise manner and in a conversational manner.");
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        });

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
//                    setTextAsync(topArtists.toString(), responseText);
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
//                    setTextAsync(topTracks.toString(), responseText);
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

    private void sendQuestionToAPI(String question) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateUIWithResponse("Loading...");
                String response = fetchResponseFromGeminiAPI(question);
                updateUIWithResponse(response);
            }
        }).start();
    }

    private String fetchResponseFromGeminiAPI(String question) {
        GenerativeModel gm = new GenerativeModel(/* modelName */ "gemini-pro",
                /* apiKey */ API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText(question)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Executor executor = Executors.newFixedThreadPool(5);

        SettableFuture<String> futureResult = SettableFuture.create();

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                futureResult.set(resultText);
            }

            @Override
            public void onFailure(Throwable t) {
                futureResult.setException(t);
            }
        }, executor);

        try {
            return futureResult.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return "Maximum request reached. Please try again later.";
        }

    }
    private void updateUIWithResponse(final String response) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                responseText.setText(response);
            }
        });
    }
}