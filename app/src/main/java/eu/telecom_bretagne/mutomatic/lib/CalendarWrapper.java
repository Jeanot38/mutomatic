package eu.telecom_bretagne.mutomatic.lib;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.*;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by math on 22/01/15.
 */
public class CalendarWrapper {

    private ContentResolver contentResolver;
    private LinkedList <Calendar> calendars = null;
    private LinkedList <Event> events = null;

    public CalendarWrapper(ContentResolver cr) {
        this.contentResolver = cr;
    }

    public LinkedList <Calendar> getCalendars() {
        calendars = new LinkedList<>();
        Cursor cur;
        Uri uri = Calendars.CONTENT_URI;
        cur = contentResolver.query(uri, Calendar.PROJECTION, null, null, null);

        while(cur.moveToNext()) {
            calendars.add(new Calendar(cur.getInt(Calendar.ID_INDEX), cur.getString(Calendar.NAME_INDEX), cur.getInt(Calendar.CALENDAR_COLOR_INDEX)));
        }

        return calendars;
    }

    public LinkedList <Event> getEvents(Long day, Integer[] calendarIdToSelect) {
        events = new LinkedList<>();
        Cursor cur;
        Uri uri = Events.CONTENT_URI;
        String selection = "";
        String[] arguments = null;
        ArrayList<String> argumentsCalendar;

        if (day != null) {
            selection = "((" + Events.DTEND + " >= ?) AND (" + Events.DTEND + " < ?)) ";
            arguments = new String[] {Long.toString(day), Long.toString(day+3600*1000*24)};
        }

        if(calendarIdToSelect != null) {
            argumentsCalendar = new ArrayList<>();

            if(day != null) {
                selection += "AND ";
                argumentsCalendar.add(arguments[0]);
                argumentsCalendar.add(arguments[1]);
            }

            for(Integer idCalendar : calendarIdToSelect) {
                selection += Events.CALENDAR_ID+" = ? AND ";
                argumentsCalendar.add(Integer.toString(idCalendar));
            }

            selection = selection.substring(0, selection.length()-4);
            arguments = new String[argumentsCalendar.size()];
            argumentsCalendar.toArray(arguments);
        }

        cur = contentResolver.query(uri, Event.PROJECTION, selection, arguments, null);

        while(cur.moveToNext()) {
            events.add(new Event(cur.getInt(Event.ID_INDEX), cur.getLong(Event.DTSTART_INDEX), cur.getLong(Event.DTEND_INDEX), cur.getString(Event.TITLE_INDEX), cur.getString(Event.DESCRIPTION_INDEX), cur.getInt(Event.AVAILABILITY_INDEX)));
        }

        cur.close();

        return events;
    }

}
