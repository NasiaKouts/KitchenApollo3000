package aueb.nasia_kouts.gr.kitchenapollo;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
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

import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oven);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setUpSharedPreferences();

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
    }

    private void openAlertCountDownBuilder(final int startValue, final boolean openForEdit){
        AlertDialog.Builder builder = new AlertDialog.Builder(OvenActivity.this);
        builder.setTitle("Set a notification for the oven:");

        LayoutInflater inflater = getLayoutInflater();
        View viewInflated = inflater.inflate(R.layout.alert_dialog, null);

        final NumberPicker picker = viewInflated.findViewById(R.id.number_picker);
        final CheckBox closeStoveStoveAfter = viewInflated.findViewById(R.id.close_stove_after_time);
        closeStoveStoveAfter.setText("Close oven when time exceed");

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
}
