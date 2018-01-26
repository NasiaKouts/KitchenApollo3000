package aueb.nasia_kouts.gr.kitchenapollo;

import android.content.Intent;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.mikepenz.iconics.context.IconicsLayoutInflater2;

public class WholeKitchen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whole_kitchen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    public void openStoveActivity(View view) {
        Intent openStoveIntent = new Intent(this, Stove.class);
        startActivity(openStoveIntent);
    }

    public void openOvenActivity(View view) {
    }
}
