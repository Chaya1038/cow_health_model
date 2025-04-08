package com.aadhya.herdhealth;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class VetCowDetails extends AppCompatActivity {
    private TextView tvCowId, tvCowName, tvCowStatus, tvLameness, tvEstrus, tvDigitalTwin, tvFarmerName, tvFarmerContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_cow_details);

        // Initialize TextViews
        tvCowId = findViewById(R.id.tvCowId);
        tvCowName = findViewById(R.id.tvCowName);
        tvCowStatus = findViewById(R.id.tvCowStatus);
        tvLameness = findViewById(R.id.tvLameness);
        tvEstrus = findViewById(R.id.tvEstrus);
        tvDigitalTwin = findViewById(R.id.tvDigitalTwin);
        tvFarmerName = findViewById(R.id.tvFarmerName);
        tvFarmerContact = findViewById(R.id.tvFarmerContact);

        // Get data from intent
        if (getIntent() != null) {
            String cowId = getIntent().getStringExtra("cowId");
            String cowName = getIntent().getStringExtra("cowName");
            String cowStatus = getIntent().getStringExtra("cowStatus");
            String lameness = getIntent().getStringExtra("lameness");
            String estrus = getIntent().getStringExtra("estrus");
            String digitalTwin = getIntent().getStringExtra("digitalTwin");
            String farmerName = getIntent().getStringExtra("farmerName");
            String farmerContact = getIntent().getStringExtra("farmerContact");

            // Set data to TextViews
            tvCowId.setText("Cow ID: " + cowId);
            tvCowName.setText("Cow Name: " + cowName);
            tvCowStatus.setText("Status: " + cowStatus);
            tvLameness.setText("Lameness: " + lameness);
            tvEstrus.setText("Estrus: " + estrus);
            tvDigitalTwin.setText("Digital Twin Insights: " + digitalTwin);
            tvFarmerName.setText("Farmer: " + farmerName);
            tvFarmerContact.setText("Contact: " + farmerContact);
        }
    }
}
