package aueb.nasia_kouts.gr.kitchenapollo;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.os.SystemClock;
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
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.iconics.view.IconicsImageButton;
import com.travijuu.numberpicker.library.NumberPicker;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class StoveActivity extends AppCompatActivity {

    StoveButton stoveButtons[];
    CountDownTimer countDownTimers[];

    TextToSpeech textToSpeechClient;
    int result;

    int alertMinutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stove);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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
            label.setOnClickListener(new StoveTextViewAlertOnClickListener(stoveButtons[i]));

            IconicsImageButton addButton = stoveButtons[i].getControls().findViewById(R.id.plus);
            addButton.setOnClickListener(new StoveControlsOnClickListener(stoveButtons[i], true, label));

            IconicsImageButton minusButton = stoveButtons[i].getControls().findViewById(R.id.minus);
            minusButton.setOnClickListener(new StoveControlsOnClickListener(stoveButtons[i], false, label));

            IconicsImageButton alertButton = stoveButtons[i].getControls().findViewById(R.id.alert);
            alertButton.setOnClickListener(new StoveAlertOnClickListener(stoveButtons[i]));
        }

        countDownTimers = new CountDownTimer[4];
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
        }

        String msg = stoveButton.getStringFromStove(stoveButton.getPosition(), stoveButton.isOpened(), false);
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void openAlertCountDownBuilder(final StoveButton stoveButton){
        final IconicsImageButton alertButton = stoveButton.getControls().findViewById(R.id.alert);
        final TextView textView = stoveButton.getControls().findViewById(R.id.clock_alert);

        alertButton.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(StoveActivity.this);
        builder.setTitle("Set a notification for the " + stoveButton.getOrientationName() + " stove:");

        LayoutInflater inflater = getLayoutInflater();
        View viewInflated = inflater.inflate(R.layout.alert_dialog, null);

        final NumberPicker picker = viewInflated.findViewById(R.id.number_picker);
        final CheckBox closeStoveStoveAfter = viewInflated.findViewById(R.id.close_stove_after_time);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                alertMinutes = picker.getValue();
                textView.setTextColor(getResources().getColor(R.color.accent));

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

                                // Sets an ID for the notification
                                int mNotificationId = 001;
                                // Gets an instance of the NotificationManager service
                                NotificationManager mNotifyMgr =
                                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                // Builds the notification and issues it.
                                mNotifyMgr.notify(mNotificationId, mBuilder.build());

                                textView.setVisibility(View.GONE);
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
            if(!stoveButton.isOpened()) return;
            if(stoveButton.getHeatLevel() == 0) return;

            openAlertCountDownBuilder(stoveButton);
        }
    }

    private class StoveTextViewAlertOnClickListener implements View.OnClickListener{
        private StoveButton stoveButton;

        private StoveTextViewAlertOnClickListener(StoveButton stoveButton) {this.stoveButton = stoveButton;}

        @Override
        public void onClick(View view) {
            if(!stoveButton.isOpened()) return;
            if(stoveButton.getHeatLevel() == 0) return;

            openAlertCountDownBuilder(stoveButton);
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


