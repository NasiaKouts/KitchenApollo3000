package aueb.nasia_kouts.gr.kitchenapollo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;

public class WholeKitchen extends AppCompatActivity {

    private TextToSpeech textToSpeechClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whole_kitchen);

        SharedPreferences sharedPref = getSharedPreferences("userSettings", MODE_PRIVATE);

        boolean openedFirstTime = sharedPref.getBoolean("openedFirstTime", false);
        if(!openedFirstTime){
            SharedPreferences.Editor prefEditor = sharedPref.edit();
            prefEditor.putBoolean("openedFirstTime", true);
            prefEditor.apply();

            textToSpeechClient = new TextToSpeech(WholeKitchen.this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    if(i == TextToSpeech.SUCCESS){
                        textToSpeechClient.setLanguage(Locale.US);

                        textToSpeechClient.speak("Do you want any speak assistance?", TextToSpeech.QUEUE_FLUSH, null);
                        //TODO: open speech to text and hold the answer and set it to the following boolean
                        boolean userWantsSpeechAssistance = false;

                        SharedPreferences sharedPref = getSharedPreferences("userSettings", MODE_PRIVATE);
                        SharedPreferences.Editor prefEditor = sharedPref.edit();
                        prefEditor.putBoolean("speechAssist", userWantsSpeechAssistance);
                        prefEditor.apply();
                    }else{
                        Toast.makeText(getApplicationContext(), "Text to speech is not enabled", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.help_button){
            if(textToSpeechClient != null){
                textToSpeechClient.speak(
                        getResources().getString(R.string.general_info),
                        TextToSpeech.QUEUE_FLUSH,
                        null);
            }
        }else if(item.getItemId() == R.id.settings_button){
            //goto settings acticity
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    public void openStoveActivity(View view) {
        Intent openStoveIntent = new Intent(this, StoveActivity.class);
        startActivity(openStoveIntent);
    }

    public void openOvenActivity(View view) {
        Intent openOvenIntent = new Intent(this, OvenActivity.class);
        startActivity(openOvenIntent);
    }
}
