package aueb.nasia_kouts.gr.kitchenapollo;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class TutorialActivity extends AppCompatActivity {

    CarouselView carouselView;

    int[] sampleImages = {R.drawable.oven, R.drawable.stove};

    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean performingSpeechSetup;
    private TextToSpeech toSpeech;
    private TextToSpeech textToSpeechClient;
    private String result;
    // Show me commands na einai panta to teleutaio
    private String[] commands = {
            "yes",
            "no",
            "Show me Commands"};
    // responses to commands
    private String[] responses = {
            "Enabling speech assistance",
            "disabling speech assistance",
            "I'm sorry i can't do that"};

    public void initializeSpeechClient(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean speechEnabled = sharedPreferences.getBoolean(getString(R.string.pref_speech_enabled_key), true);
        if(!speechEnabled) return;

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        performingSpeechSetup = true;
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.UK);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 300000);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 300000);
        mSpeechRecognizer.setRecognitionListener(new TutorialActivity.SpeechRecognitionListener());

        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

        // This is for reading the input
        toSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    toSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onDone(String utteranceId) {
                            Log.d("MainActivity", "TTS finished");
                        }

                        @Override
                        public void onError(String utteranceId) {
                        }

                        @Override
                        public void onStart(String utteranceId) {
                            Log.d("MainActivity", "On start");

                        }
                    });
                } else {
                    Log.e("MainActivity", "Initilization Failed!");
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        carouselView = findViewById(R.id.tutorial_carousel);
        carouselView.setPageCount(sampleImages.length);

        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(sampleImages[position]);
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        });

        textToSpeechClient = new TextToSpeech(TutorialActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    textToSpeechClient.setLanguage(Locale.US);

                    textToSpeechClient.speak("Do you want any speak assistance?", TextToSpeech.QUEUE_FLUSH, null);
                    SystemClock.sleep(3000);
                    initializeSpeechClient();

                } else {
                    Toast.makeText(getApplicationContext(), "Text to speech is not enabled", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            if(mSpeechRecognizer != null){
                mSpeechRecognizer.destroy();
            }
            if(toSpeech != null){
                toSpeech.shutdown();
            }
            if(textToSpeechClient != null){
                textToSpeechClient.shutdown();
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.finish_button:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tutorial_menu, menu);
        return true;
    }

    protected class SpeechRecognitionListener implements RecognitionListener
    {
        @Override
        public void onBeginningOfSpeech()
        {
            Log.d(TAG, "onBeginingOfSpeech");
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {

        }

        @Override
        public void onEndOfSpeech()
        {
            Log.d(TAG, "onEndOfSpeech");
        }

        @Override
        public void onError(int error)
        {
            if (performingSpeechSetup && error == SpeechRecognizer.ERROR_NO_MATCH) return;
            if(error == 8){
                mSpeechRecognizer.stopListening();
                mSpeechRecognizer.cancel();
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            }else{
                mSpeechRecognizer.stopListening();
                mSpeechRecognizer.cancel();
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            }

            String message = "";

            if(error == SpeechRecognizer.ERROR_AUDIO)                           message = "audio";
            else if(error == SpeechRecognizer.ERROR_CLIENT)                     message = "client";
            else if(error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS)   message = "insufficient permissions";
            else if(error == SpeechRecognizer.ERROR_NETWORK)                    message = "network";
            else if(error == SpeechRecognizer.ERROR_NETWORK_TIMEOUT)            message = "network timeout";
            else if(error == SpeechRecognizer.ERROR_NO_MATCH)                   message = "no match found";
            else if(error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY)            message = "recognizer busy";
            else if(error == SpeechRecognizer.ERROR_SERVER)                     message = "server";
            else if(error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT)             message = "speech timeout";

            Log.d(TAG,"error " + message);
        }

        @Override
        public void onEvent(int eventType, Bundle params)
        {

        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {

        }

        @Override
        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "onReadyForSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onResults(Bundle results)
        {
            Log.d(TAG, "onResults"); //$NON-NLS-1$
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            // matches are the return values of speech recognition engine
            // Use these values for whatever you wish to do
            result = matches.get(0);
            mSpeechRecognizer.cancel();

            // TODO: 5/2/2018 Here is where you pass the result of the speech
            if(checkCommands(result)==commands.length-1){
                SystemClock.sleep(3500);
            }else {
                SystemClock.sleep(2000);
            }
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
        }

        @Override
        public void onRmsChanged(float rmsdB)
        {
        }
    }

    public int checkCommands(String result){
        System.out.println(result);
        int index = isValidCommand(result);
        System.out.println(index);
        System.out.println(commands.length-1);
        HashMap<String, String> myHashAlarm = new HashMap<>();
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "SOME MESSAGE");
        // Here it speaks
        if ( index==commands.length-1){
            toSpeech.setSpeechRate(0.8f);
            toSpeech.speak(getCommands(), TextToSpeech.QUEUE_FLUSH, myHashAlarm);
            toSpeech.setSpeechRate(1.0f);

        }else if(index != -1){
            toSpeech.speak(responses[index], TextToSpeech.QUEUE_FLUSH, myHashAlarm);

        }
        else{
            index = responses.length -1;
            toSpeech.speak(responses[index], TextToSpeech.QUEUE_FLUSH, myHashAlarm);
        }
        runCommands(index);
        return index;
    }

    public String getCommands(){
        String input = "The available commands are  ";

        for (int i =0; i<commands.length-1; i++){
            input += commands[i];
            input +=" ";
        }
        return input;
    }

    // Mono auto allazeis
    public void runCommands(int index) {
        switch (index) {
            case 0: {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor prefEditor = sharedPreferences.edit();
                prefEditor.putBoolean(getString(R.string.pref_speech_enabled_key), true);
                prefEditor.apply();
                break;
            }
            case 1: {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor prefEditor = sharedPreferences.edit();
                prefEditor.putBoolean(getString(R.string.pref_speech_enabled_key), false);
                prefEditor.apply();
                if (mSpeechRecognizer != null) {
                    mSpeechRecognizer.destroy();
                }
                if (toSpeech != null) {
                    toSpeech.shutdown();
                }
                break;
            }

        }
    }

    public int isValidCommand(String result){
        int index = -1;
        int i = 0;
        for(String command: commands){
            if(command.equalsIgnoreCase(result)){
                index = i;
            }
            i++;
        }
        return index;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSpeechRecognizer != null){
            mSpeechRecognizer.destroy();
        }
        if(toSpeech != null){
            toSpeech.shutdown();
        }
        if(textToSpeechClient != null){
            textToSpeechClient.shutdown();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mSpeechRecognizer != null){
            mSpeechRecognizer.destroy();
        }
        if(toSpeech != null){
            toSpeech.shutdown();
        }
        if(textToSpeechClient != null){
            textToSpeechClient.shutdown();
        }
    }
}
