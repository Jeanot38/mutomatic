package eu.telecom_bretagne.mutomatic;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;

/**
 * Created by Vincent on 04/02/2015.
 */
public class SettingsActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        // Get the view from the layout
        ImageButton refresh = (ImageButton)findViewById(R.id.refresh);
        ImageButton settings = (ImageButton)findViewById(R.id.settings);
    }


}
