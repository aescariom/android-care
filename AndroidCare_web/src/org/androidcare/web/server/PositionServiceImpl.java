package org.androidcare.web.server;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;

import org.androidcare.web.client.PositionService;
import org.androidcare.web.shared.persistent.Position;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import javax.jdo.Query;

/**
 * The server side implementation of the RPC service.
 * @author Alejandro Escario M�ndez
 */
@SuppressWarnings("serial")
public class PositionServiceImpl extends RemoteServiceServlet implements
		PositionService {

	@Override
	public List<Position> getLastPositions(int num) {
		List<Position> list = new ArrayList<Position>();
		PersistenceManager pm = PMF.get().getPersistenceManager();

		UserService userService = UserServiceFactory.getUserService();
	    User user = userService.getCurrentUser();
		
		Query query = pm.newQuery(Position.class);
	    query.setFilter("owner == reminderOwner");
	    query.declareParameters("String reminderOwner");
	    query.setRange(0, num);
	    query.setOrdering("date descending");

	    try {
	        List<Position> rs = (List<Position>) query.execute(user.getUserId());
	        if(rs != null){
		        for (Position p : rs) {
		            list.add(p);
		        }
	        }
	    } catch(Exception ex){
			ex.printStackTrace();
	    }finally {
	        query.closeAll();
	    }
	    return list;
	}
}
