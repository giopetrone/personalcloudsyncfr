package com.unito.tableplus.shared.model;
   
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Extension;


@PersistenceCapable(detachable = "true")
public class Bookmark implements Serializable {

	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
	private String key;

	@Persistent
	private String title = null;

	@Persistent
	private String url = null;
	
	@Persistent
	private String legend;

	@Persistent
	private Table table;
	
	@Persistent
	private List<String> annotation=new LinkedList<String>();	
	
	@Persistent
	private LinkedList<String> tag= new LinkedList<String>();	
	
	@Persistent(mappedBy = "bookmark")
	private LinkedList<Comment> commentList = new LinkedList<Comment>();

	
	//setters

	public void setTitle(String string) {
		this.title=string;	
	}

	public void setUrl(String string) {
		this.url=string;	
	}	
	
	public void setLegend(String legend) {
		this.legend = legend;
	}
	
	public void setTable(Table table) {
		this.table=table;		
	}
	
	public void setAnnotation(List<String> annotation) {
		this.annotation=annotation;		
	}
	
	public void setTagCategory (LinkedList<String> tag) {
		this.tag=tag;			
	}

	//getters

	public String getKey() {
		return key;
	}
	
	public String getTitle() {
		return title;
	}		
	
	public String getUrl() {
		return url;
	}
	
	public String getLegend() {
		return legend;
	}
	
	public Table getTable() {	
		return table;
	}

	public String getTableName() {
		return getTable().getName();
	}	
	
	public List<String> getAnnotation() {	
		return annotation;
	}
	public int getAnnotationNumber() {	
		return getAnnotation().size();
	}
	
	public LinkedList<String> getTag() {	
		return tag;
	}
	
	public String getTagString() {	
		String listTag="";
		for(String t: tag) listTag+=t+", ";
		if (listTag.length()>0)
			return listTag.substring(0, listTag.length()-2);
		else return "No Tag";
	}	
	
	public LinkedList<Comment> getComments(){		
		return commentList;
	}		
	
	public String getStringComments() {
		String comments="";
		List<Comment> commentList= getComments();
		for (Comment c : commentList){
			comments+=c.toString()+"\n";
		}
		if (commentList!=null && commentList.size()>0)
			return "\nComment:\n"+comments;
		else return "";
	}
	
	//others
	
	@Override
	public String toString() {
		return "Title: "+this.getTitle() + ", Url: " + this.getUrl() + ", Legend: "+ this.getLegend()
				+"\nComments: "+this.getComments().size()+" Tag: "+this.getTagString()
				+ ", Annotations: "+this.getAnnotation().size();
	}

	public void addComment(Comment comment) {
		getComments().add(comment);	
	}

	public void addTag(String string) {
		boolean existing=false;
		for (String s: this.getTag()){
			if (s.equalsIgnoreCase(string)) existing=true;
		}
		if(!existing) this.tag.add(string);	
	}
	
	public void removeTag(int tag) {
		this.getTag().remove(tag);
		
	}

	public void addAnnotation(String value) {
		this.getAnnotation().add(value);
	}

	public String getAnnotationString() {
		String listAnnotation="";
		for(String a: getAnnotation()) listAnnotation+=a+", ";
		if (listAnnotation.length()>0)
			return listAnnotation.substring(0, listAnnotation.length()-2);
		else return "No Annotation";
	}

	public void removeAnnotation(int annotation) {
		this.getAnnotation().remove(annotation);
	}

}
