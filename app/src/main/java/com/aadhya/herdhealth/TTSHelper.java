package com.aadhya.herdhealth;



import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TTSHelper {
    private static TextToSpeech tts;

    public static void speak(Context context, String message) {
        if (tts == null) {
            tts = new TextToSpeech(context.getApplicationContext(), status -> {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.ENGLISH); // You can change to Kannada if needed
                    tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "ttsMessage");
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            });
        } else {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "ttsMessage");
        }
    }

    public static void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }
}
