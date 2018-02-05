package aueb.nasia_kouts.gr.kitchenapollo;


import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity{

    TextView responseText;

    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean mIslistening,performingSpeechSetup;

    private TextToSpeech toSpeech;

    private String result;
    private String[] commands = {"start stove", "start oven"};
    // responses to commands
    private String[] responses = {"Starting the stove", "Starting the oven", "I'm sorry i can't do that"};
    //legel commands
    private static final String[] VALID_COMMANDS = {
            "what time is it",
            "what day is it",
            "who are you",
            "exit"
    };
    private static final int VALID_COMMANDS_SIZE = VALID_COMMANDS.length;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        responseText = findViewById(R.id.TestingText);


        // This is for getting the input
        requestRecordAudioPermission();
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        performingSpeechSetup = true;
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.UK);
        SpeechRecognitionListener listener = new SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

        // this is for reading the input

    }




    public void StartListening(View view){
    }

    public void StopListening(View view){
        mSpeechRecognizer.destroy();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSpeechRecognizer.destroy();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSpeechRecognizer.destroy();
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
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

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
            responseText.setText(matches.get(0));
            result = matches.get(0);
            // TODO: 5/2/2018 Here is where you pass the result of the speech
            checkCommands(result);
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

        }

        @Override
        public void onRmsChanged(float rmsdB)
        {
        }
    }

    public void checkCommands(String result){
        int index = isValidCommand(result);
        // Here it speaks
        if ( index != -1){
            toSpeech.speak(responses[index], TextToSpeech.QUEUE_FLUSH, null);
        }else{
            index = responses.length -1;
            toSpeech.speak(responses[index], TextToSpeech.QUEUE_FLUSH, null);
        }
        runCommands(index);
    }

    public void runCommands(int index){
        switch (index){
            case 0:

                break;
            default:
                break;
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
