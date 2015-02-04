package eu.telecom_bretagne.mutomatic;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import eu.telecom_bretagne.mutomatic.lib.EventPendingIntentMapping;
import eu.telecom_bretagne.mutomatic.lib.Parameters;
import eu.telecom_bretagne.mutomatic.service.SchedulerService;


public class MainActivity extends Activity {

    private ResponseReceiver receiver;

    //Button for refresh event which are displays
    private Button refresh = null;

    //Button to access settings
    private Button settings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parameters.configurePreferences(getApplicationContext());

        Parameters.setPreference(Parameters.ENABLED, true);
        Parameters.setPreference(Parameters.SCHEDULING_INTERVAL, 30);

        IntentFilter filter = new IntentFilter(ResponseReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);

        TextView textView = (TextView)findViewById(R.id.dateJour);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        textView.setText(sdf.format(new Date()));



        // Get the view from the layout
        refresh = (Button)findViewById(R.id.refresh);
        settings = (Button)findViewById(R.id.settings);

        // Service start
        startService(new Intent(this, SchedulerService.class));

    }

    @Override
    public void onDestroy() {
        this.unregisterReceiver(receiver);
        super.onDestroy();
    }

    public class ResponseReceiver extends BroadcastReceiver
    {
        public static final String PROCESS_RESPONSE = "eu.telecom_bretagne.mutomatic.process_response";

        @Override
        public void onReceive(Context context, Intent intent) {

            LinearLayout layoutScroll = (LinearLayout)findViewById(R.id.layoutScroll);

            // Destruct all elements in the layout
            layoutScroll.removeAllViewsInLayout();

            // Test add layout
            SimpleDateFormat time = new SimpleDateFormat("HH:mm");

            for (EventPendingIntentMapping task : SchedulerService.getScheduledTasks()) {
                String title = task.getEvent().getTitle();
                long hD = task.getEvent().getDtStart();
                long hF = task.getEvent().getDtEnd();

                TextView nomTitre = new TextView(getApplicationContext());
                nomTitre.setText(title);

                TextView start = new TextView(getApplicationContext());
                start.setText(" | Début : ");
                TextView hStart = new TextView(getApplicationContext());
                hStart.setText(time.format(hD));

                TextView end = new TextView(getApplicationContext());
                end.setText(" | Fin : ");
                TextView hEnd = new TextView(getApplicationContext());
                hEnd.setText(time.format(hF));

                LinearLayout displayEvent = new LinearLayout(getApplicationContext());
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

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
