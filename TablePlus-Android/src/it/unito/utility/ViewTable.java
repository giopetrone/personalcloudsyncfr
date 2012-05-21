package it.unito.utility;


//Object View Table need for show an object in List Table
public class ViewTable {
	private String tableName;
	private Long key;
	private int numDocuments;
	private int numMembers;

	public ViewTable() {
	}

	public ViewTable(String name, Long key,int numDocuments,int numMembers) {
		this.tableName = name;
		this.key = key;
		this.numDocuments=numDocuments;
		this.numMembers=numMembers;
	}

	public int getNumDocuments() {
		return numDocuments;
	}

	public void setNumDocuments(int numDocuments) {
		this.numDocuments = numDocuments;
	}

	public int getNumMembers() {
		return numMembers;
	}

	public void setNumMembers(int numMembers) {
		this.numMembers = numMembers;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public String toString() {
		return tableName;
	}
}
