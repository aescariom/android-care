package org.androidcare.web.client.widgets;

import java.util.Date;
import java.util.List;

import org.androidcare.web.client.LocalizedConstants;
import org.androidcare.web.client.PositionService;
import org.androidcare.web.client.PositionServiceAsync;
import org.androidcare.web.shared.persistent.Position;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;

public class UserLocationMap extends FlowPanel {
	
	private LocalizedConstants LocalizedConstants = GWT.create(LocalizedConstants.class);

	private final PositionServiceAsync positionService = GWT
			.create(PositionService.class);
	
	private MapWidget mapWidget;
	private LatLng center;
	private Date date;
	private int zoom = 14;
	
	public UserLocationMap(){
		getPositions();
	}

	public void centerMap() {
	    mapWidget.setCenter(center, zoom);
	    // Add an info window to highlight a point of interest
	    mapWidget.getInfoWindow().open(mapWidget.getCenter(),
	        new InfoWindowContent(
	        		"Lat: " + this.center.getLatitude() + 
	        		"<br/>Lng: " + this.center.getLongitude() + 
	        		"<br/>" + this.date.toString()));
	    
		mapWidget.checkResize();
	}

	private void mapSetup(List<Position> rs) {
		if(rs.size() <= 0){
			// Open a map centered on Madrid, Spain
		    center = LatLng.newInstance(40.416667, -3.70355);
		}else{
			Position p = rs.get(0);
			center = LatLng.newInstance(p.getLatitude(), p.getLongitude());
			date = p.getDate();
		}
	    
	    mapWidget = new MapWidget();
	    mapWidget.setCenter(center, zoom);
	    mapWidget.setSize("100%", "100%");
	    // Add some controls for the zoom level
	    mapWidget.addControl(new LargeMapControl());

	    // Add a marker
	    mapWidget.addOverlay(new Marker(center));

	    final DockLayoutPanel dock = new DockLayoutPanel(Unit.PX);
	        dock.addNorth(mapWidget, 500);
		mapWidget.checkResize();
		
	    this.add(dock);	    
	}
	
	private void getPositions(){
		// Then, we send the input to the server.
		positionService.getLastPositions(1,
			new AsyncCallback<List<Position>>() {
				public void onFailure(Throwable caught) {
					Window.alert(LocalizedConstants.serverError());
					caught.printStackTrace();
				}

				@Override
				public void onSuccess(List<Position> rs) {
					fill(rs);
				}
			});
	}

	private void fill(final List<Position> rs) {
		Maps.loadMapsApi("", "2", false, new Runnable() {
	        public void run() {
	        	mapSetup(rs);
	        }
	      });
	}
}
