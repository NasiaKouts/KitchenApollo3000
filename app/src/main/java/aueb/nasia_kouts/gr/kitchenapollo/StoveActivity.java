package aueb.nasia_kouts.gr.kitchenapollo;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.iconics.view.IconicsImageButton;

import java.util.Locale;

public class StoveActivity extends AppCompatActivity {

    ImageButton leftTopStove, rightTopStove, leftBottomStove, rightBottomStove;
    boolean[] isOpen = new boolean[4];
    boolean[] isBigOpened = new boolean[2];

    TextToSpeech textToSpeechClient;
    int result;

    View[] stoveControls;

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

        leftTopStove = findViewById(R.id.ib_stove_leftup);
        rightTopStove = findViewById(R.id.ib_stove_rightup);
        leftBottomStove = findViewById(R.id.ib_stove_leftbottom);
        rightBottomStove = findViewById(R.id.ib_stove_rightbottom);

        leftTopStove.setOnClickListener(new StoveOnClickListener(0));
        rightTopStove.setOnClickListener(new StoveOnClickListener(1));
        leftBottomStove.setOnClickListener(new StoveOnClickListener(2));
        rightBottomStove.setOnClickListener(new StoveOnClickListener(3));

        stoveControls = new View[4];
        stoveControls[0] = findViewById(R.id.buttons_leftup);
        stoveControls[1] = findViewById(R.id.buttons_rightup);
        stoveControls[2] = findViewById(R.id.buttons_leftbottom);
        stoveControls[3] = findViewById(R.id.buttons_rightbottom);

        for(int i = 0; i < stoveControls.length; i++){
            TextView label = stoveControls[i].findViewById(R.id.heat_level);
            label.setText("" + 0);

            IconicsImageButton addButton = stoveControls[i].findViewById(R.id.plus);
            addButton.setOnClickListener(new StoveControlsOnClickListener(i, true, label));

            IconicsImageButton minusButton = stoveControls[i].findViewById(R.id.minus);
            minusButton.setOnClickListener(new StoveControlsOnClickListener(i, false, label));

            System.out.println(i);
        }

    }

    private ImageButton getImageButtonFromPos(int pos){
        ImageButton imageButton = null;
        switch (pos){
            case 0: { imageButton = leftTopStove; break;}
            case 1: { imageButton = rightTopStove; break;}
            case 2: { imageButton = leftBottomStove; break;}
            case 3: { imageButton = rightBottomStove; break;}
        }

        return imageButton;
    }

    private String getStringFromStove(int pos, boolean opened, boolean bigOpened){
        switch (pos){
            case 0:{
                return opened ?
                        getResources().getString(R.string.stove_left_top_opened) :
                        getResources().getString(R.string.stove_left_top_closed);
            }
            case 1:{
                return opened ? (bigOpened ?
                        getResources().getString(R.string.stove_right_top_big_opened) :
                        getResources().getString(R.string.stove_right_top_opened)) :
                        getResources().getString(R.string.stove_right_top_closed);
            }
            case 2:{
                return opened ? (bigOpened ?
                        getResources().getString(R.string.stove_left_bottom_big_opened) :
                        getResources().getString(R.string.stove_left_bottom_opened)) :
                        getResources().getString(R.string.stove_left_bottom_closed);
            }
            default:{
                return opened ?
                        getResources().getString(R.string.stove_right_bottom_opened) :
                        getResources().getString(R.string.stove_right_bottom_closed);
            }
        }
    }

    private void openStove(int pos){
        LayerDrawable layer = (LayerDrawable) (getImageButtonFromPos(pos)).getDrawable();

        int firstItem = 0;
        if(pos == 1 || pos == 2) firstItem = 1;

        GradientDrawable gradient = (GradientDrawable)layer.findDrawableByLayerId(layer.getId(firstItem));
        gradient.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        gradient.setGradientRadius(150);
        gradient.setColors(new int[] {getResources().getColor(android.R.color.holo_red_light), getResources().getColor(android.R.color.holo_red_dark)});
        isOpen[pos] = true;

        String msg = getStringFromStove(pos, isOpen[pos], false);
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void openBigStove(int pos){
        if(pos == 0 || pos == 3) return;

        LayerDrawable layer = (LayerDrawable) (getImageButtonFromPos(pos)).getDrawable();

        GradientDrawable gradient = (GradientDrawable)layer.findDrawableByLayerId(layer.getId(0));
        gradient.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        gradient.setGradientRadius(150);
        gradient.setColors(new int[] {getResources().getColor(android.R.color.holo_red_light), getResources().getColor(android.R.color.holo_red_dark)});
        isOpen[pos] = true;
        isBigOpened[pos-1] = true;

        String msg = getStringFromStove(pos, isOpen[pos], true);
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void closeStove(int pos){
        LayerDrawable layer = (LayerDrawable) (getImageButtonFromPos(pos)).getDrawable();

        int firstItem = 0;
        if(pos == 1 || pos == 2) firstItem = 1;

        GradientDrawable gradient = (GradientDrawable)layer.findDrawableByLayerId(layer.getId(firstItem));
        gradient.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        gradient.setGradientRadius(150);
        gradient.setColors(new int[] {getResources().getColor(android.R.color.transparent), getResources().getColor(android.R.color.transparent)});

        if(pos == 1 || pos == 2){
            GradientDrawable gradient2 = (GradientDrawable)layer.findDrawableByLayerId(layer.getId(0));
            gradient2.setGradientType(GradientDrawable.RADIAL_GRADIENT);
            gradient2.setGradientRadius(150);
            gradient2.setColors(new int[] {getResources().getColor(android.R.color.transparent), getResources().getColor(android.R.color.transparent)});
        }

        isOpen[pos] = false;
        if(pos == 1 || pos == 2){
            isBigOpened[pos-1] = false;
        }

        String msg = getStringFromStove(pos, isOpen[pos], false);
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private class StoveOnClickListener extends OnDoubleClickListener{
        private int pos;

        private StoveOnClickListener(int pos){
            this.pos = pos;
        }

        @Override
        public void onClick(View v) {
            if(isDoubleClick()) {
                onDoubleClick(v);
            }else{
                if(pos == 0 || pos == 3){
                    if(isOpen[pos]){
                        closeStove(pos);
                    }
                    else{
                        openStove(pos);
                    }
                    return;
                }

                final boolean isBigOpenBefore = isBigOpened[pos-1];
                final boolean isOpenedBefore = isOpen[pos];
                System.out.println("Before Big: " + isBigOpenBefore);
                System.out.println("Before Open: " + isOpenedBefore);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(200);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("After Big: " + isBigOpened[pos-1]);
                                System.out.println("After Open: " + isOpen[pos]);
                                if(!isBigOpenBefore && isBigOpened[pos-1]){
                                    System.out.println("Going to abort");
                                    return;
                                }

                                if(isOpen[pos]){
                                    System.out.println("Going to close");
                                    closeStove(pos);
                                }
                                else{
                                    System.out.println("Going to open");
                                    openStove(pos);
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
            openBigStove(pos);
        }
    }

    private class StoveControlsOnClickListener implements View.OnClickListener{
        private int stoveId;
        private boolean isAddition;
        private TextView label;

        private StoveControlsOnClickListener(int stoveId, boolean isAddition, TextView label){
            this.stoveId = stoveId;
            this.isAddition = isAddition;
            this.label = label;
        }

        @Override
        public void onClick(View view) {
            int numBefore = Integer.parseInt(label.getText().toString());
            int newNum = numBefore;
            if(isAddition){
                if(numBefore < 9){
                    label.setText("" + ++newNum);
                }
            }else{
                if(numBefore > 0){
                    label.setText("" + --newNum);
                }
            }

            if(newNum == 0){
                closeStove(stoveId);
            }
            else if(newNum > 0 && numBefore == 0){
                openStove(stoveId);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


