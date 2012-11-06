package org.androidcare.web.shared.persistent;

import java.io.Serializable;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * 
 * @author Alejandro Escario MŽndez
 *
 */
@PersistenceCapable
public class Position implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8108731095389801231L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;

	@Persistent
    private Float latitude;
	@Persistent
    private Float longitude;
	
	@Persistent 
	private Date date;
	
	@Persistent
	private String owner;
	
	public Position(){}
	
	public Position(float latitude, float longitude, String owner, Date date){
		this.latitude = latitude;
		this.longitude = longitude;
		this.owner = owner;
		this.date = date;
	}
	
	public Position(float latitude, float longitude, String owner){
		this(latitude, longitude, owner, new Date());
	}
	
	public float getLatitude(){
		return latitude;
	}
	
	public float getLongitude(){
		return longitude;
	}
}
