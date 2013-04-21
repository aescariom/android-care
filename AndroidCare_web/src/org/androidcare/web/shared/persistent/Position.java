package org.androidcare.web.shared.persistent;

import java.io.Serializable;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Position implements Serializable {


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
	private Date serverDate;
	
	@Persistent
	private String owner;
	
	public Position(){}
	
	public Position(float latitude, float longitude, String owner, Date date){
		this.latitude = latitude;
		this.longitude = longitude;
		this.owner = owner;
		this.date = new Date(date.getTime());
		//this.serverDate = new Date();
	}
	
	public Position(Position p){
		this.latitude = p.getLatitude();
		this.longitude = p.getLongitude();
		this.owner = p.getOwner();
		this.date = new Date(p.getDate().getTime());
		this.serverDate = new Date(p.getServerDate().getTime());
	}
	
	private String getOwner() {
		return owner;
	}

	public float getLatitude(){
		return latitude;
	}
	
	public float getLongitude(){
		return longitude;
	}
	
	public Date getDate(){
		return date;
	}
	
	public Date getServerDate(){
		return serverDate;
	}
}
