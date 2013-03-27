package org.androidcare.web.server;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.androidcare.web.client.rpc.ReminderService;
import org.androidcare.web.shared.persistent.Reminder;
import org.androidcare.web.shared.persistent.ReminderLog;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
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
		}else{
			reminder = saveNewReminder(reminder);
		}
		return new Reminder(reminder);
	}

	private Reminder editReminder(Reminder reminder) {
		final PersistenceManager pm = PMF.get().getPersistenceManager();  
		final Transaction txn = pm.currentTransaction();
		try{
			txn.begin();

			Reminder r = (Reminder)pm.getObjectById(Reminder.class, reminder.getId());
			r.setTitle(reminder.getTitle());
			r.setDescription(reminder.getDescription());
			r.setRepeat(reminder.isRepeat());
			r.setActiveFrom(reminder.getActiveFrom());
			if(reminder.getActiveUntil() != null){
				r.setActiveUntil(reminder.getActiveUntil());
				r.setNumerOfRepetitions(null);
				r.setEndType(Reminder.END_TYPE_UNTIL_DATE);
			}else if(reminder.getNumerOfRepetitions() != null){
				r.setNumerOfRepetitions(reminder.getNumerOfRepetitions());
				r.setEndType(Reminder.END_TYPE_ITERATIONS);
				r.setActiveUntil(null);
			}else{
				r.setNumerOfRepetitions(null);
				r.setActiveUntil(null);
				r.setEndType(Reminder.END_TYPE_NEVER_ENDS);
			}
			r.setRepeatPeriod(reminder.getRepeatPeriod());
			r.setDaysOfWeekInWhichShouldTrigger(reminder.getDaysOfWeekInWhichShouldTrigger());
			r.setRepeatEachXPeriods(reminder.getRepeatEachXPeriods());
			r.setRequestConfirmation(reminder.isRequestConfirmation());
			
			//setting the owner
			UserService userService = UserServiceFactory.getUserService();
		    User user = userService.getCurrentUser();
			r.setOwner(user.getUserId());
		    
			txn.commit();
			
			return r;
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
	    query.setOrdering("title asc");

	    try {
	        List<Reminder> rs = (List<Reminder>) query.execute(user.getUserId());
	        if(rs != null){
		        for (Reminder r : rs) {
		        	Reminder rem = new Reminder(r);
		            list.add(rem);
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
			Reminder r = (Reminder)pm.getObjectById(Reminder.class, reminder.getId());

			BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
			try{
				if(r.getBlobKey() != null && r.getBlobKey() != ""){
					BlobKey blobKeys = new BlobKey(r.getBlobKey());
				    blobstoreService.delete(blobKeys);
				}
			}catch(RuntimeException e){
				// imagen no borrada
			}
			
			pm.deletePersistent(r);
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
		            list.add(new ReminderLog(log));
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
		    			list.add(new ReminderLog(rs.get(i)));
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

	@Override
	public Boolean deleteReminderPhoto(Reminder reminder) {
		PersistenceManager pm = PMF.get().getPersistenceManager(); 
		final Transaction txn = pm.currentTransaction();
		try{
			txn.begin();
			Reminder r = (Reminder)pm.getObjectById(Reminder.class, reminder.getId());

			BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
			try{
				if(r.getBlobKey() != null && r.getBlobKey() != ""){
					BlobKey blobKeys = new BlobKey(r.getBlobKey());
				    blobstoreService.delete(blobKeys);
				}
			}catch(RuntimeException e){
				// imagen no borrada
			}
			r.setBlobKey("");
		    
			txn.commit();
		} catch(Exception ex){
			ex.printStackTrace();
        	return false;
        } finally {
            pm.close();
        }
		
		return true;
	}
}
