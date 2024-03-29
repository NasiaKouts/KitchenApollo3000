package aueb.nasia_kouts.gr.kitchenapollo;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.CountDownTimer;
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
import android.widget.ImageView;
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

public class OvenActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    SeekArc ovenTemp;
    TextView tempTextView;
    TextView modeSelectedTextView;
    ImageView[] ovenModesImageView;
    ImageView openedMode;

    CountDownTimer alarmCountDown;
    IconicsImageButton alarmButton;
    IconicsImageButton stopAlarmButton;
    TextView alarmTextView;

    int alertMinutes;

    private int number;
    private int posInt = 9;

    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean performingSpeechSetup;
    private TextToSpeech toSpeech;
    private String result;

    IconicsImageButton camera;

    private String[] commands = {
            "select light mode",
            "select fan mode",
            "select bottom only heat mode",
            "select both heat mode",
            "select grill and heat mode",
            "select unfreeze mode",
            "close selected mode",
            "start stove",
            "start oven",
            "set oven temperature to degrees",
            "help",
            "Show me Commands"
    };
    // responses to commands
    private String[] responses = {
            "light mode selected",
            "fan mode selected",
            "bottom only heat mode selected",
            "both heat mode selected",
            "grill and heat mode selected",
            "unfreeze mode selected",
            "selected mode closed",
            "starting stove",
            "oven is already opened, I am waiting for your next command",
            "oven temperature has been set to ",
            "",
            "I'm sorry i can't do that"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oven);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setUpSharedPreferences();

        responses[10] = getString(R.string.stove_info);

        ovenTemp = findViewById(R.id.temperature_controller);
        tempTextView = findViewById(R.id.temperature_text);
        modeSelectedTextView = findViewById(R.id.mode_selected);

        alarmButton = findViewById(R.id.alertOven);
        alarmButton.setOnClickListener(new OvenAlarmOnClickListener());
        stopAlarmButton = findViewById(R.id.close_oven_alarm);
        stopAlarmButton.setOnClickListener(new OvenStopAlarmOnClickListener());
        alarmTextView = findViewById(R.id.clock_oven_alert);
        alarmTextView.setOnClickListener(new OvenEditAlarmOnClickListener());

        ovenModesImageView = new ImageView[6];
        ovenModesImageView[0] = findViewById(R.id.light_mode);
        ovenModesImageView[1] = findViewById(R.id.fan_mode);
        ovenModesImageView[2] = findViewById(R.id.bottom_heat_mode);
        ovenModesImageView[3] = findViewById(R.id.full_heat_mode);
        ovenModesImageView[4] = findViewById(R.id.grill_bottom_heat_mode);
        ovenModesImageView[5] = findViewById(R.id.unfreeze_mode);

        for(int i = 0; i < ovenModesImageView.length; i++){
            ovenModesImageView[i].setOnClickListener(new OvenModeOnClickListener());
        }

        ovenTemp.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                tempTextView.setText(progress + "\u2103");
                updateLabel();
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {

            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {

            }
        });

        initializeSpeechClient();
      
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(temperature)){
               String selectedTemp = savedInstanceState.getString(temperature);
               ovenTemp.setProgress(Integer.parseInt(selectedTemp));
               tempTextView.setText(selectedTemp + "\\u2103");
               ovenTemp.setProgress(Integer.parseInt(selectedTemp));
            }
            if(savedInstanceState.containsKey(openedModeId)){
                int id = savedInstanceState.getInt(openedModeId);
                if(id != -1){
                    openedMode = findViewById(id);
                    openedMode.callOnClick();
                    updateLabel();
                }
            }
        }

        camera = findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Now you are going to see what is going on your oven", Toast.LENGTH_LONG).show();
            }
        });
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
        mSpeechRecognizer.setRecognitionListener(new OvenActivity.SpeechRecognitionListener());

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

    private void openAlertCountDownBuilder(final int startValue, final boolean openForEdit){
        AlertDialog.Builder builder = new AlertDialog.Builder(OvenActivity.this);
        builder.setTitle("Set a notification for the oven:");

        LayoutInflater inflater = getLayoutInflater();
        View viewInflated = inflater.inflate(R.layout.alert_dialog, null);

        final NumberPicker picker = viewInflated.findViewById(R.id.number_picker);
        final CheckBox closeStoveStoveAfter = viewInflated.findViewById(R.id.close_stove_after_time);
        closeStoveStoveAfter.setText("Close oven when time exceed");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firsttime = sharedPreferences.getBoolean(getString(R.string.pref_auto_close_oven_key),false);
        closeStoveStoveAfter.setChecked(firsttime);

        picker.setValue(startValue);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertMinutes = picker.getValue();

                if(openForEdit){
                    if(alertMinutes == startValue){
                        return;
                    }

                    alarmCountDown.cancel();
                    alarmButton.setVisibility(View.VISIBLE);
                    stopAlarmButton.setVisibility(View.GONE);
                    alarmTextView.setVisibility(View.GONE);
                }

                alarmButton.setVisibility(View.GONE);
                stopAlarmButton.setVisibility(View.VISIBLE);
                alarmTextView.setVisibility(View.VISIBLE);

                dialog.dismiss();

                if(openForEdit){
                    Toast.makeText(getApplicationContext(), "The notification for the oven has successfully updated!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "The notification for the oven stove has successfully enabled!", Toast.LENGTH_LONG).show();
                }

                alarmCountDown =
                        new CountDownTimer(TimeUnit.MINUTES.toMillis(alertMinutes), 1000) {
                            public void onTick(long millisUntilFinished) {
                                String text = String.format(Locale.ENGLISH, "%d:%d",
                                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                                );

                                alarmTextView.setText(text);
                            }

                            public void onFinish() {
                                if(closeStoveStoveAfter.isChecked()){
                                    ovenTemp.setProgress(0);
                                    openedMode.callOnClick();
                                }
                                NotificationCompat.Builder mBuilder =
                                        new NotificationCompat.Builder(getApplicationContext())
                                                .setSmallIcon(R.mipmap.ic_launcher)
                                                .setContentTitle("Time is up")
                                                .setContentText("Time exceed for the oven");

                                NotificationManager mNotifyMgr =
                                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                mNotifyMgr.notify(001, mBuilder.build());

                                alarmTextView.setVisibility(View.GONE);
                                stopAlarmButton.setVisibility(View.GONE);
                                alarmButton.setVisibility(View.VISIBLE);
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

    private static final String temperature = "temperature";
    private static final String openedModeId = "openedModeId";

    @Override
    protected void onSaveInstanceState(Bundle state){
        super.onSaveInstanceState(state);
        state.putString( temperature, String.valueOf(ovenTemp.getProgress()));
        if(openedMode==null) state.putInt(openedModeId, -1);
        else state.putInt(openedModeId, openedMode.getId());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.settings_button:
                Intent openSettingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(openSettingsIntent);
                return true;
            case R.id.help_button:
                toSpeech.speak(getString(R.string.general_info), TextToSpeech.QUEUE_FLUSH, null);
                SystemClock.sleep(5000);
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateLabel(){
        if(openedMode == null ||
                (ovenTemp.getProgress() == 0 && openedMode.getId() != ovenModesImageView[0].getId())){
            modeSelectedTextView.setText(R.string.oven_closed);
        }else{
            int pos = -1;
            for(int i = 0; i < ovenModesImageView.length; i++){
                if(ovenModesImageView[i].getId() == openedMode.getId()){
                    pos = i;
                    break;
                }
            }
            switch (pos){
                case 0: { modeSelectedTextView.setText(R.string.oven_light_mode); break; }
                case 1: { modeSelectedTextView.setText(R.string.oven_fan_mode); break; }
                case 2: { modeSelectedTextView.setText(R.string.oven_bottom_heat_mode); break; }
                case 3: { modeSelectedTextView.setText(R.string.oven_both_heat_mode); break; }
                case 4: { modeSelectedTextView.setText(R.string.oven_grill_mode); break; }
                case 5: { modeSelectedTextView.setText(R.string.oven_unfreeze_mode); break; }
            }
        }
    }

    private void selectMode(View view){
        if(openedMode != null){
            openedMode.setScaleX(openedMode.getScaleX()*(2F/3));
            openedMode.setScaleY(openedMode.getScaleY()*(2F/3));

            if(view.getId() == openedMode.getId()){
                openedMode = null;
                ovenTemp.setProgress(0);
            }else{
                openedMode = (ImageView)view;
                openedMode.setScaleX(1.5F);
                openedMode.setScaleY(1.5F);
            }
        }else{
            openedMode = (ImageView)view;
            openedMode.setScaleX(1.5F);
            openedMode.setScaleY(1.5F);
        }
        updateLabel();
    }


    private class OvenModeOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(openedMode != null){
                openedMode.setScaleX(openedMode.getScaleX()*(2F/3));
                openedMode.setScaleY(openedMode.getScaleY()*(2F/3));

                if(view.getId() == openedMode.getId()){
                    openedMode = null;
                    ovenTemp.setProgress(0);
                }else{
                    openedMode = (ImageView)view;
                    openedMode.setScaleX(1.5F);
                    openedMode.setScaleY(1.5F);
                }
            }else{
                openedMode = (ImageView)view;
                openedMode.setScaleX(1.5F);
                openedMode.setScaleY(1.5F);
            }
            updateLabel();
        }
    }

    private class OvenAlarmOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(modeSelectedTextView.getText().equals(R.string.oven_closed)) {
                Toast.makeText(getApplicationContext(), "You must select a mode to set an alarm!", Toast.LENGTH_LONG).show();
                return;
            }
            if(ovenTemp.getProgress() == 0){
                Toast.makeText(getApplicationContext(), "You must set a heat level to set an alarm!", Toast.LENGTH_LONG).show();
                return;
            }

            openAlertCountDownBuilder(1, false);
        }
    }

    private class OvenEditAlarmOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(modeSelectedTextView.getText().equals(R.string.oven_closed)) {
                Toast.makeText(getApplicationContext(), "You must select a mode to set an alarm!", Toast.LENGTH_LONG).show();
                return;
            }
            if(ovenTemp.getProgress() == 0){
                Toast.makeText(getApplicationContext(), "You must set a heat level to set an alarm!", Toast.LENGTH_LONG).show();
                return;
            }

            StringTokenizer tokenizer = new StringTokenizer(alarmTextView.getText().toString(), ":");
            openAlertCountDownBuilder(Integer.parseInt(tokenizer.nextToken()), true);
        }
    }

    private class OvenStopAlarmOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(alarmCountDown != null){
                alarmCountDown.cancel();
                alarmButton.setVisibility(View.VISIBLE);
                stopAlarmButton.setVisibility(View.GONE);
                alarmTextView.setVisibility(View.GONE);

                Toast.makeText(getApplicationContext(), "The notification for oven has successfully disabled!", Toast.LENGTH_LONG).show();
            }
        }
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
            Log.d(TAG, "onReadyForSpeech");
        }

        @Override
        public void onResults(Bundle results)
        {
            Log.d(TAG, "onResults");
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            result = matches.get(0);
            mSpeechRecognizer.cancel();

            // TODO: 5/2/2018 Here is where you pass the result of the speech
            if(checkCommands(result) == commands.length-1){
                SystemClock.sleep(3500);
            }
            if(checkCommands(result) == 10){
                SystemClock.sleep(5000);
            }else{
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
        HashMap<String, String> myHashAlarm = new HashMap<>();
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_ALARM));
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "SOME MESSAGE");
        // Here it speaks
        System.out.println(index + " pos: " + posInt);
        if (index==commands.length-1){
            toSpeech.setSpeechRate(0.8f);
            toSpeech.speak(getCommands(), TextToSpeech.QUEUE_FLUSH, myHashAlarm);
            toSpeech.setSpeechRate(1.0f);
        }else if(index == posInt){
            number = getNumber(result);
            toSpeech.speak(responses[index] + number, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
        }
        else if(index != -1){
            toSpeech.speak(responses[index], TextToSpeech.QUEUE_FLUSH, myHashAlarm);
        }
        else{
            index = responses.length -1;
            toSpeech.speak(responses[index], TextToSpeech.QUEUE_FLUSH, myHashAlarm);
        }
        runCommands(index);
        return index;
    }

    public int getNumber(String command){
        StringTokenizer str = new StringTokenizer(command);
        int number = -1;
        int i = 0;
        int pos = str.countTokens() - 2;
        while (str.hasMoreTokens()){
            if (i!=pos){
                str.nextToken();
            }else {
                try{
                    number = Integer.parseInt(str.nextToken());
                }catch (NumberFormatException e){
                    e.printStackTrace();
                    return -3;
                }
            }
            i++;
        }
        return number;
    }

    public String getNewCommand(String command ,int number){
        StringTokenizer st = new StringTokenizer(command.replace(Integer.toString(number),""));
        StringBuilder string = new StringBuilder();
        while (st.hasMoreTokens()){
            string.append(st.nextToken() + " ");
        }
        return string.toString().substring(0, string.toString().length()-1);
    }

    public int isValidCommand(String result){
        int index;
        int i = 0;
        for(String command: commands){
            if(command.equalsIgnoreCase(result)){
                index = i;
                return index;
            }
            i++;
        }

        int tempature = getNumber(result);
        System.out.println(tempature);
        if(tempature < 0) return -1;

        String newCommand = getNewCommand(result,tempature);
        index = -1;
        System.out.println("new "+newCommand);
        i = 0;
        for(String command: commands){
            if(command.equalsIgnoreCase(newCommand)){
                index = i;
                return index;
            }
            i++;
        }
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
        if(index <= 5){
            selectMode(ovenModesImageView[index]);
        }else if(index == 6){
            openedMode.setScaleX(openedMode.getScaleX()*(2F/3));
            openedMode.setScaleY(openedMode.getScaleY()*(2F/3));
            openedMode = null;
            ovenTemp.setProgress(0);
            updateLabel();
        }else if(index == 7){
            Intent openStoveIntent = new Intent(this, StoveActivity.class);
            startActivity(openStoveIntent);
        }else if(index == 9){
            System.out.println(number + " NUMBER");
            ovenTemp.setProgress(number);
            if(number == 0 && openedMode != null){
                openedMode.setScaleX(openedMode.getScaleX()*(2F/3));
                openedMode.setScaleY(openedMode.getScaleY()*(2F/3));
                openedMode = null;
                updateLabel();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        if(mSpeechRecognizer != null){
            mSpeechRecognizer.destroy();
        }
        if(toSpeech != null){
            toSpeech.shutdown();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mSpeechRecognizer != null){
            mSpeechRecognizer.destroy();
            SystemClock.sleep(500);
        }
        if(toSpeech != null){
            toSpeech.shutdown();
        }
    }
}
