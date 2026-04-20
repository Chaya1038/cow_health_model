package com.aadhya.herdhealth;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CowDetailsActivity extends AppCompatActivity {

    private TextView dataTextView;
    private Button arButton;
    private ProgressBar progressBar;
    private TextToSpeech textToSpeech;
    private final OkHttpClient httpClient = new OkHttpClient();
    private String lastPredictionLabel = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cow_details);

        dataTextView = findViewById(R.id.dataTextView);
        arButton = findViewById(R.id.arButton);
        progressBar = findViewById(R.id.progressBar);

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) { }

                    @Override
                    public void onDone(String utteranceId) {
                        runOnUiThread(() -> new Handler().postDelayed(() ->
                                speakInKannada(lastPredictionLabel), 1000));
                    }

                    @Override
                    public void onError(String utteranceId) { }
                });
            }
        });

        String cowId = getIntent().getStringExtra("cow_id");
        if (cowId == null) cowId = "co-01";
        fetchCowDetails(cowId);
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    private void fetchCowDetails(String cowId) {
        progressBar.setVisibility(View.VISIBLE);

        Request request = new Request.Builder()
                .url("http://192.168.176.205:8000/cow/" + cowId)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CowDetailsActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    runOnUiThread(() -> displayCowData(jsonData));
                }
            }
        });
    }

    private void displayCowData(String json) {
        try {
            JSONObject data = new JSONObject(json);
            JSONObject cow = data.getJSONArray("cows").getJSONObject(0);

            String predictionLabel = cow.getString("predictionLabel");
            lastPredictionLabel = predictionLabel;

            String details = "Cow ID: " + cow.getString("$dtId") + "\n\n" +
                    "Breed: " + cow.getString("breed") + "\n\n" +
                    "Health Status: " + predictionLabel + "\n\n" +
                    "Estrus Sign: " + cow.getString("estrus_sign") + "\n\n" +
                    "Rumination: " + cow.getInt("rumination_time") + " mins\n\n" +
                    "Step Count: " + cow.getInt("step_count") + "\n\n" +
                    "Motion: [" + cow.getDouble("ax") + ", " + cow.getDouble("ay") + ", " + cow.getDouble("az") + "]\n\n" +
                    "Gyroscope: [" + cow.getDouble("gx") + ", " + cow.getDouble("gy") + ", " + cow.getDouble("gz") + "]";

            dataTextView.setText(details);

            setupARButton(predictionLabel);
            speakInEnglish(predictionLabel);
        } catch (JSONException e) {
            dataTextView.setText("Error parsing JSON.");
        }
    }

    private void setupARButton(String label) {
        arButton.setOnClickListener(v -> {
            String modelUrl = getModelUrl(label);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://arvr.google.com/scene-viewer/1.0?file=" + modelUrl));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    private String getModelUrl(String label) {
        label = label.toLowerCase();
        if (label.contains("unhealthy") && label.contains("estrus")) {
            return "https://raw.githubusercontent.com/Chaya1038/cow_health_model/refs/heads/main/Cow_unhealthy_inestrus.gltf";
        } else if (label.contains("unhealthy")) {
            return "https://raw.githubusercontent.com/Chaya1038/cow_health_model/refs/heads/main/Cow_unhealthy_not_inestrus.gltf";
        } else if (label.contains("healthy") && label.contains("estrus")) {
            return "https://raw.githubusercontent.com/Chaya1038/cow_health_model/refs/heads/main/Cow_healthy_inestrus.gltf";
        } else {
            return "https://raw.githubusercontent.com/Chaya1038/cow_health_model/refs/heads/main/Cow_healthy_not_inestrus.gltf";
        }
    }

    private void speakInEnglish(String predictionLabel) {
        String message = "The cow is " + predictionLabel.replace("_", " ");
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, "ENGLISH_TTS");
    }

    private void speakInKannada(String predictionLabel) {
        String kannadaText;

        switch (predictionLabel.toLowerCase()) {
            case "healthy_in_estrus":
                kannadaText = "ಹಸು ಎಸ್ಟ್ರಸ್‌ನಲ್ಲಿ ಆರೋಗ್ಯವಾಗಿವೆ";
                break;
            case "healthy_not_in_estrus":
                kannadaText = "ಹಸು ಎಸ್ಟ್ರಸ್‌ನಲ್ಲಿ ಇಲ್ಲದಿದ್ದರೂ ಆರೋಗ್ಯವಾಗಿವೆ";
                break;
            case "unhealthy_not_in_estrus":
                kannadaText = "ಹಸು ಎಸ್ಟ್ರಸ್‌ನಲ್ಲಿ ಇಲ್ಲದಿದ್ದು ಅಸ್ವಸ್ಥವಾಗಿದೆ";
                break;
            case "unhealthy_in_estrus":
                kannadaText = "ಹಸು ಎಸ್ಟ್ರಸ್‌ನಲ್ಲಿ ಅಸ್ವಸ್ಥವಾಗಿದೆ";
                break;
            default:
                kannadaText = "ಹಸು ಬಗ್ಗೆ ಮಾಹಿತಿ ಲಭ್ಯವಿಲ್ಲ";
        }

        callIndicTTSAPI(kannadaText);
    }

    private void callIndicTTSAPI(String kannadaText) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("input", kannadaText);
            payload.put("lang", "kn");
            payload.put("voice", "female");

            Request request = new Request.Builder()
                    .url("https://indic-tts-api-v2.onrender.com/tts")
                    .post(RequestBody.create(payload.toString(), MediaType.parse("application/json")))
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("TTS Kannada", "Failed to fetch Kannada audio", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.e("TTS Kannada", "API error: " + response.code());
                        return;
                    }

                    File audioFile = new File(getCacheDir(), "kannada_tts.mp3");
                    FileOutputStream fos = new FileOutputStream(audioFile);
                    InputStream is = response.body().byteStream();
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                    fos.close();
                    is.close();

                    runOnUiThread(() -> {
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(audioFile.getAbsolutePath());
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (IOException e) {
                            Log.e("TTS Kannada", "Playback error", e);
                        }
                    });
                }
            });

        } catch (JSONException e) {
            Log.e("TTS Kannada", "JSON Error", e);
        }
    }
}
