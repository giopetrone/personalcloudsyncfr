package com.unito.tableplus.server.services;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.unito.tableplus.client.services.TableService;
import com.unito.tableplus.server.TableQueries;
import com.unito.tableplus.shared.model.BlackBoardMessage;
import com.unito.tableplus.shared.model.Bookmark;
import com.unito.tableplus.shared.model.DriveFile;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.User;

public class TableServiceImpl extends RemoteServiceServlet implements TableService {

	private static final long serialVersionUID = -3403034728905706407L;

	@Override
	public List<Table> queryTables(List<Long> keys) {
		return TableQueries.queryTables(keys);
	}

	@Override
	public Long storeTable(Table table) {
		return TableQueries.storeTable(table);
	}

	@Override
	public Table queryTable(Long key) {
		return TableQueries.queryTable(key);
	}

	@Override
	public void deleteTable(Long key) {
		TableQueries.deleteTable(key);
	}

	@Override
	public boolean addBlackBoardMessage(Long key, BlackBoardMessage bbMessage) {
		return TableQueries.addBlackBoardMessage(key, bbMessage);
	}

	@Override
	public void removeMessage(String messageKey) {
		TableQueries.removeMessage(messageKey);
	}

	@Override
	public boolean clearMessages(Long key) {
		return TableQueries.clearMessages(key);
	}

	@Override
	public boolean addDocumentToTable(String DocId, User user, Long tableKey) {
		return TableQueries.addResource(DocId, user, tableKey);
	}

	@Override
	public void docAccessToNewMember(User newMember, Table table) {
		TableQueries.docAccessToNewMember(newMember, table);
	}

	@Override
	public List<DriveFile> getTableDriveFiles(Table table) {
		return TableQueries.getTableDriveFiles(table);
	}

	@Override
	public void addMember(Long userKey, Long tableKey) {
		TableQueries.addMember(userKey, tableKey);
	}

	@Override
	public List<BlackBoardMessage> getTableMessages(Long tableKey) {
		return TableQueries.getBlackBoardMessages(tableKey);
	}

	@Override
	public List<Bookmark> getTableBookmark(Long tableKey) {
		return TableQueries.getBookmark(tableKey);
	}

	@Override
	public boolean addBookmark(Long key, Bookmark bookmark) {
		return TableQueries.addBookmark(key, bookmark);
	}

	@Override
	public void removeBookmark(String key) {
		TableQueries.removeBookmark(key);
		
	}

}
