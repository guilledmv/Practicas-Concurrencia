package Practica1;

import java.util.ArrayList;

import java.util.HashMap;

import java.util.Map;

import es.upm.babel.cclib.Monitor;

//Grupo: Guillermo De Miguel Villanueva (160262) , Marcos Arevalo Salas (160266)

public class EnclavamientoMonitor implements Enclavamiento {

//Declaro variables necesarias para el programa

  Monitor mutex = new Monitor();
  
  private boolean presencia; // Declaramos booleano para la presencia
  
  private enum color {Rojo,Amarillo,Verde}; // Declaramos un tipo enumerado para los colores del semaforo
  
  private Map<Integer,Integer> trenes = new HashMap<Integer,Integer>(); // Declaramos un mapa de trenes para controlar el paso por las balizas
  
  // private Map<Integer,color> colores = new HashMap<Integer,color>(); // Declaramos id para los colores, que nos indican por que semaforo pasa 
  
  private String [] coloresBaliza; // Declaramos array de String para los colores de los semaforos
  
  // private color actual; // Con esta variable sabemos el color actual en cada momento
  
  public EnclavamientoMonitor() {
	  
	  this.presencia = false; // Inicializamos la presencia--> false  
	  this.coloresBaliza = new String [] {"Verde","Verde","Verde","Verde"};
	  for ( Integer i = 0; i<trenes.size();i++) {
		  trenes.put(i,0); // Inicializamos a 0, el numero de trenes detras de la baliza
	  }
	  
	
	    
  }
  
  // Metodo auxiliar con los colores correctos
  private color coloresCorrectos () {
	// Implementacion colores correctos
	   if( tren[1] > 0 ) {
		   actual = color.Rojo;
	   } else if( tren[1] == 0 && ( tren[2] > 0 || presencia == true)) {
		   actual = color.Amarillo;
	   } else if( tren[1] == 0 && tren[2] == 0 && presencia == false) {
		   actual = color.Verde;
	   } else if( tren[2] > 0 || presencia == true) {
		   actual = color.Rojo;
	   } else if( tren[2] == 0 && presencia == false) {
		   actual = color.Verde;
	   } else {
		   actual = color.Verde;
	   }
	return actual;
  }
  
  @Override
  public void avisarPresencia(boolean presencia) {
    mutex.enter();
    // chequeo de la PRE
    // chequeo de la CPRE y posible bloqueo
    // implementacion de la POST
   this.presencia = presencia;
   // Falta por programar la condicion--> self.tren = self.PRE.tren 
   coloresCorrectos();
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
