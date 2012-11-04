package org.androidcare.web.client;

import java.util.List;

import org.androidcare.web.shared.persistent.Alert;
import org.androidcare.web.shared.persistent.AlertLog;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 * @author Alejandro Escario MŽndez
 */
@RemoteServiceRelativePath("alert")
public interface AlertService extends RemoteService {
	String saveAlert(Alert alert);

	List<Alert> fetchAlerts();
	
	Boolean deleteAlert(Alert alert);

	List<AlertLog> fetchAlertLogs(Alert alert);

	List<AlertLog> fetchAlertLogPage(Alert a, int start, int length);

	int AlertLogCount(Alert alert);
}
