package eu.telecom_bretagne.mutomatic.lib;

import android.provider.CalendarContract;

/**
 * Created by math on 22/01/15.
 */
public class Calendar {

    public static final String[] PROJECTION = new String[] {
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.CALENDAR_COLOR
    };

    public static final int ID_INDEX = 0;
    public static final int NAME_INDEX = 1;
    public static final int CALENDAR_COLOR_INDEX = 2;


    private int id;
    private String name;
    private int calendarColor;

    public Calendar(int id, String name, int calendarColor) {
        this.id = id;
        this.name = name;
        this.calendarColor = calendarColor;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int calendarColor() {
        return calendarColor;
    }
}
