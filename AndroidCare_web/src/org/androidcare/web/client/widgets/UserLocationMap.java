package org.androidcare.web.client.widgets;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.androidcare.web.client.LocalizedConstants;
import org.androidcare.web.client.rpc.PositionService;
import org.androidcare.web.client.rpc.PositionServiceAsync;
import org.androidcare.web.shared.persistent.Position;

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
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.ibm.icu.text.SimpleDateFormat;

public class UserLocationMap extends FlowPanel {
	
	private LocalizedConstants LocalizedConstants = GWT.create(LocalizedConstants.class);

	private final PositionServiceAsync positionService = GWT
			.create(PositionService.class);

	private final DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm:ss");
	
	private List<Overlay> overlays;

	private HorizontalPanel horizontalPanel;
    private DockLayoutPanel dock;
	private MapWidget mapWidget;
	private Label lblItems;
	private TextBox txtItems;
	private Image imgRefresh;
	private Image imgLoading;
	private Button btnRefresh;
	private LatLng center;
	private int zoom = 14;
		
	protected int refreshTime = 5*60*1000; // 5 min
	
	public UserLocationMap(){		
		horizontalPanel = new HorizontalPanel();
		horizontalPanel.setSpacing(5);
		initRefreshButton();
		txtItems = new TextBox();
		txtItems.setText("10");
		txtItems.setWidth("30px");
		txtItems.setHeight("10px");
		lblItems = new Label(LocalizedConstants.positionNumber() + ": ");
		imgLoading = new Image("./images/loading.gif");
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

	private void mapSetup(List<Position> rs) {
		setupMapWidget();
	    
		if(rs.size() <= 0){
			// Open a map centered on Madrid, Spain
		    center = LatLng.newInstance(40.416667, -3.70355);
		}else{
			int i = 0;
			overlays = new ArrayList<Overlay>();
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
						showWindow(point, p.getDate());
					}
					
				});
				if(i == 0){
					showWindow(point, p.getDate());
				}
				
			    mapWidget.addOverlay(m);
			    overlays.add(m);
			    i++;
			}
			Position p = rs.get(0);
			center = LatLng.newInstance(p.getLatitude(), p.getLongitude());
		}
		mapWidget.checkResize();
		
	    this.centerMap();
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
		    mapWidget.setSize("100%", "100%");
		    // Add some controls for the zoom level
		    mapWidget.addControl(new LargeMapControl());
	
			setLayout();
		}else{
			removeOverlays();
		}
	}

	private void removeOverlays() {
		for(Overlay o : overlays){
			mapWidget.removeOverlay(o);
		}
	}

	private void setLayout() {		
		dock = new DockLayoutPanel(Unit.PX);
	    dock.addNorth(mapWidget, 570);
		dock.addNorth(horizontalPanel, 40);
	    this.add(dock);	
	}

	private void initRefreshButton() {
		imgRefresh = new Image("./images/refresh.png");
		imgRefresh.setSize("15px", "15px");
		btnRefresh = new Button();
		btnRefresh.getElement().appendChild(imgRefresh.getElement());
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
			int num = Integer.valueOf(txtItems.getText());
			if(num < 1){
				num = 1;
			}else if(num > 100){
				num = 100;
			}
			// Then, we send the input to the server.
			positionService.getLastPositions(num,
				new AsyncCallback<List<Position>>() {
					public void onFailure(Throwable caught) {
						setRefreshButtonIdle();
						Window.alert(LocalizedConstants.serverError());
						caught.printStackTrace();
						setRefreshButtonIdle();
					}

					@Override
					public void onSuccess(List<Position> rs) {
						fill(rs);
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

	private void fill(final List<Position> rs) {
		Maps.loadMapsApi("", "2", false, new Runnable() {
	        public void run() {
	        	mapSetup(rs);
	        }
	      });
	}
	  
	 
	private float round(double d){
		long aux = Math.round(d * 10000);
		return (float) aux / 10000;
	}
}
