package aueb.nasia_kouts.gr.kitchenapollo;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
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

        leftTopStove.setOnClickListener(new StoveOnClickListener());
        rightTopStove.setOnClickListener(new StoveOnClickListener());
        leftBottomStove.setOnClickListener(new StoveOnClickListener());
        rightBottomStove.setOnClickListener(new StoveOnClickListener());

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

    private class StoveOnClickListener extends OnDoubleClickListener{
        @Override
        public void onClick(View v) {
            if(isDoubleClick()) {
                onDoubleClick(v);
            }else{
                LayerDrawable twoStove = (LayerDrawable) ((ImageButton)v).getDrawable();
                int pos = -1;
                switch (twoStove.getId(0)){
                    case R.id.stove1:{
                        pos = 0;
                        break;
                    }
                    case R.id.outerStove1: {
                        pos = 1;
                        break;
                    }
                    case R.id.outerStove2:{
                        pos = 2;
                        break;
                    }
                    case R.id.stove2:{
                        pos = 3;
                        break;
                    }

                }
                System.out.println(pos);

                int firstItem = 0;
                if(pos == 1 || pos == 2) firstItem = 1;

                if(isOpen[pos]){
                    GradientDrawable gradient = (GradientDrawable)twoStove.findDrawableByLayerId(twoStove.getId(firstItem));
                    gradient.setGradientType(GradientDrawable.RADIAL_GRADIENT);
                    gradient.setGradientRadius(150);
                    gradient.setColors(new int[] {getResources().getColor(android.R.color.transparent), getResources().getColor(android.R.color.transparent)});

                    if(pos == 1 || pos == 2){
                        GradientDrawable gradient2 = (GradientDrawable)twoStove.findDrawableByLayerId(twoStove.getId(0));
                        gradient2.setGradientType(GradientDrawable.RADIAL_GRADIENT);
                        gradient2.setGradientRadius(150);
                        gradient2.setColors(new int[] {getResources().getColor(android.R.color.transparent), getResources().getColor(android.R.color.transparent)});
                    }

                    isOpen[pos] = false;
                }
                else{
                    GradientDrawable gradient = (GradientDrawable)twoStove.findDrawableByLayerId(twoStove.getId(firstItem));
                    gradient.setGradientType(GradientDrawable.RADIAL_GRADIENT);
                    gradient.setGradientRadius(150);
                    gradient.setColors(new int[] {getResources().getColor(android.R.color.holo_red_light), getResources().getColor(android.R.color.holo_red_dark)});
                    isOpen[pos] = true;
                }
            }
            updateTime();
        }

        @Override
        public void onDoubleClick(View view) {
            LayerDrawable twoStove = (LayerDrawable) ((ImageButton)view).getDrawable();
            int pos = 0;
            switch (twoStove.getId(0)){
                case R.id.stove1:{
                    return;
                }
                case R.id.outerStove1: {
                    pos = 1;
                    break;
                }
                case R.id.outerStove2:{
                    pos = 2;
                    break;
                }
                case R.id.stove2:{
                    return;
                }
            }
            GradientDrawable gradient = (GradientDrawable)twoStove.findDrawableByLayerId(twoStove.getId(0));
            gradient.setGradientType(GradientDrawable.RADIAL_GRADIENT);
            gradient.setGradientRadius(150);
            gradient.setColors(new int[] {getResources().getColor(android.R.color.holo_red_light), getResources().getColor(android.R.color.holo_red_dark)});
            isOpen[pos] = true;
        }
    }

    private class StoveControlsOnClickListener implements View.OnClickListener{
        private boolean isAddition;
        private TextView label;

        public StoveControlsOnClickListener(int id, boolean isAddition, TextView label){
            this.isAddition = isAddition;
            this.label = label;
        }

        @Override
        public void onClick(View view) {
            int num = Integer.parseInt(label.getText().toString());
            if(isAddition){
                if(num < 9){
                    label.setText("" + ++num);
                }
            }else{
                if(num > 0){
                    label.setText("" + --num);
                }
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


