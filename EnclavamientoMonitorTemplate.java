package Practica1;
import es.upm.babel.cclib.Monitor;
import java.util.LinkedList;
import java.util.List;

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
	   } else if( trenes[1] == 0 && ( trenes[2]>0 || presencia == true)) {
		   this.coloresBaliza[1] = AMARILLO; 	   
	   } else if( trenes[1] == 0 && trenes[2] == 0 && presencia == false) {
		   this.coloresBaliza[1] =VERDE; 
	   } else if( trenes[2]>0 || presencia == true) {
		   this.coloresBaliza[2] = ROJO; 
	   } else if( trenes[2] == 0 && presencia == false) {
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
	  	if ( actual == false ) {
	  // Creamos condicion de barrera
	  		Monitor.Cond condBarrera = mutex.newCond();
	  // metemos a la lista
	  		lista.add(new Peticion(actual,condBarrera));
	  // Ponemos en espera
	  		condBarrera.await();
	  // Lanzamos excepcion
	  		throw new PreconditionFailedException();
	  // Si se cumple la CPRE--> SENALIZA
	  	} else {
	  		//Cambio valor de activado--> Levanto barrera
		  	activado = true;
	  		// Desbloqueo
		  	desbloquear();	
		  mutex.leave();
	  	}
		  return activado;
	  }

  @Override
  public boolean leerCambioFreno(boolean actual) {
	  // Analizamos dos casos--> caso actual = true Y caso actual = false
	  mutex.enter();
	  //Declaramos variable booleana que devolveremos
	  boolean activado = false;
	  // Si no se cumple la CPRE--> ESPERA
	  	if ( actual == false ) {
	  // Creamos condicion de freno
	  		Monitor.Cond condFreno = mutex.newCond();
	  // metemos a la lista
	  		lista.add(new Peticion(actual,condFreno));
	  // Ponemos en espera
	  		condFreno.await();
	  // Lanzamos excepcion
	  		throw new PreconditionFailedException();
	  // Si se cumple la CPRE--> SENALIZA
	  	} else {
	  		//Cambio valor de activado--> Levanto barrera
		  	activado = true;
	  		// Desbloqueo
		  	desbloquear();	
		  mutex.leave();
	  	}
		  return activado;
   
  	
  
  }

  @Override
  public Control.Color leerCambioSemaforo(int i, Control.Color actual) {
	  // Si no esta en ningun semaforo
	  if ( i <= 0 || i > 3) {
		  throw new PreconditionFailedException();
	  }
	    mutex.enter();
	    // Declaramos color que devolveremos 
	    Control.Color encendido = VERDE;
	    // SI NO SE CUMPLE LA CPRE--> ESPERA
	    if (  ) {
	  // Creamos condicion de freno
	  		Monitor.Cond condS1 = mutex.newCond();
	  // metemos a la lista
	  		listaSemaforo.add(new Semaforo(i,actual,condS1));
	  // Ponemos en espera
	  		condS1.await();
	  // Lanzamos excepcion
	  		throw new PreconditionFailedException();	
	    	
	    }
	    
	    mutex.leave();
	    return encendido;
  }

  @Override
  public void avisarPasoPorBaliza(int i) {
	    mutex.enter();
	    // Si i == 0 --> EXCEPCION
	    if ( i == 0 || i > 3) {
	    	throw new PreconditionFailedException();
	    } else if ( i == 1) {
	    	this.trenes[i] = trenes[i]+1;
	    } else if( i == 2) {
	    	this.trenes[i-1]= trenes[i-1]-1;
	    	this.trenes[i] = trenes[i]+1;
	    } else if ( i == 3) {
	    	this.trenes[i-1]= trenes[i-1]-1;
	    	this.trenes[i] = trenes[i]+1;
	    } else {
	    	throw new PreconditionFailedException();
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
	  private Control.Color [] color;
	  private Monitor.Cond [] condition;
	  
	  private Semaforo(int id , Control.Color [] col, Monitor.Cond [] cond) {
		  this.numeroSemaforo = id;
		  for (int i = 1; i <= 3; i++) {
			 this.color[i] = col[i];
			 this.condition[i] = cond[i];
		  }
	  }
	  private int getNumeroSemaforo() {
		  return this.numeroSemaforo;
	  }
	  private Control.Color getColor() {
			  return this.color[numeroSemaforo];
		  
	  }
	 private Monitor.Cond getCondition(){
			 return this.condition[numeroSemaforo];
	 }
	 
	 }
  
  private void desbloquear() {
		boolean senalizado = false;
		int size = lista.size(); 
		for (int i = 0; i < size && !senalizado; i++) {
			Peticion peticion = lista.getFirst();
			lista.getLast(); // quitamos la peticion
			
			//se cumple la CPRE
			if(peticion.getActivo() == true) { 
				peticion.getCondition().signal(); // hacemos un signal
				senalizado = true;    // ya hemos senalizado
			// No se cumple la CPRE
			} else {
				// condition que no has de despertar, a la lista de nuevo
				lista.add(peticion);
			}
		}
	}
 
 
 
}
