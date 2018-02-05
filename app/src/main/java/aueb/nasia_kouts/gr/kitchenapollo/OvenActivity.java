package aueb.nasia_kouts.gr.kitchenapollo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OvenActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    SeekArc ovenTemp;
    TextView tempTextView;
    TextView modeSelectedTextView;
    ImageView[] ovenModesImageView;
    ImageView openedMode;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
