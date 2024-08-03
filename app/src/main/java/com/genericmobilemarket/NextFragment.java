package com.genericmobilemarket;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NextFragment extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String[] cats = {"Furniture", "Tech", "Entertainment", "Misc"};
    String cat;

    Boolean pickCat = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_fragment);

        Spinner spinner = findViewById(R.id.spinner);
        EditText editText1 = findViewById(R.id.editTextTextPersonName3);
        EditText editText2 = findViewById(R.id.editTextNumberDecimal);
        Button button = findViewById(R.id.button2);

        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cats);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cat = spinner.getSelectedItem().toString();
                String name = editText1.getText().toString().trim();
                String priceString = editText2.getText().toString().trim();
                if (priceString.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid price", Toast.LENGTH_SHORT).show();
                    return;
                }
                Float price = Float.parseFloat(priceString);
                LocalDate currentDate = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    currentDate = LocalDate.now();
                }

                DateTimeFormatter formatter = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                }
                String formattedDate = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    formattedDate = currentDate.format(formatter);
                }

                String username = UserData.username;

                System.out.println(cat + " " + price + " " + name + " " + formattedDate + " " + username);
                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new FormBody.Builder()
                        .add("Item_status", "Pending")
                        .add("Item_Cat", cat)
                        .add("Item_Price", price.toString())
                        .add("Item_Name", name)
                        .add("Item_Desc", getIntent().getStringExtra("desc"))
                        .add("Item_Date_Posted", formattedDate)
                        .add("Item_Posted_By", username)
                        .build();

                Request request = new Request.Builder()
                        .url("https://lamp.ms.wits.ac.za/home/s2343495/items.php")
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Please fill in all details", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            if (!response.isSuccessful()) {
                                throw new IOException("Unexpected code" + response);
                            }

                            final String result = response.body().string();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject jsonObject = new JSONObject(result);
                                        boolean success = jsonObject.getBoolean("Successful");
                                        String message = jsonObject.getString("message");

                                        if (success) {
                                            Toast.makeText(NextFragment.this, "Item uploaded", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(NextFragment.this, "Please fill in all the details", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(NextFragment.this, "Item uploaded", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } finally {
                            if (response.body() != null) {
                                response.body().close();
                            }
                            if (response != null) {
                                response.close();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(getApplicationContext(), cats[i], Toast.LENGTH_LONG).show();
        cat = cats[i];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        pickCat = false;
    }
}