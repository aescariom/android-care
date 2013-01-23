package org.androidcare.web.server;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.androidcare.web.client.rpc.ReminderService;
import org.androidcare.web.shared.persistent.Reminder;
import org.androidcare.web.shared.persistent.ReminderLog;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import javax.jdo.Query;

@SuppressWarnings("serial")
public class ReminderServiceImpl extends RemoteServiceServlet implements
		ReminderService {
	
	public Reminder saveReminder(Reminder reminder) {
		if(reminder.getId() > 0){
			reminder = editReminder(reminder);
			reminder.cleanForAPI();
			return reminder;
		}else{
			reminder = saveNewReminder(reminder);
			reminder.cleanForAPI();
			return reminder;
		}
	}

	private Reminder editReminder(Reminder reminder) {
		final PersistenceManager pm = PMF.get().getPersistenceManager();  
		final Transaction txn = pm.currentTransaction();
		try{
			txn.begin();

			Reminder a = (Reminder)pm.getObjectById(Reminder.class, reminder.getId());
			a.setTitle(reminder.getTitle());
			a.setDescription(reminder.getDescription());
			a.setRepeat(reminder.isRepeat());
			a.setActiveFrom(reminder.getActiveFrom());
			if(reminder.getActiveUntil() != null){
				a.setActiveUntil(reminder.getActiveUntil());
				a.setNumerOfRepetitions(null);
				a.setEndType(Reminder.END_TYPE_UNTIL_DATE);
			}else if(reminder.getNumerOfRepetitions() != null){
				a.setNumerOfRepetitions(reminder.getNumerOfRepetitions());
				a.setEndType(Reminder.END_TYPE_ITERATIONS);
				a.setActiveUntil(null);
			}else{
				a.setNumerOfRepetitions(null);
				a.setActiveUntil(null);
				a.setEndType(Reminder.END_TYPE_NEVER_ENDS);
			}
			a.setRepeatPeriod(reminder.getRepeatPeriod());
			a.setDaysOfWeekInWhichShouldTrigger(reminder.getDaysOfWeekInWhichShouldTrigger());
			a.setRepeatEachXPeriods(reminder.getRepeatEachXPeriods());
			a.setRequestConfirmation(reminder.isRequestConfirmation());
			
			//setting the owner
			UserService userService = UserServiceFactory.getUserService();
		    User user = userService.getCurrentUser();
			a.setOwner(user.getUserId());
		    
			txn.commit();
			
			return a;
		} catch(Exception ex){
			ex.printStackTrace();
	    } finally {
	    	if (txn.isActive()) {
		        txn.rollback();
		    }
	    }
		
		return null;
	}

	private Reminder saveNewReminder(Reminder reminder) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
        try {
			//setting the owner
			UserService userService = UserServiceFactory.getUserService();
		    User user = userService.getCurrentUser();
			reminder.setOwner(new String(user.getUserId()));
			reminder.setId(null);
			

			if(reminder.getActiveUntil() != null){
				reminder.setEndType(Reminder.END_TYPE_UNTIL_DATE);
			}else if(reminder.getNumerOfRepetitions() != null){
				reminder.setEndType(Reminder.END_TYPE_ITERATIONS);
			}else{
				reminder.setEndType(Reminder.END_TYPE_NEVER_ENDS);
			}
			
            pm.makePersistent(reminder);
            return reminder;
        } catch(Exception ex){
			ex.printStackTrace();
        } finally {
 
            pm.close();
        }
		return null;
	}

	public List<Reminder> fetchReminders() {
		List<Reminder> list = new ArrayList<Reminder>();
		PersistenceManager pm = PMF.get().getPersistenceManager();

		UserService userService = UserServiceFactory.getUserService();
	    User user = userService.getCurrentUser();
		
		Query query = pm.newQuery(Reminder.class);
	    query.setFilter("owner == reminderOwner");
	    query.declareParameters("String reminderOwner");
	    //query.setRange(0, 1000);
	    query.setOrdering("title asc");

	    try {
	        List<Reminder> rs = (List<Reminder>) query.execute(user.getUserId());
	        if(rs != null){
		        for (Reminder a : rs) {
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

	public Boolean deleteReminder(Reminder reminder)  {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			Reminder a = (Reminder)pm.getObjectById(Reminder.class, reminder.getId());
			pm.deletePersistent(a);
		} catch(Exception ex){
			ex.printStackTrace();
        	return false;
        } finally {
            pm.close();
        }
		
		return true;
	}
	
	@Override
	public List<ReminderLog> fetchReminderLogs(Reminder reminder) {
		List<ReminderLog> list = new ArrayList<ReminderLog>();
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			Reminder a = (Reminder)pm.getObjectById(Reminder.class, reminder.getId());
			List<ReminderLog> rs = a.getLog();	
		    if(rs != null){
				for (ReminderLog log : rs) {
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
	public List<ReminderLog> fetchReminderLogPage(Reminder reminder, int start, int length) {
		List<ReminderLog> list = new ArrayList<ReminderLog>();
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			Reminder a = (Reminder)pm.getObjectById(Reminder.class, reminder.getId());
			List<ReminderLog> rs = a.getLog();	
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
	public int ReminderLogCount(Reminder reminder) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			Reminder a = (Reminder)pm.getObjectById(Reminder.class, reminder.getId());
			return a.getLog().size();
		} catch(Exception ex){
			ex.printStackTrace();
        } finally {
            pm.close();
        }
		return 0;
	}
}
