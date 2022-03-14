package assignment;

/**
* This file handles the operations related to reading, setting and returning the path of the source and target files
* @author Shubham Sharma
*
* **/


import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourceInitializer {

	public static Logger log = LogManager.getLogger(RecordMaintenanceSystem.class.getName());
	public static Properties configFile = new Properties();
	
	//Initializing properties file
	public static void initializeFile() {
		
		try(FileReader reader = new FileReader("resources\\confsig.properties");){
			
			configFile.load(reader);
		
		}  catch (IOException e) {
			log.error("Config file not loaded/found, default files will be considered");
		}
	}
	
	
	//return properties values
	public static String getResource(String property) {
		
		return configFile.getProperty(property);
	}

	
}
