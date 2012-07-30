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

package org.androidcare.web.server;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.androidcare.web.client.AlertService;
import org.androidcare.web.shared.PeriodOfTime;
import org.androidcare.web.shared.persistent.Alert;
import org.androidcare.web.shared.persistent.AlertLog;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import javax.jdo.Query;

/**
 * The server side implementation of the RPC service.
 * @author Alejandro Escario MŽndez
 */
@SuppressWarnings("serial")
public class AlertServiceImpl extends RemoteServiceServlet implements
		AlertService {
	
	/**
	 * 
	 */
	public String saveAlert(Alert alert) {
		if(alert.getId() > 0){
			return editAlert(alert);
		}else{
			return saveNewAlert(alert);
		}
	}

	/**
	 * 
	 * @param alert
	 * @return
	 */
	private String editAlert(Alert alert) {
		final PersistenceManager pm = PMF.get().getPersistenceManager();  
		final Transaction txn = pm.currentTransaction();
		try{
			txn.begin();

			Alert a = (Alert)pm.getObjectById(Alert.class, alert.getId());
			a.setTitle(alert.getTitle());
			a.setDescription(alert.getDescription());
			a.setRepeat(alert.isRepeat());
			a.setSince(alert.getSince());
			if(alert.getUntilDate() != null){
				a.setUntilDate(alert.getUntilDate());
				a.setUntilIterations(null);
				a.setEndType(PeriodOfTime.UNTIL_DATE);
			}else if(alert.getUntilIterations() != null){
				a.setUntilIterations(alert.getUntilIterations());
				a.setEndType(PeriodOfTime.ITERATIONS);
				a.setUntilDate(null);
			}else{
				a.setUntilIterations(null);
				a.setUntilDate(null);
				a.setEndType(PeriodOfTime.NEVER_ENDS);
			}
			a.setRepeatPeriod(alert.getRepeatPeriod());
			a.setWeekDays(alert.getWeekDays());
			a.setRepeatEach(alert.getRepeatEach());
			a.setRequestConfirmation(alert.isRequestConfirmation());
			
			//setting the owner
			UserService userService = UserServiceFactory.getUserService();
		    User user = userService.getCurrentUser();
			a.setOwner(user.getUserId());
		    
			txn.commit();
		} catch(Exception ex){
	    	return ex.getMessage();
	    } finally {
	    	if (txn.isActive()) {
		        txn.rollback();
		    }
	    }
		
		return "OKKK";
	}

	/**
	 * 
	 * @param alert
	 * @return
	 */
	private String saveNewAlert(Alert alert) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
        try {
			
			//setting the owner
			UserService userService = UserServiceFactory.getUserService();
		    User user = userService.getCurrentUser();
			alert.setOwner(new String(user.getUserId()));
			alert.setId(null);
			

			if(alert.getUntilDate() != null){
				alert.setEndType(PeriodOfTime.UNTIL_DATE);
			}else if(alert.getUntilIterations() != null){
				alert.setEndType(PeriodOfTime.ITERATIONS);
			}else{
				alert.setEndType(PeriodOfTime.NEVER_ENDS);
			}
			
            pm.makePersistent(alert);
        } catch(Exception ex){
			ex.printStackTrace();
        	return ex.getMessage();
        } finally {
 
            pm.close();
        }
		return "OKKK";
	}

	/**
	 * 
	 */
	public List<Alert> fetchAlerts() {
		List<Alert> list = new ArrayList<Alert>();
		PersistenceManager pm = PMF.get().getPersistenceManager();

		UserService userService = UserServiceFactory.getUserService();
	    User user = userService.getCurrentUser();
		
		Query query = pm.newQuery(Alert.class);
	    query.setFilter("owner == alertOwner");
	    query.declareParameters("String alertOwner");
	    //query.setRange(0, 1000);
	    query.setOrdering("title asc");

	    try {
	        List<Alert> rs = (List<Alert>) query.execute(user.getUserId());
	        if(rs != null){
		        for (Alert a : rs) {
		            list.add(a);
		        }
	        }
	    } catch(Exception ex){
			ex.printStackTrace();
	    }finally {
	        query.closeAll();
	    }
	    return list;
	}
	
	/**
	 * 
	 */
	public Boolean deleteAlert(Alert alert)  {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			Alert a = (Alert)pm.getObjectById(Alert.class, alert.getId());
			pm.deletePersistent(a);
		} catch(Exception ex){
			ex.printStackTrace();
        	return false;
        } finally {
            pm.close();
        }
		
		return true;
	}

	/**
	 * 
	 */
	@Override
	public List<AlertLog> fetchAlertLogs(Alert alert) {
		List<AlertLog> list = new ArrayList<AlertLog>();
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			Alert a = (Alert)pm.getObjectById(Alert.class, alert.getId());
			List<AlertLog> rs = a.getLog();	
		    if(rs != null){
				for (AlertLog log : rs) {
		            list.add(log);
		        }
	        }
		} catch(Exception ex){
			ex.printStackTrace();
        } finally {
            pm.close();
        }
		return list;
	}

	@Override
	public List<AlertLog> fetchAlertLogPage(Alert alert, int start, int length) {
		List<AlertLog> list = new ArrayList<AlertLog>();
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			Alert a = (Alert)pm.getObjectById(Alert.class, alert.getId());
			List<AlertLog> rs = a.getLog();	
		    if(rs != null){
		    	if(rs.size() > start){
		    		int end = start + length;
		    		end = Math.min(end, rs.size());
		    		for(int i = start; i < end; i++){
		    			list.add(rs.get(i));
		    		}
		    	}
	        }
		} catch(Exception ex){
			ex.printStackTrace();
        } finally {
            pm.close();
        }
		return list;
	}

	@Override
	public int AlertLogCount(Alert alert) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			Alert a = (Alert)pm.getObjectById(Alert.class, alert.getId());
			return a.getLog().size();
		} catch(Exception ex){
			ex.printStackTrace();
        } finally {
            pm.close();
        }
		return 0;
	}
}
