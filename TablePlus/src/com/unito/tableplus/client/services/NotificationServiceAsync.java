package com.unito.tableplus.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.shared.model.Notification;

public interface NotificationServiceAsync {

	void sendEmail(List<String> recipientList, String emailSubject,String emailBody,
			AsyncCallback<Boolean> callback);

	void waitForNotification(List<Long> groupKeySubscription,
			Long clientSeqNumber,String clientEmail, AsyncCallback<List<Notification>> callback);

	void sendNotification(Notification notification,
			AsyncCallback<Boolean> callback);

}
