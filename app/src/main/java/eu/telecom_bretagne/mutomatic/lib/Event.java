package eu.telecom_bretagne.mutomatic.lib;

import android.provider.CalendarContract.*;

import java.io.Serializable;

/**
 * Created by math on 22/01/15.
 */
public class Event implements Serializable {

    private int id;
    private long dtStart;
    private long dtEnd;
    private String title;
    private String description;
    private int availability;

    public static final String[] PROJECTION = new String[] {
            Events._ID,
            Events.DTSTART,
            Events.DTEND,
            Events.TITLE,
            Events.DESCRIPTION,
            Events.AVAILABILITY
    };
    public static final int ID_INDEX = 0;
    public static final int DTSTART_INDEX = 1;
    public static final int DTEND_INDEX = 2;
    public static final int TITLE_INDEX = 3;
    public static final int DESCRIPTION_INDEX = 4;
    public static final int AVAILABILITY_INDEX = 5;

    public Event(int id, long dtStart, long dtEnd, String title, String description, int availability) {
        this.id = id;
        this.dtStart = dtStart;
        this.dtEnd = dtEnd;
        this.title = title;
        this.description = description;
        this.availability = availability;
    }

    public int getId() {
        return id;
    }

    public long getDtStart() {
        return dtStart;
    }

    public long getDtEnd() {
        return dtEnd;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getAvailability() {
        return availability;
    }

    public boolean equals(Object o) {
        if(o instanceof Event) {
            Event event = (Event) o;

            if(title != null && !title.equals(event.getTitle())) {
                return false;
            } else if(title == null && event.getTitle() != null) {
                return false;
            }

            if(description != null && !description.equals(event.getDescription())) {
                return false;
            } else if(description == null && event.getDescription() != null) {
                return false;
            }

            return id == event.getId() &&
                    dtStart == event.getDtStart() &&
                    dtEnd == event.getDtEnd() &&
                    availability == event.getAvailability();
        }

        return false;
    }
}
