package assignment;


/**
* This file is a template class for data fields
* @author Shubham Sharma
*
* **/

public class User {
	
	private int id;
	private String name;
	private String address;
	private String gender;
	private String salary;
	
	
	//getters and setter methods
	public final int getId() {
		return id;
	}
	public final void setId(int id) {
		this.id = id;
	}
	public final String getName() {
		return name;
	}
	public final void setName(String name) {
		this.name = name;
	}
	public final String getAddress() {
		return address;
	}
	public final void setAddress(String address) {
		this.address = address;
	}
	public final String getGender() {
		return gender;
	}
	public final void setGender(String gender) {
		this.gender = gender;
	}
	public final String getSalary() {
		return salary;
	}
	public final void setSalary(String salary) {
		this.salary = salary;
	}
	
	
	@Override
	public String toString() {
		return id + "," + name + "," +  address + "," +  gender+ "," + salary + "\n";
	}

}
