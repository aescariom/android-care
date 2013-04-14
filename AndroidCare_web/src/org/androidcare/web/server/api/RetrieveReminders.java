package org.androidcare.web.server.api;

import java.io.*;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.*;
import javax.servlet.http.*;

import java.util.ArrayList;
import java.util.List;

import org.androidcare.web.server.PMF;
import org.androidcare.web.shared.persistent.Reminder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class RetrieveReminders extends HttpServlet {


	private static final long serialVersionUID = -4706823493170719155L;


	public void process(HttpServletRequest req, HttpServletResponse resp)  
			   throws IOException, ServletException, JSONException {  
	
		JSONArray jsonArray = null;

		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");

		PersistenceManager pm = PMF.get().getPersistenceManager();
			     
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser(); 
		List<JSONObject> list = null;
		if(user != null){

			Query query = pm.newQuery(Reminder.class);
			query.setOrdering("id asc");
			if(req.getParameter("reminderId") != null){
				List<Reminder> reminders = new ArrayList<Reminder>();
				int reminderId = Integer.parseInt(req.getParameter("reminderId").toString());
				Reminder r = (Reminder)pm.getObjectById(Reminder.class, reminderId);
				if(r != null){
					if(r.getOwner().compareToIgnoreCase(user.getUserId()) == 0){
						reminders.add(r);
					}
				}
				list = getReminderList(reminders);
			}else{
				query.setFilter("owner == reminderOwner");
				query.declareParameters("String reminderOwner");
				list = getReminderList((List<?>) query.execute(user.getUserId()));
			}  
		}else{
			resp.getWriter().write("{\"status\": -2}");
		}
		//Create a JSONArray based from the list of JSONObejcts  
		jsonArray = new JSONArray(list);   
		//Then output the JSON string to the servlet response  
		resp.getWriter().println(jsonArray.toString());
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)  
		throws IOException, ServletException {  
		try {
			process(req, resp);
		} catch (JSONException e) {
			e.printStackTrace();
		}  
	}  

	public void doGet(HttpServletRequest req, HttpServletResponse resp)  
	    throws IOException, ServletException {  
	    try {
			process(req, resp);
		} catch (JSONException e) {
			e.printStackTrace();
		}  
	}  
	
	public List<JSONObject> getReminderList(List<?> reminders){		
		ArrayList<JSONObject> list = new ArrayList<JSONObject>();
		for(Object reminder : reminders){
			list.add(new JSONObject(new Reminder((Reminder)reminder)));  
		}
		return list;
	}
}
