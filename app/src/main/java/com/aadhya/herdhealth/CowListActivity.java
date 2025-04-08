package com.aadhya.herdhealth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class CowListActivity extends AppCompatActivity {

    private ListView cowListView;
    private EditText searchBar;
    private CowListAdapter adapter;
    private List<Cow> cowList, filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_list);

        cowListView = findViewById(R.id.cowListView);
        searchBar = findViewById(R.id.searchBar);

        // Dummy Data for Cows
        cowList = new ArrayList<>();
        cowList.add(new Cow("COW001", "Healthy", "Normal"));
        cowList.add(new Cow("COW002", "Warning", "Lameness detected"));
        cowList.add(new Cow("COW003", "Urgent", "Severe illness detected"));

        filteredList = new ArrayList<>(cowList);

        adapter = new CowListAdapter(this, filteredList);
        cowListView.setAdapter(adapter);

        // Search Bar Functionality
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCows(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Click on a cow to go to details page
        cowListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CowListActivity.this, CowDetailsActivity.class);
                intent.putExtra("cow_id", filteredList.get(position).getId());
                startActivity(intent);
            }
        });
    }

    private void filterCows(String query) {
        filteredList.clear();
        for (Cow cow : cowList) {
            if (cow.getId().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(cow);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
