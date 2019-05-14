package Practica1;
import java.util.Arrays;
import es.upm.babel.cclib.Monitor;

//Grupo: Guillermo De Miguel Villanueva (160262) , Marcos Arevalo Salas (160266)

public class EnclavamientoMonitor implements Enclavamiento {

	//------------- VARIABLES GLOBALES -------------

  Monitor mutex;
  private Monitor.Cond condicion;
  private boolean presencia; 												// Declaramos booleano para la presencia
  private enum color {Rojo,Amarillo,Verde};									// Declaramos un tipo enumerado para los colores del semaforo
  private int [] trenes; 	// Declaramos un mapa de trenes para controlar el paso por las balizas
  private String [] coloresBaliza; 											// Declaramos array de String para los colores de los semaforos
  
  public EnclavamientoMonitor() {
	  this.mutex = new Monitor();
	  this.condicion = mutex.newCond(); 
	  this.presencia = false; // Inicializamos la presencia--> false
	  this.coloresBaliza = new String [] {"VERDE","VERDE","VERDE","VERDE"};
	  for ( int i = 1; i<4;i++) {
		  trenes[i] = 0; // Inicializamos a 0, el numero de trenes detras de la baliza
	  }    
  }
  
  // Metodo auxiliar con los colores correctos
  private void coloresCorrectos () {
	// Implementacion colores correctos
	   if( trenes[1]>0 ) {
		   coloresBaliza[1] = "ROJO";
	   } else if( trenes[1] == 0 && ( trenes[2]>0 || presencia == true)) {
		   coloresBaliza[1] = "AMARILLO";
	   } else if( trenes[1] == 0 && trenes[2] == 0 && presencia == false) {
		   coloresBaliza[1] = "VERDE";
	   } else if( trenes[2]>0 || presencia == true) {
		   coloresBaliza[2] = "ROJO";
	   } else if( trenes[2] == 0 && presencia == false) {
		   coloresBaliza[2] = "VERDE";
	   } else {
		   coloresBaliza[3] = "VERDE";
	   }
  }
  
  @Override
  public void avisarPresencia(boolean presencia) {
    mutex.enter();
    
   //------- PRE == CIERTO -------
    
  //------- POST -------
   this.presencia = presencia;
   coloresCorrectos();
    mutex.leave();
     }

  @Override
  public boolean leerCambioBarrera(boolean actual) {
    mutex.enter();
    
    //------- SI NO SE CUMPLE LA CPRE -> EXCEPCION -------
    if(actual == (this.trenes[1] + this.trenes[2] == 0)) { 
    	condicion.await(); // Se queda en espera
    
    }	//------- POST -------  	
    boolean esperado = (this.trenes[1] + this.trenes[2] == 0);
    if(condicion.waiting() > 0) { // Si existen esperando procesos
    	condicion.signal(); // Ejecuta los procesos
    } 
    	mutex.leave();
    	return esperado;
  }

  @Override
  public boolean leerCambioFreno(boolean actual) {
    mutex.enter();
    //------- SI NO SE CUMPLE LA CPRE -> EXCEPCION -------
    
    boolean resul=false;
    if(this.trenes[1]>1 || this.trenes[2]>1 || this.trenes[2]==1 && this.presencia==true) {
    	resul=true;
    }

    if (actual==resul) {
		mutex.leave();
		throw new PreconditionFailedException();
	}
    
  //------- POST -------
    
    boolean esperado= (this.trenes[1] > 1 || this.trenes[2] > 1 || this.trenes[2] == 1 && this.presencia == true);
    mutex.leave();
    return esperado;
  }

  @Override
  public Control.Color leerCambioSemaforo(int i, Control.Color actual) {
	    mutex.enter();
	    //------- SI NO SE CUMPLE LA PRE Y CPRE -> EXCEPCION -------
	    
	    if (i == 0 && actual.toString().equals(this.coloresBaliza[i])) {
			mutex.leave();
			throw new PreconditionFailedException();
		}
	    
	  //------- POST -------

	    Control.Color esperado = actual.valueOf(this.coloresBaliza[i]);
	    mutex.leave();
	    return esperado;
	  }

  @Override
  public void avisarPasoPorBaliza(int i) {
	    mutex.enter();
	    //------- SI NO SE CUMPLE LA PRE Y CPRE -> EXCEPCION -------
	    if (i == 0) {
			mutex.leave();
			throw new PreconditionFailedException();
		}
	    
	  //------- POST -------
	    this.trenes[i-1]-= 1 ;
	    this.trenes[i]+= 1 ;
	    //this.trenes.put(i, this.trenes.get(i)+1);	    
	    mutex.leave();
	   
	  }
 
}
