package com.aadhya.herdhealth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private String cowId = "co-01"; // example cow ID
    private String predictionLabel = ""; // will be fetched from backend

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Step 1: Initialize TTS
        textToSpeech = new TextToSpeech(MainActivity.this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                Log.d("TTS", "TextToSpeech initialized");

                // English default
                textToSpeech.setLanguage(Locale.ENGLISH);

                // Step 2: Fetch prediction from backend
                fetchCowPrediction();
            }
        });
    }

    private void fetchCowPrediction() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.176.205:8000/cow/" + cowId) // 🔁 Replace with actual backend URL
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();

                    try {
                        JSONObject obj = new JSONObject(jsonResponse);
                        JSONArray cowsArray = obj.getJSONArray("cows");
                        JSONObject cowObject = cowsArray.getJSONObject(0);

                        if (cowObject.has("predictionLabel")) {
                            predictionLabel = cowObject.getString("predictionLabel");

                            runOnUiThread(() -> {
                                if (!predictionLabel.equalsIgnoreCase("normal")) {
                                    speakOut(predictionLabel);
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() ->
                                Toast.makeText(MainActivity.this, "Parsing error", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(MainActivity.this, "Server error", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void speakOut(String prediction) {
        String englishMessage = "Cow " + cowId + " is showing " + prediction.replace("_", " ");
        textToSpeech.setLanguage(Locale.ENGLISH);
        textToSpeech.speak(englishMessage, TextToSpeech.QUEUE_FLUSH, null, null);

        // Optional Kannada TTS (if supported by device)
        /*
        Locale kannadaLocale = new Locale("kn", "IN");
        textToSpeech.setLanguage(kannadaLocale);
        String kannadaMessage = "ಆಕಳು " + cowId + " ಅನಾರೋಗ್ಯದಲ್ಲಿದೆ";
        textToSpeech.speak(kannadaMessage, TextToSpeech.QUEUE_ADD, null, null);
        */
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
