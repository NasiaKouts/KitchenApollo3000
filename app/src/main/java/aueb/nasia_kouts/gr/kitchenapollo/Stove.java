package aueb.nasia_kouts.gr.kitchenapollo;

import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.mikepenz.iconics.context.IconicsLayoutInflater2;

public class Stove extends AppCompatActivity {
    ImageButton leftTopStove, rightTopStove, leftBottomStove, rightBottomStove;
    boolean[] isOpen = new boolean[4];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stove);

        leftTopStove = findViewById(R.id.ib_stove_leftup);
        rightTopStove = findViewById(R.id.ib_stove_rightup);
        leftBottomStove = findViewById(R.id.ib_stove_leftbottom);
        rightBottomStove = findViewById(R.id.ib_stove_rightbottom);

        rightTopStove.setOnClickListener(new StoveOnClickListener());

        rightTopStove.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LayerDrawable twoStove = (LayerDrawable) ((ImageButton)v).getDrawable();
                GradientDrawable gradient = (GradientDrawable)twoStove.findDrawableByLayerId(R.id.outerStove1);
                gradient.setGradientType(GradientDrawable.RADIAL_GRADIENT);
                gradient.setGradientRadius(150);
                gradient.setColors(new int[] {getResources().getColor(android.R.color.holo_red_light), getResources().getColor(android.R.color.holo_red_dark)});
                //isRightTopOpen = true;
                return true;
            }
        });

    }

    private class StoveOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            LayerDrawable twoStove = (LayerDrawable) ((ImageButton)v).getDrawable();
            int pos = 0;
            switch (twoStove.getId(0)){
                case R.id.stove1:{
                    pos = 0;
                    break;
                }
                case R.id.innerStove1: {
                    pos = 1;
                    break;
                }
                case R.id.innerStove2:{
                    pos = 2;
                    break;
                }
                case R.id.stove2:{
                    pos = 3;
                    break;
                }
            }

            if(isOpen[pos]){
                GradientDrawable gradient = (GradientDrawable)twoStove.findDrawableByLayerId(twoStove.getId(0));
                gradient.setGradientType(GradientDrawable.RADIAL_GRADIENT);
                gradient.setGradientRadius(150);
                gradient.setColors(new int[] {getResources().getColor(android.R.color.transparent), getResources().getColor(android.R.color.transparent)});

                if(pos == 1 || pos == 2){
                    GradientDrawable gradient2 = (GradientDrawable)twoStove.findDrawableByLayerId(twoStove.getId(1));
                    gradient2.setGradientType(GradientDrawable.RADIAL_GRADIENT);
                    gradient2.setGradientRadius(150);
                    gradient2.setColors(new int[] {getResources().getColor(android.R.color.transparent), getResources().getColor(android.R.color.transparent)});
                }

                isOpen[pos] = false;
            }
            else{
                GradientDrawable gradient = (GradientDrawable)twoStove.findDrawableByLayerId(twoStove.getId(0));
                gradient.setGradientType(GradientDrawable.RADIAL_GRADIENT);
                gradient.setGradientRadius(150);
                gradient.setColors(new int[] {getResources().getColor(android.R.color.holo_red_light), getResources().getColor(android.R.color.holo_red_dark)});
                isOpen[pos] = true;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}


