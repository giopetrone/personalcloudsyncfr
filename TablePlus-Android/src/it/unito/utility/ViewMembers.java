package it.unito.utility;

public class ViewMembers {

	private Long key;
	private String status;
	
	public ViewMembers(){
		key=(long)-1;
		status="not_define";
	}
	public ViewMembers(Long key,String status){
		this.key=key;
		this.status=status;
	}
	public Long getKey() {
		return key;
	}
	public void setKey(Long key) {
		this.key = key;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String toString(){
		return "Status: "+status+"-- Key: "+ key;
	}
}
