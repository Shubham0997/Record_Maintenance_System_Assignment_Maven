package assignment;

import org.apache.logging.log4j.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Problem Statement/ Assignment : Record Maintenance System Task: 
a) Read a file (CSV format) e.g ID,Name,Add,Gender,....etc. (ID is unique key of each record)
b) Insert all the records in a target file (in the same format) e.g FileName :- EmployeeData.dat.
c) Make sure it must not duplicate the records in target file, infact overwrite the record which is already present.
d) At the end, must display how many new records have been added and how many over-written.
e) Save previous record value, for each record over-written.
f) And show all the newly added ids in sorted order.
Note: If Input file contains any duplicate record (ID) then only the latest entry should go through and we have to save the previous value in a separate file (step e).
 *
 * @author Shubham Sharma
 *
 *
 **/

public class RecordMaintenanceSystem {
	
	private static Logger log = LogManager.getLogger(RecordMaintenanceSystem.class.getName()); // Logger Method Object
	
	private static HashMap<Integer, User> updatedRecords = new HashMap<>(); // contains the updated records out of both files
	private static List<User> overwrittenRecord = new ArrayList<>(); // contains the overwritten records
	// Collections declarations

	private static int newRecordCounter = 0; // counts the number of newly added records
	private static int overWrittenCounter = 0; // counts the number of old records which are overwritten
	private static int key;
	private static String[] fieldHeaderTemplate = new String[] {"Id,","Name,","Address,","Gender,","Salary"};
	// variable declarations


	final static String DEFAULT_NEW_DATA_FILE_PATH="defaultFiles\\newData.csv"; // default new data csv file path, selected in case no path found in properties
	final static String DEFAULT_EMPLOYEE_DATA_FILE_PATH="defaultFiles\\EmployeeData.dat"; //default employeeData dat file path, selected in case no path found in properties
	final static String OLD_FILE_PATH = "defaultFiles\\oldData.csv";
	
	// main method
	public static void main(String[] args) {
		
		

		try {
			
			
			initializeResource();
			
			
			
			String newDataFilePath=assignSourceFilePath(DEFAULT_NEW_DATA_FILE_PATH); // CSV file path with new data, needed to be copied into the target file
			String employeeDataFilePath=assignTargetFilePath(DEFAULT_EMPLOYEE_DATA_FILE_PATH);	// .dat file path into which the data will be copied		
			
			log.debug("Source File Path : " + newDataFilePath);
			log.debug("Target File Path : " + employeeDataFilePath);
			log.info("******************************");
			
			// Method call for file read/write operations
			readEmployeeDataFile(employeeDataFilePath);
			readNewDataFile(newDataFilePath);
			populateFinalDatabase(employeeDataFilePath);

			log.info("Number of New records added : " + newRecordCounter + "\n");
			log.info("******************************");
			log.info("Number of overwritten Records : " + overWrittenCounter + "\n");
			log.info("******************************");

			log.info("Following entries have been overwritten : \n");
			handleOverwrittenRecord(overwrittenRecord, OLD_FILE_PATH);

			log.info("EmployeeData.dat now have the updated version of old entries and new entries. \n");
			log.info(OLD_FILE_PATH+ " Have the overwritten data");

		} catch (Exception e) {
			log.fatal("An exception has occured - " + e);
		}

	}
	
	//initializing properties file 
	public static void initializeResource() {
		ResourceInitializer.initializeFile();
	}
	
	
	//method to assign source file path, from config.properties, if not found default path will be assigned
	public static String assignSourceFilePath(String defaultnewDataFilePath) {
		try {
			String absoluteSourceFilePath= ResourceInitializer.getResource("RecordMaintenanceSystem.source_File_Path");
			if(absoluteSourceFilePath== null || absoluteSourceFilePath.isEmpty() ) {
				absoluteSourceFilePath=defaultnewDataFilePath ;
			}
	
			return absoluteSourceFilePath;
			
		}catch(Exception e) {
			log.debug(e);
		}
		return "";
	}
	
	//method to assign target file path, from config.properties, if not found default path will be assigned
	public static String assignTargetFilePath(String defaultemployeeDataFilePath) {
		try {
			String absoluteTargetFilePath= ResourceInitializer.getResource("RecordMaintenanceSystem.target_File_Path");
			if(absoluteTargetFilePath==null || absoluteTargetFilePath.isEmpty()  ) {
				absoluteTargetFilePath=defaultemployeeDataFilePath ;
			}
			
			return absoluteTargetFilePath;
			
		}catch(Exception e) {
			log.debug(e);
		}
		return "";
		
	}


	// This method sets the data fields w.r.t the User class
	public static User setDataFields(String[] fields, User user) {

		key = Integer.parseInt(fields[0]);

		// setting data fields from the each line of file using template class
		user.setId(Integer.parseInt(fields[0]));
		user.setName(fields[1]);
		user.setAddress(fields[2]);
		user.setGender(fields[3]);
		user.setSalary(fields[4]);

		return user;

	}

	// Below method reads target file -EmployeeData.dat file (contains old data entries), and copies the content into the "updatedRecords" HashMap

	public static void readEmployeeDataFile(String employeeDataFilePath)  {

		String line = "";

		try (BufferedReader reader = new BufferedReader(new FileReader(employeeDataFilePath));) {

			reader.readLine(); // skipping the header line as it needs not be written into other files

			while ((line = reader.readLine()) != null) { // reading file line by line
				String[] fields = line.split(",");

				if (fields.length > 0) {

					User user = new User();
					user = setDataFields(fields, user);

					User userRecord = updatedRecords.put(key, user);

					// check for existing record using return value of put method of hashmap
					if (userRecord != null) {
						overWrittenCounter++;
						overwrittenRecord.add(userRecord);// entering overwritten into "overwrittenRecord" ArrayList

					}
				}
			}

		} catch (FileNotFoundException e) {
			log.error("Target File not found, new one will be created");
		} catch (Exception e) {
			log.error(e);
		}

	}

	// Below method reads newData.csv file (contains new data entries to be updated into existing data file), and copies the content to "updatedRecords" HashMap
	public static void readNewDataFile(String newDataFilePath) throws Exception, FileNotFoundException {
		String line = "";
		try (BufferedReader reader = new BufferedReader(new FileReader(newDataFilePath));) {

			reader.readLine();

			while ((line = reader.readLine()) != null) { // reading file line by line
				String[] fields = line.split(",");

				if (fields.length > 0) {
					User user = new User(); // object of template class containing data fields

					user = setDataFields(fields, user);

					User userRecord = updatedRecords.put(key, user);
					// check for existing record using return value of put method of hashmap
					if (userRecord != null) {
						overWrittenCounter++;
						overwrittenRecord.add(userRecord); // entering overwritten into "overwrittenRecord" ArrayList

					} else {
						newRecordCounter++; // counter increment for new values

					}
				}
			}
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Source File not found");
		} catch (Exception e) {
			throw new Exception();
		}
	}

	
	// Below method updates the updated data into the EmployeeData.dat file using "updatedRecords" HashMap
	public static void populateFinalDatabase(String employeeDataFilePath) throws Exception, FileNotFoundException {

		try (FileWriter fileWriter = new FileWriter(employeeDataFilePath);) {

			// setting up the field heading in EmployeeData.dat file
			for(String header: fieldHeaderTemplate) {
				fileWriter.write(header);
			}
			fileWriter.write("\n");
			
			// write data from the HashMap into the EmployeeData.dat
			for (Map.Entry<Integer, User> entry : updatedRecords.entrySet()) {

				fileWriter.write(entry.getValue() + "");

			}
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Target File not found");
		} catch (Exception e) {
			throw new Exception();
		}
	}
	

	// Below method displays and enters the the Overwritten records from the "overwrittenRecord" HashMap into the oldData.csv
	public static void handleOverwrittenRecord(List<User> overwrittenRecord, String oldDataFilePath)
			throws Exception, FileNotFoundException {

		try (FileWriter fileWriter = new FileWriter(oldDataFilePath);) {
			
			// setting up the field heading in EmployeeData.dat file
			for(String header: fieldHeaderTemplate) {
				fileWriter.write(header);
			}
			fileWriter.write("\n");
			
			//fileWriter.write(fieldHeaderTemplate[0]+fieldHeaderTemplate[1]+fieldHeaderTemplate[2]+fieldHeaderTemplate[3]+fieldHeaderTemplate[4]);

			for (User u : overwrittenRecord) {
				log.info(u);
				fileWriter.write(u.toString());
			}

		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("oldRecord file not found");
		} catch (Exception e) {
			throw new Exception();
		}

		log.info("******************************");
	}

}
