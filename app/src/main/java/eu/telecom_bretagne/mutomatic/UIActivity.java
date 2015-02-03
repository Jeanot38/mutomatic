package eu.telecom_bretagne.mutomatic;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Display the date of the day
        TextView textView = (TextView)findViewById(R.id.dateJour);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        textView.setText(sdf.format(new Date()));



        // Add component to the layout
        LinearLayout layoutScroll = (LinearLayout)findViewById(R.id.layoutScroll);

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

            TextView titre = new TextView(this);
            titre.setText(title);

            TextView hStart = new TextView(this);
            hStart.setText(time.format(hD));

            TextView hEnd = new TextView(this);
            hEnd.setText(time.format(hF));

            layoutScroll.addView(titre);
            layoutScroll.addView(hStart);
            layoutScroll.addView(hEnd);
        }
    }
}
