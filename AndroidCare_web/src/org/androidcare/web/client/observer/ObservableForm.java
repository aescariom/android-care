package org.androidcare.web.client.observer;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FormPanel;

public class ObservableForm extends FormPanel{
	private List<Observer> observers; 
	
	public ObservableForm(){
		super();
	}
	
    public void deleteObservers() { 
    	synchronized(observers){
    		getObservers().clear();
    	}
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
