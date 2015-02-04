package eu.telecom_bretagne.mutomatic;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import eu.telecom_bretagne.mutomatic.lib.EventPendingIntentMapping;
import eu.telecom_bretagne.mutomatic.service.SchedulerService;

/**
 * Created by Vincent on 03/02/2015.
 */
public class UIActivity extends Activity {

    //Button for refresh event which are displays
    private Button refresh = null;

    //Button to access settings
    private Button settings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Display the date of the day
        TextView textView = (TextView)findViewById(R.id.dateJour);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        textView.setText(sdf.format(new Date()));



        // Get the view from the layout
        LinearLayout layoutScroll = (LinearLayout)findViewById(R.id.layoutScroll);
        refresh = (Button)findViewById(R.id.refresh);
        settings = (Button)findViewById(R.id.settings);

        // Destruct all elements in the layout
        layoutScroll.removeAllViewsInLayout();

        // Test add layout
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        int i;

        //TODO A revoir
        for(i=0;i<=9;i++) {
            LinkedList<EventPendingIntentMapping> task = SchedulerService.getScheduledTasks();
            if(task != null && SchedulerService.isFinished) {
                break;
            }
            SystemClock.sleep(1000);
        }

        if(i == 10) {
            Toast toast = Toast.makeText(this, "Le service a mis trop de temps à démarrer", Toast.LENGTH_SHORT);
            toast.show();
            System.exit(1);
        }

        for (EventPendingIntentMapping task : SchedulerService.getScheduledTasks()) {
            String title = task.getEvent().getTitle();
            long hD = task.getEvent().getDtStart();
            long hF = task.getEvent().getDtEnd();

            TextView nomTitre = new TextView(this);
            nomTitre.setText(title);

            TextView start = new TextView(this);
            start.setText(" | Début : ");
            TextView hStart = new TextView(this);
            hStart.setText(time.format(hD));

            TextView end = new TextView(this);
            end.setText(" | Fin : ");
            TextView hEnd = new TextView(this);
            hEnd.setText(time.format(hF));

            LinearLayout displayEvent=new LinearLayout(this);
            displayEvent.setOrientation(LinearLayout.HORIZONTAL);
            // Set the color of the calendar from which the event is taken
            //task.getEvent().
            //displayEvent.setBackgroundColor(Color.RED);

            ((LinearLayout) findViewById(R.id.layoutScroll)).addView(displayEvent);

            displayEvent.addView(nomTitre);
            displayEvent.addView(start);
            displayEvent.addView(hStart);
            displayEvent.addView(end);
            displayEvent.addView(hEnd);
        }

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
