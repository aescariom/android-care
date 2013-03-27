package org.androidcare.web.server.api;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class ReminderPhoto extends HttpServlet {
        
	private static final long serialVersionUID = -1036350173054180307L;
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
            BlobKey blobKey = new BlobKey(req.getParameter("id"));
            blobstoreService.serve(blobKey, res);
    }
        
}