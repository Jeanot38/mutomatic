package eu.telecom_bretagne.mutomatic.service;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by math on 21/01/15.
 */
public class SchedulerService extends IntentService {


    public SchedulerService() {
        super("SchedulerService");
    }


    /* Use for testing well launched application
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("Scheduler", "Service is started");
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {
                Context context = getApplicationContext();
                CharSequence text = "Hello menn !!!! ";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });

    }*/

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
