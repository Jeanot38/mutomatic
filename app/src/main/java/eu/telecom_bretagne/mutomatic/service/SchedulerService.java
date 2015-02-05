package eu.telecom_bretagne.mutomatic.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import eu.telecom_bretagne.mutomatic.MainActivity;
import eu.telecom_bretagne.mutomatic.lib.AudioReceiver;
import eu.telecom_bretagne.mutomatic.lib.Calendar;
import eu.telecom_bretagne.mutomatic.lib.CalendarWrapper;
import eu.telecom_bretagne.mutomatic.lib.Event;
import eu.telecom_bretagne.mutomatic.lib.EventPendingIntentMapping;
import eu.telecom_bretagne.mutomatic.lib.Parameters;

/**
 * Created by math on 21/01/15.
 */
public class SchedulerService extends IntentService {

    private static CopyOnWriteArrayList<EventPendingIntentMapping> scheduledTasks = null;

    public SchedulerService() {
        super("SchedulerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("SchedulerService", "Launching service...");

        Parameters.configurePreferences(getApplicationContext());

        if(Parameters.getBooleanPreference(Parameters.APPLICATION_ENABLED) == true) {

            /*Handler handler = new Handler(Looper.getMainLooper());

            handler.post(new Runnable() {

                @Override
                public void run() {
                    Context context = getApplicationContext();
                    CharSequence text = "Checking Calendar modifications...";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });*/


            Date today = new Date();
            CalendarWrapper calendarWrapper = new CalendarWrapper(getContentResolver());

            //TODO Take care of this part when options will be develop

            ArrayList<Integer> calendarToUse = new ArrayList<>();
            Integer [] calendarsToUseArray;

            for(Calendar calendar : calendarWrapper.getCalendars()) {
                if(calendar.getName() != null && ! calendar.getName().equals("Jours fériés en France") && ! calendar.getName().equals("Numéros de semaine") && ! calendar.getName().equals("Anniversaires")) {
                    calendarToUse.add(calendar.getId());
                }
            }

            calendarsToUseArray = new Integer[calendarToUse.size()];
            calendarToUse.toArray(calendarsToUseArray);

            //When service is called for the first time

            if (scheduledTasks == null) {

                scheduledTasks = new CopyOnWriteArrayList<>();

                for (Event event : calendarWrapper.getEvents(today.getTime(), calendarsToUseArray)) {
                    EventPendingIntentMapping epim = new EventPendingIntentMapping(event);
                    PendingIntent pi = this.scheduleProfileChange(event);
                    epim.setPiStart(pi);
                    scheduledTasks.add(epim);

                }
            } else {
                int counterScheduledTasks = 0;

                // On parcourt l'ensemble des événements du calendrier pour que les tâches planifiées associées soient crées et qu'on supprime celles dont les événements ont été supprimées
                for (Event event : calendarWrapper.getEvents(today.getTime(), calendarsToUseArray)) {

                    // Pour éviter les IndexOutOfBoundException et permet de repérer la fin de la liste scheduledTasks. Le else représente la fin de la liste, on ajoute donc tous les événements restants
                    if (counterScheduledTasks < scheduledTasks.size()) {

                        EventPendingIntentMapping epim = scheduledTasks.get(counterScheduledTasks);

                        //On ajoute les événements qui ne sont pas présents dans les tâches planifiées

                        if (!event.equals(epim.getEvent())) {
                            PendingIntent pi = scheduleProfileChange(event);
                            scheduledTasks.add(counterScheduledTasks, new EventPendingIntentMapping(event, pi));

                            // Si le taskScheduled à une date de début inférieure à l'event en cours, c'est qu'il a été supprimé

                            if (event.getDtStart() > epim.getEvent().getDtStart()) {
                                cancelProfileChange(epim);
                                scheduledTasks.remove(epim);
                            }
                        }
                    } else {
                        PendingIntent pi = scheduleProfileChange(event);
                        scheduledTasks.add(new EventPendingIntentMapping(event, pi));
                    }

                    counterScheduledTasks++;
                }

                // Pour supprimer toutes les tâches en fin de liste scheduledTasks qui ont été supprimées dans le calendrier

                for (; counterScheduledTasks < scheduledTasks.size(); counterScheduledTasks++) {
                    cancelProfileChange(scheduledTasks.get(counterScheduledTasks));
                    scheduledTasks.remove(counterScheduledTasks);
                }

            }
        } else {
            if(scheduledTasks != null && scheduledTasks.size() > 0) {
                for (EventPendingIntentMapping epim : scheduledTasks) {
                    cancelProfileChange(epim);
                }
                scheduledTasks = null;
            }
        }



        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.ResponseReceiver.END_SCHEDULER_PROCESS);
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onDestroy() {

        if(Parameters.getBooleanPreference(Parameters.APPLICATION_ENABLED) == true) {
            AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarm.set(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + (1000 * Parameters.getIntPreference(Parameters.SCHEDULING_INTERVAL)),
                    PendingIntent.getService(this, 0, new Intent(this, SchedulerService.class), 0)
            );
        }
    }

    private PendingIntent scheduleProfileChange(Event event) {

        Context context = getApplicationContext();
        AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent=new Intent(context, AudioReceiver.class);
        intent.setAction("dtStart_"+event.getId()+event.getDtStart());
        intent.putExtra(AudioReceiver.EVENT, event);
        PendingIntent pi=PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmManager.set(AlarmManager.RTC_WAKEUP, event.getDtStart(), pi);

        return pi;

    }

    private void cancelProfileChange(EventPendingIntentMapping epim) {
        Context context = getApplicationContext();
        AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(epim.getPiStart() != null) {
            alarmManager.cancel(epim.getPiStart());
        }

        if(epim.getPiEnd() != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), epim.getPiEnd());
        }
    }

    public static CopyOnWriteArrayList<EventPendingIntentMapping> getScheduledTasks() {
        return scheduledTasks;
    }
}
