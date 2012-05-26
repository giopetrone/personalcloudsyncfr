package com.unito.tableplus.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.shared.model.Notification;

public interface NotificationServiceAsync {

	void sendEmail(String sender, String recipient,String emailSubject, String emailBody, Long tableKey,
			AsyncCallback<Boolean> callback);

	void waitForNotification(List<Long> tableKeySubscription,
			Long clientSeqNumber,String clientEmail, AsyncCallback<List<Notification>> callback);

	void sendNotification(Notification notification,
			AsyncCallback<Boolean> callback);

	void getInvitedTableKey(String code, String email,
			AsyncCallback<Long> callback);

}
