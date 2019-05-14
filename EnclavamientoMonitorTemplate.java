import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;
import es.upm.babel.cclib.Monitor;

//Grupo: Guillermo De Miguel Villanueva (160262) , Marcos Arevalo Salas (160266)

public class EnclavamientoMonitor implements Enclavamiento {

	//------------- VARIABLES GLOBALES -------------

  Monitor mutex = new Monitor();
  private boolean presencia; 												// Declaramos booleano para la presencia
  private enum color {Rojo,Amarillo,Verde};									// Declaramos un tipo enumerado para los colores del semaforo
  private Map<Integer,Integer> trenes = new HashMap<Integer,Integer>(); 	// Declaramos un mapa de trenes para controlar el paso por las balizas
  private String [] coloresBaliza; 											// Declaramos array de String para los colores de los semaforos
  
  
  public EnclavamientoMonitor() {
	  
	  this.presencia = false; // Inicializamos la presencia--> false  
	  this.coloresBaliza = new String [] {"VERDE","VERDE","VERDE","VERDE"};
	  for ( Integer i = 1; i<4;i++) {
		  trenes.put(i,0); // Inicializamos a 0, el numero de trenes detras de la baliza
	  }    
  }
  
  // Metodo auxiliar con los colores correctos
  private void coloresCorrectos () {
	// Implementacion colores correctos
	   if( trenes.get(1)>0 ) {
		   coloresBaliza[1]= "ROJO";
	   } else if( trenes.get(1) == 0 && ( trenes.get(2)>0 || presencia == true)) {
		   coloresBaliza[1] = "AMARILLO";
	   } else if( trenes.get(1) == 0 && trenes.get(2) == 0 && presencia == false) {
		   coloresBaliza[1]= "VERDE";
	   } else if( trenes.get(2)>0 || presencia == true) {
		   coloresBaliza[2] = "ROJO";
	   } else if( trenes.get(2) == 0 && presencia == false) {
		   coloresBaliza[2] = "VERDE";
	   } else {
		   coloresBaliza[3] = "VERDE";
	   }
  }
  
  @Override
  public void avisarPresencia(boolean presencia) {
    mutex.enter();
    // chequeo de la PRE
    // chequeo de la CPRE y posible bloqueo
    // implementacion de la POST
    
   //------- PRE == CIERTO -------
    
  //------- POST -------
   this.presencia = presencia;
   coloresCorrectos();
    mutex.leave();
     }

  @Override
  public boolean leerCambioBarrera(boolean actual) {
    mutex.enter();
    // chequeo de la PRE
    // chequeo de la CPRE y posible bloqueo
    // implementacion de la POST
    // codigo de desbloqueo
    
    //------- SI NO SE CUMPLE LA CPRE -> EXCEPCION -------
    
    boolean resul=false;
    if(this.trenes.get(1)+this.trenes.get(2)==0) {
    	resul=true;
    }
    if (actual==resul) {
		mutex.leave();
		throw new PreconditionFailedException();
	}
    
  //------- POST -------
  boolean esperado=(this.trenes.get(1)+this.trenes.get(2)==0);
  mutex.leave();
  return esperado;
  }

  @Override
  public boolean leerCambioFreno(boolean actual) {
    mutex.enter();
    // chequeo de la PRE
    // chequeo de la CPRE y posible bloqueo
    // implementacion de la POST
    // codigo de desbloqueo
    
    //------- SI NO SE CUMPLE LA CPRE -> EXCEPCION -------
    
    boolean resul=false;
    if(this.trenes.get(1)>1 || this.trenes.get(2)>1 || this.trenes.get(2)==1 && this.presencia==true) {
    	resul=true;
    }

    if (actual==resul) {
		mutex.leave();
		throw new PreconditionFailedException();
	}
    
  //------- POST -------
    
    boolean esperado= (this.trenes.get(1)>1 || this.trenes.get(2)>1 || this.trenes.get(2)==1 && this.presencia==true);
    mutex.leave();
    return esperado;
  }

  @Override
  public Control.Color leerCambioSemaforo(int i, Control.Color actual) {
	    mutex.enter();
	    // chequeo de la PRE
	    // chequeo de la CPRE y posible bloqueo
	    // implementacion de la POST
	    // codigo de desbloqueo
	    
	    //------- SI NO SE CUMPLE LA PRE Y CPRE -> EXCEPCION -------
	    
	    if (i==0 && actual.toString().equals(this.coloresBaliza[i])) {
	    	    	
			mutex.leave();
			throw new PreconditionFailedException();
		}
	    
	  //------- POST -------

	    Control.Color esperado=actual.valueOf(this.coloresBaliza[i]);
	    mutex.leave();
	    return esperado;
	  }

  @Override
  public void avisarPasoPorBaliza(int i) {
	    mutex.enter();
	    // chequeo de la PRE
	    // chequeo de la CPRE y posible bloqueo
	    // implementacion de la POST
	    // codigo de desbloqueo
	    
	    //------- SI NO SE CUMPLE LA PRE Y CPRE -> EXCEPCION -------
	    
	    if (i==0) {
			mutex.leave();
			throw new PreconditionFailedException();
		}
	    
	  //------- POST -------
	   // this.trenes.put(i-1, this.trenes.get(i-1)-1);
	    //this.trenes.put(i, this.trenes.get(i)+1);	    
	    mutex.leave();
	   
	  }
 
}
