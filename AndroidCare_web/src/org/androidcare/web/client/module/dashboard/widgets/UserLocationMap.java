package org.androidcare.web.client.module.dashboard.widgets;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.androidcare.web.client.module.dashboard.LocalizedConstants;
import org.androidcare.web.client.module.dashboard.rpc.*;
import org.androidcare.web.shared.persistent.Alarm;
import org.androidcare.web.shared.persistent.GeoPoint;
import org.androidcare.web.shared.persistent.Position;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserLocationMap extends FlowPanel {
	
	private LocalizedConstants LocalizedConstants = GWT.create(LocalizedConstants.class);

    private final PositionServiceAsync positionService = GWT.create(PositionService.class);
    private final AlarmServiceAsync alarmService = GWT.create(AlarmService.class);

	private final DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("HH:mm:ss dd/MM/yyyy");
	
	private List<Overlay> overlays;

	private HorizontalPanel horizontalPanel;
    private DockLayoutPanel dock;
	private MapWidget mapWidget;
	private Label lblItems;
	private TextBox txtItems;
	private Image imgLoading;
	private Button btnRefresh;
	private LatLng center;
	private int zoom = 14;
	
	public UserLocationMap(){		
		horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(5);
		initRefreshButton();
		txtItems = new TextBox();
		txtItems.setText("10");
		txtItems.setWidth("30px");
		lblItems = new Label(LocalizedConstants.positionNumber() + ": ");
		imgLoading = new Image("./images/loading_small.gif");
		imgLoading.setSize("20px", "20px");
		horizontalPanel.add(lblItems);
		horizontalPanel.add(txtItems);
		horizontalPanel.add(btnRefresh);
		horizontalPanel.add(imgLoading);
	}
	
	public void centerMap() {
	    mapWidget.setCenter(center, zoom);
		mapWidget.checkResize();
	}

	private void mapSetup(List<Position> positions) {
		setupMapWidget();
	    
		if(positions.size() <= 0){
			// Open a map centered on Madrid, Spain
		    center = LatLng.newInstance(40.416667, -3.70355);
		} else {
			int positionNumber = 0;
			overlays = new ArrayList<Overlay>();
			for(final Position position : positions){
				final LatLng point = LatLng.newInstance(position.getLatitude(), position.getLongitude());
			    // Add a marker
				MarkerOptions markerOptions = MarkerOptions.newInstance();
				String bgColor = "FE6256";
				String foreColor = "000000";
				if(positionNumber == 0){
					bgColor = "C0FF31";
				}
				Icon icon = Icon.newInstance("http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=" + 
							(positionNumber+1) + "|" + bgColor + "|" + foreColor);
				if(positionNumber == 0){
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
				
				Marker marker = new Marker(point, markerOptions);
				marker.addMarkerClickHandler(new MarkerClickHandler(){

					@Override
					public void onClick(MarkerClickEvent event) {
						showWindow(point, position.getDate());
					}
					
				});
				if(positionNumber == 0){
					showWindow(point, position.getDate());
				}
				
			    mapWidget.addOverlay(marker);
			    overlays.add(marker);
			    positionNumber++;
			}
			Position lastPosition = positions.get(0);
			center = LatLng.newInstance(lastPosition.getLatitude(), lastPosition.getLongitude());
		}
		mapWidget.checkResize();
		
	    centerMap();
        loadGreenZones();
	}

    private void loadGreenZones() {

        alarmService.getActiveAlarms(new AsyncCallback<List<Alarm>>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Error");
                caught.printStackTrace();
            }

            @Override
            public void onSuccess(List<Alarm> result) {
                for (Alarm alarm : result) {
                    List<GeoPoint> positions = alarm.getPositions();
                    LatLng[] points = convertToLatLng(positions);
                    mapWidget.addOverlay(new Polygon(points));
                }
            }

        });
    }

    private LatLng[] convertToLatLng(List<GeoPoint> positions) {
        LatLng[] lats = new LatLng[positions.size()];
        for (int i = 0; i < positions.size(); i++) {
            lats[i] = positions.get(i).toLatLng();
        }
        return lats;

    }

    protected void showWindow(LatLng point, Date date) {
		mapWidget.getInfoWindow().open(point,
		        new InfoWindowContent(
        		LocalizedConstants.latitude() + ": " + round(point.getLatitude()) + 
        		"<br/>" + LocalizedConstants.longitude() + ": " + round(point.getLongitude()) + 
        		((date != null) ? "<br/>" + LocalizedConstants.time() + ": " + dateTimeFormat.format(date) : "")));
	}

	private void setupMapWidget() {
		if(mapWidget == null){
		    mapWidget = new MapWidget();
		    mapWidget.setCenter(center, zoom);
		    mapWidget.setSize("100%", "570px");
		    // Add some controls for the zoom level
		    mapWidget.addControl(new LargeMapControl());
	
			setLayout();
		}else{
			removeOverlays();
		}
	}

	private void removeOverlays() {
		if(overlays != null){
			for(Overlay o : overlays){
				mapWidget.removeOverlay(o);
			}
		}
	}

	private void setLayout() {		
		dock = new DockLayoutPanel(Unit.PX);
	    dock.addNorth(mapWidget, 570);
		dock.addNorth(horizontalPanel, 40);
	    this.add(dock);	
	}

	private void initRefreshButton() {
		btnRefresh = new Button(LocalizedConstants.refresh());
		btnRefresh.addStyleName("refresh");
		btnRefresh.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event) {
	        	imgLoading.setVisible(true);
	        	btnRefresh.setEnabled(false);
	        	getPositions();
	        }
	      });
	}

	public void getPositions(){
		try{
			int numOfPositions = Integer.valueOf(txtItems.getText());
			if(numOfPositions < 1){
				numOfPositions = 1;
			}else if(numOfPositions > 999){
				numOfPositions = 999;
			}
			// Then, we send the input to the server.
			positionService.getLastPositions(numOfPositions,
				new AsyncCallback<List<Position>>() {
					public void onFailure(Throwable caught) {
						setRefreshButtonIdle();
						Window.alert(LocalizedConstants.serverError());
						caught.printStackTrace();
						setRefreshButtonIdle();
					}

					@Override
					public void onSuccess(List<Position> positions) {
						fill(positions);
						setRefreshButtonIdle();
					}
				});
		}catch(NumberFormatException ex){
			Window.alert(LocalizedConstants.mustBeBetween1and100());
			setRefreshButtonIdle();
		}
	}

	private void setRefreshButtonIdle() {
    	imgLoading.setVisible(false);
    	btnRefresh.setEnabled(true);
	}

	private void fill(final List<Position> positions) {
		Maps.loadMapsApi("", "2", false, new Runnable() {
	        public void run() {
	        	mapSetup(positions);
	        }
	      });
	}
	 
	private float round(double d){
		long aux = Math.round(d * 10000);
		return (float) aux / 10000;
	}
}
