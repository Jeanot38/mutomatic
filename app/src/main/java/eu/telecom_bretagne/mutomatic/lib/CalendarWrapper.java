package eu.telecom_bretagne.mutomatic.lib;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;

/**
 * This class provides simple methods to get a list of Calendars and events created in Google Calendar application.
 * @author Mathis GAVILLON
 */
public class CalendarWrapper {

    /**
     * The {@link ContentResolver} object used to query informations from Google Calendar application
     */

    private ContentResolver contentResolver;
    //private LinkedList <Calendar> calendars = null;
    //private LinkedList <Event> events = null;

    /**
     * Create an {@link CalendarWrapper} object and set the {@link ContentResolver} object as attribut of the object.
     * @param cr
     */

    public CalendarWrapper(ContentResolver cr) {
        this.contentResolver = cr;
    }

    /**
     * Allow to get a list of all calendars set up in Google Calendar application.
     * @return A list of {@link Calendar} objects after having been instantiated.
     */

    public LinkedList <Calendar> getCalendars() {

        LinkedList<Calendar> calendars = new LinkedList<>();

        // The cursor used to get results from the Google Calendar database
        Cursor cur;

        // The URI of Calendars ressource
        Uri uri = Calendars.CONTENT_URI;

        // Get all calendars with defined field in Calendar class
        cur = contentResolver.query(uri, Calendar.PROJECTION, null, null, null);

        // Create Calendar objects and add the in the list
        while(cur.moveToNext()) {
            calendars.add(new Calendar(cur.getInt(Calendar.ID_INDEX), cur.getString(Calendar.NAME_INDEX), cur.getInt(Calendar.CALENDAR_COLOR_INDEX)));
        }

        cur.close();

        return calendars;
    }

    /**
     * Get a calendar from its id
     * @param id The id of the calendar you want to get
     * @return The calendar you want to get
     */

    public Calendar getCalendarById(Integer id) {
        // The cursor used to get results from the Google Calendar database
        Cursor cur;

        // The URI of Calendars ressource
        Uri uri = Calendars.CONTENT_URI;

        String selection = "("+Calendars._ID+" = ?)";
        String [] arguments = {Integer.toString(id)};

        // Get all calendars with defined field in Calendar class
        cur = contentResolver.query(uri, Calendar.PROJECTION, selection, arguments, null);
        cur.moveToFirst();

        return new Calendar(cur.getInt(Calendar.ID_INDEX), cur.getString(Calendar.NAME_INDEX), cur.getInt(Calendar.CALENDAR_COLOR_INDEX));

    }

    /**
     * Allow to get the list of all events from a certain list of Calendars defined in Google Calendar application,
     * for one day beggining at day parameter.
     *
     * @param now Represents the beginning time of the day.
     * @param calendarIdToSelect The Ids of the Calendars from which events will be returned.
     * @return The list of events after selecting them thanks to the parameters.
     */

    public LinkedList <Event> getEvents(Long now, Set<Integer> calendarIdToSelect) {

        LinkedList<Event> events = new LinkedList<>();
        java.util.Calendar calendarLib = java.util.Calendar.getInstance();
        Long nextDay = null;
        Long beginningOfDay = null;

        // The cursor used to get results from the Google Calendar database
        Cursor cur;

        // The URI of Events ressource
        Uri uri = Events.CONTENT_URI;

        // Used to filter results (WHERE clause in SQL query)
        String selection = "";

        // Values of WHERE clause conditions
        String[] arguments = null;

        //Only used if the id list of Calendars is provide. Required for dynamic values of calendarIdToSelect
        ArrayList<String> argumentsCalendar;

        if(calendarIdToSelect.size() == 0) {
            return events;
        }

        // In the case of providing the day parameter...
        if (now != null) {

            //Initialize beginningDay parameter which represent today at midnight
            calendarLib.setTimeInMillis(now);
            calendarLib.set(calendarLib.get(java.util.Calendar.YEAR), calendarLib.get(java.util.Calendar.MONTH), calendarLib.get(java.util.Calendar.DAY_OF_MONTH), 0, 0);
            beginningOfDay = calendarLib.getTimeInMillis();

            //Initialize nextDay parameter which represent the next day at midnight
            calendarLib.setTimeInMillis(now);
            calendarLib.add(java.util.Calendar.DATE, 1);
            calendarLib.set(calendarLib.get(java.util.Calendar.YEAR), calendarLib.get(java.util.Calendar.MONTH), calendarLib.get(java.util.Calendar.DAY_OF_MONTH), 0, 0);
            nextDay = calendarLib.getTimeInMillis();

            // ... add a WHERE condition
            selection = "((" + Events.ALL_DAY + " = 0 AND " + Events.DTEND + " >= ? AND " + Events.DTEND + " < ?) OR (" + Events.ALL_DAY + " = 1 AND " + Events.DTSTART + " >= ? AND " + Events.DTSTART + " < ?)) ";
            String beginningOfDayString = Long.toString(beginningOfDay);
            String nowString = Long.toString(now);
            String nextDayString = Long.toString(nextDay);

            arguments = new String[] {nowString, nextDayString, beginningOfDayString, nextDayString};
        }
        // In the case of providing calendarIdToSelect array
        if(calendarIdToSelect != null) {
            argumentsCalendar = new ArrayList<>();

            // Add the two first arguments corresponding to day parameter...
            if(now != null) {
                selection += "AND (";
                argumentsCalendar.add(arguments[0]);
                argumentsCalendar.add(arguments[1]);
                argumentsCalendar.add(arguments[2]);
                argumentsCalendar.add(arguments[3]);
            }

            // ... and the add a condition for each id of calendar provided

            for(Integer idCalendar : calendarIdToSelect) {
                selection += Events.CALENDAR_ID+" = ? OR ";
                argumentsCalendar.add(Integer.toString(idCalendar));
            }

            //To erase the last "AND " word
            selection = selection.substring(0, selection.length()-4);

            // Convert the list to an array used by the query method above
            if(now != null) {
                selection += ")";
            }

            arguments = new String[argumentsCalendar.size()];
            argumentsCalendar.toArray(arguments);
        }
        // Get the list of events filtered due to parameter defined above
        cur = contentResolver.query(uri, Event.PROJECTION, selection, arguments, Events.DTSTART);

        // Create Event objects and add to the list
        while(cur.moveToNext()) {
            events.add(new Event(cur.getInt(Event.ID_INDEX), cur.getLong(Event.DTSTART_INDEX), cur.getLong(Event.DTEND_INDEX), cur.getInt(Event.ALL_DAY_INDEX), cur.getString(Event.TITLE_INDEX), cur.getString(Event.DESCRIPTION_INDEX), cur.getInt(Event.AVAILABILITY_INDEX), cur.getInt(Event.CALENDAR_ID_INDEX), cur.getInt(Event.CALENDAR_COLOR_INDEX)));
        }

        cur.close();

        return events;
    }

}
