package org.androidcare.web.client.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FormPanel;

public class ObservableForm extends FormPanel{
	private List<Observer> observers; 
	
	public ObservableForm(){
		super();
	}
	
	//@Comentario ya te he comentado una ocasión que  no me gusta sincronizar  a nivel de método
    public synchronized void deleteObservers() { 
    	getObservers().clear(); 
    } 
    
    public synchronized void addObserver(Observer o) { 
    	getObservers().add(o); 
    } 
    
    public synchronized int countObservers() { 
    	return getObservers().size(); 
    } 
    
    public synchronized void deleteObserver(Observer o) { 
    	getObservers().remove(o); 
    } 
    
    private List<Observer> getObservers() { 
    	if(observers == null) {
    		observers = new ArrayList<Observer>(); 
    	}
    	return observers; 
    } 
    
    protected void broadcastObservers(){ 
    	for(Observer o: getObservers()) 
    		o.update(); 
    } 
}
