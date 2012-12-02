package org.androidcare.web.client.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FormPanel;

/**
 * This class implements in GWT an observable form
 * @author Alejandro Escario MŽndez
 */
public class ObservableForm extends FormPanel{
	private List<Observer> observers; 
	
	/**
	 * 
	 */
	public ObservableForm(){
		super();
	}
	
	/**
	 * 
	 */
	//@Comentario ya te he comentado una ocasión que  no me gusta sincronizar  a nivel de método
    public synchronized void deleteObservers() { 
    	getObservers().clear(); 
    } 
    
    /**
     * 
     * @param o
     */
    public synchronized void addObserver(Observer o) { 
    	getObservers().add(o); 
    } 
    
    /**
     * 
     * @return
     */
    public synchronized int countObservers() { 
    	return getObservers().size(); 
    } 
    
    /**
     * 
     * @param o
     */
    public synchronized void deleteObserver(Observer o) { 
    	getObservers().remove(o); 
    } 
    
    /**
     * 
     * @return
     */
    private List<Observer> getObservers() { 
    	if(observers == null) {
    		observers = new ArrayList<Observer>(); 
    	}
    	return observers; 
    } 
    
    /**
     * 
     */
    protected void broadcastObservers(){ 
    	for(Observer o: getObservers()) 
    		o.update(); 
    } 
}
