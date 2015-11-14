package at.irsigler.valiantcalendar;

/**
 * Container class for a valiant release.
 * @author Florian
 *
 */
public class ValiantRelease {
	
	private String date;
	private String title;
	private String description;

	/**
	 * Create a new release containing date, title and a description.
	 * @param date date of the release
	 * @param title title of the release
	 * @param description description of the release
	 */
	public ValiantRelease(String date, String title, String description) {
		super();
		this.date = date;
		this.title = title;
		this.description = description;
	}

	public String getDate() {
		return date;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

}
