package com.unito.tableplus.server.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.unito.tableplus.shared.model.Group;
import com.unito.tableplus.shared.model.Message;
import com.unito.tableplus.shared.model.User;

public class Proxy extends HttpServlet {

	private static final long serialVersionUID = -6455653509373554816L;

	GroupServiceImpl groupProxy = new GroupServiceImpl();
	UserServiceImpl userProxy = new UserServiceImpl();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		InputStream is = req.getInputStream();
		try {
			JSONTokener jt = new JSONTokener(is);
			JSONObject jo = new JSONObject(jt);
			String request = jo.getString("request");
			PrintWriter pw = resp.getWriter();

			if(request.equals("queryUser"))
				queryUser(jo, pw);
			else if(request.equals("queryTable"))
				queryTable(jo, pw);
			else if(request.equals("queryTables"))
				queryTables(jo, pw);

		} catch (JSONException e) {
			System.err.println("Error while processing post request.");
			System.err.println(e);
			e.printStackTrace();
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
				
				rj.put("results", uj);
			}

			pw.print(rj.toString());
			pw.flush();
			pw.close();

		} catch (JSONException e) {
			System.err.println("Error while querying user");
			System.err.println(e);
			e.printStackTrace();
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

				List<Message> messages = group.getBlackBoard();
				int size = messages.size();

				table.put("messages",
						group.getBlackBoard().subList(size - 11, size - 1));
				table.put("documents", group.getDocuments());

				rj.put("results", table);
			}
			
			pw.print(rj.toString());
			pw.flush();
			pw.close();

		} catch (JSONException e) {
			System.err.println("Error while querying table");
			System.err.println(e);
			e.printStackTrace();
		}
	}

	private void queryTables(JSONObject jo, PrintWriter pw) {
		try {
			JSONArray tableKeysArray = jo.getJSONArray("tablesKeyList");
			List<Long> queryList = new ArrayList<Long>();
			JSONObject table = new JSONObject();
			JSONObject rj = new JSONObject();
			JSONArray ja = new JSONArray();

			for (int i = 0; i < tableKeysArray.length(); i++) {
				queryList.add((Long) tableKeysArray.getLong(i));
			}

			List<Group> groups = groupProxy.queryGroups(queryList);

			if (groups == null) {
				rj.put("status", "ERROR");
				rj.put("error", "No tables found for requested key");
			} else {
				rj.put("status", "OK");

				for (Group group : groups) {
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

			pw.print(rj.toString());
			pw.flush();
			pw.close();

		} catch (JSONException e) {
			System.err.println("Error while querying tables");
			System.err.println(e);
			e.printStackTrace();
		}

	}

}
