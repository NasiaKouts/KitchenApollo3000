package aueb.nasia_kouts.gr.kitchenapollo;

import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.iconics.view.IconicsImageButton;

import java.util.Locale;

public class StoveActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    StoveButton stoveButtons[];

    TextToSpeech textToSpeechClient;
    int result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stove);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setUpSharedPreferences();

        textToSpeechClient = new TextToSpeech(StoveActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i == TextToSpeech.SUCCESS){
                    result = textToSpeechClient.setLanguage(Locale.US);
                }else{
                    Toast.makeText(getApplicationContext(), "Text to speech is not enabled", Toast.LENGTH_LONG).show();
                }
            }
        });

        stoveButtons = new StoveButton[4];
        for(int i = 0; i < stoveButtons.length; i++){
            switch (i){
                case 0: {
                    stoveButtons[i] = findViewById(R.id.ib_stove_leftup);
                    stoveButtons[i].setControls(findViewById(R.id.buttons_leftup));
                    break;
                }
                case 1: {
                    stoveButtons[i] = findViewById(R.id.ib_stove_rightup);
                    stoveButtons[i].setControls(findViewById(R.id.buttons_rightup));
                    break;
                }
                case 2: {
                    stoveButtons[i] = findViewById(R.id.ib_stove_leftbottom);
                    stoveButtons[i].setControls(findViewById(R.id.buttons_leftbottom));
                    break;
                }
                case 3: {
                    stoveButtons[i] = findViewById(R.id.ib_stove_rightbottom);
                    stoveButtons[i].setControls(findViewById(R.id.buttons_rightbottom));
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

            if(speechEnabled != speechEnabled){
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

        String msg = stoveButton.getStringFromStove(stoveButton.getPosition(), stoveButton.isOpened(), false);
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
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

            if(newNum == 0){
                closeStove(stoveButton);
            }
            else if(newNum > 0 && numBefore == 0){
                openStove(stoveButton);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}


