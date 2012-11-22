package com.unito.tableplus.client.services;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.unito.tableplus.shared.model.BlackBoardMessage;
import com.unito.tableplus.shared.model.Bookmark;
import com.unito.tableplus.shared.model.Resource;
import com.unito.tableplus.shared.model.SharedResource;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.User;
import com.unito.tableplus.shared.model.UserStatus;

@RemoteServiceRelativePath("table-service")
public interface TableService extends RemoteService {
	
	

	/**
	 * This methods start a transaction storing a new Table and adding it to the
	 * user specified as creator. If something goes wrong will be executed a
	 * rollback.
	 * 
	 * @param newTable
	 *            The Table to store.
	 * @param creator
	 *            The user who is creating the table and will be set as its
	 *            owner.
	 * @return The stored table if transaction is committed or <i>null</i> otherwise.  
	 */
	Table storeNewTable(Table newTable, User creator);
	
	boolean storeTable(Table table);

	/**
	 * Adds a message on the table blackboard.
	 * 
	 * @param tableKey
	 *            The key of the table where the message should be added.
	 * @param message
	 *            The message to add.
	 * @return True if the message has been added, false otherwise.
	 */
	boolean addBlackBoardMessage(Long tableKey, BlackBoardMessage message);



	/**
	 * Adds a registered TablePlus user as a new member to a table and shares
	 * the table resources with him. Every resource will be shared according
	 * with its own share policy. For instance, when sharing a Google Drive
	 * resource, the wallet of the current user will be used for accessing to
	 * the resource.
	 * <p>
	 * If current user hasn't allowed TablePlus to access his Drive, the table
	 * resources hosted on Drive won't be shared with the new table member. They
	 * will appear in the table resource window, but user won't be able to
	 * access them.
	 * </p>
	 * 
	 * @param currentUserKey
	 *            The key of the user who is already a member of the table.
	 * @param newUserKey
	 *            The key of the user to be added.
	 * @param tableKey
	 *            The key of the table where the user will be added to.
	 * 
	 */
	void addMember(Long currentUserKey, Long newUserKey, Long tableKey);

	/**
	 * Adds a resource to a table. The resource will be shared according with
	 * its own share policy.
	 * 
	 * @param resource
	 *            The resource to share
	 * @param user
	 *            The user who is committing the share action.
	 * @param tableKey
	 *            The table to share the resource with.
	 * @return
	 */
	boolean addResource(Resource resource, User user, Long tableKey);

	boolean clearMessages(Long key);

	void deleteTable(Long key);



	List<BlackBoardMessage> loadBlackBoardMessages(Long tableKey);

	List<SharedResource> loadResources(Long tableKey);

	Table queryTable(Long key);

	Map<Long, Table> queryTables(List<Long> keys);

	void removeMessage(String messageKey);

	Map<Long, UserStatus> getUsersStatus(Long tableKey);
}