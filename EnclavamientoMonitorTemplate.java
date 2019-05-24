//package Practica1;
import es.upm.babel.cclib.Monitor;
import es.upm.babel.cclib.Monitor.Cond;

import java.util.LinkedList;


//Grupo: Guillermo De Miguel Villanueva (160262) , Marcos Arevalo Salas (160266)

public class EnclavamientoMonitor implements Enclavamiento {
	

	//------------- VARIABLES GLOBALES -------------

  Monitor mutex = new Monitor();
  private boolean presencia;// Declaramos un tipo enumerado para los colores del semaforo
  private Control.Color ROJO = Control.Color.ROJO;
  private Control.Color AMARILLO = Control.Color.AMARILLO;
  private Control.Color VERDE = Control.Color.VERDE;
  private int [] trenes; 	// Declaramos un array de trenes para controlar el paso por las balizas
  private Control.Color [] coloresBaliza; // Declaramos array del enumerado control.Color para los colores de los semaforos
  private LinkedList<Peticion> lista = new LinkedList<Peticion>();
  private LinkedList<Semaforo> listaSemaforo = new LinkedList<Semaforo>();
  
  public EnclavamientoMonitor() {
	  this.presencia = false; // Inicializamos la presencia--> false
	  this.trenes = new int [] {0,0,0,0}; // Declaro el array de trenes inicializando a 0 el numero de trenes despues de cada baliza
	  // Inicializamos a VERDE todos los colores de los semaforos
	  // Inicializamos a verde todos los semaforos 
	  this.coloresBaliza= new Control.Color [] {VERDE, VERDE, VERDE, VERDE};
  }
  
  // Metodo auxiliar con los colores correctos
  private void coloresCorrectos () {
	// Implementacion colores correctos
	   if( trenes[1]>0 ) {
		   this.coloresBaliza[1] = ROJO; 
	   } else if( trenes[1] == 0 && ( trenes[2]>0 || presencia == true )) {
		   this.coloresBaliza[1] = AMARILLO; 	   
	   } else if( trenes[1] == 0 && trenes[2] == 0 && presencia == false ) {
		   this.coloresBaliza[1] =VERDE; 
	   } else if( trenes[2]>0 || presencia == true ) {
		   this.coloresBaliza[2] = ROJO; 
	   } else if( trenes[2] == 0 && presencia == false ) {
		   this.coloresBaliza[2] = VERDE; 
	   } else {
		   this.coloresBaliza[3] = VERDE; 
	   }
  }
  
  public void avisarPresencia(boolean presencia) {
	  // Modificar este metodo con presencia = false y || presencia = true
    mutex.enter();
    //-----POST--------
    this.presencia = presencia;
    coloresCorrectos();
    mutex.leave();
     
  }

  public boolean leerCambioBarrera(boolean actual) {
	  // Analizamos dos casos--> caso actual = true Y caso actual = false
	  mutex.enter();
	  //Declaramos variable booleana que devolveremos
	  boolean activado = false;
	  // Si no se cumple la CPRE--> ESPERA
	  	if ( actual == (this.trenes[1] + this.trenes[2] == 0) ) {
	  // Creamos condicion de barrera
	  		Monitor.Cond levantarBarrera = mutex.newCond();
	  // metemos a la lista
	  		lista.add(new Peticion(actual,levantarBarrera));
	  // Ponemos en espera
	  		levantarBarrera.await();
	  // Si se cumple la CPRE--> SENALIZA
	  	}
	  		//Cambio valor de activado--> Levanto barrera
		  activado = true;
	  		// Desbloqueo de condicion
		  desbloquear();	
		  mutex.leave();
	  	
		  return activado;
	  }

  @Override
  public boolean leerCambioFreno(boolean actual) {
	  // Analizamos dos casos--> caso actual = true Y caso actual = false
	  mutex.enter();
	  //Declaramos variable booleana que devolveremos
	  boolean activado = false;
	  // Si no se cumple la CPRE--> ESPERA
	  	if ( actual == (this.trenes[1] > 1 || this.trenes[2] > 1 || this.trenes[2] == 1 && this.presencia == true )) {
	  // Creamos condicion de freno
	  		Monitor.Cond activarFreno = mutex.newCond();
	  // metemos a la lista
	  		lista.add(new Peticion(actual,activarFreno));
	  // Ponemos en espera
	  		activarFreno.await();
	  // Si se cumple la CPRE--> SENALIZA
	  	} 
	  		//Cambio valor de activado--> Levanto barrera
		  activado = true;
	  		// Desbloqueo de condicion
		  desbloquear();	
		  mutex.leave();
		  
		  return activado;
  }

  @Override
  public Control.Color leerCambioSemaforo(int i, Control.Color actual) {
	  // Si no esta en ningun semaforo
	  if ( i == 0) {
		  throw new PreconditionFailedException();
	  }
	    mutex.enter();
	    // Declaramos color que devolveremos 
	    Control.Color encendido;
	    // Si no se cumple la CPRE--> ESPERA
	    if ( i == 1 && actual.equals(coloresBaliza[1])) {
	    // Creamos condicion para semaforo 1
	    	Monitor.Cond condS1 = mutex.newCond();
	    // Metemos a la lista el semaforo
	    	listaSemaforo.add(new Semaforo(i,actual,condS1));
	    // Ponemos en espera la condicion
	    	condS1.await();
	    } 
	    	// Cambio valor de encendido al que haya en self.color(i)
	    	encendido = coloresBaliza[1];
	    	// Desbloqueo
	    	desbloquear();
	 // Si no se cumple la CPRE--> ESPERA
	    if ( i == 2 && actual.equals(coloresBaliza[2])) {
	    // Creamos condicion para semaforo 2
	    	Monitor.Cond condS2 = mutex.newCond();
	    // Metemos a la lista el semaforo
	    	listaSemaforo.add(new Semaforo(i,actual,condS2));
	    // Ponemos en espera la condicion
	    	condS2.await();
	    } 
	    	// Cambio valor de encendido al que haya en self.color(i)
	    	encendido = coloresBaliza[2];
	    	// Desbloqueo
	    	desbloquear();
	 // Si no se cumple la CPRE--> ESPERA
	    if ( i == 3 && actual.equals(coloresBaliza[3])) {
	    // Creamos condicion para semaforo 1
	    	Monitor.Cond condS3 = mutex.newCond();
	    // Metemos a la lista el semaforo
	    	listaSemaforo.add(new Semaforo(i,actual,condS3));
	    // Ponemos en espera la condicion
	    	condS3.await();
	    }
	    	// Cambio valor de encendido al que haya en self.color(i)
	    	encendido = coloresBaliza[3];
	    	// Desbloqueo
	    	desbloquear();
	    	
	    	mutex.leave();
	    	return encendido;
  }

  @Override
  public void avisarPasoPorBaliza(int i) {
	    // Si i == 0 --> EXCEPCION
	    if ( i == 0) {
	    	throw new PreconditionFailedException();
	    } 
	    mutex.enter();
	    if ( i == 1) {
	    	this.trenes[i] = trenes[i]+1;
	    } else if( i == 2) {
	    	this.trenes[i-1]= trenes[i-1]-1;
	    	this.trenes[i] = trenes[i]+1;
	    } else if ( i == 3) {
	    	this.trenes[i-1]= trenes[i-1]-1;
	    	this.trenes[i] = trenes[i]+1;
	    } 
	    
	    coloresCorrectos();
	    mutex.leave();
	  }
  private class Peticion {
	  
	  private boolean activo;
	  private Monitor.Cond condition;
	  
	  private Peticion(boolean esperado, Monitor.Cond cond) {
		  this.activo = esperado;
		  this.condition = cond;
	  }
	  private boolean getActivo() {
		  return this.activo;
	  }
	  private Monitor.Cond getCondition(){
		  return this.condition;
	  }
	  
  }
  private class Semaforo {
	  
	  private int numeroSemaforo;
	  private Control.Color color;
	  private Monitor.Cond condition;
	  
	  private Semaforo(int id , Control.Color actual, Cond cond) {
		  this.numeroSemaforo = id;
		  this.color = actual;
		  this.condition = cond;
	  }
	  private int getNumeroSemaforo() {
		  return this.numeroSemaforo;
	  }
	  private Control.Color getColor() {
			  return this.color;  
	  }
	 private Monitor.Cond getCondition(){
			 return this.condition;
	 }
	 
	 }
  
  private void desbloquear() {
		boolean senalizado = false;
		// Almaceno longitudes de mis listas donde almaceno los bloqueos
		int size = lista.size();
		int sizeSem = listaSemaforo.size();
		// Recorro dichas listas, para saber si existen elementos en espera
		for (int i = 0; i < sizeSem && !senalizado; i++) {
			for ( int j = 0 ; j<size && !senalizado; j++) {
			Peticion peticion = lista.getFirst();
			lista.getLast(); // quitamos la ultima peticion			
			//se cumple la CPRE para Barrera y Freno
			if((peticion.getActivo() != (this.trenes[1] + this.trenes[2] == 0)) || (peticion.getActivo() !=(this.trenes[1] > 1 || this.trenes[2] > 1 || this.trenes[2] == 1 && this.presencia == true)) &&
					(peticion.getCondition().waiting() > 0)) { 
				peticion.getCondition().signal(); // hacemos un signal
				senalizado = true;    // ya hemos senalizado
			// No se cumple la CPRE
			} 
				// condition que no has de despertar, a la lista de nuevo
				lista.add(peticion);
		}
			Semaforo sem = listaSemaforo.getFirst();
			listaSemaforo.getLast(); // quitamos el ultimo semaforo
			if(( sem.getNumeroSemaforo() == 1 && !sem.getColor().equals(coloresBaliza[1]) && sem.getCondition().waiting() > 0) ||
					( sem.getNumeroSemaforo() == 2 && !sem.getColor().equals(coloresBaliza[2]) && sem.getCondition().waiting() > 0)
					|| ( sem.getNumeroSemaforo() == 3 && !sem.getColor().equals(coloresBaliza[3]) && sem.getCondition().waiting() > 0)){
						sem.getCondition().signal();
						senalizado = true;
					}
				listaSemaforo.add(sem);
		}
	}
}
