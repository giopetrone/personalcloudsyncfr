package it.unito.utility;
/**
 * 
 * @author hp Un oggetto ViewTable serve per visualizzare un oggetto nella
 *         ListTable
 */
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
