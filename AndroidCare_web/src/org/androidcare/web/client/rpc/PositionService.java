package org.androidcare.web.client.rpc;

import java.util.List;

import org.androidcare.web.shared.persistent.Position;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("position")
public interface PositionService extends RemoteService {

	List<Position> getLastPositions(int numOfPositions);
}
