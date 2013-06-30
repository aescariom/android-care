package org.androidcare.web.server.module.dashboard;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.androidcare.web.server.PMF;
import org.androidcare.web.shared.persistent.Reminder;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobstoreService;

@SuppressWarnings("serial")
public class UploadReminderPhoto extends HttpServlet{
	
	private static final Logger log = Logger.getLogger(UploadReminderPhoto.class.getName());
	private static final int ONE_MB = 1024*1024; // 1MB

	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	
	public void doPost(HttpServletRequest request, HttpServletResponse res) throws ServletException, IOException {
		
		int length = request.getContentLength();
		
		if(length > ONE_MB){
			log.log(Level.SEVERE, "The file is too large, it's sice should be lower than 1MB");
			throw new RuntimeException("The file is too large, it's sice should be lower than 1MB");			
		}
		
		Map<String, List<BlobKey>> blobArray = blobstoreService.getUploads(request);
		List<BlobKey> photos = blobArray.get("photo");
		if(photos == null || photos.size() == 0){
			throw new RuntimeException("No photo found");
		}
		
		BlobKey photo = photos.get(0);
		BlobInfoFactory bi = new BlobInfoFactory();
		BlobInfo blobInfo = bi.loadBlobInfo(photo);
		String mime = blobInfo.getContentType();
		
		if(isNotASupportedFormat(mime)){
			log.log(Level.SEVERE, "Image format not supported or it is not an image");
			throw new RuntimeException("This is not an image");			
		}
		
		int id = Integer.parseInt(request.getParameter("reminderId").toString());
		
		final PersistenceManager pm = PMF.get().getPersistenceManager();  
		final Transaction txn = pm.currentTransaction();
		try{
			txn.begin();

			Reminder persistedReminder = (Reminder)pm.getObjectById(Reminder.class, id);
			deleteReminderImage(persistedReminder, blobstoreService);
		    
			persistedReminder.setBlobKey(photo.getKeyString());
			
			txn.commit();	
		} catch(Exception ex){
			log.log(Level.SEVERE, "Image could not be uploaded", ex);
	    } finally {
	    	if (txn.isActive()) {
		        txn.rollback();
		    }
	    }
	}


	private boolean isNotASupportedFormat(String mime) {
		return !mime.equalsIgnoreCase("image/png") && !mime.equalsIgnoreCase("image/gif") && !mime.equalsIgnoreCase("image/bmp") && !mime.equalsIgnoreCase("image/jpeg");
	}


	static void deleteReminderImage(Reminder persistedReminder,
			BlobstoreService blobstoreService) {
		try{
			if(persistedReminder.getBlobKey() != null && persistedReminder.getBlobKey() != ""){
				BlobKey blobKeys = new BlobKey(persistedReminder.getBlobKey());
			    blobstoreService.delete(blobKeys);
			}
		}catch(RuntimeException e){
			log.log(Level.SEVERE, "Image could not be deleted", e);
		}
	}
}
