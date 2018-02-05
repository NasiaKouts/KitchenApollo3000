package aueb.nasia_kouts.gr.kitchenapollo;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class StoveButton extends android.support.v7.widget.AppCompatImageButton {
    private int position;
    private int heatLevel;
    private boolean isOpened;
    private boolean isBigStoveOpened;
    private View controls;

    public StoveButton(Context context) {
        super(context, null);
    }

    public StoveButton(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.imageButtonStyle);
    }

    public StoveButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String getStringFromStove(int pos, boolean opened, boolean bigOpened){
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

    public boolean containsBigStove(){
        return position == 1 || position == 2;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getHeatLevel() {
        return heatLevel;
    }

    public void setHeatLevel(int heatLevel) {
        this.heatLevel = heatLevel;

        TextView textView = getControls().findViewById(R.id.heat_level);
        textView.setText("" + heatLevel);
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;

        if(isOpened){
            LayerDrawable layer = (LayerDrawable) getDrawable();

            int firstItem = 0;
            if(containsBigStove()) firstItem = 1;

            GradientDrawable gradient = (GradientDrawable)layer.findDrawableByLayerId(layer.getId(firstItem));
            gradient.setGradientType(GradientDrawable.RADIAL_GRADIENT);
            gradient.setGradientRadius(150);
            gradient.setColors(new int[] {getResources().getColor(android.R.color.holo_red_light), getResources().getColor(android.R.color.holo_red_dark)});
        }
        else{
            LayerDrawable layer = (LayerDrawable) getDrawable();

            int firstItem = 0;
            if(containsBigStove()) firstItem = 1;

            GradientDrawable gradient = (GradientDrawable)layer.findDrawableByLayerId(layer.getId(firstItem));
            gradient.setGradientType(GradientDrawable.RADIAL_GRADIENT);
            gradient.setGradientRadius(150);
            gradient.setColors(new int[] {getResources().getColor(android.R.color.transparent), getResources().getColor(android.R.color.transparent)});

            if(containsBigStove()){
                GradientDrawable gradient2 = (GradientDrawable)layer.findDrawableByLayerId(layer.getId(0));
                gradient2.setGradientType(GradientDrawable.RADIAL_GRADIENT);
                gradient2.setGradientRadius(150);
                gradient2.setColors(new int[] {getResources().getColor(android.R.color.transparent), getResources().getColor(android.R.color.transparent)});
            }

            if(containsBigStove()){
                isBigStoveOpened = false;
            }
        }
    }

    public boolean isBigStoveOpened() {
        return isBigStoveOpened;
    }

    public void setBigStoveOpened(boolean bigStoveOpened) {
        isBigStoveOpened = bigStoveOpened;

        if(isBigStoveOpened){
            if(!containsBigStove()) return;

            LayerDrawable layer = (LayerDrawable) getDrawable();

            GradientDrawable gradient = (GradientDrawable)layer.findDrawableByLayerId(layer.getId(0));
            gradient.setGradientType(GradientDrawable.RADIAL_GRADIENT);
            gradient.setGradientRadius(150);
            gradient.setColors(new int[] {getResources().getColor(android.R.color.holo_red_light), getResources().getColor(android.R.color.holo_red_dark)});
            isOpened = true;
        }
    }

    public View getControls() {
        return controls;
    }

    public void setControls(View controls) {
        this.controls = controls;
    }
}
