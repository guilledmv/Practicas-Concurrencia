//package Practica1;
import java.util.Arrays;
import es.upm.babel.cclib.Monitor;

//Grupo: Guillermo De Miguel Villanueva (160262) , Marcos Arevalo Salas (160266)

public class EnclavamientoMonitor implements Enclavamiento {

	//------------- VARIABLES GLOBALES -------------

  Monitor mutex = new Monitor();
  private Monitor.Cond cBarrera;
  private Monitor.Cond cFreno;
  private Monitor.Cond cSemaforo;
  private Monitor.Cond cPresencia;
  private boolean presencia;// Declaramos un tipo enumerado para los colores del semaforo
  private enum color {ROJO,AMARILLO,VERDE};
  private int [] trenes; 	// Declaramos un array de trenes para controlar el paso por las balizas
  private color [] coloresBaliza; // Declaramos array del enumerado Color para los colores de los semaforos
  
  public EnclavamientoMonitor() {
	  this.cBarrera = mutex.newCond(); // Condicion barrera
	  this.cFreno = mutex.newCond(); // Condicion Freno
	  this.cSemaforo = mutex.newCond(); // Condicion semaforo
	  this.cPresencia = mutex.newCond(); // Condicion Presencia
	  this.presencia = false; // Inicializamos la presencia--> false
	  this.trenes = new int [] {0,0,0,0}; // Declaro el array de trenes inicializando a 0 el numero de trenes despues de cada baliza
	  this.coloresBaliza = new color[] {color.VERDE,color.VERDE,color.VERDE,color.VERDE};
	  // Inicializo a verde todos los colores de los semaforos
  }
  
  // Metodo auxiliar con los colores correctos
  private void coloresCorrectos () {
	// Implementacion colores correctos
	   if( trenes[1]>0 ) {
		   coloresBaliza[1] = color.ROJO;
	   } else if( trenes[1] == 0 && ( trenes[2]>0 || presencia == true)) {
		   coloresBaliza[1] = color.AMARILLO;
	   } else if( trenes[1] == 0 && trenes[2] == 0 && presencia == false) {
		   coloresBaliza[1] = color.VERDE;
	   } else if( trenes[2]>0 || presencia == true) {
		   coloresBaliza[2] = color.ROJO;
	   } else if( trenes[2] == 0 && presencia == false) {
		   coloresBaliza[2] = color.VERDE;
	   } else {
		   coloresBaliza[3] = color.VERDE;
	   }
  }
  
  public void avisarPresencia(boolean presencia) {
    mutex.enter();
    
   //------- PRE == CIERTO -------
    
  //------- POST -------
   this.presencia = presencia;
   coloresCorrectos();
    mutex.leave();
     }

  public boolean leerCambioBarrera(boolean actual) {
    mutex.enter();
    
    //------- SI NO SE CUMPLE LA CPRE -> EN ESPERA -------
    if(actual == ((this.trenes[1] + this.trenes[2]) == 0)) { 
    	cBarrera.await(); // Se queda en espera
    }	
    //------- POST -------  	
    boolean esperado = ((this.trenes[1] + this.trenes[2]) == 0);
    if(cBarrera.waiting() > 0 && esperado) { // Si existen esperando procesos
    	cBarrera.signal(); // Ejecuta los procesos
    	mutex.leave();
    }
    	return esperado;
  }

  @Override
  public boolean leerCambioFreno(boolean actual) {
    mutex.enter();
    //------- SI NO SE CUMPLE LA CPRE -> EN ESPERA -------
    if(actual == this.trenes[1] > 1 || this.trenes[2] > 1 || this.trenes[2] == 1 && this.presencia == true) {
    	cFreno.await(); // Se queda en espera
    }
  //------- POST -------
    
    boolean esperado= (this.trenes[1] > 1 || this.trenes[2] > 1 || this.trenes[2] == 1 && this.presencia == true);
    if( cFreno.waiting() > 0 && esperado) {
    cFreno.signal();
    mutex.leave();
    }
    return esperado;
  }

  @Override
  public Control.Color leerCambioSemaforo(int i, Control.Color actual) {
	    mutex.enter();
	    //------- SI NO SE CUMPLE LA PRE Y CPRE -> EN ESPERA -------
	    if (i == 0 || i!=0 && actual.equals(this.coloresBaliza)) {
			cSemaforo.await();
		}
	  //------- POST -------
	    Control.Color esperado = actual.valueOf(this.coloresBaliza[i].name()) ;
	    if(cSemaforo.waiting() > 0) {
	    cSemaforo.signal();
	    mutex.leave();
	    }
	    return esperado;
	  }

  @Override
  public void avisarPasoPorBaliza(int i) {
	    mutex.enter();
	    //------- SI NO SE CUMPLE LA PRE Y CPRE -> EN ESPERA -------
	    if (i == 0) {
			cPresencia.await();
		}
	  //------- POST -------
	    this.trenes[i-1]-= 1 ;
	    this.trenes[i]+= 1 ;
	    cPresencia.signal();
	    //this.trenes.put(i, this.trenes.get(i)+1);	    
	    mutex.leave();
	   
	  }
 
}
