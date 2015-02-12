package eu.telecom_bretagne.mutomatic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import eu.telecom_bretagne.mutomatic.lib.Calendar;
import eu.telecom_bretagne.mutomatic.lib.CalendarWrapper;
import eu.telecom_bretagne.mutomatic.lib.Parameters;
import eu.telecom_bretagne.mutomatic.services.SchedulerService;

/**
 * Created by Vincent on 04/02/2015.
 */
public class SettingsActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        Parameters.configurePreferences(getApplicationContext());



        // Get the view from the layout
        ImageButton goBack = (ImageButton)findViewById(R.id.goBack);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Switch switchService = (Switch)findViewById(R.id.switchService);

        switchService.setChecked(Parameters.getBooleanPreference(Parameters.APPLICATION_ENABLED));

        switchService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Parameters.setPreference(Parameters.APPLICATION_ENABLED, isChecked);
                startService(new Intent(getApplicationContext(), SchedulerService.class));
            }
        });

        RadioGroup checkProfile= (RadioGroup)findViewById(R.id.checkProfile);
        if (Parameters.getIntPreference(Parameters.PROFILE_SELECTED)==AudioManager.RINGER_MODE_SILENT) {
            checkProfile.check(R.id.checkSilent);
        }else if(Parameters.getIntPreference(Parameters.PROFILE_SELECTED)==AudioManager.RINGER_MODE_VIBRATE){
            checkProfile.check(R.id.checkVibrate);
        }

        checkProfile.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId==R.id.checkSilent) {
                    Parameters.setPreference(Parameters.PROFILE_SELECTED, AudioManager.RINGER_MODE_SILENT);
                }else if(checkedId==R.id.checkVibrate){
                    Parameters.setPreference(Parameters.PROFILE_SELECTED, AudioManager.RINGER_MODE_VIBRATE);
                }
            }
        });

        LinearLayout layoutScrollCalendar = (LinearLayout)findViewById(R.id.layoutScrollCalendar);
        LinkedList<Calendar> calendars = new LinkedList<>();
        CalendarWrapper calendarWrapper = new CalendarWrapper(getContentResolver());
        calendars= calendarWrapper.getCalendars();

        final LinkedList <Integer> checkBoxIds = new LinkedList();

        if(calendars.size() == 0) {
            TextView eventInfo = new TextView(getApplicationContext());
            eventInfo.setText("Aucun calendrier n'a été trouvé.");
            eventInfo.setTextColor(Color.BLACK);

            LinearLayout displayEvent=new LinearLayout(getApplicationContext());
            displayEvent.addView(eventInfo);

            layoutScrollCalendar.addView(displayEvent);

        } else {

            Set <Integer> idCalendarsSelected = Parameters.getIntPreferenceSet(Parameters.CALENDAR_SELECTED);

            for (Calendar calendar : calendars) {
                String calendarName = calendar.getName();
                CheckBox calendarCheckbox = new CheckBox(getApplicationContext());
                calendarCheckbox.setText(calendarName);
                calendarCheckbox.setTextColor(Color.BLACK);
                calendarCheckbox.setId(calendar.hashCode());

                if (idCalendarsSelected.contains(calendar.getId())){
                    calendarCheckbox.setChecked(true);
                }

                checkBoxIds.add(new Integer(calendarCheckbox.getId()));

                LinearLayout displayCalendar = new LinearLayout(getApplicationContext());
                displayCalendar.setOrientation(LinearLayout.VERTICAL);

                displayCalendar.addView(calendarCheckbox);
                layoutScrollCalendar.addView(displayCalendar);


                calendarCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        HashSet<Integer> calendarSelected = new HashSet<Integer>();
                        CalendarWrapper calendarWrapper = new CalendarWrapper(getContentResolver());
                        LinkedList<Calendar> calendars = calendarWrapper.getCalendars();

                        for (Calendar calendar : calendars) {
                            CheckBox checkbox = (CheckBox) findViewById(calendar.hashCode());

                            if (checkbox.isChecked()) {
                                calendarSelected.add(calendar.getId());
                            }
                        }

                        if(Parameters.getIntPreferenceSet(Parameters.CALENDAR_SELECTED).size() == 0 && calendarSelected.size() > 0) {
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction(MainActivity.ResponseReceiver.END_SCHEDULER_PROCESS);
                            sendBroadcast(broadcastIntent);
                        }

                        Parameters.setPreference(Parameters.CALENDAR_SELECTED,calendarSelected);

                        if(calendarSelected.size() == 0) {
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction(MainActivity.ResponseReceiver.END_SCHEDULER_PROCESS);
                            sendBroadcast(broadcastIntent);
                        }
                    }
                });
            }

            for (Calendar calendar : calendars){
                String calendarName = calendar.getName();
                CheckBox calendarCheckbox = new CheckBox(getApplicationContext());
                calendarCheckbox.setText(calendarName);
            }

        }
    }

    private static class SpinnerServiceIntervalMapping {

        private static String[] spinnerDescriptions = {"30 s", "1 min", "2 min", "5 min", "15 min", "30 min", "1 h"};
        private static Integer[] spinnerValues = {30, 60, 120, 300, 900, 1800, 3600};

        public static String[] getSpinnerDescriptions() {
            return spinnerDescriptions;
        }

        public static Integer[] getSpinnerValues() {
            return spinnerValues;
        }

        public static Integer getSpinnerValuesFromDescription(String spinnerValue) {
            Integer indexOfSpinner = null;
            for (int i = 0; i < spinnerDescriptions.length; i++) {
                if (spinnerDescriptions[i].equals(spinnerValue)) {
                    indexOfSpinner = i;
                    break;
                }
            }

            if (indexOfSpinner != null) {
                return spinnerValues[indexOfSpinner];
            }

            return null;
        }

        public static int getDefaultSpinnerPosition() {

            Integer serviceIntervalParameter = Parameters.getIntPreference(Parameters.SCHEDULING_INTERVAL);

            if (serviceIntervalParameter != null) {
                for (int i = 0; i < spinnerValues.length; i++) {
                    if (spinnerValues[i].equals(serviceIntervalParameter)) {
                        return i;
                    }
                }
            }

            return -1;
        }
    }

}
