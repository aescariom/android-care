package org.androidcare.web.shared.persistent;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.io.Serializable;
import java.util.Date;

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
		if(date != null){
			this.date = new Date(date.getTime());
		}else{
			this.date = new Date();
		}
		this.serverDate = new Date();
	}
	
	public Position(Position position){
		latitude = position.getLatitude();
		longitude = position.getLongitude();
		owner = position.getOwner();
		if(position.getDate() != null){
			date = new Date(position.getDate().getTime());
		}else{
			date = new Date();
		}
		if(position.getServerDate() != null){
			serverDate = new Date(position.getServerDate().getTime());
		}else{
			serverDate = new Date();
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
		return date;
	}
	
	public Date getServerDate(){
		return serverDate;
	}
	
	public void setDate(Date d){
		this.date = d;
	}
	
	public void initializeServerDate(){
		serverDate = new Date();
	}
}
