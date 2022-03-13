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
 * Problem Statement/ Assignment : Record Maintenance System Task: a) Read a
 * file (CSV format) e.g ID,Name,Add,Gender,....etc. (ID is unique key of each
 * record) b) Insert all the records in a target file (in the same format) e.g
 * FileName :- EmployeeData.dat. c) Make sure it must not duplicate the records
 * in target file, in fact overwrite the record which is already present. d) At
 * the end, must display how many new records have been added and how many
 * over-written. e) Save previous record value, for each record over-written. f)
 * And show all the newly added ids in sorted order. Note: If Input file
 * contains any duplicate record (ID) then only the latest entry should go
 * through and we have to save the previous value in a separate file (step e).
 * 
 * 
 * @author Shubham Sharma
 *
 *
 **/

public class RecordMaintenanceSystem {

	private static Logger log = LogManager.getLogger(RecordMaintenanceSystem.class.getName()); // Logger Method Object
	private static String newDataFilePath; // CSV file path with new data, needed to be copied into the target file
	private static String employeeDataFilePath; // .dat file path into which the data will be copied
	private static String oldDataFilePath = "oldData.csv";
	// File paths variable

	private static HashMap<Integer, User> updatedRecords = new HashMap<>(); // contains the updated records out of both files
	private static List<User> overwrittenRecord = new ArrayList<>(); // contains the overwritten records
	// Collections declarations

	private static int newRecordCounter = 0; // counts the number of newly added records
	private static int overWrittenCounter = 0; // counts the number of old records which are overwritten
	private static int key;
	// variable declarations

	// main method
	public static void main(String[] args) {

		try {
			
			//assigning values to properties file path and their properties, default file paths.
			String propertiesFilePath="resources\\config.properties"; //
			String propertyKey1="RecordMaintenanceSystem.source_File_Path";
			String propertyKey2="RecordMaintenanceSystem.target_File_Path";
			
			String defaultnewDataFilePath="defaultFiles\\newData.csv";
			String defaultemployeeDataFilePath="defaultFiles\\EmployeeData.dat";

			assignAbsoluteFilePath(propertiesFilePath,propertyKey1,propertyKey2, defaultnewDataFilePath, defaultemployeeDataFilePath); // method call to assign absolute file path to path variables
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
			handleOverwrittenRecord(overwrittenRecord, oldDataFilePath);

			log.info("EmployeeData.dat now have the updated version of old entries and new entries. \n");
			log.info("oldData.csv Have the overwritten data");

		} catch (Exception e) {
			log.fatal("An exception has occured - " + e);
		}

	}

	// Below method sets the source and target file paths using the property file if found, if not found, default paths will be assigned
	public static void assignAbsoluteFilePath(String propertiesFilePath, String propertyKey1, String propertyKey2, String defaultnewDataFilePath, String defaultemployeeDataFilePath) throws Exception {

		try {

			HandleFilePath objectHandleFilePath = new HandleFilePath(propertiesFilePath,propertyKey1,propertyKey2 ); // object of "HandleAbsoluteFilePath", which will set and return the path variables

			// method calls to assign the paths using properties file
			newDataFilePath = objectHandleFilePath.getSourceFilePath();
			employeeDataFilePath = objectHandleFilePath.getTargetFilePath();
			
			
			//checking if the path gotten is empty, if empty, the default path will be given 
			if(newDataFilePath.isEmpty()) {
				newDataFilePath=defaultnewDataFilePath ;
			}
			if(employeeDataFilePath.isEmpty()) {
				 employeeDataFilePath=defaultemployeeDataFilePath ;
			}
			
			log.debug("New data file path : " + newDataFilePath);
			log.debug("Target data file path : " + employeeDataFilePath);
			
		} catch (Exception e) {
		throw new Exception();
		}

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

	// Below method reads target file -EmployeeData.dat file (contains old data
	// entries), and copies the content into the "updatedRecords" HashMap

	public static void readEmployeeDataFile(String employeeDataFilePath) throws Exception, FileNotFoundException {

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
			throw new FileNotFoundException ("Target File not found");
		} catch (Exception e) {
			throw new Exception();
		}

	}

	// Below method reads newData.csv file (contains new data entries to be updated
	// into existing data file), and copies the content to "updatedRecords" HashMap
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
			fileWriter.write("Id, Name, Address, Gender, Salary \n");

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

			fileWriter.write("Id, Name, Address, Gender, Salary \n");

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
