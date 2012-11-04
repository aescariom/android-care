package org.androidcare.web.shared.persistent;

import java.io.Serializable;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.GeoPt;

/**
 * 
 * @author Alejandro Escario MŽndez
 *
 */
@PersistenceCapable
public class Position implements Serializable {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	@Persistent
    private GeoPt geoPoint;
	
	@Persistent 
	private Date date;
	
	@Persistent
	private String owner;
	
	public Position(float latitude, float longitude, String owner, Date date){
		this.geoPoint = new GeoPt(latitude, longitude);
		this.owner = owner;
		this.date = date;
	}
	
	public Position(float latitude, float longitude, String owner){
		this(latitude, longitude, owner, new Date());
	}
	
	public String toString(){
		return "lat: " + geoPoint.getLatitude() + " long: " + geoPoint.getLongitude() + " date: " + date.toString();
	}
}
