package it.unito.utility;


public class ViewDoc{

	public ViewDoc(String docId, String title,String link){
		this.docId=docId;
		this.title=title;
		this.link=link;
	}

	public ViewDoc(){
		this.docId="";
		this.title="";
		this.link="";
	}
	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	public String toString(){
		return title;
	}
	
	private String docId;
	private String title;
	private String link;
}
