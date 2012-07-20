/** 
 * This library/program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library/program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library/program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

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
}
