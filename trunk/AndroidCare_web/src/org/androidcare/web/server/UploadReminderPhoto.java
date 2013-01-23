package org.androidcare.web.server;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.androidcare.web.shared.persistent.Reminder;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class UploadReminderPhoto extends HttpServlet{

	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	
	public void doPost(HttpServletRequest request, HttpServletResponse res) throws ServletException, IOException {

//		UserService userService = UserServiceFactory.getUserService();
//		User user = userService.getCurrentUser();
		
		int length = request.getContentLength();
		int oneMB = 1024*1024; // 1MB
		
		if(length > oneMB){
			throw new RuntimeException("The file is too large, it's sice should be lower than 1MB");
		}
		
		Map<String, List<BlobKey>> blobArray = blobstoreService.getUploads(request);
		List<BlobKey> photos = blobArray.get("photo");
		if(photos == null || photos.size() == 0){
			throw new RuntimeException("No photo found");
		}
		
		BlobKey photo = photos.get(0);
		
		int id = Integer.parseInt(request.getParameter("reminderId").toString());
		
		final PersistenceManager pm = PMF.get().getPersistenceManager();  
		final Transaction txn = pm.currentTransaction();
		try{
			txn.begin();

			Reminder r = (Reminder)pm.getObjectById(Reminder.class, id);
			//setting the owner
//			if(r.getId().toString() != user.getUserId()){
//				throw new RuntimeException("Users don't match");
//			}
			
			try{
				if(r.getBlobKey() != null && r.getBlobKey() != ""){
					BlobKey blobKeys = new BlobKey(r.getBlobKey());
				    blobstoreService.delete(blobKeys);
				}
			}catch(RuntimeException e){
				// imagen no borrada
			}
		    
			r.setBlobKey(photo.getKeyString());
			
			txn.commit();	
		} catch(Exception ex){
			ex.printStackTrace();
	    } finally {
	    	if (txn.isActive()) {
		        txn.rollback();
		    }
	    }
	}
}
