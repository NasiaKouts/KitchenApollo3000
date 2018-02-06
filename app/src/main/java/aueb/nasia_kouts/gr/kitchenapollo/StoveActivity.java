package aueb.nasia_kouts.gr.kitchenapollo;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.iconics.view.IconicsImageButton;
import com.travijuu.numberpicker.library.NumberPicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class StoveActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    StoveButton stoveButtons[];
    CountDownTimer countDownTimers[];

    int alertMinutes;

    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean performingSpeechSetup;
    private TextToSpeech toSpeech;
    private String result;
    // Show me commands na einai panta to teleutaio
    private String[] commands = {
            "open left top side stove",
            "close left top side stove",
            "open right top side stove",
            "open right top side big stove",
            "close right top side stove",
            "open left bottom side stove",
            "open left bottom side big stove",
            "close left bottom side stove",
            "open right bottom side stove",
            "close right bottom side stove",
            "set left top heat level to zero",
            "set left top heat level to one",
            "set left top heat level to two",
            "set left top heat level to three",
            "set left top heat level to four",
            "set left top heat level to five",
            "set left top heat level to six",
            "set left top heat level to seven",
            "set left top heat level to eight",
            "set left top heat level to nine",
            "set right top heat level to zero",
            "set right top heat level to one",
            "set right top heat level to two",
            "set right top heat level to three",
            "set right top heat level to four",
            "set right top heat level to five",
            "set right top heat level to six",
            "set right top heat level to seven",
            "set right top heat level to eight",
            "set right top heat level to nine",
            "set left bottom heat level to zero",
            "set left bottom heat level to one",
            "set left bottom heat level to two",
            "set left bottom heat level to three",
            "set left bottom heat level to four",
            "set left bottom heat level to five",
            "set left bottom heat level to six",
            "set left bottom heat level to seven",
            "set left bottom heat level to eight",
            "set left bottom heat level to nine",
            "set right bottom heat level to zero",
            "set right bottom heat level to one",
            "set right bottom heat level to two",
            "set right bottom heat level to three",
            "set right bottom heat level to four",
            "set right bottom heat level to five",
            "set right bottom heat level to six",
            "set right bottom heat level to seven",
            "set right bottom heat level to eight",
            "set right bottom heat level to nine",
            "Show me Commands"
    };
    // responses to commands
    private String[] responses = {
            "left top stove opened",
            "left top stove closed",
            "right top stove opened",
            "right top big stove opened",
            "right top stove closed",
            "left bottom stove opened",
            "left bottom big stove opened",
            "left bottom stove closed",
            "right bottom stove opened",
            "right bottom stove closed",
            "left top heat level has been set to zero",
            "left top heat level has been set to one",
            "left top heat level has been set to two",
            "left top heat level has been set to three",
            "left top heat level has been set to four",
            "left top heat level has been set to five",
            "left top heat level has been set to six",
            "left top heat level has been set to seven",
            "left top heat level has been set to eight",
            "left top heat level has been set to nine",
            "right top heat level has been set to zero",
            "right top heat level has been set to one",
            "right top heat level has been set to two",
            "right top heat level has been set to three",
            "right top heat level has been set to four",
            "right top heat level has been set to five",
            "right top heat level has been set to six",
            "right top heat level has been set to seven",
            "right top heat level has been set to eight",
            "right top heat level has been set to nine",
            "left bottom heat level has been set to zero",
            "left bottom heat level has been set to one",
            "left bottom heat level has been set to two",
            "left bottom heat level has been set to three",
            "left bottom heat level has been set to four",
            "left bottom heat level has been set to five",
            "left bottom heat level has been set to six",
            "left bottom heat level has been set to seven",
            "left bottom heat level has been set to eight",
            "left bottom heat level has been set to nine",
            "right bottom heat level has been set to zero",
            "right bottom heat level has been set to one",
            "right bottom heat level has been set to two",
            "right bottom heat level has been set to three",
            "right bottom heat level has been set to four",
            "right bottom heat level has been set to five",
            "right bottom heat level has been set to six",
            "right bottom heat level has been set to seven",
            "right bottom heat level has been set to eight",
            "right bottom heat level has been set to nine",
            "I'm sorry i can't do that"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stove);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setUpSharedPreferences();

        stoveButtons = new StoveButton[4];
        for(int i = 0; i < stoveButtons.length; i++){
            switch (i){
                case 0: {
                    stoveButtons[i] = findViewById(R.id.ib_stove_leftup);
                    stoveButtons[i].setControls(findViewById(R.id.buttons_leftup));
                    stoveButtons[i].setOrientationName("left top");
                    break;
                }
                case 1: {
                    stoveButtons[i] = findViewById(R.id.ib_stove_rightup);
                    stoveButtons[i].setControls(findViewById(R.id.buttons_rightup));
                    stoveButtons[i].setOrientationName("right top");
                    break;
                }
                case 2: {
                    stoveButtons[i] = findViewById(R.id.ib_stove_leftbottom);
                    stoveButtons[i].setControls(findViewById(R.id.buttons_leftbottom));
                    stoveButtons[i].setOrientationName("left bottom");
                    break;
                }
                case 3: {
                    stoveButtons[i] = findViewById(R.id.ib_stove_rightbottom);
                    stoveButtons[i].setControls(findViewById(R.id.buttons_rightbottom));
                    stoveButtons[i].setOrientationName("right bottom");
                    break;
                }
            }
            stoveButtons[i].setPosition(i);
            stoveButtons[i].setOpened(false);
            if(stoveButtons[i].containsBigStove()){
                stoveButtons[i].setBigStoveOpened(false);
            }
            stoveButtons[i].setOnClickListener(new StoveOnClickListener());
            stoveButtons[i].setHeatLevel(0);

            TextView label = stoveButtons[i].getControls().findViewById(R.id.heat_level);

            IconicsImageButton addButton = stoveButtons[i].getControls().findViewById(R.id.plus);
            addButton.setOnClickListener(new StoveControlsOnClickListener(stoveButtons[i], true, label));

            IconicsImageButton minusButton = stoveButtons[i].getControls().findViewById(R.id.minus);
            minusButton.setOnClickListener(new StoveControlsOnClickListener(stoveButtons[i], false, label));

            IconicsImageButton alertButton = stoveButtons[i].getControls().findViewById(R.id.alert);
            alertButton.setOnClickListener(new StoveAlertOnClickListener(stoveButtons[i]));

            IconicsImageButton stopAlertButton = stoveButtons[i].getControls().findViewById(R.id.stop_alert);
            stopAlertButton.setOnClickListener(new StoveStopAlertOnClickListener(stoveButtons[i]));

            TextView cdTimer = stoveButtons[i].getControls().findViewById(R.id.clock_alert);
            cdTimer.setOnClickListener(new StoveTextViewAlertOnClickListener(stoveButtons[i]));
        }

        countDownTimers = new CountDownTimer[4];

        initializeSpeechClient();
    }

    public void initializeSpeechClient(){
        SystemClock.sleep(1000);
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
        mSpeechRecognizer.setRecognitionListener(new StoveActivity.SpeechRecognitionListener());

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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        setUpSharedPreferences();
    }

    private  void setUpSharedPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        sharedPreferences.getBoolean(getString(R.string.pref_speech_enabled_key), true);
        sharedPreferences.getBoolean(getString(R.string.pref_auto_close_stoves_key), false);
        sharedPreferences.getBoolean(getString(R.string.pref_auto_close_oven_key), false);
    }

    private void openStove(StoveButton stoveButton){
        stoveButton.setOpened(true);

        String msg;
        if(stoveButton.getHeatLevel() == 0){
            msg = "Please set a heat level";
        }
        else{
            msg = stoveButton.getStringFromStove(stoveButton.getPosition(), stoveButton.isOpened(), false);
        }

        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void openBigStove(StoveButton stoveButton){
        stoveButton.setBigStoveOpened(true);

        String msg;
        if(stoveButton.getHeatLevel() == 0){
            msg = "Please set a heat level";
        }
        else{
            msg = stoveButton.getStringFromStove(stoveButton.getPosition(), stoveButton.isOpened(), true);
        }
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void closeStove(StoveButton stoveButton){
        stoveButton.setOpened(false);
        stoveButton.setHeatLevel(0);

        if(countDownTimers[stoveButton.getPosition()] != null){
            countDownTimers[stoveButton.getPosition()].cancel();
            IconicsImageButton alertButton = stoveButton.getControls().findViewById(R.id.alert);
            IconicsImageButton stopAlertButton = stoveButton.getControls().findViewById(R.id.stop_alert);
            TextView textView = stoveButton.getControls().findViewById(R.id.clock_alert);
            alertButton.setVisibility(View.VISIBLE);
            stopAlertButton.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
        }

        String msg = stoveButton.getStringFromStove(stoveButton.getPosition(), stoveButton.isOpened(), false);
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void openAlertCountDownBuilder(final StoveButton stoveButton, final int startValue, final boolean openForEdit){
        final IconicsImageButton alertButton = stoveButton.getControls().findViewById(R.id.alert);
        final IconicsImageButton stopAlertButton = stoveButton.getControls().findViewById(R.id.stop_alert);
        final TextView textView = stoveButton.getControls().findViewById(R.id.clock_alert);

        AlertDialog.Builder builder = new AlertDialog.Builder(StoveActivity.this);
        builder.setTitle("Set a notification for the " + stoveButton.getOrientationName() + " stove:");

        LayoutInflater inflater = getLayoutInflater();
        View viewInflated = inflater.inflate(R.layout.alert_dialog, null);

        final NumberPicker picker = viewInflated.findViewById(R.id.number_picker);
        final CheckBox closeStoveStoveAfter = viewInflated.findViewById(R.id.close_stove_after_time);
        closeStoveStoveAfter.setText("Close stove when time exceed");

        picker.setValue(startValue);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertMinutes = picker.getValue();
                textView.setTextColor(getResources().getColor(R.color.accent));

                if(openForEdit){
                    if(alertMinutes == startValue){
                        return;
                    }

                    countDownTimers[stoveButton.getPosition()].cancel();
                    alertButton.setVisibility(View.VISIBLE);
                    stopAlertButton.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                }

                alertButton.setVisibility(View.GONE);
                stopAlertButton.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);

                dialog.dismiss();

                if(openForEdit){
                    Toast.makeText(getApplicationContext(), "The notification for " + stoveButton.getOrientationName() + " stove has successfully updated!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "The notification for " + stoveButton.getOrientationName() + " stove has successfully enabled!", Toast.LENGTH_LONG).show();
                }

                countDownTimers[stoveButton.getPosition()] =
                        new CountDownTimer(TimeUnit.MINUTES.toMillis(alertMinutes), 1000) {
                            public void onTick(long millisUntilFinished) {
                                String text = String.format(Locale.ENGLISH, "%d:%d",
                                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                                );

                                textView.setText(text);
                            }

                            public void onFinish() {
                                if(closeStoveStoveAfter.isChecked()){
                                    closeStove(stoveButton);
                                }
                                NotificationCompat.Builder mBuilder =
                                        new NotificationCompat.Builder(getApplicationContext())
                                                .setSmallIcon(R.mipmap.ic_launcher)
                                                .setContentTitle("Time is up")
                                                .setContentText("Time exceed for the " + stoveButton.getOrientationName() + " stove");

                                NotificationManager mNotifyMgr =
                                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                mNotifyMgr.notify(001, mBuilder.build());

                                textView.setVisibility(View.GONE);
                                stopAlertButton.setVisibility(View.GONE);
                                alertButton.setVisibility(View.VISIBLE);
                            }
                        }.start();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private class StoveOnClickListener extends OnDoubleClickListener{
        @Override
        public void onClick(View v) {
            if(isDoubleClick()) {
                onDoubleClick(v);
            }else{
                final StoveButton stoveButton = (StoveButton)v;

                if(!stoveButton.containsBigStove()){
                    if(stoveButton.isOpened()){
                        closeStove(stoveButton);
                    }
                    else{
                        openStove(stoveButton);
                    }
                    return;
                }

                final boolean isBigOpenBefore = stoveButton.isBigStoveOpened();
                final boolean isOpenedBefore = stoveButton.isOpened();
                System.out.println("Before Big: " + isBigOpenBefore);
                System.out.println("Before Open: " + isOpenedBefore);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(200);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("After Big: " + stoveButton.isBigStoveOpened());
                                System.out.println("After Open: " + stoveButton.isOpened());
                                if(!isBigOpenBefore && stoveButton.isBigStoveOpened()){
                                    System.out.println("Going to abort");
                                    return;
                                }

                                if(stoveButton.isOpened()){
                                    System.out.println("Going to close");
                                    closeStove(stoveButton);
                                }
                                else{
                                    System.out.println("Going to open");
                                    openStove(stoveButton);
                                }
                            }
                        });
                    }
                }).start();
            }

            updateTime();
        }

        @Override
        public void onDoubleClick(View view) {
            openBigStove((StoveButton)view);
        }
    }

    private class StoveControlsOnClickListener implements View.OnClickListener{
        private StoveButton stoveButton;
        private boolean isAddition;
        private TextView label;

        private StoveControlsOnClickListener(StoveButton stoveButton, boolean isAddition, TextView label){
            this.stoveButton = stoveButton;
            this.isAddition = isAddition;
            this.label = label;
        }

        @Override
        public void onClick(View view) {
            int numBefore = Integer.parseInt(label.getText().toString());
            int newNum = numBefore;
            if(isAddition){
                if(numBefore < 9){
                    stoveButton.setHeatLevel(++newNum);
                }
            }else{
                if(numBefore > 0){
                    stoveButton.setHeatLevel(--newNum);
                }
            }

            if(newNum == 0 && numBefore != 0){
                closeStove(stoveButton);
            }
            else if(newNum > 0 && numBefore == 0){
                openStove(stoveButton);
            }
        }
    }

    private class StoveAlertOnClickListener implements View.OnClickListener{
        private StoveButton stoveButton;

        private StoveAlertOnClickListener(StoveButton stoveButton){ this.stoveButton = stoveButton; }

        @Override
        public void onClick(final View view) {
            if(!stoveButton.isOpened()) {
                Toast.makeText(getApplicationContext(), "The " + stoveButton.getOrientationName() + " stove must be opened to set an alarm!", Toast.LENGTH_LONG).show();
                return;
            }
            if(stoveButton.getHeatLevel() == 0) {
                Toast.makeText(getApplicationContext(), "The " + stoveButton.getOrientationName() + " stove must have a heat level to set an alarm!", Toast.LENGTH_LONG).show();
                return;
            }

            openAlertCountDownBuilder(stoveButton, 1, false);
        }
    }

    private class StoveTextViewAlertOnClickListener implements View.OnClickListener{
        private StoveButton stoveButton;

        private StoveTextViewAlertOnClickListener(StoveButton stoveButton) {this.stoveButton = stoveButton;}

        @Override
        public void onClick(View view) {
            if(!stoveButton.isOpened()) return;
            if(stoveButton.getHeatLevel() == 0) return;

            TextView textView = stoveButton.getControls().findViewById(R.id.clock_alert);

            StringTokenizer tokenizer = new StringTokenizer(textView.getText().toString(), ":");
            openAlertCountDownBuilder(stoveButton, Integer.parseInt(tokenizer.nextToken()), true);
        }
    }

    private class StoveStopAlertOnClickListener implements View.OnClickListener{
        private StoveButton stoveButton;

        private StoveStopAlertOnClickListener(StoveButton stoveButton){ this.stoveButton = stoveButton; }

        @Override
        public void onClick(final View view) {
            if(countDownTimers[stoveButton.getPosition()] != null){
                countDownTimers[stoveButton.getPosition()].cancel();
                IconicsImageButton alertButton = stoveButton.getControls().findViewById(R.id.alert);
                IconicsImageButton stopAlertButton = stoveButton.getControls().findViewById(R.id.stop_alert);
                TextView textView = stoveButton.getControls().findViewById(R.id.clock_alert);
                alertButton.setVisibility(View.VISIBLE);
                stopAlertButton.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);

                Toast.makeText(getApplicationContext(), "The notification for " + stoveButton.getOrientationName() + " stove has successfully disabled!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.settings_button:
                Intent openSettingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(openSettingsIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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
            if (performingSpeechSetup && error == SpeechRecognizer.ERROR_NO_MATCH
                    || error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) return;
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
                SystemClock.sleep(2500);
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
        if (index==commands.length-1){
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
        if(index == 0){
            openStove(stoveButtons[0]);
        }else if(index == 1){
            closeStove(stoveButtons[0]);
        }else if(index == 2){
            openStove(stoveButtons[1]);
        }else if(index == 3){
            openBigStove(stoveButtons[1]);
        }else if(index == 4){
            closeStove(stoveButtons[1]);
        }else if(index == 5){
            openStove(stoveButtons[2]);
        }else if(index == 6){
            openBigStove(stoveButtons[2]);
        }else if(index == 7){
            closeStove(stoveButtons[2]);
        }else if(index == 8){
            openStove(stoveButtons[3]);
        }else if(index == 9){
            closeStove(stoveButtons[3]);
        }else if(index >= 10 && index <= 19){
            int pos = index - 10;
            stoveButtons[0].setHeatLevel(pos);
        }else if(index >= 20 && index <= 29){
            int pos = index - 20;
            stoveButtons[1].setHeatLevel(pos);
        }else if(index >= 30 && index <= 39){
            int pos = index - 30;
            stoveButtons[2].setHeatLevel(pos);
        }else if(index >= 40 && index <= 49){
            int pos = index - 40;
            stoveButtons[3].setHeatLevel(pos);
        }
    }

    public int isValidCommand(String result){
        int index = -1;
        int i = 0;
        for(String command: commands){
            String temp = command
                    .replace("1", "one")
                    .replace("2", "two")
                    .replace("3", "three")
                    .replace("4", "four")
                    .replace("5", "five")
                    .replace("6", "six")
                    .replace("7", "seven")
                    .replace("8", "eight")
                    .replace("9", "nine")
                    .replace("lift", "left")
                    .replace("sad", "set")
                    .replace("said", "set");
            if(temp.equalsIgnoreCase(result)){
                index = i;
            }
            i++;
        }
        return index;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        if(mSpeechRecognizer != null){
            mSpeechRecognizer.destroy();
        }
        toSpeech.shutdown();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mSpeechRecognizer != null){
            mSpeechRecognizer.destroy();
        }

        toSpeech.shutdown();
    }

    @Override
    protected void onPause() {
        super.onPause();
        super.onStop();
        if(mSpeechRecognizer != null){
            mSpeechRecognizer.destroy();
        }

        toSpeech.shutdown();
    }
}


