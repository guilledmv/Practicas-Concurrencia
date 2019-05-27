//package Practica1;
import es.upm.babel.cclib.Monitor;
import es.upm.babel.cclib.Monitor.Cond;
import es.upm.aedlib.fifo.FIFO;
import es.upm.aedlib.fifo.FIFOList;


//Grupo: Guillermo De Miguel Villanueva (160262) , Marcos Arevalo Salas (160266)

public class EnclavamientoMonitor implements Enclavamiento {
	

	//------------- VARIABLES GLOBALES -------------

  Monitor mutex = new Monitor();
  private boolean presencia;// Declaramos un tipo enumerado para los colores del semaforo
  private int [] trenes; 	// Declaramos un array de trenes para controlar el paso por las balizas
  private Control.Color [] coloresBaliza; // Declaramos array del enumerado control.Color para los colores de los semaforos
  private FIFO<PeticionBarrera> listaBarrera = new FIFOList<PeticionBarrera>();
  private FIFO<PeticionFreno> listaFreno = new FIFOList<PeticionFreno>();
  private FIFO<PeticionSemaforo> listaSemaforo = new FIFOList<PeticionSemaforo>();
  
  public EnclavamientoMonitor() {
	  this.presencia = false; // Inicializamos la presencia--> false
	  this.trenes = new int [] {0,0,0,0}; // Declaro el array de trenes inicializando a 0 el numero de trenes despues de cada baliza
	  // Inicializamos a VERDE todos los colores de los semaforos
	  // Inicializamos a verde todos los semaforos 
	  this.coloresBaliza = new Control.Color [] {Control.Color.VERDE, Control.Color.VERDE,Control.Color.VERDE,Control.Color.VERDE};
  }
  
  // Metodo auxiliar con los colores correctos
  private void coloresCorrectos () {
	// Implementacion colores correctos
	   if( trenes[1] > 0 ) {
		   this.coloresBaliza[1] = Control.Color.ROJO; 
	   } if( trenes[1] == 0 && ( trenes[2] > 0 || presencia == true )) {
		   this.coloresBaliza[1] = Control.Color.AMARILLO; 	   
	   } if( trenes[1] == 0 && trenes[2] == 0 && presencia == false ) {
		   this.coloresBaliza[1] = Control.Color.VERDE; 
	   } if( trenes[2] > 0 || presencia == true ) {
		   this.coloresBaliza[2] = Control.Color.ROJO; 
	   } if( trenes[2] == 0 && presencia == false ) {
		   this.coloresBaliza[2] = Control.Color.VERDE; 
	   } 
		   this.coloresBaliza[3] = Control.Color.VERDE; 
  }
  
  public void avisarPresencia(boolean presencia) {
    mutex.enter();
    //-----POST--------
    this.presencia = presencia;
    coloresCorrectos();
    desbloquear();
    mutex.leave();
     
  }

  public boolean leerCambioBarrera(boolean actual) {
	  // Analizamos dos casos--> caso actual = true Y caso actual = false
	  mutex.enter();
	  //Declaramos variable booleana que devolveremos
	  boolean esperado = (this.trenes[1] + this.trenes[2] == 0);
	  // Si no se cumple la CPRE--> ESPERA
	  	if ( actual == (this.trenes[1] + this.trenes[2] == 0) ) {
	  // Creamos condicion de barrera
	  		Monitor.Cond levantarBarrera = mutex.newCond();
	  // metemos a la lista
	  		listaBarrera.enqueue(new PeticionBarrera(actual,levantarBarrera));
	  // Ponemos en espera
	  		levantarBarrera.await();
	  // Si se cumple la CPRE--> SENALIZA
	  	}
	  		// Desbloqueo de condicion
		  desbloquear();	
		  mutex.leave();
		  return esperado;
		  
	  }

  @Override
  public boolean leerCambioFreno(boolean actual) {
	  // Analizamos dos casos--> caso actual = true Y caso actual = false
	  mutex.enter();
	  //Declaramos variable booleana que devolveremos
	  boolean esperado = (this.trenes[1] > 1 || this.trenes[2] > 1 || this.trenes[2] == 1 && this.presencia == true);
	  // Si no se cumple la CPRE--> ESPERA
	  	if ( actual == (this.trenes[1] > 1 || this.trenes[2] > 1 || this.trenes[2] == 1 && this.presencia == true )) {
	  // Creamos condicion de freno
	  		Monitor.Cond activarFreno = mutex.newCond();
	  // metemos a la lista
	  		listaFreno.enqueue(new PeticionFreno(actual,activarFreno));
	  // Ponemos en espera
	  		activarFreno.await();
	  // Si se cumple la CPRE--> SENALIZA
	  	} 
	  		// Desbloqueo de condicion
		  desbloquear();	
		  mutex.leave();
		  return esperado;
  }

  @Override
  public Control.Color leerCambioSemaforo(int i, Control.Color actual) {
	  // Si no esta en ningun semaforo
	  if ( i == 0) {
		  throw new PreconditionFailedException();
	  }
	    mutex.enter();
	    // Declaramos color que devolveremos 
	    Control.Color encendido = coloresBaliza[i];
	    // Si no se cumple la CPRE--> ESPERA
	    if ( actual.equals(coloresBaliza[i]) ) {
	    // Creamos condicion para semaforo 1
	    	Monitor.Cond condSemaforo = mutex.newCond();
	    // Metemos a la lista el semaforo
	    	listaSemaforo.enqueue(new PeticionSemaforo(i,actual,condSemaforo));
	    // Ponemos en espera la condicion
	    	condSemaforo.await();
	    } 
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
	    this.trenes[i-1] = trenes [i-1]-1;
	    this.trenes[i] = trenes[i]+1;
	    coloresCorrectos();
	    desbloquear();
	    mutex.leave();
	    }
  
  private class PeticionBarrera {
	  
	  private boolean activo;
	  private Monitor.Cond condition;
	  
	  private PeticionBarrera(boolean esperado, Monitor.Cond cond) {
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
 private class PeticionFreno {
	  
	  private boolean activo;
	  private Monitor.Cond condition;
	  
	  private PeticionFreno(boolean esperado, Monitor.Cond cond) {
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
  private class PeticionSemaforo {
	  
	  private int numeroSemaforo;
	  private Control.Color color;
	  private Monitor.Cond condition;
	  
	  private PeticionSemaforo(int id , Control.Color actual, Cond cond) {
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
		int sizeBarrera = listaBarrera.size();
		int sizeFreno = listaFreno.size();
		int sizeSemaforo = listaSemaforo.size();
		// Recorro dichas listas, para saber si existen elementos en espera
		for (int i = 0; i < sizeBarrera && !senalizado; i++) {
			
			PeticionBarrera petBar = listaBarrera.first(); // Obtenemos primer elemento de la cola
			listaBarrera.dequeue(); // desencolamos primer elemento
			
			// SI SE CUMPLE LA CPRE DE BARRERA--> desbloquea y senaliza
			if( petBar.getActivo() != (trenes[1] + trenes[2] == 0) && petBar.getCondition().waiting() > 0) {
				petBar.getCondition().signal();
				senalizado = true;
			// SI NO SE CUMPLE LA CPRE DE BARRERA--> Vuelve a encolar el elemento
			} else {
				listaBarrera.enqueue(petBar);
			}
		} // Cierre bucle cola Barrera
			for ( int j = 0 ; j < sizeFreno && !senalizado; j++) { 
				
			PeticionFreno petFren = listaFreno.first();
			listaFreno.dequeue();
			
			// SI SE CUMPLE LA CPRE DEL FRENO--> Desbloquea y senaliza
			if ( petFren.getActivo() != ((trenes[1] > 1) || (trenes[2] > 1) || (trenes[2] == 1) && (presencia == true)) && petFren.getCondition().waiting() > 0) {
				petFren.getCondition().signal();
				senalizado = true;
			// SI NO SE CUMPLE LA CPRE DE FRENO--> Vuelve a encolar elemento
			} else {
				listaFreno.enqueue(petFren);
			}
			}// Cierre bucle cola Freno
			
			for( int z = 0; z < sizeSemaforo && !senalizado; z++) {
				
				PeticionSemaforo petSem = listaSemaforo.first();
				listaSemaforo.dequeue();
				
			// SI SE CUMPLE LA CPRE PARA SEMAFORO 1--> desbloquea y senaliza
			if ( petSem.getNumeroSemaforo() == 1 && (petSem.getColor() != (coloresBaliza[1])) && petSem.getCondition().waiting() > 0 ) {
				petSem.getCondition().signal();
				senalizado = true;
			} else {
			// SI NO SE CUMPLE LA CRE DE SEMAFORO 2--> Vuelve a encolar la peticion del semaforo
				listaSemaforo.enqueue(petSem);
			}
			if ( (petSem.getNumeroSemaforo() == 2) && (petSem.getColor() != (coloresBaliza[2])) && petSem.getCondition().waiting() > 0 ) {
				petSem.getCondition().signal();
				senalizado = true;
			} else {
			// SI NO SE CUMPLE LA CRE DE SEMAFORO 3--> Vuelve a encolar la peticion del semaforo
				listaSemaforo.enqueue(petSem);
			}
			
} // Cierre bucle cola Semaforo
} // Cierre desbloquear
}
