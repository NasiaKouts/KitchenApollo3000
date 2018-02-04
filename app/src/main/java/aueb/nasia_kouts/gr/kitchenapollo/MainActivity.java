package aueb.nasia_kouts.gr.kitchenapollo;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getName();

    //wakelock to keep screen on
    protected PowerManager.WakeLock mWakeLock;

    //speach recognizer for callbacks
    private SpeechRecognizer mSpeechRecognizer;

    //handler to post changes to progress bar
    private Handler mHandler = new Handler();

    //ui textview
    TextView responseText;

    //intent for speech recogniztion
    Intent mSpeechIntent;

    //this bool will record that it's time to kill P.A.L.
    boolean killCommanded = false;

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
        responseText = (TextView) findViewById(R.id.TestingText);
    }

    @Override
    protected void onStart() {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
        mSpeechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Given an hint to the recognizer about what the user is going to say
        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.UK);
        // Specify how many results you want to receive. The results will be sorted
        // where the first result is the one with higher confidence.
//        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 20);
//
//
//        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        //aqcuire the wakelock to keep the screen on until user exits/closes app
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
        this.mWakeLock.acquire();
        startActivityForResult(mSpeechIntent,10);
        super.onStart();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
    private String getResponse(int command){
        Calendar c = Calendar.getInstance();

        String retString =  "I'm sorry, Dave. I'm afraid I can't do that.";
        SimpleDateFormat dfDate_day;
        switch (command) {
            case 0:
                dfDate_day= new SimpleDateFormat("HH:mm:ss");
                retString = "The time is " + dfDate_day.format(c.getTime());
                break;
            case 1:
                dfDate_day = new SimpleDateFormat("dd/MM/yyyy");
                retString= " Today is " + dfDate_day.format(c.getTime());
                break;
            case 2:
                retString = "My name is R.A.L. - Responsive Android Language program";
                break;

            case 3:
                killCommanded = true;
                break;

            default:
                break;
        }
        return retString;
    }

    @Override
    protected void onPause() {
        //kill the voice recognizer
        if(mSpeechRecognizer != null){
            mSpeechRecognizer.destroy();
            mSpeechRecognizer = null;

        }
        this.mWakeLock.release();
        super.onPause();
    }

    private void processCommand(ArrayList<String> matchStrings){
        String response = "I'm sorry, Dave. I'm afraid I can't do that.";
        int maxStrings = matchStrings.size();
        boolean resultFound = false;
        for(int i =0; i < VALID_COMMANDS_SIZE && !resultFound;i++){
            for(int j=0; j < maxStrings && !resultFound; j++){
                response = getResponse(i);
            }
        }

        final String finalResponse = response;
        mHandler.post(new Runnable() {
            public void run() {
                responseText.setText(finalResponse);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 10:
                if(resultCode == RESULT_OK && data!= null){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                }
                break;
        }
    }
}
