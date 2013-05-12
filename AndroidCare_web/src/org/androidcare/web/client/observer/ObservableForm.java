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
  //@comentario ojo, est�s tomando lock sobre un objeto diferente que el m�todo anterior!
    public synchronized void addObserver(Observer o) { 
    	getObservers().add(o); 
    } 
    
    public synchronized int countObservers() { 
    	return getObservers().size(); 
    } 
    
    public synchronized void deleteObserver(Observer o) { 
    	getObservers().remove(o); 
    } 
  //@comentario yo coger�a el lock dde nuevo, no vaya a ser que el d�a de ma�ana llames a este m�todo es de otro lado
 //De todos modos, como que todo queda m�s f�cil si construyes la lista en el constructor
    private List<Observer> getObservers() { 
    	if(observers == null) {
    		observers = new ArrayList<Observer>(); 
    	}
    	return observers; 
    } 
  //@comentario no coger ning�n lock
    protected void broadcastObservers(){ 
    	for(Observer o: getObservers()) 
    		o.update(); 
    } 
}
