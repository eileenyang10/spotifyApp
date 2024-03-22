package com.example.spotifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
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

public class ProfileFragment extends Fragment {
    FirebaseAuth auth;
    TextView textView;
    FirebaseUser user;

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
}