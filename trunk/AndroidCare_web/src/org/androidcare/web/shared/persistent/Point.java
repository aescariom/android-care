package org.androidcare.web.shared.persistent;

import com.google.gwt.maps.client.geom.LatLng;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.io.Serializable;

public class Point implements Serializable {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private Double latitude;
    @Persistent
    private Double longitude;

    public Point() {

    }

    public Point(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Point (Point point) {
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
