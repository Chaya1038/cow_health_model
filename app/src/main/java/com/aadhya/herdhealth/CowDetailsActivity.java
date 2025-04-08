package com.aadhya.herdhealth;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CowDetailsActivity extends AppCompatActivity {

    private TextView cowIdTextView, ageTextView, breedTextView;
    private TextView healthStatusTextView, lamenessStatusTextView, estrusStatusTextView, digitalTwinInsightsTextView;
    private TextView motionTextView, temperatureTextView, heartRateTextView;
    private ImageView healthIndicatorImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_details);

        // Initialize UI elements
        cowIdTextView = findViewById(R.id.cowIdTextView);
        ageTextView = findViewById(R.id.ageTextView);
        breedTextView = findViewById(R.id.breedTextView);
        healthStatusTextView = findViewById(R.id.healthStatusTextView);
        lamenessStatusTextView = findViewById(R.id.lamenessStatusTextView);
        estrusStatusTextView = findViewById(R.id.estrusStatusTextView);
        digitalTwinInsightsTextView = findViewById(R.id.digitalTwinInsightsTextView);
        motionTextView = findViewById(R.id.motionTextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        heartRateTextView = findViewById(R.id.heartRateTextView);
        healthIndicatorImage = findViewById(R.id.healthIndicatorImage);

        // Get cow details from intent
        String cowId = getIntent().getStringExtra("cowId");
        String age = getIntent().getStringExtra("age");
        String breed = getIntent().getStringExtra("breed");
        String healthStatus = getIntent().getStringExtra("healthStatus");
        String lamenessStatus = getIntent().getStringExtra("lamenessStatus");
        String estrusStatus = getIntent().getStringExtra("estrusStatus");
        String digitalTwinInsights = getIntent().getStringExtra("digitalTwinInsights");
        String motion = getIntent().getStringExtra("motion");
        String temperature = getIntent().getStringExtra("temperature");
        String heartRate = getIntent().getStringExtra("heartRate");

        // Set data to UI
        cowIdTextView.setText("Cow ID: " + cowId);
        ageTextView.setText("Age: " + age);
        breedTextView.setText("Breed: " + breed);
        healthStatusTextView.setText("Health: " + healthStatus);
        lamenessStatusTextView.setText("Lameness: " + lamenessStatus);
        estrusStatusTextView.setText("Estrus: " + estrusStatus);
        digitalTwinInsightsTextView.setText("Digital Twin Insights: " + digitalTwinInsights);
        motionTextView.setText("Motion: " + motion);
        temperatureTextView.setText("Temperature: " + temperature + "°C");
        heartRateTextView.setText("Heart Rate: " + heartRate + " bpm");

        // Set health indicator color based on status
        if ("Healthy".equals(healthStatus)) {
            healthIndicatorImage.setImageResource(R.drawable.green_dot);
        } else if ("Warning".equals(healthStatus)) {
            healthIndicatorImage.setImageResource(R.drawable.yellow_dot);
        } else {
            healthIndicatorImage.setImageResource(R.drawable.red_dot);
        }
    }
}
