package org.androidcare.web.server;

import org.androidcare.web.client.rpc.UploadReminderPhotoService;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class UploadReminderPhotoServiceImpl  extends RemoteServiceServlet implements UploadReminderPhotoService {

    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	@Override
	public String getBlobStoreUrl() {
		String url = blobstoreService.createUploadUrl("/reminderUpload");
		return url;
	} 
    

}
