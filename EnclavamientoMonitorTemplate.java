//package Practica1;
import es.upm.babel.cclib.Monitor;
import java.util.LinkedList;
import java.util.List;

//Grupo: Guillermo De Miguel Villanueva (160262) , Marcos Arevalo Salas (160266)

public class EnclavamientoMonitor implements Enclavamiento {
	

	//------------- VARIABLES GLOBALES -------------

  Monitor mutex = new Monitor();
  private Monitor.Cond [] condiciones = new Monitor.Cond [11]; // Declaro array de condiciones
  private boolean presencia;// Declaramos un tipo enumerado para los colores del semaforo
  private Control.Color ROJO = Control.Color.ROJO;
  private Control.Color AMARILLO = Control.Color.AMARILLO;
  private Control.Color VERDE = Control.Color.VERDE;
  private int [] trenes; 	// Declaramos un array de trenes para controlar el paso por las balizas
  
  
  
  private Control.Color [] coloresBaliza; // Declaramos array del enumerado control.Color para los colores de los semaforos
  
  
  //private int [] [] matrizSemaforo = new int [4] [4]; // Declaramos matriz para gestionar leerCambioSemaforo con las filas para el numero de semaforo
  // y las columnas para el color del semaforo
  private LinkedList<Semaforo> lista = new LinkedList <Semaforo>();
  
  public EnclavamientoMonitor() {
	  // Inicializamos todas las condiciones
	  for ( int i = 0; i < 10 ; i++) {
		  this.condiciones[i] = mutex.newCond();  
	  }
	  this.presencia = false; // Inicializamos la presencia--> false
	  this.trenes = new int [] {0,0,0,0}; // Declaro el array de trenes inicializando a 0 el numero de trenes despues de cada baliza
	  // Inicializamos a VERDE todos los colores de los semaforos
	  //this.coloresBaliza = new color[] {color.VERDE,color.VERDE,color.VERDE,color.VERDE};
	  // Inicializamos a verde todos los semaforos 
	  //this.matrizSemaforo = new int [] [] {{0,1,2,3},{VERDE.ordinal(), VERDE.ordinal(),VERDE.ordinal(),VERDE.ordinal()}};
	  this.coloresBaliza= new Control.Color [] {VERDE, VERDE, VERDE, VERDE};
	  
  }
  
  // Metodo auxiliar con los colores correctos
  private void coloresCorrectos () {
	// Implementacion colores correctos
	   if( trenes[1]>0 ) {
		   //this.matrizSemaforo[1] [VERDE.ordinal()] = matrizSemaforo [1] [ROJO.ordinal()];
		  this.coloresBaliza[1] = ROJO; 
	   } else if( trenes[1] == 0 && ( trenes[2]>0 || presencia == true)) {
		   //this.matrizSemaforo[1][VERDE.ordinal()] = matrizSemaforo [1] [AMARILLO.ordinal()];
		   this.coloresBaliza[1] = AMARILLO; 	   
	   } else if( trenes[1] == 0 && trenes[2] == 0 && presencia == false) {
		   //this.matrizSemaforo[1][VERDE.ordinal()] = matrizSemaforo [1] [VERDE.ordinal()];
		   this.coloresBaliza[1] =VERDE; 
	   } else if( trenes[2]>0 || presencia == true) {
		   //coloresBaliza[2] = color.ROJO;
		   //this.matrizSemaforo[2][VERDE.ordinal()] = matrizSemaforo [2] [ROJO.ordinal()];
		   this.coloresBaliza[2] = ROJO; 
	   } else if( trenes[2] == 0 && presencia == false) {
		   //this.matrizSemaforo[2][VERDE.ordinal()] = matrizSemaforo [2] [VERDE.ordinal()];
		   this.coloresBaliza[2] = VERDE; 
	   } else {
		   //this.matrizSemaforo[3][VERDE.ordinal()] = matrizSemaforo [3] [VERDE.ordinal()];
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
	  boolean esperado = false;
	  /* Si actual == true --> No se cumple la CPRE --> Se queda en espera el hilo, no es el resultado esperado
	   * por tanto esperado es falso y salta la excepcion
	   */
	  if(actual == true) {
		  condiciones[0].await();
		  esperado = false;
		  throw new PreconditionFailedException();
	  }
	  
	  /* Si actual == false --> Se cumple la CPRE --> Senaliza el hilo y esperado es verdadero
	   */
	  if ( actual == false && condiciones[0].waiting() > 0 ) { 
		  condiciones[0].signal();
		  esperado = true;
		  mutex.leave();
	  }
	  return esperado;
  }

  @Override
  public boolean leerCambioFreno(boolean actual) {
    mutex.enter();
    // Declaramos variable booleana que nos devuelve el resultado esperado
    boolean esperado = false;
    /* Si actual == true --> No se cumple la CPRE --> Se queda en espera el hilo, no es el resultado esperado
	   * por tanto esperado es falso y salta la excepcion
	   */
    if ( actual == false ) { 
    	condiciones[1].await();
    	esperado = false;
    	throw new PreconditionFailedException();
    }
    /* Si actual == false --> Se cumple la CPRE --> Senaliza el hilo y esperado es verdadero
	   */
    if ( actual == true && condiciones[1].waiting() > 0) {
    	condiciones[1].signal();
    	esperado = true;
    	mutex.leave(); 
  }
   return esperado;
  }

  @Override
  public Control.Color leerCambioSemaforo(int i, Control.Color actual) {
	    mutex.enter();
	    Control.Color esperado = VERDE;
	    /* Caso 1: Semaforo 1, color : Verde
	     */
	    // SI NO SE CUMPLE LA CPRE --> SE CREA NUEVA CONDITION Y SE ALMACENA EN COLA
	    if ( i == 0 || actual.equals(this.coloresBaliza[i])) {
	    	// Se crea nueva condicion
	    	Semaforo pet1 = new Semaforo (i,actual);
	    	// Se almacena en la lista
	    	lista.add(pet1);
	    	// Se pone en espera en la lista
	    	pet1.condicion1Verde.await();
	    	
	    // SI SE CUMPLE LA CPRE -->	
	    } else if ( i != 0 && matrizSemaforo[i][actual.ordinal()] != matrizSemaforo[1][VERDE.ordinal()]) {
	    	//POST
	    	esperado = VERDE;
	    //DESBLOQUEOS
	    	if( !lista.isEmpty() ) {
	    		lista.getLast().condicion1Verde.signal();
	    	}
	    }
	    /* Caso 2: Semaforo 1, color : Amarillo
	     */
	    // SI NO SE CUMPLE LA CPRE --> SE CREA NUEVA CONDITION Y SE ALMACENA EN COLA
	    if ( i == 0 || matrizSemaforo[i][actual.ordinal()] == matrizSemaforo[1][AMARILLO.ordinal()]) {
	    	// Se crea nueva condicion
	    	Semaforo pet2 = new Semaforo (i,actual);
	    	// Se almacena en la lista
	    	lista.add(pet2);
	    	// Se pone en espera en la lista
	    	pet2.condicion1Amarillo.await();
	    	
	    // SI SE CUMPLE LA CPRE -->	
	    } else if ( i != 0 && matrizSemaforo[i][actual.ordinal()] != matrizSemaforo[1][AMARILLO.ordinal()]) {
	    	//POST
	    	esperado = AMARILLO;
	    //DESBLOQUEOS
	    	if( !lista.isEmpty() ) {
	    		lista.getLast().condicion1Amarillo.signal();
	    	}
	    }
	    /* Caso 3: Semaforo 1, color : Rojo
	     */
	    // SI NO SE CUMPLE LA CPRE --> SE CREA NUEVA CONDITION Y SE ALMACENA EN COLA
	    if ( i == 0 || matrizSemaforo[i][actual.ordinal()] == matrizSemaforo[1][ROJO.ordinal()]) {
	    	// Se crea nueva condicion
	    	Semaforo pet3 = new Semaforo (i,actual);
	    	// Se almacena en la lista
	    	lista.add(pet3);
	    	// Se pone en espera en la lista
	    	pet3.condicion1Rojo.await();
	    	
	    // SI SE CUMPLE LA CPRE -->	
	    } else if ( i != 0 && matrizSemaforo[i][actual.ordinal()] != matrizSemaforo[1][ROJO.ordinal()]) {
	    	//POST
	    	esperado = ROJO;
	    //DESBLOQUEOS
	    	if( !lista.isEmpty() ) {
	    		lista.getLast().condicion1Rojo.signal();
	    	}
	    	/* Caso 4: Semaforo 2, color : Verde
		     */
		    // SI NO SE CUMPLE LA CPRE --> SE CREA NUEVA CONDITION Y SE ALMACENA EN COLA
		    if ( i == 0 || matrizSemaforo[i][actual.ordinal()] == matrizSemaforo[2][VERDE.ordinal()]) {
		    	// Se crea nueva condicion
		    	Semaforo pet4 = new Semaforo (i,actual);
		    	// Se almacena en la lista
		    	lista.add(pet4);
		    	// Se pone en espera en la lista
		    	pet4.condicion2Verde.await();
		    	
		    // SI SE CUMPLE LA CPRE -->	
		    } else if ( i != 0 && matrizSemaforo[i][actual.ordinal()] != matrizSemaforo[2][VERDE.ordinal()]) {
		    	//POST
		    	esperado = VERDE;
		    //DESBLOQUEOS
		    	if( !lista.isEmpty() ) {
		    		lista.getLast().condicion2Verde.signal();
		    	}
		    }
		    /* Caso 5: Semaforo 2, color : Amarillo
		     */
		    // SI NO SE CUMPLE LA CPRE --> SE CREA NUEVA CONDITION Y SE ALMACENA EN COLA
		    if ( i == 0 || matrizSemaforo[i][actual.ordinal()] == matrizSemaforo[2][AMARILLO.ordinal()]) {
		    	// Se crea nueva condicion
		    	Semaforo pet5 = new Semaforo (i,actual);
		    	// Se almacena en la lista
		    	lista.add(pet5);
		    	// Se pone en espera en la lista
		    	pet5.condicion2Amarillo.await();
		    	
		    // SI SE CUMPLE LA CPRE -->	
		    } else if ( i != 0 && matrizSemaforo[i][actual.ordinal()] != matrizSemaforo[2][AMARILLO.ordinal()]) {
		    	//POST
		    	esperado = AMARILLO;
		    //DESBLOQUEOS
		    	if( !lista.isEmpty() ) {
		    		lista.getLast().condicion2Amarillo.signal();
		    	}
		    }
		    /* Caso 3: Semaforo 1, color : Rojo
		     */
		    // SI NO SE CUMPLE LA CPRE --> SE CREA NUEVA CONDITION Y SE ALMACENA EN COLA
		    if ( i == 0 || matrizSemaforo[i][actual.ordinal()] == matrizSemaforo[2][ROJO.ordinal()]) {
		    	// Se crea nueva condicion
		    	Semaforo pet6 = new Semaforo (i,actual);
		    	// Se almacena en la lista
		    	lista.add(pet6);
		    	// Se pone en espera en la lista
		    	pet6.condicion2Rojo.await();
		    	
		    // SI SE CUMPLE LA CPRE -->	
		    } else if ( i != 0 && matrizSemaforo[i][actual.ordinal()] != matrizSemaforo[2][ROJO.ordinal()]) {
		    	//POST
		    	esperado = ROJO;
		    //DESBLOQUEOS
		    	if( !lista.isEmpty() ) {
		    		lista.getLast().condicion2Rojo.signal();
		    	}
	    }
		    /* Caso 7: Semaforo 3, color : Verde
		     */
		    // SI NO SE CUMPLE LA CPRE --> SE CREA NUEVA CONDITION Y SE ALMACENA EN COLA
		    if ( i == 0 || matrizSemaforo[i][actual.ordinal()] == matrizSemaforo[3][VERDE.ordinal()]) {
		    	// Se crea nueva condicion
		    	Semaforo pet7 = new Semaforo (i,actual);
		    	// Se almacena en la lista
		    	lista.add(pet7);
		    	// Se pone en espera en la lista
		    	pet7.condicion3Verde.await();
		    	
		    // SI SE CUMPLE LA CPRE -->	
		    } else if ( i != 0 && matrizSemaforo[i][actual.ordinal()] != matrizSemaforo[3][VERDE.ordinal()]) {
		    	//POST
		    	esperado = VERDE;
		    //DESBLOQUEOS
		    	if( !lista.isEmpty() ) {
		    		lista.getLast().condicion3Verde.signal();
		    	}
		    }
		    /* Caso 8: Semaforo 3, color : Amarillo
		     */
		    // SI NO SE CUMPLE LA CPRE --> SE CREA NUEVA CONDITION Y SE ALMACENA EN COLA
		    if ( i == 0 || matrizSemaforo[i][actual.ordinal()] == matrizSemaforo[3][AMARILLO.ordinal()]) {
		    	// Se crea nueva condicion
		    	Semaforo pet8 = new Semaforo (i,actual);
		    	// Se almacena en la lista
		    	lista.add(pet8);
		    	// Se pone en espera en la lista
		    	pet8.condicion3Amarillo.await();
		    	
		    // SI SE CUMPLE LA CPRE -->	
		    } else if ( i != 0 && matrizSemaforo[i][actual.ordinal()] != matrizSemaforo[3][AMARILLO.ordinal()]) {
		    	//POST
		    	esperado = AMARILLO;
		    //DESBLOQUEOS
		    	if( !lista.isEmpty() ) {
		    		lista.getLast().condicion3Amarillo.signal();
		    	}
		    }
		    /* Caso 9: Semaforo 1, color : Rojo
		     */
		    // SI NO SE CUMPLE LA CPRE --> SE CREA NUEVA CONDITION Y SE ALMACENA EN COLA
		    if ( i == 0 || matrizSemaforo[i][actual.ordinal()] == matrizSemaforo[3][ROJO.ordinal()]) {
		    	// Se crea nueva condicion
		    	Semaforo pet9 = new Semaforo (i,actual);
		    	// Se almacena en la lista
		    	lista.add(pet9);
		    	// Se pone en espera en la lista
		    	pet9.condicion3Rojo.await();
		    	
		    // SI SE CUMPLE LA CPRE -->	
		    } else if ( i != 0 && matrizSemaforo[i][actual.ordinal()] != matrizSemaforo[3][ROJO.ordinal()]) {
		    	//POST
		    	esperado = ROJO;
		    //DESBLOQUEOS
		    	if( !lista.isEmpty() ) {
		    		lista.getLast().condicion3Rojo.signal();
		    	}
	   
  }
}
	    mutex.leave();
	    return esperado;
  }

  @Override
  public void avisarPasoPorBaliza(int i) {
	    mutex.enter();
	    // Si i == 0 --> EXCEPCION
	    if ( i == 0 ) {
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
  private static class Semaforo extends EnclavamientoMonitor {
	  // Declaramos parametros que necesitamos almacenar
	  
	  private int i;
	  private Control.Color actual;
	  private Monitor.Cond condicion1Verde;
	  private Monitor.Cond condicion1Amarillo;
	  private Monitor.Cond condicion1Rojo;
	  private Monitor.Cond condicion2Verde;
	  private Monitor.Cond condicion2Amarillo;
	  private Monitor.Cond condicion2Rojo;
	  private Monitor.Cond condicion3Verde;
	  private Monitor.Cond condicion3Amarillo;
	  private Monitor.Cond condicion3Rojo;
	  private Semaforo (int i, Control.Color col) {
		  this.i = i;
		  this.actual = col;
		  this.condicion1Verde = mutex.newCond();
		  this.condicion1Amarillo = mutex.newCond();
		  this.condicion1Rojo = mutex.newCond();
		  this.condicion2Verde = mutex.newCond();
		  this.condicion2Amarillo = mutex.newCond();
		  this.condicion2Rojo = mutex.newCond();
		  this.condicion3Verde = mutex.newCond();
		  this.condicion3Amarillo = mutex.newCond();
		  this.condicion3Rojo = mutex.newCond();
	  }
	  
  }
 
 
}
