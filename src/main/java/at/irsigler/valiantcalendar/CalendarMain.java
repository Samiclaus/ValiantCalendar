package at.irsigler.valiantcalendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;

/**
 * Starts the Google calendar connector, visits the Valiant homepage and loads or updates new releases.
 * @author Florian
 *
 */
public class CalendarMain {

    public static void main(String[] args) throws IOException {
    	
    	
        // Build a new authorized API client service.
        // Note: Do not confuse this class with the
        //   com.google.api.services.calendar.model.Calendar class.
    	
    	ValiantCalendarConnector calendarConnector = new ValiantCalendarConnector();
        
        Events existingEvents = calendarConnector.getExistingEvents();
        
        ValiantHomepageConnector hpConnector = new ValiantHomepageConnector();
        
        for (ValiantRelease release: hpConnector.readReleaseEvents()) {
        	
        	boolean exists = false;
        	String eventId = "";
        	for (Event e : existingEvents.getItems()) {
        		if (e.getSummary().equals(release.getTitle())){
        			exists = true;
        			eventId = e.getId();
        			break;
        		}
        	}
        	if (!exists) {
        		calendarConnector.createNewEvent(release);
        		System.out.println("Event " + release.getTitle() + " created!");
        	} else {
        		calendarConnector.updateEvent(release, eventId);
        		System.out.println("Event " + release.getTitle() + " already exists - updating!");
        	}
		}
        System.out.println("Done!");
        
    }

}
