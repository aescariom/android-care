package org.androidcare.web.client.widgets;

import java.util.Date;
import java.util.List;

import org.androidcare.web.client.LocalizedConstants;
import org.androidcare.web.client.rpc.PositionService;
import org.androidcare.web.client.rpc.PositionServiceAsync;
import org.androidcare.web.shared.persistent.Position;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.user.client.Timer;
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
	
	protected Timer timer = new Timer() {
        public void run() {
        	getPositions();
		    }
		};
		
	protected int refreshTime = 5*60*1000; // 5 min
	
	public UserLocationMap(){
	}

	public void centerMap() {
	    mapWidget.setCenter(center, zoom);
	    // Add an info window to highlight a point of interest
	    mapWidget.getInfoWindow().open(mapWidget.getCenter(),
	        new InfoWindowContent(
	        		"Lat: " + this.center.getLatitude() + 
	        		"<br/>Lng: " + this.center.getLongitude() + 
	        		((this.date != null) ? "<br/>" + this.date.toString() : "")));
	    
		mapWidget.checkResize();
	}

	private void mapSetup(List<Position> rs) {	    
	    mapWidget = new MapWidget();
	    mapWidget.setCenter(center, zoom);
	    mapWidget.setSize("100%", "100%");
	    // Add some controls for the zoom level
	    mapWidget.addControl(new LargeMapControl());
	    
		if(rs.size() <= 0){
			// Open a map centered on Madrid, Spain
		    center = LatLng.newInstance(40.416667, -3.70355);
		}else{
			int i = 0;
			for(final Position p : rs){
				final LatLng point = LatLng.newInstance(p.getLatitude(), p.getLongitude());
			    // Add a marker
				MarkerOptions markerOptions = MarkerOptions.newInstance();
				String bgColor = "FE6256";
				String foreColor = "000000";
				if(i == 0){
					bgColor = "C0FF31";
				}
				Icon icon = Icon.newInstance("http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=" + (i+1) + "|" + bgColor + "|" + foreColor);
				if(i == 0){
					icon.setIconSize(Size.newInstance(27, 45));
					icon.setShadowSize(Size.newInstance(49, 45));
					icon.setIconAnchor(Point.newInstance(27, 45));
				}else{
					icon.setIconSize(Size.newInstance(18, 30));
					icon.setShadowSize(Size.newInstance(33, 30));
					icon.setIconAnchor(Point.newInstance(18, 30));
				}
				icon.setShadowURL("http://labs.google.com/ridefinder/images/mm_20_shadow.png");
				markerOptions.setIcon(icon);
				
				Marker m = new Marker(point, markerOptions);
				m.addMarkerClickHandler(new MarkerClickHandler(){

					@Override
					public void onClick(MarkerClickEvent event) {
						mapWidget.getInfoWindow().open(point,
						        new InfoWindowContent(
				        		"Lat: " + point.getLatitude() + 
				        		"<br/>Lng: " + point.getLongitude()+ 
				        		((p.getDate() != null) ? "<br/>" + p.getDate().toString() : "")));
					}
					
				});
				
			    mapWidget.addOverlay(m);
			    i++;
			}
			Position p = rs.get(0);
			center = LatLng.newInstance(p.getLatitude(), p.getLongitude());
			date = p.getDate();
		}

	    final DockLayoutPanel dock = new DockLayoutPanel(Unit.PX);
	    dock.addNorth(mapWidget, 500);
		mapWidget.checkResize();
		
	    this.add(dock);	
	    this.centerMap();
	}
	
	public void getPositions(){
		// Then, we send the input to the server.
		positionService.getLastPositions(30,
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
	
	public void setAutorefresh(boolean value){
		if(value){
			timer.scheduleRepeating(refreshTime);
		}else{
			timer.cancel();
		}
	}
}
