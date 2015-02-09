package eu.telecom_bretagne.mutomatic.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import eu.telecom_bretagne.mutomatic.MainActivity;
import eu.telecom_bretagne.mutomatic.lib.AudioWrapper;
import eu.telecom_bretagne.mutomatic.lib.Event;
import eu.telecom_bretagne.mutomatic.lib.EventPendingIntentMapping;
import eu.telecom_bretagne.mutomatic.services.SchedulerService;

/**
 * Created by math on 23/01/15.
 */
public class AudioReceiver extends BroadcastReceiver {

    public static final String AUDIO_WRAPPER_PREVIOUS_PROFILE = "eu.telecom_bretagne.mutomatic.audio_wrapper_previous_profile";
    public static final String EVENT = "eu.telecom_bretagne.mutomatic.event";

    @Override
    public void onReceive(Context context, Intent intent) {

        AudioWrapper audioWrapper = new AudioWrapper(context);

        Bundle bundle = intent.getExtras();
        Integer previousProfile = bundle.getInt(AUDIO_WRAPPER_PREVIOUS_PROFILE, -255);
        Event event = (Event) bundle.getSerializable(EVENT);

        if(previousProfile < 0 && intent.getAction().contains("dtStart")) {
            Integer currentSettings = audioWrapper.getCurrentSettings();
            audioWrapper.autoSettings();

            AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent i = new Intent(context, AudioReceiver.class);
            i.setAction("dtEnd_"+event.getId()+event.getDtEnd());
            i.putExtra(AUDIO_WRAPPER_PREVIOUS_PROFILE, currentSettings);
            i.putExtra(EVENT, event);
            PendingIntent pi=PendingIntent.getBroadcast(context, 0, i, 0);

            alarmManager.set(AlarmManager.RTC_WAKEUP, event.getDtEnd(), pi);

            EventPendingIntentMapping epim = getEpimInstanceInList(new EventPendingIntentMapping(event));

            if(epim != null) {
                epim.setPiEnd(pi);
            }

        } else if(previousProfile >= 0 && intent.getAction().contains("dtEnd")) {
            audioWrapper.setPreviousRingerMode(previousProfile);
            audioWrapper.restoreSettings();

            if(SchedulerService.getScheduledTasks() != null) {

                SchedulerService.getScheduledTasks().remove(new EventPendingIntentMapping(event));
            }

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(MainActivity.ResponseReceiver.END_SCHEDULER_PROCESS);
            context.sendBroadcast(broadcastIntent);
        }

    }

    private EventPendingIntentMapping getEpimInstanceInList(EventPendingIntentMapping epimSearched) {
        if(SchedulerService.getScheduledTasks() != null && SchedulerService.getScheduledTasks().contains(epimSearched)) {
            int indexOfEpimSearched = SchedulerService.getScheduledTasks().indexOf(epimSearched);
            return SchedulerService.getScheduledTasks().get(indexOfEpimSearched);
        } else {
            return null;
        }
    }
}
