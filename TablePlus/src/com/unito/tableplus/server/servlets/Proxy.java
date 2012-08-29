package com.unito.tableplus.server.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.unito.tableplus.server.TableQueries;
import com.unito.tableplus.server.UserQueries;
import com.unito.tableplus.server.WalletQueries;
import com.unito.tableplus.server.services.DriveServiceImpl;
import com.unito.tableplus.shared.model.BlackBoardMessageType;
import com.unito.tableplus.shared.model.DriveFile;
import com.unito.tableplus.shared.model.BlackBoardMessage;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.User;
import com.unito.tableplus.shared.model.Wallet;

public class Proxy extends HttpServlet {

	private static final long serialVersionUID = -6455653509373554816L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		InputStream is = req.getInputStream();
		try {
			JSONTokener jt = new JSONTokener(is);
			JSONObject jo = new JSONObject(jt);

			String request = jo.getString("request");
			PrintWriter pw = resp.getWriter();

			if (request.equals("queryUser"))
				queryUser(jo, pw);
			else if (request.equals("queryUsers"))
				queryUsers(jo, pw);
			else if (request.equals("queryUsersStatus"))
				queryUsersStatus(jo, pw);
			else if (request.equals("queryTable"))
				queryTable(jo, pw);
			else if (request.equals("queryTables"))
				queryTables(jo, pw);
			else if (request.equals("queryMessages"))
				queryMessages(jo, pw);
			else if (request.equals("writeMessage"))
				writeMessage(jo, pw);
			else if (request.equals("deleteMessage"))
				deleteMessage(jo, pw);
			else if (request.equals("toggleHide"))
				toggleHide(jo, pw);
			else if (request.equals("toggleStatus"))
				toggleStatus(jo, pw);
			else if (request.equals("setPresence"))
				setPresence(jo, pw);
			else {
				JSONObject rj = new JSONObject();
				rj.put("status", "ERROR");
				rj.put("error", "Request unkown.");
				pw.print(rj);
				pw.flush();
				pw.close();
			}

		} catch (Exception e) {
			System.err.println("Error while processing post request.");
			System.err.println(e);
		}
	}

	private void queryUser(JSONObject jo, PrintWriter pw) {
		try {
			String email = jo.getString("userEmail");
			User user = UserQueries.queryUser("email", email);
			JSONObject rj = new JSONObject();
			JSONObject uj = new JSONObject();

			if (user == null) {
				rj.put("status", "ERROR");
				rj.put("error", "User not found");
			} else {
				rj.put("status", "OK");

				uj.put("key", user.getKey());
				uj.put("username", user.getUsername());
				uj.put("firstname", user.getFirstName());
				uj.put("lastname", user.getLastName());
				uj.put("email", user.getEmail());
				uj.put("tables", user.getTables());
				//FIXME
				//uj.put("online", user.isOnline());

				rj.put("results", uj);
			}

			pw.print(rj);
			pw.flush();

		} catch (Exception e) {
			System.err.println("Error while querying user");
			System.err.println(e);
		} finally {
			pw.close();
		}
	}

	private void queryUsers(JSONObject jo, PrintWriter pw) {
		try {
			JSONArray kja, uja;
			JSONObject uj, rj;

			kja = jo.getJSONArray("userKeysList");
			int size = kja.length();
			List<Long> keys = new LinkedList<Long>();
			for (int i = 0; i < size; i++) {
				keys.add(kja.getLong(i));
			}

			List<User> users = UserQueries.queryUsers(keys);
			uja = new JSONArray();
			for (User user : users) {
				uj = new JSONObject(user);
				uja.put(uj);
			}

			rj = new JSONObject();
			rj.put("status", "OK");
			rj.put("results", uja);

			pw.print(rj);
			pw.flush();

		} catch (Exception e) {
			System.err.println("Error while querying users");
			System.err.println(e);
		} finally {
			pw.close();
		}
	}

	private void queryTable(JSONObject jo, PrintWriter pw) {
		try {
			Long tableKey = jo.getLong("tableKey");
			Long userKey = jo.getLong("userKey");
			Table table = TableQueries.queryTable(tableKey);
			JSONObject rj = new JSONObject();
			JSONObject tableJSON = new JSONObject();
			JSONArray jDocuments = new JSONArray();
			JSONObject jDoc;

			if (table == null) {
				rj.put("status", "ERROR");
				rj.put("error", "No table found for requested key");
			} else {

				tableJSON.put("key", table.getKey());
				tableJSON.put("name", table.getName());
				tableJSON.put("members", table.getMembers());
				tableJSON.put("creator", table.getCreator());
				tableJSON.put("owner", table.getOwner());

				Wallet wallet = WalletQueries.getWallet(userKey);
				List<DriveFile> docList = DriveServiceImpl
						.getDriveFileList(wallet.getDriveToken());
				Map<String, DriveFile> docsMap = new HashMap<String, DriveFile>();
				for (DriveFile d : docList)
					docsMap.put(d.getDocId(), d);
				List<String> tableDocuments = table.getDocuments();
				for (String d : tableDocuments) {
					jDoc = new JSONObject(docsMap.get(d));
					jDocuments.put(jDoc);
				}

				tableJSON.put("documents", jDocuments);

				rj.put("results", tableJSON);
				rj.put("status", "OK");
			}
			pw.print(rj);
			pw.flush();
		} catch (Exception e) {
			System.err.println("Error while querying table");
			System.err.println(e);
		} finally {
			pw.close();
		}
	}

	private void queryTables(JSONObject jo, PrintWriter pw) {
		try {
			JSONArray tableKeysArray = jo.getJSONArray("tableKeysList");
			List<Long> queryList = new ArrayList<Long>();
			JSONObject tableJSON, rj;
			JSONArray ja;

			for (int i = 0; i < tableKeysArray.length(); i++) {
				queryList.add(tableKeysArray.getLong(i));
			}

			List<Table> tables = TableQueries.queryTables(queryList);

			rj = new JSONObject();

			if (tables == null) {
				rj.put("status", "ERROR");
				rj.put("error", "No tables found for requested key");
			} else {
				rj.put("status", "OK");
				ja = new JSONArray();
				for (Table table : tables) {
					tableJSON = new JSONObject();
					tableJSON.put("key", table.getKey());
					tableJSON.put("name", table.getName());
					tableJSON.put("members", table.getMembers().size());
					tableJSON.put("creator", table.getCreator());
					tableJSON.put("owner", table.getOwner());
					tableJSON.put("messages", table.getBlackBoard().size());
					tableJSON.put("documents", table.getDocuments().size());
					ja.put(tableJSON);
				}
				rj.put("results", ja);
			}

			pw.print(rj);
			pw.flush();

		} catch (Exception e) {
			System.err.println("Error while querying tables");
			System.err.println(e);
		} finally {
			pw.close();
		}

	}

	private void queryMessages(JSONObject jo, PrintWriter pw) {
		Long tableKey;
		JSONObject rj = new JSONObject();
		try {
			tableKey = jo.getLong("tableKey");
			Table table = TableQueries.queryTable(tableKey);
			if (table == null) {
				rj.put("status", "ERROR");
				rj.put("error", "No table found for requested key");
			} else {
				rj.put("status", "OK");
				List<BlackBoardMessage> bbMessages = table.getBlackBoard();

				int n = 1;
				int size = bbMessages.size();
				JSONArray ja = new JSONArray();
				JSONObject jm;
				BlackBoardMessage m;

				while (size - n >= 0 && n < 10) {
					jm = new JSONObject();
					m = bbMessages.get(size - n);

					jm.put("key", m.getKey());
					jm.put("timestamp", m.getDate());
					jm.put("author", m.getAuthor());
					jm.put("type", m.getType().toString());
					jm.put("content", m.getContent());

					ja.put(jm);

					n++;
				}

				rj.put("results", ja);

				pw.print(rj);
				pw.flush();
			}
		} catch (Exception e) {
			System.err.println("Error while querying messages");
			System.err.println(e);
		} finally {
			pw.close();
		}
	}

	private void writeMessage(JSONObject jo, PrintWriter pw) {
		try {
			JSONObject rj = new JSONObject();
			Long tableKey = jo.getLong("tableKey");
			String author = jo.getString("authorKey");
			String type = jo.getString("messageType");
			String content = jo.getString("messageContent");

			BlackBoardMessageType mType = Enum.valueOf(BlackBoardMessageType.class, type);

			BlackBoardMessage bbMessage = new BlackBoardMessage(author, mType, content);

			Table table = TableQueries.queryTable(tableKey);
			table.getBlackBoard().add(bbMessage);
			TableQueries.storeTable(table);

			rj.put("status", "OK");
			pw.print(rj);
			pw.flush();

		} catch (Exception e) {
			System.err.println("Error while writing message.");
			System.err.println(e);
		} finally {
			pw.close();
		}
	}

	private void deleteMessage(JSONObject jo, PrintWriter pw) {
		JSONObject rj = new JSONObject();
		String key;
		try {
			key = jo.getString("messageKey");
			TableQueries.removeMessage(key);

			rj.put("status", "OK");
			pw.print(rj);
			pw.flush();

		} catch (Exception e) {
			System.err.println("Error while deleting message.");
			System.err.println(e);
		} finally {
			pw.close();
		}
	}

	private void queryUsersStatus(JSONObject jo, PrintWriter pw) {
		List<Long> visible, others;
		JSONObject sj, rj;
		JSONArray online, offline;
		Long tableKey;
		try {
			tableKey = jo.getLong("tableKey");
			Table t = TableQueries.queryTable(tableKey);
			//FIXME: t.getSelectivePresenceMembers() - getHiddenMembers()
			visible = t.getMembers();
			visible.removeAll(t.getMembers());
			online = new JSONArray(visible);
			others = t.getMembers();
			others.removeAll(visible);
			offline = new JSONArray(others);

			sj = new JSONObject();
			sj.put("online", online);
			sj.put("offline", offline);

			rj = new JSONObject();
			rj.put("status", "OK");
			rj.put("results", sj);

			pw.print(rj);
			pw.flush();

		} catch (Exception e) {
			System.err.println("Error while querying users status.");
			System.err.println(e);
		} finally {
			pw.close();
		}
	}

	private void toggleHide(JSONObject jo, PrintWriter pw) {
		try {
			Long user = jo.getLong("userKey");
			Long tableKey = jo.getLong("tableKey");
			Table table = TableQueries.queryTable(tableKey);
			//FIXME: getHiddenMembers
			List<Long> hiddenMembers = table.getMembers();

			if (hiddenMembers.contains(user))
				hiddenMembers.remove(user);
			else
				hiddenMembers.add(user);
			TableQueries.storeTable(table);

			JSONObject rj = new JSONObject();
			rj.put("status", "OK");
			pw.print(rj);
			pw.flush();

		} catch (Exception e) {
			System.err.println("Error while toggling hide.");
			System.err.println(e);
		} finally {
			pw.close();
		}
	}

	private void toggleStatus(JSONObject jo, PrintWriter pw) {
		try {
			Long user = jo.getLong("userKey");
			//Boolean online = jo.getBoolean("online");

			User u = UserQueries.queryUser(user);
			//FIXME
			//u.setOnline(online);
			UserQueries.storeUser(u);

			JSONObject rj = new JSONObject();
			rj.put("status", "OK");
			pw.print(rj);
			pw.flush();

		} catch (Exception e) {
			System.err.println("Error while toggling status.");
			System.err.println(e);
		} finally {
			pw.close();
		}
	}

	private void setPresence(JSONObject jo, PrintWriter pw) {
		try {
			Table table;

			Long user = jo.getLong("userKey");
			Long tableKey = jo.getLong("tableKey");
			Boolean presence = jo.getBoolean("presence");

			table = TableQueries.queryTable(tableKey);
			
			//FIXME: getSelectivePresenceMembers()
			if (presence)
				table.getMembers().add(user);
			else
				table.getMembers().remove(user);
	
			TableQueries.storeTable(table);

			JSONObject rj = new JSONObject();
			rj.put("status", "OK");
			pw.print(rj);
			pw.flush();

		} catch (Exception e) {
			System.err.println("Error while changing presence.");
			System.err.println(e);
		} finally {
			pw.close();
		}
	}
}