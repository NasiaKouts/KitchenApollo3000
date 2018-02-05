package aueb.nasia_kouts.gr.kitchenapollo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class WholeKitchen extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private TextToSpeech textToSpeechClient;
    // xreiazontai
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean performingSpeechSetup;
    private TextToSpeech toSpeech;
    private String result;
    // Show me commands na einai panta to teleutaio
    private String[] commands = {"start stove", "start oven", "Show me Commands"};
    // responses to commands
    private String[] responses = {"Starting the stove", "Starting the oven", "I'm sorry i can't do that"};




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

        // This is for getting the input
        requestRecordAudioPermission();
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(WholeKitchen.this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        performingSpeechSetup = true;
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.UK);
        WholeKitchen.SpeechRecognitionListener listener = new WholeKitchen.SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);

        // This is for reading the input
        toSpeech= new TextToSpeech(WholeKitchen.this, new TextToSpeech.OnInitListener() {
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

        // start listening
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

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
        mSpeechRecognizer.destroy();
        toSpeech.shutdown();
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

    //----------------------------------------------------//
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSpeechRecognizer.destroy();
        toSpeech.shutdown();
    }

    private void requestRecordAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String requiredPermission = Manifest.permission.RECORD_AUDIO;

            // If the user previously denied this permission then show a message explaining why
            // this permission is needed
            if (checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{requiredPermission}, 101);
            }
        }
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
            if(error == 8) {
                mSpeechRecognizer.cancel();
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            }else{
                mSpeechRecognizer.cancel();
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            }
            Log.d(TAG, "error = " + error);

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
                SystemClock.sleep(4500);
            }else {
                SystemClock.sleep(3000);
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
        HashMap<String, String> myHashAlarm = new HashMap<String, String>();
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
    public void runCommands(int index){
        switch (index){
            case 0:
                Intent openStoveIntent = new Intent(this, StoveActivity.class);
                startActivity(openStoveIntent);
                break;
            case 1:
                Intent openOvenIntent = new Intent(this, OvenActivity.class);
                startActivity(openOvenIntent);
                break;
            case 2:

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

}
