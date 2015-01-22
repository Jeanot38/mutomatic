package eu.telecom_bretagne.mutomatic.lib;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.*;

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
        Cursor cur = null;
        Uri uri = Calendars.CONTENT_URI;
        /*String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND ("
                + Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[] {"sampleuser@gmail.com", "com.google",
                "sampleuser@gmail.com"};*/
// Submit the query and get a Cursor object back.
        cur = contentResolver.query(uri, Calendar.PROJECTION, null, null, null);

        while(cur.moveToNext()) {
            calendars.add(new Calendar(cur.getInt(Calendar.ID_INDEX), cur.getString(Calendar.NAME_INDEX), cur.getInt(Calendar.CALENDAR_COLOR_INDEX)));
        }

        return calendars;
    }

    public LinkedList <Event> getEvents(Long day) {
        events = new LinkedList<>();
        Cursor cur = null;
        Uri uri = Events.CONTENT_URI;
        String selection = "";
        String[] arguments = null;

        if (day != null) {
            selection = "((" + Events.DTSTART + " >= ?) AND (" + Events.DTEND + " < ?))";
            arguments = new String[] {Long.toString(day), Long.toString(day+3600*1000*24)};
        }

        cur = contentResolver.query(uri, Event.PROJECTION, selection, arguments, null);

        while(cur.moveToNext()) {
            events.add(new Event(cur.getInt(Event.ID_INDEX), cur.getLong(Event.DTSTART_INDEX), cur.getLong(Event.DTEND_INDEX), cur.getString(Event.TITLE_INDEX), cur.getString(Event.DESCRIPTION_INDEX), cur.getInt(Event.AVAILABILITY_INDEX)));
        }

        return events;
    }
}
