package at.irsigler.valiantcalendar;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

	private static PropertiesLoader instance;
	private Properties connectionProperties;

	private PropertiesLoader() {

	}

	public static PropertiesLoader getInstance() {
		if (instance != null) {
			return instance;
		} else {
			instance = new PropertiesLoader();
			instance.connectionProperties = new Properties();
			try {
				InputStream in = PropertiesLoader.class.getResourceAsStream("connector.properties");
				instance.connectionProperties.load(in);
				in.close();
			} catch (IOException e) {
				System.out.println("Could not load properties.");
			}
			return instance;
		}
	}
	
	public String getProperty(String key) {
		return connectionProperties.getProperty(key);
	}
	
	public String getProperty(String key, String defaultValue) {
		return connectionProperties.getProperty(key, defaultValue);
	}

}
