package com.unito.tableplus.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.unito.tableplus.shared.model.Notification;

@RemoteServiceRelativePath("notification-service")
public interface NotificationService extends RemoteService{

	boolean sendEmail(String sender, String recipient,String emailSubject, String emailBody, Long groupKey);
	
	List<Notification> waitForNotification(List<Long> groupKeySubscription, Long clientSeqNumber, String clientEmail);
	
	boolean sendNotification(Notification notification);
	
	Long getInvitedGroupKey(String code,String email);
	
}
