package org.androidcare.web.server;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobstoreService;

@SuppressWarnings("serial")
public class UploadReminderPhoto extends HttpServlet{

	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	
	public void doPost(HttpServletRequest request, HttpServletResponse res) throws ServletException, IOException {
		// url that is going to be used with this request
		String url = blobstoreService.createUploadUrl("/reminderUpload");
		
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
		
		System.out.println(request.getParameter("reminderId").toString());
		System.out.println(photo.getKeyString());
	}
}
