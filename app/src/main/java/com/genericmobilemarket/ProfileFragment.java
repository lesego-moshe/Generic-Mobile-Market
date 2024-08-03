package com.genericmobilemarket;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileFragment extends Fragment {

    private TextView fullName, uname, uemail, ucontact, avgRating;
    private Button logout;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        fullName = view.findViewById(R.id.fullNametextView);
        uname = view.findViewById(R.id.usernametextView);
        uemail = view.findViewById(R.id.emailtextView);
        ucontact = view.findViewById(R.id.contactTextView);
        //avgRating = view.findViewById(R.id.ratingTextView);
        logout = view.findViewById(R.id.logoutButton);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setBackgroundResource(R.drawable.rick);

        String username = UserData.username;

        if (username != null) {
            getUserInfo(username);
        } else {
            Toast.makeText(requireContext(), "Username not found", Toast.LENGTH_SHORT).show();
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutConfirmationDialog();
            }
        });

        return view;
    }



    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logout();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getUserInfo(String username) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://lamp.ms.wits.ac.za/home/s2343495/user_info.php?username=" + username;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected Code" + response);
                }

                final String result = Objects.requireNonNull(response.body()).string();

                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleUserInfoResponse(result);
                    }
                });
            }
        });
    }

    private void handleUserInfoResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean success = jsonObject.getBoolean("success");

            if (success) {
                String userID = jsonObject.getString("userId");
                String firstname = jsonObject.getString("firstname");
                String lastname = jsonObject.getString("lastname");
                String username = jsonObject.getString("username");
                String email = jsonObject.getString("email");
                String contact = jsonObject.getString("contact");
                //String rating = jsonObject.getString("rating");

                fullName.setText(firstname + " " + lastname);
                uname.setText(username + " " + "#" + userID);
                uemail.setText("Email: " + email);
                ucontact.setText("Contact Details: " + contact);
                //avgRating.setText("Rating: " + rating + "(★)");
            } else {
                String message = jsonObject.getString("message");
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void logout() {
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}