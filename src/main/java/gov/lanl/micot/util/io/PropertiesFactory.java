package gov.lanl.micot.util.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A general wrapper class for reading properties files
 * @author Russell Bent
 */
public class PropertiesFactory {

	private static PropertiesFactory INSTANCE = null;
	
	public static PropertiesFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PropertiesFactory();
		}
		return INSTANCE;
	}
	
	/**
	 * 
	 * Singleton constructor
	 */
	private PropertiesFactory() {		
	}
	
	/**
	 * Create a properties from a file name
	 * @param propertiesFile
	 * @throws IOException 
	 */
	public Properties createProperties(String propertiesFile) throws IOException {
		Properties properties = new Properties();		
		InputStream inputStream = new FileInputStream(propertiesFile);
		properties.load(inputStream);
		inputStream.close();
		return properties;
	}
	
}
