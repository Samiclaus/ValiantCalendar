package at.irsigler.valiantcalendar;
	public class ValiantRelease {
		
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
		private String date;
		private String title;
		private String description;
		
	}
	