package com.aadhya.herdhealth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CowListActivity extends AppCompatActivity {
    private ListView cowListView;
    // Use consistent lowercase IDs that match your API's $dtId format
    private String[] cowIds = {"co-01", "co-02", "co-03"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_list);

        cowListView = findViewById(R.id.cowListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                cowIds
        );
        cowListView.setAdapter(adapter);

        cowListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, CowDetailsActivity.class);
            // Pass the exact ID format your API expects
            intent.putExtra("cow_id", cowIds[position]);
            startActivity(intent);
        });
    }
}