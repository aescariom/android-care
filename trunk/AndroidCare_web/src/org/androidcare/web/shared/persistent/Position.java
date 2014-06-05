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
	private long date;
	
	@Persistent 
	private long serverDate;
	
	@Persistent
	private String owner;
	
	public Position(){}
	
	public Position(float latitude, float longitude, String owner, Date date){
		this.latitude = latitude;
		this.longitude = longitude;
		this.owner = owner;
		if(date != null){
			this.date = date.getTime();
		}else{
			this.date = new Date().getTime();
		}
		this.serverDate = new Date().getTime();
	}
	
	public Position(Position position){
		latitude = position.getLatitude();
		longitude = position.getLongitude();
		owner = position.getOwner();
		if(position.getDate() != null){
			date = new Date(position.getDate().getTime()).getTime();
		}else{
 			date = new Date().getTime();
		}
		if(position.getServerDate() != null){
			serverDate = new Date(position.getServerDate().getTime()).getTime();
		}else{
			serverDate = new Date().getTime();
		}
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
		return new Date(date);
	}
	
	public Date getServerDate(){
		return new Date(serverDate);
	}
	
	public void setDate(Date d){
		this.date = d.getTime();
	}
	
	public void initializeServerDate(){
		serverDate = new Date().getTime();
	}
	
}
