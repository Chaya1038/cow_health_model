package com.aadhya.herdhealth;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;

public class UserSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);

        // Initialize buttons
        Button farmerButton = findViewById(R.id.btn_farmer);
        Button vetButton = findViewById(R.id.btn_vet);

        // Farmer button click - Navigate to CowListActivity
        farmerButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserSelectionActivity.this, CowListActivity.class);
            startActivity(intent);
        });

        // Veterinary Doctor button click - Navigate to VetDashboardActivity
        vetButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserSelectionActivity.this, VetDashboardActivity.class);
            startActivity(intent);
        });
    }
}
