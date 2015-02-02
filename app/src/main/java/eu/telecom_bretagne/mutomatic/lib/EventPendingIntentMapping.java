package eu.telecom_bretagne.mutomatic.lib;

import android.app.PendingIntent;

import java.io.Serializable;

/**
 * Created by math on 22/01/15.
 */
public class EventPendingIntentMapping implements Serializable{

    private Event event = null;
    private PendingIntent piStart = null;
    private PendingIntent piEnd = null;

    public EventPendingIntentMapping(Event event) {
        this.event = event;
    }

    public EventPendingIntentMapping(Event event, PendingIntent piStart) {
        this.event = event;
        this.piStart = piStart;
    }

    public Event getEvent() {
        return event;
    }

    public PendingIntent getPiStart() { return piStart; }

    public void setPiStart(PendingIntent piStart) { this.piStart = piStart; }

    public PendingIntent getPiEnd() { return piEnd; }

    public void setPiEnd(PendingIntent piEnd) { this.piEnd = piEnd; }

    public boolean equals(Object o) {
        if(o instanceof EventPendingIntentMapping) {
            EventPendingIntentMapping epm = (EventPendingIntentMapping) o;
            return this.event.equals(epm.getEvent());
        }

        return false;
    }
}
