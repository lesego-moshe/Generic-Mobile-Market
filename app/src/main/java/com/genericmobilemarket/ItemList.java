package com.genericmobilemarket;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class ItemList extends LinearLayout {

    TextView name;
    TextView price;
    TextView uploader;
    TextView rating;
    private Context context;
    private ItemClickListener itemClickListener;
    Button buy = findViewById(R.id.buyItemButton);

    public ItemList(Context context) {
        super(context);
        this.context = context;
        //this.buy = buy;
        setOrientation(LinearLayout.HORIZONTAL);
        name = new TextView(context);
        price = new TextView(context);
        rating = new TextView(context);
        uploader = new TextView(context);


        addView(name);
        LinearLayout rightLayout = new LinearLayout(context);
        rightLayout.setOrientation(LinearLayout.VERTICAL);
        rightLayout.addView(price);
        addView(rightLayout);



        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick();
                }
            }
        });
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void populate(JSONObject jo) throws JSONException {
        name.setText(jo.getString("Item_Name") + " ");
        price.setText("R" + jo.getString("Item_Price"));

    }

    public interface ItemClickListener {
        void onItemClick();
    }

    public void updateItemRating(JSONObject jsonObject){


    }

}