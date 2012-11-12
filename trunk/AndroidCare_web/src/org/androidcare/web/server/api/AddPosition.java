package org.androidcare.web.server.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.androidcare.web.server.PMF;
import org.androidcare.web.shared.persistent.Alert;
import org.androidcare.web.shared.persistent.AlertLog;
import org.androidcare.web.shared.persistent.Position;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import org.androidcare.web.shared.*;


/**
 * @author Alejandro Escario MŽndez
 *
 */
public class AddPosition extends HttpServlet {

	/**
	 * 
	 * @param req
	 * @param resp
	 * @throws IOException
	 * @throws ServletException
	 */
	public void process(HttpServletRequest req, HttpServletResponse resp)  
			   throws IOException, ServletException { 
		
		resp.setContentType("text/plain");  

		PersistenceManager pm = PMF.get().getPersistenceManager();
			     
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(user != null){
			try {
				if(req.getParameter("longitude") == null || req.getParameter("latitude") == null){
					return;
				}
				float longitude = Float.parseFloat(req.getParameter("longitude").toString()); 
				float latitude = Float.parseFloat(req.getParameter("latitude").toString()); 
				String owner = user.getUserId();
				
				Position p = new Position(latitude, longitude, owner);

	            pm.makePersistent(p);
	            resp.getWriter().write("{\"status\": 0}");
			} catch(Exception ex){
	            resp.getWriter().write("{\"status\": -1}");				
			}finally {
				pm.close();
	        } 
		}else{
            resp.getWriter().write("{\"status\": -2}");
		} 
	}  
			 
	/**
	 * 
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse resp)  
		throws IOException, ServletException {  
		process(req, resp);  
	}  
	/**
	 * 		  
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)  
	    throws IOException, ServletException {  
	    process(req, resp);  
	}  
}
