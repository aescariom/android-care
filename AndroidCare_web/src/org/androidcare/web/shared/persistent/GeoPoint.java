package org.androidcare.web.shared.persistent;

import com.google.gwt.maps.client.geom.LatLng;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.io.Serializable;

public class GeoPoint implements Serializable {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private Double latitude;
    @Persistent
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

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public LatLng toLatLng() {
        return LatLng.newInstance(latitude, longitude);
    }

}
