package org.androidcare.web.server.module.dashboard;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.androidcare.web.client.module.dashboard.rpc.PositionService;
import org.androidcare.web.server.PMF;
import org.androidcare.web.shared.persistent.Position;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class PositionServiceImpl extends RemoteServiceServlet implements
		PositionService {

	@Override
	public List<Position> getLastPositions(int numOfPositions) {
		List<Position> positions = new ArrayList<Position>();
		PersistenceManager pm = PMF.get().getPersistenceManager();

		UserService userService = UserServiceFactory.getUserService();
	    User user = userService.getCurrentUser();
		
		Query query = pm.newQuery(Position.class);
	    query.setFilter("owner == reminderOwner");
	    query.declareParameters("String reminderOwner");
	    query.setRange(0, numOfPositions);
	    query.setOrdering("date descending");

	    try {
	        List<?> rs = (List<?>) query.execute(user.getUserId());
	        if(rs != null){
		        for (Object position : rs) {
		            positions.add(new Position((Position)position));
		        }
	        }
	    } catch(Exception ex){
			ex.printStackTrace();
	    }finally {
	        query.closeAll();
	    }
	    return positions;
	}
}
