package assignment;

/**
* This file handles the operations related to reading, setting and returning the path of the source and target files
* @author Shubham Sharma
*
* **/

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HandleFilePath {

	private static Logger log = LogManager.getLogger(RecordMaintenanceSystem.class.getName());
	
	//variable declaration for paths
	private String sourceFilePath;
	private String targetFilePath;
	
	private String propertiesFilePath; // Properties file path
	private String propertyKey1; // property key1
	private String propertyKey2; //property key2
	
	//overwritten default constructor 
	public HandleFilePath(String propertiesFilePath, String propertyKey1, String propertyKey2 ) {
		
	this.propertiesFilePath=propertiesFilePath;
	this.propertyKey1=propertyKey1;
	this.propertyKey2=propertyKey2;
		//reading paths from the properties file
		try (FileReader reader = new FileReader(this.propertiesFilePath);) {
			Properties p = new Properties();
			p.load(reader);
			
			String newDataFilePath = p.getProperty(this.propertyKey1); //getting the absolute path of source file from properties file
			String employeeDataFilePath = p.getProperty(this.propertyKey2); // getting the absolute path of target file from properties file
			
			setSourceFilePath(newDataFilePath); 
			setTargetFilePath(employeeDataFilePath);
			
		} catch (FileNotFoundException e) {
			log.fatal("Properties file Not Found");
		} catch (Exception e) {
			log.fatal(e);
		}
		
	}
	
	public final String getSourceFilePath() {
		return sourceFilePath;
	}
	
	public final void setSourceFilePath(String sourceAbsolutePath) {
		
		this.sourceFilePath = sourceAbsolutePath;
		
	}
	
	public final String getTargetFilePath() {
		return targetFilePath;
	}
	

	public final void setTargetFilePath(String targetAbsolutePath) {
		this.targetFilePath = targetAbsolutePath;
	}
	

	
}
