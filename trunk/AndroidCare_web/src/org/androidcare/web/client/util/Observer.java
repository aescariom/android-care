//@Comentario a no ser que termines metiendo algo más en este paquete, yo lo llamaría
//org.androidcare.web.client.observer
package org.androidcare.web.client.util;

public interface Observer {
    
  //@Comentario es bastante habitual (aunque no siempre se hace, a lo mejor esto es la mejor solución en tu caso)
    //en el patrón Observer pasarle al método/métodos que notifican las actualizaciones
    //información de que ha cambiado para evitar que el cliente que observa tenga que
    //"pedir todos los datos de nuevo" porque no sabe cuál es el cambio.
	public void update();
}
