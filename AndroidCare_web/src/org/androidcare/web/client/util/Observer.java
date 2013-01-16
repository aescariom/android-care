//@Comentario a no ser que termines metiendo algo m�s en este paquete, yo lo llamar�a
//org.androidcare.web.client.observer
package org.androidcare.web.client.util;

public interface Observer {
    
  //@Comentario es bastante habitual (aunque no siempre se hace, a lo mejor esto es la mejor soluci�n en tu caso)
    //en el patr�n Observer pasarle al m�todo/m�todos que notifican las actualizaciones
    //informaci�n de que ha cambiado para evitar que el cliente que observa tenga que
    //"pedir todos los datos de nuevo" porque no sabe cu�l es el cambio.
	public void update();
}
