package aueb.nasia_kouts.gr.kitchenapollo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Locale;

public class WholeKitchen extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private TextToSpeech textToSpeechClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whole_kitchen);

        setUpSharedPreferences();

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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        setUpSharedPreferences();
    }

    private  void setUpSharedPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences sharedPref = getSharedPreferences("userSettings", MODE_PRIVATE);
        boolean openedFirstTime = sharedPref.getBoolean("openedFirstTime", false);

        if(openedFirstTime){
            boolean speechEnabledOld = sharedPref.getBoolean("speechAssist", true);
            boolean speechEnabled = sharedPreferences.getBoolean(getString(R.string.pref_speech_enabled_key), true);

            SharedPreferences.Editor prefEditor = sharedPref.edit();
            prefEditor.putBoolean("speechAssist", speechEnabled);
            prefEditor.apply();

            if(speechEnabledOld != speechEnabled){
                if(speechEnabled){
                    Toast.makeText(getApplicationContext(),"Speech assist is now enabled!", Toast.LENGTH_LONG).show();
                    //TODO ?
                }
                else{
                    Toast.makeText(getApplicationContext(), "Speech assist is now disabled!", Toast.LENGTH_LONG).show();
                }
            }
        }
        sharedPreferences.getBoolean(getString(R.string.pref_auto_close_stoves_key), false);
        sharedPreferences.getBoolean(getString(R.string.pref_auto_close_oven_key), false);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
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
            Intent openSettingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(openSettingsIntent);
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
