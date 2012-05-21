package com.unito.tableplus.server.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.unito.tableplus.shared.model.Group;
import com.unito.tableplus.shared.model.Message;
import com.unito.tableplus.shared.model.MessageType;
import com.unito.tableplus.shared.model.User;

public class Proxy extends HttpServlet {

	private static final long serialVersionUID = -6455653509373554816L;

	GroupServiceImpl groupProxy = new GroupServiceImpl();
	UserServiceImpl userProxy = new UserServiceImpl();
	TokenServiceImpl tokenProxy = new TokenServiceImpl();

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
			else if (request.equals("switchTable"))
				switchTable(jo, pw);
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
			User user = userProxy.queryUser("email", email);
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
				uj.put("tables", user.getGroups());
				uj.put("online", user.isOnline());

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

			List<User> users = userProxy.queryUsers(keys);
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
			Group group = groupProxy.queryGroup(tableKey);
			JSONObject rj = new JSONObject();
			JSONObject table = new JSONObject();

			if (group == null) {
				rj.put("status", "ERROR");
				rj.put("error", "No table found for requested key");
			} else {
				rj.put("status", "OK");

				table.put("key", group.getKey());
				table.put("name", group.getName());
				table.put("members", group.getMembers());
				table.put("creator", group.getCreator());
				table.put("owner", group.getOwner());
				table.put("documents", group.getDocuments());

				rj.put("results", table);
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
			JSONObject table, rj;
			JSONArray ja;

			for (int i = 0; i < tableKeysArray.length(); i++) {
				queryList.add((Long) tableKeysArray.getLong(i));
			}

			List<Group> groups = groupProxy.queryGroups(queryList);

			rj = new JSONObject();

			if (groups == null) {
				rj.put("status", "ERROR");
				rj.put("error", "No tables found for requested key");
			} else {
				rj.put("status", "OK");
				ja = new JSONArray();
				for (Group group : groups) {
					table = new JSONObject();
					table.put("key", group.getKey());
					table.put("name", group.getName());
					table.put("members", group.getMembers().size());
					table.put("creator", group.getCreator());
					table.put("owner", group.getOwner());
					table.put("messages", group.getBlackBoard().size());
					table.put("documents", group.getDocuments().size());
					ja.put(table);
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
			Group group = groupProxy.queryGroup(tableKey);
			if (group == null) {
				rj.put("status", "ERROR");
				rj.put("error", "No table found for requested key");
			} else {
				rj.put("status", "OK");
				List<Message> messages = group.getBlackBoard();

				int n = 1;
				int size = messages.size();
				JSONArray ja = new JSONArray();
				JSONObject jm;
				Message m;

				while (size - n >= 0 && n < 10) {
					jm = new JSONObject();
					m = messages.get(size - n);

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
			Long table = jo.getLong("tableKey");
			Long author = jo.getLong("authorKey");
			String type = jo.getString("messageType");
			String content = jo.getString("messageContent");

			MessageType mType = Enum.valueOf(MessageType.class, type);

			Message message = new Message(author, mType, content);

			Group group = groupProxy.queryGroup(table);
			group.getBlackBoard().add(message);
			groupProxy.storeGroup(group);

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
			groupProxy.removeMessage(key);

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
		Long key;
		try {
			key = jo.getLong("tableKey");
			Group g = groupProxy.queryGroup(key);

			visible = g.getSelectivePresenceMembers();
			visible.removeAll(g.getHiddenMembers());
			online = new JSONArray(visible);
			others = g.getMembers();
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
			Long table = jo.getLong("tableKey");
			Group group = groupProxy.queryGroup(table);
			List<Long> hiddenMembers = group.getHiddenMembers();

			if (hiddenMembers.contains(user))
				hiddenMembers.remove(user);
			else
				hiddenMembers.add(user);
			groupProxy.storeGroup(group);

			JSONObject rj = new JSONObject();
			rj.put("status", "OK");
			pw.print(rj);
			pw.flush();

		} catch (Exception e) {
			System.err.println("Error while querying users status.");
			System.err.println(e);
		} finally {
			pw.close();
		}
	}
	
	private void toggleStatus(JSONObject jo, PrintWriter pw){
		try {
			Long user = jo.getLong("userKey");
			String status = jo.getString("status");

			User u = userProxy.queryUser(user);
			if(status.equals("online"))
				u.setOnline(true);
			else
				u.setOnline(false);
			userProxy.storeUser(u);
			
			JSONObject rj = new JSONObject();
			rj.put("status", "OK");
			pw.print(rj);
			pw.flush();

		} catch (Exception e) {
			System.err.println("Error while querying users status.");
			System.err.println(e);
		} finally {
			pw.close();
		}
	}

	private void switchTable(JSONObject jo, PrintWriter pw) {
		try {
			Group group;

			Long user = jo.getLong("userKey");
			Long currentTable = jo.getLong("currentTable");

			if (jo.has("prevTable")) {
				Long prevTable = jo.getLong("prevTable");
				group = groupProxy.queryGroup(prevTable);
				group.getSelectivePresenceMembers().remove(user);
				groupProxy.storeGroup(group);
			}

			group = groupProxy.queryGroup(currentTable);
			group.getSelectivePresenceMembers().add(user);
			groupProxy.storeGroup(group);

			JSONObject rj = new JSONObject();
			rj.put("status", "OK");
			pw.print(rj);
			pw.flush();

		} catch (Exception e) {
			System.err.println("Error while querying users status.");
			System.err.println(e);
		} finally {
			pw.close();
		}
	}
}
