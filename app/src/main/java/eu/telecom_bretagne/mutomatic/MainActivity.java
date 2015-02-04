package eu.telecom_bretagne.mutomatic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import eu.telecom_bretagne.mutomatic.lib.CalendarWrapper;
import eu.telecom_bretagne.mutomatic.lib.Event;
import eu.telecom_bretagne.mutomatic.service.SchedulerService;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Log.i("MainActivity", "Service should be started");

        /*CalendarWrapper cw = new CalendarWrapper(getContentResolver());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        try {
            date = sdf.parse("22/01/2015");
        } catch(Exception e) {}

        LinkedList<Event> events = cw.getEvents(date.getTime());

        Log.d("Main Activity","Voici le premier event : "+events.get(0).getId());
        Log.d("Main Activity","Sa date de début : "+events.get(0).getDtStart());
        Log.d("Main Activity","Son titre : "+events.get(0).getTitle());
        Log.d("Main Activity","Sa description : "+events.get(0).getDescription());*/

        startService(new Intent(this, SchedulerService.class));
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
