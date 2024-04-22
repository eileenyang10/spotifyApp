package com.example.spotifyapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    FirebaseAuth auth;
    TextView textView;
    FirebaseUser user;

    private String mAccessToken;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        Button resetPasswordButton = view.findViewById(R.id.reset_password_button);
        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

        // Find TextView by id from the inflated layout
        textView = view.findViewById(R.id.user_details);

        // Get current user
        user = auth.getCurrentUser();

        textView.setText(user.getEmail());

        mAccessToken = ((MainActivity)getActivity()).getmAccessToken();

        // Find logout and delete account buttons
        Button logoutButton = view.findViewById(R.id.logout_button);
        Button deleteAccountButton = view.findViewById(R.id.delete_button);

        // Set click listener for logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        // Set click listener for delete account button
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        // Initialize the RequestQueue using Volley
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // URL of the Spotify Web API's endpoint for getting the current user's profile
        String url = "https://api.spotify.com/v1/me";

        // Create a JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Get the URL of the user's profile picture
                            String imageUrl = response.getJSONArray("images").getJSONObject(0).getString("url");

                            // Get the ImageView
                            ImageView profileImageView = view.findViewById(R.id.profileImageView);

                            // Use Glide to load the image from the URL and set it to the ImageView
                            Glide.with(requireContext()).load(imageUrl).into(profileImageView);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String auth = "Bearer " + mAccessToken;
                headers.put("Authorization", auth);
                return headers;
            }
        };

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest);
        return view;
    }
    private void resetPassword() {
        String email = user.getEmail();

        // Send password reset email
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Password reset email sent.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(requireContext(), Login.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Reauthenticate");
        builder.setMessage("Please enter your password to proceed:");

        // Set up the input
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = input.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(requireContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

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
                                                        Intent intent = new Intent(requireContext(), Login.class);
                                                        startActivity(intent);
                                                        requireActivity().finish();
                                                    } else {
                                                        // Account deletion failed
                                                        Toast.makeText(requireContext(), "Failed to delete account. Please try again.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    // Reauthentication failed
                                    Toast.makeText(requireContext(), "Reauthentication failed. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}