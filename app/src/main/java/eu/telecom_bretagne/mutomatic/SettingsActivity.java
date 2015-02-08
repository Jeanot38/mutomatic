package eu.telecom_bretagne.mutomatic;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioGroup;
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

        LinkedList<Calendar> calendars = new LinkedList<>();
        CalendarWrapper calendarWrapper = new CalendarWrapper(getContentResolver());
        calendars= calendarWrapper.getCalendars();
        for (Calendar calendar : calendars){
            String calendarName = calendar.getName();
            CheckBox calendarCheckbox = new CheckBox(getApplicationContext());
            calendarCheckbox.setText(calendarName);
        }
    }


}
