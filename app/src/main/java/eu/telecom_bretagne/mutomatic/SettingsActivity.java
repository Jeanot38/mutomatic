package eu.telecom_bretagne.mutomatic;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.LinkedList;

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

        LinkedList<Calendar> calendars;
        CalendarWrapper calendarWrapper = new CalendarWrapper(getContentResolver());
        calendars= calendarWrapper.getCalendars();
        for (Calendar calendar : calendars){
            String calendarName = calendar.getName();
            CheckBox calendarCheckbox = new CheckBox(getApplicationContext());
            calendarCheckbox.setText(calendarName);
        }

        Spinner spinnerServiceInterval = (Spinner) findViewById(R.id.spinnerServiceInterval);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SpinnerServiceIntervalMapping.getSpinnerDescriptions());

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerServiceInterval.setAdapter(spinnerAdapter);
        spinnerServiceInterval.setSelection(SpinnerServiceIntervalMapping.getDefaultSpinnerPosition());

        spinnerServiceInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Integer parameterValue = SpinnerServiceIntervalMapping.getSpinnerValuesFromDescription((String) parent.getItemAtPosition(position));
                if(parameterValue != null) {
                    Parameters.setPreference(Parameters.SCHEDULING_INTERVAL, parameterValue);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
            for(int i=0 ; i < spinnerDescriptions.length ; i++) {
                if(spinnerDescriptions[i].equals(spinnerValue)) {
                    indexOfSpinner = i;
                    break;
                }
            }

            if(indexOfSpinner != null) {
                return spinnerValues[indexOfSpinner];
            }

            return null;
        }

        public static int getDefaultSpinnerPosition() {

            Integer serviceIntervalParameter = Parameters.getIntPreference(Parameters.SCHEDULING_INTERVAL);

            if(serviceIntervalParameter != null) {
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
