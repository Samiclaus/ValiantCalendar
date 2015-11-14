package at.irsigler.valiantcalendar;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Connection class to the Valiant homepage that can create {@code}{@link ValiantRelease} objects based on HTML fragments on the page.
 * @author Florian
 *
 */
public class ValiantHomepageConnector {

	private static final String VALIANT_EVENTS_URL = PropertiesLoader.getInstance().getProperty("valiant.events.url", "http://www.valiantentertainment.com/events/");

	private static final String DATA_DATE = "data-date";

	private static final String DATA_SUBJECT = "data-subject";

	private static final String DATA_DESCRIPTION = "data-description";
	
	/**
	 * Parse the Valiant homepage and create a list of release events.
	 * @return a list of release events
	 * @throws IOException
	 */
	public List<ValiantRelease> readReleaseEvents() throws IOException {
		
		List<ValiantRelease> events = new ArrayList<ValiantRelease>();
		
		Document doc = Jsoup.connect(VALIANT_EVENTS_URL).get();
		
		Elements newsHeadlines = doc.select("#releases a.ical");
		
		for (Element element : newsHeadlines) {
			String date = element.attr(DATA_DATE);
			String title = element.attr(DATA_SUBJECT);
			String description = element.attr(DATA_DESCRIPTION);
			events.add(new ValiantRelease(date, title, description));
		}

		return events;
	}
}
