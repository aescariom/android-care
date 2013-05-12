package org.androidcare.web.client.rpc;

import java.util.List;

import org.androidcare.web.shared.persistent.Position;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PositionServiceAsync {
	void getLastPositions(int numOfPositions, AsyncCallback<List<Position>> callback);
}
