package org.androidcare.web.client.module.dashboard.rpc;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.RemoteService;

@RemoteServiceRelativePath("uploadReminderPhoto")
public interface UploadReminderPhotoService extends RemoteService {

	public String getBlobStoreUrl();
}
