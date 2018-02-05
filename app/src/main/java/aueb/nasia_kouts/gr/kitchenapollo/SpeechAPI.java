package aueb.nasia_kouts.gr.kitchenapollo;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by Kabalog on 4/2/2018.
 */

// TODO: 4/2/2018 COPY PASTE THIS WHERE YOU NEED IT
/*
TextToSpeech speech = new TextToSpeech(your_class.this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status==speech.SUCCESS){
                        result = speech.setLanguage(Locale.UK);
                    }else{
                    }
                }
            });
 */


public class SpeechAPI {
    private TextToSpeech toSpeech;
    private int result;
    private String health;
}
