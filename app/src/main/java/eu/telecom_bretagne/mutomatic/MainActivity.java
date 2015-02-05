package eu.telecom_bretagne.mutomatic;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import eu.telecom_bretagne.mutomatic.lib.EventPendingIntentMapping;
import eu.telecom_bretagne.mutomatic.lib.Parameters;
import eu.telecom_bretagne.mutomatic.service.SchedulerService;


public class MainActivity extends Activity {

    private ResponseReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parameters.configurePreferences(getApplicationContext());

        if(Parameters.getBooleanPreference(Parameters.APPLICATION_ENABLED) == null) Parameters.setPreference(Parameters.APPLICATION_ENABLED, true);
        if(Parameters.getIntPreference(Parameters.SCHEDULING_INTERVAL) == null) Parameters.setPreference(Parameters.SCHEDULING_INTERVAL, 60);
        if(Parameters.getIntPreference(Parameters.PROFILE_SELECTED) == null) Parameters.setPreference(Parameters.PROFILE_SELECTED, AudioManager.RINGER_MODE_SILENT);

        IntentFilter filter = new IntentFilter(ResponseReceiver.END_SCHEDULER_PROCESS);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);

        TextView textView = (TextView)findViewById(R.id.dateJour);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        textView.setText(sdf.format(new Date()));

        // Get the view from the layout
        ImageButton refresh = (ImageButton)findViewById(R.id.refresh);
        ImageButton settings = (ImageButton)findViewById(R.id.settings);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(getApplicationContext(), SchedulerService.class));
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

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
        public static final String END_SCHEDULER_PROCESS = "eu.telecom_bretagne.mutomatic.process_response";

        @Override
        public void onReceive(Context context, Intent intent) {

            LinearLayout layoutScroll = (LinearLayout)findViewById(R.id.layoutScroll);

            // Destruct all elements in the layout
            layoutScroll.removeAllViewsInLayout();

            // Test add layout
            SimpleDateFormat time = new SimpleDateFormat("HH:mm");

            CopyOnWriteArrayList<EventPendingIntentMapping> tasks = SchedulerService.getScheduledTasks();
            LinearLayout.LayoutParams parameters = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams marginStart = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            LinearLayout.LayoutParams paramEventName = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramEventName.setMargins(15, 15, 0, 0); // paramEventName.setMargins(left, top, right, bottom);
            LinearLayout.LayoutParams paramEventInfo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramEventInfo.setMargins(15, 0, 0, 15); // paramEventInfo.setMargins(left, top, right, bottom);
            LinearLayout.LayoutParams paramDiplayEvent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            paramDiplayEvent.setMargins(0, 0, 0, 15); // paramDiplayEvent.setMargins(left, top, right, bottom);



            //TODO A revérifier
            if(tasks == null) {
                TextView eventInfo = new TextView(getApplicationContext());
                eventInfo.setText("Le service est désactivé.");
                eventInfo.setTextColor(Color.BLACK);

                LinearLayout displayEvent=new LinearLayout(getApplicationContext());
                displayEvent.addView(eventInfo);

                layoutScroll.addView(displayEvent);
            } else if(tasks.size() == 0) {
                TextView eventInfo = new TextView(getApplicationContext());
                eventInfo.setText("Aucun événement n'a été planifié.");
                eventInfo.setTextColor(Color.BLACK);

                LinearLayout displayEvent=new LinearLayout(getApplicationContext());
                displayEvent.addView(eventInfo);

                layoutScroll.addView(displayEvent);

            } else {

                for (EventPendingIntentMapping task : tasks) {
                    String title = task.getEvent().getTitle();
                    long hD = task.getEvent().getDtStart();
                    long hF = task.getEvent().getDtEnd();

                    //Creation of the dynamic TextView
                    TextView eventName = new TextView(getApplicationContext());
                    eventName.setText(title);
                    eventName.setTextColor(Color.WHITE);
                    eventName.setTypeface(null, Typeface.BOLD);
                    eventName.setLayoutParams(paramEventName);

                    TextView eventInfo = new TextView(getApplicationContext());
                    eventInfo.setText(time.format(hD) + " - " + time.format(hF));
                    eventInfo.setTextColor(Color.WHITE);
                    eventInfo.setLayoutParams(paramEventInfo);


                    //Creation of the title layout
                    LinearLayout displayEvent = new LinearLayout(getApplicationContext());
                    displayEvent.setOrientation(LinearLayout.VERTICAL);
                    displayEvent.setLayoutParams(paramDiplayEvent);

                    //Link the TextView with the layout
                    displayEvent.addView(eventName);
                    displayEvent.addView(eventInfo);

                    // Set the color of the calendar from which the event is taken
                    displayEvent.setBackgroundColor(task.getEvent().getCalendarColor());

                    //Display views
                    layoutScroll.addView(displayEvent);
                }
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
