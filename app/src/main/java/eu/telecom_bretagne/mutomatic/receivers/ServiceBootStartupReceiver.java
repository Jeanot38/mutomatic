package eu.telecom_bretagne.mutomatic.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import eu.telecom_bretagne.mutomatic.services.SchedulerService;

/**
 * Created by math on 08/02/15.
 */
public class ServiceBootStartupReceiver extends BroadcastReceiver {

    @Override

    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() == "android.intent.action.BOOT_COMPLETED") {
            Intent serviceIntent = new Intent(context, SchedulerService.class);
            context.startService(serviceIntent);
        }
    }
}
