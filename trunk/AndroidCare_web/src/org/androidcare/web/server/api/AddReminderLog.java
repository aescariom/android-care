package org.androidcare.web.server.api;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.androidcare.web.server.PMF;
import org.androidcare.web.shared.persistent.Reminder;
import org.androidcare.web.shared.persistent.ReminderLog;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import org.androidcare.web.shared.*;


@SuppressWarnings("serial")
public class AddReminderLog extends HttpServlet {

	public void process(HttpServletRequest req, HttpServletResponse resp)  
			   throws IOException, ServletException {  

		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json"); 

		PersistenceManager pm = PMF.get().getPersistenceManager();
			     
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(user != null){
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Transaction txn = datastore.beginTransaction();
			try {
				if(req.getParameter("reminderId") == null || req.getParameter("statusCode") == null){
					return;
				}
				int reminderId = Integer.parseInt(req.getParameter("reminderId").toString()); 
				int statusCode = Integer.parseInt(req.getParameter("statusCode").toString()); 
				SimpleDateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
				Date date = new Date();
				if(req.getParameter("time") != null){
					date = format.parse(req.getParameter("time").toString());
				}
				
				Reminder a = (Reminder)pm.getObjectById(Reminder.class, reminderId);

				if(a.getOwner().compareToIgnoreCase(user.getUserId()) != 0){
					return;
				}
				
				ReminderLog log = new ReminderLog();
				log.setTime(date);
				log.setCode(ReminderStatusCode.getByCode(statusCode));
				
				a.addLog(log);

			    txn.commit();

	            resp.getWriter().write("{\"status\": 0}");
			} catch (JDOObjectNotFoundException e){
	            resp.getWriter().write("{\"status\": -1}");
				e.printStackTrace();
			} catch (ParseException e) {
	            resp.getWriter().write("{\"status\": -1}");
				e.printStackTrace();
			}finally {
			    if (txn.isActive()) {
			        txn.rollback();
			    }
			}   
		}else{
            resp.getWriter().write("{\"status\": -2}");
		}
	}  
		
	public void doPost(HttpServletRequest req, HttpServletResponse resp)  
		throws IOException, ServletException {  
		process(req, resp);  
	}  

	public void doGet(HttpServletRequest req, HttpServletResponse resp)  
	    throws IOException, ServletException {  
	    process(req, resp);  
	}  
}
