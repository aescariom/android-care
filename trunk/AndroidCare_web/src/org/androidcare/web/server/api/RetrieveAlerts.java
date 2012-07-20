/** 
 * This library/program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library/program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library/program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.androidcare.web.server.api;

import java.io.*;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.*;
import javax.servlet.http.*;

import java.util.ArrayList;
import java.util.List;

import org.androidcare.web.server.PMF;
import org.androidcare.web.shared.persistent.Alert;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * 
 * @author Alejandro Escario MŽndez
 *
 */
public class RetrieveAlerts extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4706823493170719155L;

	/**
	 * 
	 * @param req
	 * @param resp
	 * @throws IOException
	 * @throws ServletException
	 */
	public void process(HttpServletRequest req, HttpServletResponse resp)  
			   throws IOException, ServletException {  
	
		JSONArray jsonArray = null;
		
		resp.setContentType("text/plain");  

		PersistenceManager pm = PMF.get().getPersistenceManager();
			     
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if(user != null){
			Query query = pm.newQuery(Alert.class);
			query.setFilter("owner == alertOwner");
			query.declareParameters("String alertOwner");
			query.setOrdering("title asc");
	
			List<Alert> alerts = (List<Alert>) query.execute(user.getUserId()); 
			ArrayList<JSONObject> list = new ArrayList<JSONObject>();
			for(Alert a : alerts) {   
				list.add(new JSONObject(a));  
			}  
			//Create a JSONArray based from the list of JSONObejcts  
			jsonArray = new JSONArray(list);   
		}else{
			jsonArray = new JSONArray();
		}
		//Then output the JSON string to the servlet response  
		resp.getWriter().println(jsonArray.toString()); 
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
