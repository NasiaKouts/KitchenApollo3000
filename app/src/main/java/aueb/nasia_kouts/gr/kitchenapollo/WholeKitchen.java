package aueb.nasia_kouts.gr.kitchenapollo;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class WholeKitchen extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean performingSpeechSetup;
    private TextToSpeech toSpeech;
    private String result;
    // Show me commands na einai panta to teleutaio
    private String[] commands = {
            "yes",
            "no",
            "start stove",
            "start oven",
            "Show me Commands"};
    // responses to commands
    private String[] responses = {
            "Enabling speech assistance",
            "disabling speech assistance",
            "Starting the stove",
            "Starting the oven",
            "I'm sorry i can't do that"};
    private TextToSpeech textToSpeechClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whole_kitchen);

        setUpSharedPreferences();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firsttime = sharedPreferences.getBoolean("firsttime",true);
        if (firsttime){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firsttime", false);
            Intent openTutorialIntent = new Intent(this, TutorialActivity.class);
            startActivity(openTutorialIntent);
        }

    }



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
        mSpeechRecognizer.setRecognitionListener(new WholeKitchen.SpeechRecognitionListener());

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==88){
            if(grantResults.length>0){
                textToSpeechClient = new TextToSpeech(WholeKitchen.this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        if (i == TextToSpeech.SUCCESS) {
                            textToSpeechClient.setLanguage(Locale.US);

                            textToSpeechClient.speak("Do you want any speak assistance?", TextToSpeech.QUEUE_FLUSH, null);
                            SystemClock.sleep(3000);
                        } else {
                            Toast.makeText(getApplicationContext(), "Text to speech is not enabled", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                initializeSpeechClient();
            }else {

            }
        }
        //initializeSpeechClient();
        Intent openTutorialIntent = new Intent(this, TutorialActivity.class);
        startActivity(openTutorialIntent);
    }

    private void requestRecordAudioPermission() {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO};
                requestPermissions(permissions,88);
            }
        }else{
            initializeSpeechClient();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        setUpSharedPreferences();
    }

    private void setUpSharedPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean speechEnabled = sharedPreferences.getBoolean(getString(R.string.pref_speech_enabled_key), true);

        if(speechEnabled){
            Toast.makeText(getApplicationContext(),"Speech assist is now enabled!", Toast.LENGTH_LONG).show();
            //initializeSpeechClient();
        }
        else{
            Toast.makeText(getApplicationContext(), "Speech assist is now disabled!", Toast.LENGTH_LONG).show();
            if(mSpeechRecognizer != null){
                mSpeechRecognizer.destroy();
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
        if(mSpeechRecognizer != null){
            mSpeechRecognizer.destroy();
        }
        if (toSpeech != null){
            toSpeech.shutdown();
        }    }



    @Override
    protected void onPause() {
        super.onPause();
        if(mSpeechRecognizer != null){
            mSpeechRecognizer.destroy();
        }
        if (toSpeech != null){
            toSpeech.shutdown();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firsttime = sharedPreferences.getBoolean("firsttime",true);
        if (!firsttime){
            requestRecordAudioPermission();
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
            Intent openSettingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(openSettingsIntent);
        }else if(item.getItemId() == R.id.tutorial_button){
            Intent openTutorialIntent = new Intent(this, TutorialActivity.class);
            startActivity(openTutorialIntent);
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
    public void runCommands(int index){
        switch (index){
            case 0:{
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor prefEditor = sharedPreferences.edit();
                prefEditor.putBoolean(getString(R.string.pref_speech_enabled_key), true);
                prefEditor.apply();
                break;
            }
            case 1:{
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor prefEditor = sharedPreferences.edit();
                prefEditor.putBoolean(getString(R.string.pref_speech_enabled_key), false);
                prefEditor.apply();
                if(mSpeechRecognizer != null){
                    mSpeechRecognizer.destroy();
                }
                if (toSpeech != null){
                    toSpeech.shutdown();
                }
                break;
            }
            case 2:
                if(mSpeechRecognizer != null){
                    mSpeechRecognizer.destroy();
                }
                toSpeech.shutdown();
                Intent openStoveIntent = new Intent(this, StoveActivity.class);
                if(mSpeechRecognizer != null){
                    mSpeechRecognizer.destroy();
                }
                if (toSpeech != null){
                    toSpeech.shutdown();
                }
                startActivity(openStoveIntent);
                break;
            case 3:
                if(mSpeechRecognizer != null){
                    mSpeechRecognizer.destroy();
                }
                toSpeech.shutdown();
                Intent openOvenIntent = new Intent(this, OvenActivity.class);
                if(mSpeechRecognizer != null){
                    mSpeechRecognizer.destroy();
                }
                if (toSpeech != null){
                    toSpeech.shutdown();
                }
                startActivity(openOvenIntent);
                break;
            case 4:{
                ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                Log.d(TAG, cn.getShortClassName());
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
}
