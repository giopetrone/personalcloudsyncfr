package com.unito.tableplus.server.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.unito.tableplus.client.services.NotificationService;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.Invitation;
import com.unito.tableplus.shared.model.Notification;
import com.unito.tableplus.shared.model.User;
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
	public boolean sendEmail(String sender, String recipient,
			String emailSubject, String emailBody, Long tableKey) {

		String code = UUID.randomUUID().toString();

		// Crea un oggetto "invito"
		Invitation i = new Invitation();
		i.setCode(code);
		i.setTableKey(tableKey);

		try {
			MailService mailService = MailServiceFactory.getMailService();

			Message mail = new Message(
					"luigi.cortese00@gmail.com",//
					recipient, //
					"TablePlusPlus - New Invitation",//
					"You have been invited by "
							+ sender
							+ " to join his/her table in TablePlusPlus environment."
							+ "Don't lose the chance, join now TablePlusPlus and start interacting with "
							+ sender
							+ " "
							+ "and many others, from all over the world! "
							+ "Click here or copy-paste in your addresses bar: <a href=\"http://tableplusplus.appspot.com/?code="
							+ code
							+ "\">http://tableplusplus.appspot.com/?code="
							+ code + "</a>");//

			mail.setHtmlBody("You have been invited by "
					+ sender
					+ " to join his/her table in TablePlusPlus environment."
					+ " Don't lose this chance, join now TablePlusPlus and start interacting with "
					+ sender
					+ " "
					+ "and many others, from all over the world! "
					+ "Click here or copy-paste in your addresses bar: <a href=\"http://tableplusplus.appspot.com/?code="
					+ code + "\">http://tableplusplus.appspot.com/?code="
					+ code + "</a>");
			System.out.println("1) " + mail.getHtmlBody());
			System.out.println("2) " + mail.getTextBody());
			mailService.send(mail);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		storeInvitation(i);

		return true;
	}

	public Long storeInvitation(Invitation invitation) {
		Long key = null;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistent(invitation);
			key = invitation.getKey();
		} catch (Exception e) {
			System.err
					.println("There has been an error storing the invitation: "
							+ e);
		} finally {
			pm.close();
		}
		return key;
	}

	@Override
	public List<Notification> waitForNotification(
			List<Long> tableKeySubscription, Long clientSeqNumber,
			String clientEmail) {

		log.info("DENTROOOO: clientSeqNumber=" + clientSeqNumber
				+ ", seqNumber=" + seqNumber);
		//System.out.println("DENTRO");

		// Timer timer=new Timer();
		// timer.schedule(new TimerTask(){
		// public void run(){
		// System.out.println("boh");
		// }
		// }, 5000);

		// String sub = "";
		// for (Long myTable : tableKeySubscription)
		// sub = sub + ", " + myTable;
		// System.out.println(clientEmail + " ("
		// + Thread.currentThread().getName()
		// +
		// ") parte con waitForNotification(), queste le sottoscrizioni: "+sub);

		List<Notification> myNotifications = new ArrayList<Notification>();
		Notification n = null;
		boolean needed = false;

		// quest'if fa sì che al primo accesso di un client A, lo stesso
		// ignori
		// la propria notifica appena inviata di --IO SONO ONLINE--, perchè
		// è vero che questa notifica viene inviata e viene aumentato il
		// seqNumber,
		// ma è anche vero che qui la prima cosa che faccio è aggiornare il
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
				// c'è qualche nuova notifica. Dovrei controllare se le
				// nuove notifiche sono più di una, ma al momento
				// do per scontato che sia una sola

				
				
				n = notificationsList.get((int) (notificationsList.size() - (seqNumber-clientSeqNumber)));
				log.info(clientEmail+" ha questo clientSeqNum="+clientSeqNumber+" e si appresta a leggere la notifica con seqNumber="+n.getSequenceNumber());
				
				
				
				
			}
			// l'ultima notifica aggiunta è interessante?
			log.info(clientEmail+" sta per analizzare la notifica: eventKind="+n.getEventKind()+" --- senderMail="+n.getSenderEmail());
			
			

			if (n.getEventKind().equals("ANSWERNOW")
					&& n.getSenderEmail().equals(clientEmail)) {
				log.info("Forzo la risposta");
				needed = true;
			}

			// quando un membro passa online/offline è interessante se
			// appartiene ad almeno un mio stesso gruppo
			if (n.getEventKind().equals("MEMBERONLINE")
					|| n.getEventKind().equals("MEMBEROFFLINE")) {
				for (Long memberTable : n.getOwningTables())
					for (Long myTable : tableKeySubscription)
						if (myTable.compareTo(memberTable) == 0)
							needed = true;
			}

			if (n.getEventKind().equals("MEMBERVISIBLE")
					|| n.getEventKind().equals("MEMBERHIDDEN"))
				for (Long myTable : tableKeySubscription)
					if (myTable.compareTo(n.getTableKey()) == 0)
						needed = true;

			if (n.getEventKind().equals("SELECTIVEPRESENCEON")
					|| n.getEventKind().equals("SELECTIVEPRESENCEOFF"))
				for (Long myTable : tableKeySubscription)
					if (myTable.compareTo(n.getTableKey()) == 0)
						needed = true;

			// quando viene aggiunto un membro ad un gruppo è interessante
			// se
			// 1) io appartengo a quel gruppo
			// 2) io non appartengo a quel gruppo, ma sono l'invitato
			if (n.getEventKind().equals("MEMBERTABLEADD")) {
				for (Long myTable : tableKeySubscription)
					if (myTable.compareTo(n.getTableKey()) == 0)
						needed = true;

				if (n.getMemberEmail().equals(clientEmail))
					needed = true;
			}

			// quando viene aggiunto un table è interessante (al momento)
			// se sono io il creatore del table, così aggiorno le mie
			// sottoscrizioni alle notifiche!
			if (n.getEventKind().equals("NEWTABLE"))
				if (clientEmail.equals(n.getSenderEmail()))
					needed = true;

			clientSeqNumber++;
			// System.out.println(clientEmail + " ("
			// + Thread.currentThread().getName()
			// + ") ha appena aumentato il proprio clientSeqNumber a "
			// + clientSeqNumber + ", e needed è " + needed);
		}

		myNotifications.add(n);
		// System.out.println(clientEmail + " ("
		// + Thread.currentThread().getName()
		// + ") abbandona waitForNotification()");
		return myNotifications;

	}

	@Override
	public boolean sendNotification(Notification notification) {
		log.info("Sta per mandare la notifica: eventKind="+notification.getEventKind()+" --- senderMail="+notification.getSenderEmail());
		synchronized (this) {
			seqNumber++;

			// System.out.println("\n" + notification.getSenderEmail() + " ("
			// + Thread.currentThread().getName()
			// + ") sta per lanciare una notifica:\n------ seqNumber = "
			// + seqNumber + "\n------ eventKind = "
			// + notification.getEventKind() + "\n------ memberEmail = "
			// + notification.getMemberEmail() + "\n");

			notification.setSequenceNumber(seqNumber);
			
			log.info("Sto per aggiungere una notifica alla lista, la lista ha dimensione "+notificationsList.size());
			
			notificationsList.add(notification);
			
			log.info("Adesso la lista ha dimensione "+notificationsList.size());

			// comporta l’estrazione di tutti i thread
			// da wait set ed il loro inserimento in entry set.
			notifyAll();
		}
		return false;
	}

	@Override
	public Long getInvitedTableKey(String code, String email) {

		Invitation i = queryInvitationByCode("code", code);
		if (i == null)
			return (long) -1;
		else {
			deleteInvitation(i.getKey());
			return i.getTableKey();
		}
	}

	public void deleteInvitation(Long key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			Invitation i = pm.getObjectById(Invitation.class, key);
			pm.deletePersistentAll(i);
		} catch (Exception e) {
			System.err.println("Something gone wrong deleting the Invitation: "
					+ e);
		} finally {
			pm.close();
		}
	}

	public Invitation queryInvitationByCode(String fieldName, String fieldValue) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(Invitation.class);
		query.setFilter(fieldName + " == param");
		query.declareParameters("String param");
		Invitation detachedInvitation = null;
		try {
			@SuppressWarnings("unchecked")
			List<Invitation> results = (List<Invitation>) query
					.execute(fieldValue);
			if (!results.isEmpty())
				detachedInvitation = pm.detachCopy(results.get(0));
		} catch (Exception e) {
			System.err.println("Something gone wrong querying the invitation: "
					+ e);
		} finally {
			query.closeAll();
			pm.close();
		}
		return detachedInvitation;
	}
}
