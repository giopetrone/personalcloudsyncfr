package com.unito.tableplus.client.gui;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.desktop.client.Shortcut;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.gui.windows.DocWindow;
import com.unito.tableplus.client.gui.windows.TableChatWindow;
import com.unito.tableplus.client.gui.windows.TableResourcesWindow;
import com.unito.tableplus.client.gui.windows.WindowPlus;
import com.unito.tableplus.client.services.TableService;
import com.unito.tableplus.client.services.TableServiceAsync;
import com.unito.tableplus.client.services.NotificationService;
import com.unito.tableplus.client.services.NotificationServiceAsync;
import com.unito.tableplus.client.services.UserService;
import com.unito.tableplus.client.services.UserServiceAsync;
import com.unito.tableplus.shared.model.Document;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.Notification;
import com.unito.tableplus.shared.model.User;

public class TableUI {

        // componenti
        public RightPanel rightPanel;
        private List<WindowPlus> windows = new ArrayList<WindowPlus>();
        private List<Shortcut> shortcuts = new ArrayList<Shortcut>();
        public WindowPlus tableChatWindow;

        // informazioni
        public String tableName;
        public Long tableKey;
        public List<String> onlineMembersEmail = new ArrayList<String>();
        public List<String> offlineMembersEmail = new ArrayList<String>();
        public List<String> hiddenMembersEmail = new ArrayList<String>();
        public List<String> selectivePresenceMembers = new ArrayList<String>();
        public List<Document> googleDocuments = new ArrayList<Document>();

        // servizi
        public final UserServiceAsync userService = GWT.create(UserService.class);
        public final TableServiceAsync tableService = GWT
                        .create(TableService.class);
        public final NotificationServiceAsync notificationService = GWT
                        .create(NotificationService.class);

        // altro
        public Timer timer = null;
        public boolean selectivePresence;
        public boolean hiddenUser = false;

        /**
         * Costruttore per personal table
         *
         * @return void
         */

        public TableUI() {
                this.rightPanel = new RightPanel(this, false);
        }

        /**
         * Costruttore per table table
         *
         * @return void
         */

        public TableUI(Table table) {
                this.tableName = table.getName();
                this.tableKey = table.getKey();
                this.rightPanel = new RightPanel(this, true);

                // -(1)- crea delle finestre; credo che di default, una volta
                // aggiunte al desktop, siano inizialmente invisibili
                WindowPlus tableResourcesWindow = new TableResourcesWindow();// createGridWindow();
                addWindow(tableResourcesWindow);
                tableChatWindow = new TableChatWindow(this);
                addWindow(tableChatWindow);

                // table resources
                Shortcut s = new Shortcut();
                s.setText("Table Resources");
                s.setId("tableresources-win-shortcut");
                s.setData("window", tableResourcesWindow);
                this.addShortcut(s);

                // table chat
                Shortcut s2 = new Shortcut();
                s2.setText("Table Chat");
                s2.setId("chat-win-shortcut");
                s2.setData("window", tableChatWindow);
                this.addShortcut(s2);

                createMembersList(table);
                createGoogleDocsList(table);

        }

        /**
         * Crea la lista dei membri online/offline
         *
         * @return void
         */

        public void createMembersList(Table table_) {
                final Table table = table_;
                userService.queryUsers(table.getMembers(),
                                new AsyncCallback<List<User>>() {
                                        @Override
                                        public void onFailure(Throwable caught) {
                                        }

                                        @Override
                                        public void onSuccess(List<User> result) {
                                                if (result != null)
                                                        // controllo ogni utente per decidere se inserirlo
                                                        // nella lista di onlineMembers o offlineMembers
                                                        for (User u : result) {

                                                                // (10) nella lista degli ONLINE ci vanno:
                                                                // --- membri online
                                                                // --- && non nascosti
                                                                // --- && con presenceSelective

                                                                if (u.isOnline()
                                                                                && !(table.getHiddenMembers()
                                                                                                .contains(u.getKey()))
                                                                                && table.getSelectivePresenceMembers()
                                                                                                .contains(u.getKey()))
                                                                        onlineMembersEmail.add(u.getEmail());

                                                                // (20) altrimenti vanno nella lista degli
                                                                // OFFLINE

                                                                else
                                                                        offlineMembersEmail.add(u.getEmail());

                                                                // (30) aggiungo i membri invisibili a
                                                                // hiddenMembers
                                                                if (table.getHiddenMembers().contains(
                                                                                u.getKey()))
                                                                        hiddenMembersEmail.add(u.getEmail());

                                                                // (40) aggiungo i membri con presenceSelective
                                                                // a
                                                                // selectivePresenceMembers
                                                                if (table.getSelectivePresenceMembers()
                                                                                .contains(u.getKey()))
                                                                        selectivePresenceMembers.add(u.getEmail());
                                                        }
                                                // setto il togglebutton pressato o meno
                                                if (hiddenMembersEmail.contains(TablePlus.user
                                                                .getEmail()))
                                                        hiddenUser = true;

                                                rightPanel.membersPanel.setHidden.toggle(hiddenUser);
                                                rightPanel.membersPanel.addData();
                                        }
                                });
        }

        /**
         * Crea la lista dei google documents del tavolo
         *
         * @return void
         */

        public void createGoogleDocsList(Table table) {
                tableService.getTableDocuments(table,
                                new AsyncCallback<List<Document>>() {
                                        @Override
                                        public void onFailure(Throwable caught) {
                                        }

                                        @Override
                                        public void onSuccess(List<Document> result) {
                                                googleDocuments = result;

                                                rightPanel.tableResourcesPanel.addData();
                                        }
                                });
        }

        /**
         * Gestisce il passaggio offline per la presence selettiva
         *
         * @return void
         */

        public void selectivePresenceOff() {
                // (10) aggiorno il flag
                selectivePresence = false;

                // (20) aggiorno l'oggetto Table nel DB
                tableService.removeSelectivePresenceMemberFromTable(
                                TablePlus.user.getKey(), tableKey,
                                new AsyncCallback<Boolean>() {
                                        @Override
                                        public void onFailure(Throwable caught) {
                                        }

                                        @Override
                                        public void onSuccess(Boolean result) {
                                        }
                                });

                // (30) lancio la notifica
                Notification n = new Notification();
                n.setEventKind("SELECTIVEPRESENCEOFF");
                n.setSenderEmail(TablePlus.user.getEmail());
                n.setMemberEmail(TablePlus.user.getEmail());
                n.setSenderKey(TablePlus.user.getKey());
                n.setTableKey(tableKey);
                throwNotification(n);
        }

        /**
         * Gestisce il passaggio online per la presence selettiva
         *
         * @return void
         */

        public void selectivePresenceOn() {
                // System.out.println("Selective presence on");

                // (10) aggiorno il flag
                selectivePresence = true;

                // (20) aggiorno l'oggetto Table nel DB
                tableService.addSelectivePresenceMemberToTable(TablePlus.user.getKey(),
                                tableKey, new AsyncCallback<Boolean>() {
                                        @Override
                                        public void onFailure(Throwable caught) {
                                        }

                                        @Override
                                        public void onSuccess(Boolean result) {
                                        }
                                });

                // (30) lancio la notifica
                Notification n = new Notification();
                n.setEventKind("SELECTIVEPRESENCEON");
                n.setSenderEmail(TablePlus.user.getEmail());
                n.setMemberEmail(TablePlus.user.getEmail());
                n.setSenderKey(TablePlus.user.getKey());
                n.setTableKey(tableKey);
                throwNotification(n);
        }

        /**
         * Lancia una notifica
         *
         * @return void
         */

        public void throwNotification(Notification notification) {
                notificationService.sendNotification(notification,
                                new AsyncCallback<Boolean>() {

                                        @Override
                                        public void onFailure(Throwable caught) {
                                                // Auto-generated method stub

                                        }

                                        @Override
                                        public void onSuccess(Boolean result) {
                                                // Auto-generated method stub

                                        }

                                });
        }

        /**
         * Questo metodo viene invocato come conseguenza di un drag n drop
         * che termina sul desktop, quindi qui viene creato lo shortcut
         * corrispondente al documento che è stato trascinato e rilasciato.
         * 
         * @param docName il titolo del documento
         * @param docLink l'url al quale reperire il documento
         * @param docId id google-generated che identifica il documento
         */
        
        public void addGdocShortcut(String docName, String docLink, String docId){
        	System.out.println("dentro TableUI.addGdocShortcut()");
        	DocWindow docWindow = new DocWindow(docLink);
            addWindow(docWindow);
            
            Shortcut s = new Shortcut();
            s.setText(docName);
            s.setId("doc-win-shortcut");
            s.setData("window", docWindow);
            this.addShortcut(s);
            
            TablePlus.desktop.addShortcut(s);
            //s.addSelectionListener(shortcutListener);
			s.setVisible(true);
			s.addSelectionListener(TablePlus.desktop.shortcutListener);
			
            TablePlus.desktop.getDesktop().layout();
        }
        
        /**
         * Vari setters e getters
         *
         * @return
         */

        public RightPanel getRightPanel() {
                return rightPanel;
        }

        public void setRightPanel(RightPanel rightPanel) {
                this.rightPanel = rightPanel;
        }

        public List<WindowPlus> getWindows() {
                return windows;
        }

        public void setWindows(List<WindowPlus> windows) {
                this.windows = windows;
        }

        public void addWindow(WindowPlus wp) {
                this.windows.add(wp);
        }

        public List<Shortcut> getShortcuts() {
                return shortcuts;
        }

        public void setShortcuts(List<Shortcut> shortcuts) {
                this.shortcuts = shortcuts;
        }

        public void addShortcut(Shortcut s) {
                this.shortcuts.add(s);
        }

}

