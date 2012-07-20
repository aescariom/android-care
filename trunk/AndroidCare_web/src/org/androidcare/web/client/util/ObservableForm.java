/** 
 * This library/program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library/program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library/program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

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
