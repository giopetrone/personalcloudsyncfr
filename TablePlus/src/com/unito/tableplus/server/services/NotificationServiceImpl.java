package com.unito.tableplus.server.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.unito.tableplus.client.services.NotificationService;
import com.unito.tableplus.shared.model.Notification;
import com.google.appengine.api.mail.MailService.Message;

public class NotificationServiceImpl extends RemoteServiceServlet implements
		NotificationService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static long seqNumber = 0;
	public static List<Notification> notificationsList = new ArrayList<Notification>();

	private static final Logger log = Logger
			.getLogger(NotificationServiceImpl.class.getName());

	@Override
	public boolean sendEmail(List<String> recipientList, String emailSubject,
			String emailBody) {

		try {
			sendMail("luigi.cortese00@gmail.com", "subject", "body",
					"htmlBody", "attachment");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	private void sendMail(String recipient, String subject, String body,
			String htmlBody, String attachment) throws IOException {
		MailService mailService = MailServiceFactory.getMailService();
		Message mail = new Message("luigi.cortese00@gmail.com", recipient,
				subject, body);
		mailService.send(mail);
	}

	@Override
	public List<Notification> waitForNotification(
			List<Long> groupKeySubscription, Long clientSeqNumber,
			String clientEmail) {

		log.warning("DENTROOOO: clientSeqNumber=" + clientSeqNumber
				+ ", seqNumber=" + seqNumber);
		System.out.println("DENTRO");

		// Timer timer=new Timer();
		// timer.schedule(new TimerTask(){
		// public void run(){
		// System.out.println("boh");
		// }
		// }, 5000);

		// String sub = "";
		// for (Long myGroup : groupKeySubscription)
		// sub = sub + ", " + myGroup;
		// System.out.println(clientEmail + " ("
		// + Thread.currentThread().getName()
		// +
		// ") parte con waitForNotification(), queste le sottoscrizioni: "+sub);

		List<Notification> myNotifications = new ArrayList<Notification>();
		Notification n = null;
		boolean needed = false;

		// quest'if fa s� che al primo accesso di un client A, lo stesso
		// ignori
		// la propria notifica appena inviata di --IO SONO ONLINE--, perch�
		// � vero che questa notifica viene inviata e viene aumentato il
		// seqNumber,
		// ma � anche vero che qui la prima cosa che faccio � aggiornare il
		// clientSeqNumber a quello corrente (come a dire: sono aggiornato
		// del
		// fatto che sono online!)
		if (clientSeqNumber < 0)
			clientSeqNumber = seqNumber;

		while (!needed) {
			// System.out.println("\n"+clientEmail+" parte col ciclo interno di waitForNotification()");
			// TODO Auto-generated method stub
			synchronized (this) {
				while (clientSeqNumber == seqNumber) {
					try {
						// comporta il rilascio del lock, la sospensione del
						// thread ed il suo inserimento in wait set.
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// c'� qualche nuova notifica. Dovrei controllare se le
				// nuove notifiche sono pi� di una, ma al momento
				// do per scontato che sia una sola

				n = notificationsList.get(notificationsList.size() - 1);

			}
			// l'ultima notifica aggiunta � interessante?

			if (n.getEventKind().equals("ANSWERNOW")
					&& n.getSenderEmail().equals(clientEmail)) {
				log.warning("Forzo la risposta");
				needed = true;
			}

			// quando un membro passa online/offline � interessante se
			// appartiene ad almeno un mio stesso gruppo
			if (n.getEventKind().equals("MEMBERONLINE")
					|| n.getEventKind().equals("MEMBEROFFLINE")) {
				for (Long memberGroup : n.getOwningGroups())
					for (Long myGroup : groupKeySubscription)
						if (myGroup.compareTo(memberGroup) == 0)
							needed = true;
			}

			if (n.getEventKind().equals("MEMBERVISIBLE")
					|| n.getEventKind().equals("MEMBERHIDDEN"))
				for (Long myGroup : groupKeySubscription)
					if (myGroup.compareTo(n.getGroupKey()) == 0)
						needed = true;

			if (n.getEventKind().equals("SELECTIVEPRESENCEON")
					|| n.getEventKind().equals("SELECTIVEPRESENCEOFF"))
				for (Long myGroup : groupKeySubscription)
					if (myGroup.compareTo(n.getGroupKey()) == 0)
						needed = true;

			// quando viene aggiunto un membro ad un gruppo � interessante
			// se
			// 1) io appartengo a quel gruppo
			// 2) io non appartengo a quel gruppo, ma sono l'invitato
			if (n.getEventKind().equals("MEMBERGROUPADD")) {
				for (Long myGroup : groupKeySubscription)
					if (myGroup.compareTo(n.getGroupKey()) == 0)
						needed = true;

				if (n.getMemberEmail().equals(clientEmail))
					needed = true;
			}

			// quando viene aggiunto un table � interessante (al momento)
			// se sono io il creatore del table, cos� aggiorno le mie
			// sottoscrizioni alle notifiche!
			if (n.getEventKind().equals("NEWTABLE"))
				if (clientEmail.equals(n.getSenderEmail()))
					needed = true;

			clientSeqNumber++;
			// System.out.println(clientEmail + " ("
			// + Thread.currentThread().getName()
			// + ") ha appena aumentato il proprio clientSeqNumber a "
			// + clientSeqNumber + ", e needed � " + needed);
		}

		myNotifications.add(n);
		// System.out.println(clientEmail + " ("
		// + Thread.currentThread().getName()
		// + ") abbandona waitForNotification()");
		return myNotifications;

	}

	@Override
	public boolean sendNotification(Notification notification) {
		synchronized (this) {
			seqNumber++;

			// System.out.println("\n" + notification.getSenderEmail() + " ("
			// + Thread.currentThread().getName()
			// + ") sta per lanciare una notifica:\n------ seqNumber = "
			// + seqNumber + "\n------ eventKind = "
			// + notification.getEventKind() + "\n------ memberEmail = "
			// + notification.getMemberEmail() + "\n");

			notification.setSequenceNumber(seqNumber);
			notificationsList.add(notification);

			// comporta l�estrazione di tutti i thread
			// da wait set ed il loro inserimento in entry set.
			notifyAll();
		}
		return false;
	}

}