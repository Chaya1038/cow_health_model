package com.aadhya.herdhealth;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class VetDashboardActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VetCowAdapter adapter;
    private List<VetCowModel> cowList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_dashboard);

        recyclerView = findViewById(R.id.recyclerViewVetCows);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cowList = new ArrayList<>();
        cowList.add(new VetCowModel("001", "Cow 1", "Healthy", "Farmer John", "9876543210", "No Lameness", "No Estrus", "Normal Health"));
        cowList.add(new VetCowModel("002", "Cow 2", "Lameness Detected", "Farmer Smith", "9876543222", "Mild Lameness", "No Estrus", "Needs Attention"));
        cowList.add(new VetCowModel("003", "Cow 3", "Severe Illness Detected", "Farmer Lee", "9876543233", "Severe Lameness", "Estrus Detected", "Critical Condition"));


        adapter = new VetCowAdapter(cowList, VetDashboardActivity.this);

        recyclerView.setAdapter(adapter);
    }
}
