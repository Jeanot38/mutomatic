package eu.telecom_bretagne.mutomatic.lib;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.*;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This class provides simple methods to get a list of Calendars and events created in Google Calendar application.
 * @author Mathis GAVILLON
 */
public class CalendarWrapper {

    /**
     * The {@link ContentResolver} object used to query informations from Google Calendar application
     */

    private ContentResolver contentResolver;
    private LinkedList <Calendar> calendars = null;
    private LinkedList <Event> events = null;

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

        calendars = new LinkedList<>();

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
     * @param day Represents the beginning time of the day.
     * @param calendarIdToSelect The Ids of the Calendars from which events will be returned.
     * @return The list of events after selecting them thanks to the parameters.
     */

    public LinkedList <Event> getEvents(Long day, Integer[] calendarIdToSelect) {

        events = new LinkedList<>();

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

        // In the case of providing the day parameter...
        if (day != null) {
            // ... add a WHERE condition
            selection = "((" + Events.DTEND + " >= ?) AND (" + Events.DTEND + " < ?)) ";
            arguments = new String[] {Long.toString(day), Long.toString(day+3600*1000*24)};
        }
        // In the case of providing calendarIdToSelect array
        if(calendarIdToSelect != null) {
            argumentsCalendar = new ArrayList<>();

            // Add the two first arguments corresponding to day parameter...
            if(day != null) {
                selection += "AND (";
                argumentsCalendar.add(arguments[0]);
                argumentsCalendar.add(arguments[1]);
            }

            // ... and the add a condition for each id of calendar provided

            for(Integer idCalendar : calendarIdToSelect) {
                selection += Events.CALENDAR_ID+" = ? OR ";
                argumentsCalendar.add(Integer.toString(idCalendar));
            }

            //To erase the last "AND " word
            selection = selection.substring(0, selection.length()-4);

            // Convert the list to an array used by the query method above
            if(day != null) {
                selection += ")";
            }

            arguments = new String[argumentsCalendar.size()];
            argumentsCalendar.toArray(arguments);
        }
        // Get the list of events filtered due to parameter defined above
        cur = contentResolver.query(uri, Event.PROJECTION, selection, arguments, null);

        // Create Event objects and add to the list
        while(cur.moveToNext()) {
            events.add(new Event(cur.getInt(Event.ID_INDEX), cur.getLong(Event.DTSTART_INDEX), cur.getLong(Event.DTEND_INDEX), cur.getString(Event.TITLE_INDEX), cur.getString(Event.DESCRIPTION_INDEX), cur.getInt(Event.AVAILABILITY_INDEX)));
        }

        cur.close();

        return events;
    }

}
