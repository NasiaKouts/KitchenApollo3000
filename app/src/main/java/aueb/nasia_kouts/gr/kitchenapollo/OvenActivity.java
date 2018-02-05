package aueb.nasia_kouts.gr.kitchenapollo;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class OvenActivity extends AppCompatActivity {

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
