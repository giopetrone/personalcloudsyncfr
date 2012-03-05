package com.unito.tableplus.shared.model;

import java.io.Serializable;
import java.util.List;

public class Notification implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long sequenceNumber;
	private long groupKey;
	private String eventKind;
	private List<Long> owningGroups;
	private String memberEmail;
	private String status;
	private Long senderKey;
	private String senderEmail;
	
	
	public void printData(){
		System.out.println();
	}
	

	public long getGroupKey() {
		return groupKey;
	}
	public void setGroupKey(long groupKey) {
		this.groupKey = groupKey;
	}
	public String getEventKind() {
		return eventKind;
	}
	public void setEventKind(String eventKind) {
		this.eventKind = eventKind;
	}
	
	public String getMemberEmail() {
		return memberEmail;
	}
	public void setMemberEmail(String memberEmail) {
		this.memberEmail = memberEmail;
	}
	public List<Long> getOwningGroups() {
		return owningGroups;
	}
	public void setOwningGroups(List<Long> owningGroups) {
		this.owningGroups = owningGroups;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}



	public String getSenderEmail() {
		return senderEmail;
	}


	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}


	public Long getSenderKey() {
		return senderKey;
	}


	public void setSenderKey(Long senderKey) {
		this.senderKey = senderKey;
	}


	
	
	
}
