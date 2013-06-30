package org.androidcare.web.server.module.dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.androidcare.web.client.module.dashboard.rpc.ReminderService;
import org.androidcare.web.server.PMF;
import org.androidcare.web.shared.persistent.Reminder;
import org.androidcare.web.shared.persistent.ReminderLog;

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
	
	private static final Logger log = Logger.getLogger(ReminderServiceImpl.class.getName());
	
	public Reminder saveReminder(Reminder reminder) {
		//@comentario aunque ahora no vamos hacer nada para corregirlo, este método podría ser usado
		//por cualquiera para añadir cualquier recordatorio
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

			Reminder persistedReminder = (Reminder)pm.getObjectById(Reminder.class, reminder.getId());
			//@comentario ¿tendría sentido que hacer la comprobación
			//if(reminder.getOwner().compareToIgnoreCase(user.getUserId()) == 0)
			//no tengo claro que de mucha seguridad, pero menos da una piedra
			persistedReminder.setTitle(reminder.getTitle());
			persistedReminder.setDescription(reminder.getDescription());
			persistedReminder.setRepeat(reminder.isRepeat());
			persistedReminder.setActiveFrom(reminder.getActiveFrom());
			if(reminder.getActiveUntil() != null){
				persistedReminder.setActiveUntil(reminder.getActiveUntil());
				persistedReminder.setNumerOfRepetitions(null);
				persistedReminder.setEndType(Reminder.END_TYPE_UNTIL_DATE);
			}else if(reminder.getNumerOfRepetitions() != null){
				persistedReminder.setNumerOfRepetitions(reminder.getNumerOfRepetitions());
				persistedReminder.setEndType(Reminder.END_TYPE_ITERATIONS);
				persistedReminder.setActiveUntil(null);
			}else{
				persistedReminder.setNumerOfRepetitions(null);
				persistedReminder.setActiveUntil(null);
				persistedReminder.setEndType(Reminder.END_TYPE_NEVER_ENDS);
			}
			persistedReminder.setRepeatPeriod(reminder.getRepeatPeriod());
			persistedReminder.setDaysOfWeekInWhichShouldTrigger(reminder.getDaysOfWeekInWhichShouldTrigger());
			persistedReminder.setRepeatEachXPeriods(reminder.getRepeatEachXPeriods());
			persistedReminder.setRequestConfirmation(reminder.isRequestConfirmation());
			
			//setting the owner
			UserService userService = UserServiceFactory.getUserService();
		    User user = userService.getCurrentUser();
			persistedReminder.setOwner(user.getUserId());
		    
			txn.commit();
			
			return persistedReminder;
		} catch(Exception ex){
			log.log(Level.SEVERE, "Reminder could not be edited", ex);
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
			//@comentario ¿deberías usar una transacción para esto?
			//Pregunto, realmente no leído todavía nada sobre transacciones en el AppEngine,  pero parece
			//que siempre las usas cuando escribes
			
            pm.makePersistent(reminder);
            return reminder;
        } catch(Exception ex){
        	log.log(Level.SEVERE, "Reminder could not be edited", ex);
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
	        List<?> resulset = (List<?>) query.execute(user.getUserId());
	        if(resulset != null){
		        for (Object persitedReminder : resulset) {
		        	Reminder rem = new Reminder((Reminder)persitedReminder);
		            list.add(rem);
		        }
	        }
	    } catch(Exception ex){
	    	log.log(Level.SEVERE, "Reminders could not be retrieved", ex);
	    }finally {
	        query.closeAll();
	    }
	    return list;
	}

	public Boolean deleteReminder(Reminder reminder)  {
		//@comentario ¿deberías usar una transacción para esto?
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			Reminder persistedReminder = (Reminder)pm.getObjectById(Reminder.class, reminder.getId());
			BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
			UploadReminderPhoto.deleteReminderImage(persistedReminder, blobstoreService);			
			pm.deletePersistent(persistedReminder);
		} catch(Exception ex){
			log.log(Level.SEVERE, "Reminder could not be deleted", ex);
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
			Reminder persistedReminder = (Reminder)pm.getObjectById(Reminder.class, reminder.getId());
			List<ReminderLog> logs = persistedReminder.getLog();	
		    if(logs != null){
				for (ReminderLog log : logs) {
		            list.add(new ReminderLog(log));
		        }
	        }
		} catch(Exception ex){
			log.log(Level.SEVERE, "Reminder's logs could not be retrieved", ex);
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
			Reminder persistedReminder = (Reminder)pm.getObjectById(Reminder.class, reminder.getId());
			List<ReminderLog> logs = persistedReminder.getLog();	
		    if(logs != null){
		    	if(logs.size() > start){
		    		int end = start + length;
		    		end = Math.min(end, logs.size());
		    		for(int i = start; i < end; i++){
		    			list.add(new ReminderLog(logs.get(i)));
		    		}
		    	}
	        }
		} catch(Exception ex){
			log.log(Level.SEVERE, "Reminder's logs could not be retrieved", ex);
        } finally {
            pm.close();
        }
		return list;
	}

	@Override
	public int ReminderLogCount(Reminder reminder) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			Reminder persistedReminder = (Reminder)pm.getObjectById(Reminder.class, reminder.getId());
			return persistedReminder.getLog().size();
		} catch(Exception ex){
			log.log(Level.SEVERE, "Reminder's logs count could not be retrieved", ex);
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
			Reminder persistedReminder = (Reminder)pm.getObjectById(Reminder.class, reminder.getId());
			BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
			UploadReminderPhoto.deleteReminderImage(persistedReminder, blobstoreService);
			persistedReminder.setBlobKey("");
		    
			txn.commit();
		} catch(Exception ex){
			log.log(Level.SEVERE, "Reminder's photo could not be deleted", ex);
        	return false;
        } finally {
            pm.close();
        }		
		return true;
	}	
}
