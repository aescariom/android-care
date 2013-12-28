package org.androidcare.web.client.module.dashboard.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.androidcare.web.shared.persistent.Position;

import java.util.List;


public interface PositionServiceAsync {
	void getLastPositions(int numOfPositions, AsyncCallback<List<Position>> callback);
}
