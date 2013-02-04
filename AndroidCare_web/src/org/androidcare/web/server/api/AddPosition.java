package org.androidcare.web.server.api;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.androidcare.web.server.PMF;
import org.androidcare.web.shared.persistent.Position;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class AddPosition extends HttpServlet {

	public void process(HttpServletRequest req, HttpServletResponse resp)  
			   throws IOException, ServletException { 
		
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");  

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
				SimpleDateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
				Date date = new Date();
				if(req.getParameter("time") != null){
					date = format.parse(req.getParameter("time").toString());
				}
				String owner = user.getUserId();
				
				Position p = new Position(latitude, longitude, owner, date);

	            pm.makePersistent(p);
	            resp.getWriter().write("{\"status\": 0}");
			} catch(Exception ex){
	            resp.getWriter().write("{\"status\": -1}");	
	            ex.printStackTrace();
			}finally {
				pm.close();
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
