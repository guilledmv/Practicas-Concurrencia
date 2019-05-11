package Practica1;

import es.upm.babel.cclib.Monitor;

//Grupo: Guillermo De Miguel Villanueva (160262) , Marcos Arevalo Salas (160266)

public class EnclavamientoMonitorTemplate implements Enclavamiento {

//Declaro variables necesarias para el programa

  Monitor mutex = new Monitor();
  
  private boolean presencia; // Declaramos booleano para la presencia
  
  private int [] tren = {0,1,2,3}; // Declaramos un array de tren, para saber a que tren se refiere
  
  private enum color {Rojo,Amarillo,Verde}; // Declaramos un tipo enumerado para los colores del semaforo
  
  
  public EnclavamientoMonitorTemplate(int[] tren) {
	  
	  this.presencia = false; // Inicializamos la presencia--> false
	  
	  this.tren = new int[0]; // Inicializamos al tren 0                // INICIAL
	  
	  color c = color.Verde; // Inicializamos al color verde
	    
  }
  
  
  @Override
  public void avisarPresencia(boolean presencia) {
    mutex.enter();
    // chequeo de la PRE
    // chequeo de la CPRE y posible bloqueo
    // implementacion de la POST
   
    
    // codigo de desbloqueo
    mutex.leave();
  }

  @Override
  public boolean leerCambioBarrera(boolean actual) {
    mutex.enter();
    // chequeo de la PRE
    // chequeo de la CPRE y posible bloqueo
    // implementacion de la POST
    // codigo de desbloqueo
    mutex.leave();
    return false;
  }

  @Override
  public boolean leerCambioFreno(boolean actual) {
    mutex.enter();
    // chequeo de la PRE
    // chequeo de la CPRE y posible bloqueo
    // implementacion de la POST
    // codigo de desbloqueo
    mutex.leave();
    return false;
  }

  @Override
  public Control.Color leerCambioSemaforo(int i, Control.Color actual) {
    mutex.enter();
    // chequeo de la PRE
    // chequeo de la CPRE y posible bloqueo
    // implementacion de la POST
    // codigo de desbloqueo
    mutex.leave();
    return null;
  }

  @Override
  public void avisarPasoPorBaliza(int i) {
    mutex.enter();
    // chequeo de la PRE
    // chequeo de la CPRE y posible bloqueo
    // implementacion de la POST
    // codigo de desbloqueo
    mutex.leave();
  }
 
}
