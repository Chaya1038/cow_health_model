package com.aadhya.herdhealth;

import static com.aadhya.herdhealth.R.id.dataTextView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ARActivity extends AppCompatActivity {
    private TextView dataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aractivity);

        String cowId = getIntent().getStringExtra("COW_ID");
        dataTextView = findViewById(R.id.dataTextView);

        // 1. First show data
        fetchCowData(cowId);

        // 2. Then launch AR button
        findViewById(R.id.arButton).setOnClickListener(v -> launchAR(cowId));
    }

    private void fetchCowData(String cowId) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://10.210.5.250:8000/cow/" + cowId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                runOnUiThread(() -> displayData(jsonData));
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        dataTextView.setText("Failed to load data for " + cowId));
            }
        });
    }

    private void displayData(String json) {
        try {
            JSONObject data = new JSONObject(json);
            JSONObject cow = data.getJSONArray("cows").getJSONObject(0);

            String displayText = "ID: " + cow.getString("$dtId") + "\n" +
                    "Breed: " + cow.getString("breed") + "\n" +
                    "Steps: " + cow.getInt("step_count") + "\n" +
                    "Status: " + cow.getString("predictionLabel");

            dataTextView.setText(displayText);
        } catch (JSONException e) {
            dataTextView.setText("Error parsing data");
        }
    }

    private void launchAR(String cowId) {
        String modelUrl = "https://raw.githubusercontent.com/yourmodels/" + cowId + ".gltf";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://arvr.google.com/scene-viewer/1.0?file=" + modelUrl));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "AR Viewer not available", Toast.LENGTH_SHORT).show();
        }
    }
}