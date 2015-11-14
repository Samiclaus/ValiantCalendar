package at.irsigler.valiantcalendar;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

/**
 * Connection class to a Google calendar that can create or update events based on a {@code}{@link ValiantRelease}.
 * @author Florian
 *
 */
public class ValiantCalendarConnector {
    /** Application name. */
    private static final String APPLICATION_NAME = "Valiant Release Dates Calendar Importer";

    /** Directory to store user credentials for this application. */
    private static final File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/calendar-api-quickstart");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart. */
    private static final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR_READONLY, CalendarScopes.CALENDAR);
    
    private static final String VALIANT_ID = PropertiesLoader.getInstance().getProperty("google.calendar.id", "5i0k9tl5i058172ea6gpm8jg8o@group.calendar.google.com");

	private static final String VALIANT_DATE_FORMAT = "yyyyMMdd";
	
	private com.google.api.services.calendar.Calendar calendarService;
	

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    private Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in = ValiantCalendarConnector.class.getResourceAsStream("client_secret.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Calendar client service.
     * @return an authorized Calendar client service
     * @throws IOException
     */
    public com.google.api.services.calendar.Calendar getCalendarService() throws IOException {
    	if (calendarService == null) {
    		Credential credential = authorize();
    		calendarService = new com.google.api.services.calendar.Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
    	}
        return calendarService;
    }
    
    /**
     * Get a list of existing future events on the Valiant calendar.
     * @return a list of events - 200 events at maximum
     * @throws IOException
     */
	public Events getExistingEvents() throws IOException {
		Events existingEvents = getCalendarService().events().list(VALIANT_ID).setTimeMin(new DateTime(System.currentTimeMillis())).setMaxResults(200).execute();
		return existingEvents;
	}
	
	/**
	 * Create a new event on the Valiant calendar.
	 * @param release the new release that an event should be created for
	 * @throws IOException
	 */
	public void createNewEvent(ValiantRelease release) throws IOException {
		Event event = new Event();
		event.setSummary(release.getTitle());
		event.setStart(parseValiantDate(release.getDate(), false));
		event.setEnd(parseValiantDate(release.getDate(), true));
		event.setDescription(release.getDescription());
		getCalendarService().events().insert(VALIANT_ID, event).execute();
	}
	
	/**.
	 * Update an event on the Valiant calendar
	 * @param release the new data that the existing event should be updated with
	 * @param eventId the id of the existing event on the Valiant calendar
	 * @throws IOException
	 */
	public void updateEvent(ValiantRelease release, String eventId) throws IOException {
		Event event = getCalendarService().events().get(VALIANT_ID, eventId).execute();
		event.setDescription(release.getDescription());
		event.setStart(parseValiantDate(release.getDate(), false));
		event.setEnd(parseValiantDate(release.getDate(), true));
		getCalendarService().events().patch(VALIANT_ID, eventId, event).execute();
	}

    
	private static EventDateTime parseValiantDate(String valiantDate, boolean tomorrow) {
		Date date = new Date();
		EventDateTime edt = new EventDateTime();
		try {
			date = new SimpleDateFormat(VALIANT_DATE_FORMAT).parse(valiantDate);
		
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		if (tomorrow) {
			c.add(Calendar.DAY_OF_YEAR, 1);
			
		}
		DateTime dt = new DateTime(true, c.getTimeInMillis(), null);
		edt.setDate(dt);
		return edt;
	}
	

}
