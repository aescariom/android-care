package org.androidcare.web.server.api;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
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
		
		final Transaction txn = pm.currentTransaction();
		
		if(user != null){
			try {
				if(req.getParameter("longitude") == null || req.getParameter("latitude") == null){
					return;
				}

				txn.begin();
				
				Position last = getLastPosition();
				float longitude = round(Float.parseFloat(req.getParameter("longitude").toString())); 
				float latitude = round(Float.parseFloat(req.getParameter("latitude").toString()));
				SimpleDateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
				Date date = new Date();
				if(req.getParameter("time") != null){
					date = format.parse(req.getParameter("time").toString());
				}
				String owner = user.getUserId();
				
				if(last != null && last.getLatitude() == latitude && last.getLongitude() == longitude){
					last.setDate(date);
					last.setServerDate();
				}else{
					Position p = new Position(latitude, longitude, owner, date);
					pm.makePersistent(p);
				}
				
				txn.commit();

	            resp.getWriter().write("{\"status\": 0}");
			} catch(Exception ex){
				txn.rollback();
	            resp.getWriter().write("{\"status\": -1}");	
	            ex.printStackTrace();
			}finally {
				pm.close();
	        } 
		}else{
            resp.getWriter().write("{\"status\": -2}");
		} 
	}  
			 
	private float round(float num){
		long aux = Math.round(num * 10000);
		return (float) aux / 10000;
	}

	private Position getLastPosition() {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		UserService userService = UserServiceFactory.getUserService();
	    User user = userService.getCurrentUser();
		
		Query query = pm.newQuery(Position.class);
	    query.setFilter("owner == reminderOwner");
	    query.declareParameters("String reminderOwner");
	    query.setRange(0, 1);
	    query.setOrdering("date descending");

	    try {
	        List<?> rs = (List<?>) query.execute(user.getUserId());
	        if(rs != null){
		        for (Object p : rs) {
		            return (Position)p;
		        }
	        }
	    } catch(Exception ex){
			ex.printStackTrace();
	    }finally {
	        query.closeAll();
	    }
	    return null;
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
