package org.androidcare.web.shared.persistent;

import com.google.gwt.maps.client.geom.LatLng;

public class GeoPoint {

    private Double latitude;
    private Double longitude;

    public GeoPoint() {

    }

    public GeoPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GeoPoint(GeoPoint point) {
        this.latitude = point.getLatitude();
        this.longitude = point.getLongitude();
    }

    public GeoPoint (String latlng) {
        //No, no me mires asi, en AppEngine la clase StringTokenizer no esta disponible.

        String lat = latlng.substring(0, latlng.indexOf(";"));
        String lng = latlng.substring(latlng.indexOf(";")+1);
        this.latitude = Double.parseDouble(lat);
        this.longitude = Double.parseDouble(lng);
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public LatLng toLatLng() {
        return LatLng.newInstance(latitude, longitude);
    }

    @Override
    public String toString() {
        return latitude + ";" + longitude;
    }
}