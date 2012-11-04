package org.androidcare.web.client;

import java.util.List;

import org.androidcare.web.shared.persistent.Alert;
import org.androidcare.web.shared.persistent.AlertLog;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 * @author Alejandro Escario MŽndez
 */
public interface AlertServiceAsync {
	void saveAlert(Alert alert, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void fetchAlerts(AsyncCallback<List<Alert>> callback);
	
	void deleteAlert(Alert alert, AsyncCallback<Boolean> callback);

	void fetchAlertLogs(Alert alert, AsyncCallback<List<AlertLog>> asyncCallback);

	void fetchAlertLogPage(Alert a, int start, int length, AsyncCallback<List<AlertLog>> callback);

	void AlertLogCount(Alert alert, AsyncCallback<Integer> asyncCallback);
}
