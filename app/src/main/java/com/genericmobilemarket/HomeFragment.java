package com.genericmobilemarket;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    LinearLayout items;
    private LinearLayout mainView;
    String searchPrompt;
    View nextLayout;
    TextView nameDesc;
    TextView priceDesc;
    TextView descDesc;
    TextView uploader;
    TextView rating;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                showLogoutConfirmationDialog();
                return true;
            }
            return false;
        });

        Button button = view.findViewById(R.id.button6);
        EditText editText = view.findViewById(R.id.editTextTextPersonName);


        items = new LinearLayout(requireContext());
        items.setId(View.generateViewId());
        items.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        items.setOrientation(LinearLayout.VERTICAL);
        ConstraintLayout mainLayout = view.findViewById(R.id.HomeLayout);

        mainLayout.addView(items);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mainLayout);
        constraintSet.connect(items.getId(), ConstraintSet.TOP, editText.getId(), ConstraintSet.BOTTOM);
        constraintSet.applyTo(mainLayout);

        LayoutInflater layoutInflater = LayoutInflater.from(requireContext());
        nextLayout = layoutInflater.inflate(R.layout.itemdesc, null);
        nameDesc = nextLayout.findViewById(R.id.textView);
        priceDesc = nextLayout.findViewById(R.id.textView2);
        descDesc = nextLayout.findViewById(R.id.textView3);
        uploader = nextLayout.findViewById(R.id.textView4);
        rating = nextLayout.findViewById(R.id.textView5);
        Button buyButton = nextLayout.findViewById(R.id.buyItemButton);


        View thisLayout = layoutInflater.inflate(R.layout.fragment_home, null);

        Button button1 = nextLayout.findViewById(R.id.button2);

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showRatingDialog(productID);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPrompt = editText.getText().toString().trim();

                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new FormBody.Builder()
                        .add("item", searchPrompt)
                        .build();

                Request request = new Request.Builder()
                        .url("https://lamp.ms.wits.ac.za/home/s2343495/search.php")
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        final String responseData = response.body().string();

                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    processSearchJSON(responseData);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        // Handle failure
                    }
                });
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().onBackPressed();
            }
        });
        mainView = view.findViewById(R.id.mainView);

        OkHttpClient client = new OkHttpClient();
        String url = "https://lamp.ms.wits.ac.za/home/s2343495/home.php";

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected Code" + response);

                final String result = Objects.requireNonNull(response.body()).string();

                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processItemJSON(result);
                    }
                });
            }
        });
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

    private void logout() {
        startActivity(new Intent(requireContext(), LoginActivity.class));
        requireActivity().finish();
    }

    public void processSearchJSON(String json) throws JSONException {
        JSONArray ja = new JSONArray(json);
        for (int i = 0; i < ja.length(); i++) {
            JSONObject jo = ja.getJSONObject(i);
            String name = jo.getString("Item_Name");
            ItemList itemList = new ItemList(requireContext());
            itemList.populate(jo);
            if (i % 2 == 0) {
                itemList.setBackgroundColor(Color.parseColor("#A020F0"));
            }
            items.addView(itemList);

            itemList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        nameDesc.setText(jo.getString("Item_Name"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        priceDesc.setText("R" + jo.getString("Item_Price"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        descDesc.setText(jo.getString("Item_Desc"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        uploader.setText(jo.getString("Item_Posted_By"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    try {
                        if (jo.optString("Rating", "null").equals("null")) {
                            rating.setText("Not rated");
                        } else {
                            rating.setText(jo.getString("Rating"));
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    requireActivity().setContentView(nextLayout);
                }
            });
        }
    }

    private void processItemJSON(String json) {
        try {
            JSONArray all = new JSONArray(json);
            for (int i = 0; i < all.length(); i++) {
                JSONObject item = all.getJSONObject(i);
                String price = item.getString("Item_Price");
                String name = item.getString("Item_Name");
                String description = item.getString("Item_Desc");
                String date = item.getString("Item_Date_Posted");
                String productId = item.getString("Item_ID");

                String itemDetails = "Item: " + name + "\n"
                        + "Price: R" + price + "\n";


                TextView textView = new TextView(requireContext());
                textView.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setText(itemDetails);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            nameDesc.setText(item.getString("Item_Name"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            uploader.setText(item.getString("Item_Posted_By"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            if (item.getString("Rating").equals("null")) {
                                rating.setText("Not rated");

                            } else{
                                rating.setText(item.getString("Rating"));
                        }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            priceDesc.setText("R" + item.getString("Item_Price"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            descDesc.setText(item.getString("Item_Desc"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        // Set content view to the next layout
                        requireActivity().setContentView(nextLayout);
                    }
                });
                if (i % 2 == 0) {
                    textView.setBackgroundColor(Color.LTGRAY);
                }

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
                layoutParams.bottomMargin = 16;
                textView.setLayoutParams(layoutParams);

                mainView.addView(textView);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void showRatingDialog(String itemID) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Thank you for purchasing");
        final EditText ratingEditText = new EditText(getContext());
        ratingEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(ratingEditText);
        builder.setMessage("Please rate the item from one to five")
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String ratingValue = ratingEditText.getText().toString().trim();
                        if (!ratingValue.isEmpty()) {
                            updateItemRating(itemID, ratingValue);
                        } else {
                            // Handle empty rating value error
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getContext(), "Please rate the item. Purchase failed! ", Toast.LENGTH_SHORT).show();
                    }
                });

        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateItemRating(String itemID, String rating) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("iItem_ID", itemID)
                .add("Rating", rating)
                .build();

        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2343495/update_rating.php")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Handle successful update
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Rating submitted successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Handle update failure
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Failed to submit rating", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Network error. Failed to submit rating", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}