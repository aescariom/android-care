package org.androidcare.web.client;

import java.util.List;

import org.androidcare.web.shared.persistent.Position;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PositionServiceAsync {
	void getLastPositions(int num, AsyncCallback<List<Position>> callback);
}
