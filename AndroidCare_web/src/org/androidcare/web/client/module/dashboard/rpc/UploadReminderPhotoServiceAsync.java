package org.androidcare.web.client.module.dashboard.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UploadReminderPhotoServiceAsync {

	void getBlobStoreUrl(AsyncCallback<String> callback);
}
