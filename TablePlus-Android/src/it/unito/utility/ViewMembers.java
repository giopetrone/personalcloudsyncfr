package it.unito.utility;

//Object View Table need for show the Members and their status
public class ViewMembers {

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

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
		return email;
	}
	
	
	private String email;
	private String status;
	private Long key;
}
