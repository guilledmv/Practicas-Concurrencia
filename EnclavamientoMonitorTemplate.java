package Practica1;
import java.util.Arrays;
import es.upm.babel.cclib.Monitor;

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
  //private color [] coloresBaliza; // Declaramos array del enumerado Color para los colores de los semaforos
  private int [] [] matrizSemaforo = new int [3] [3]; // Declaramos matriz para gestionar leerCambioSemaforo con las filas para el numero de semaforo
  // y las columnas para el color del semaforo
  
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
	  this.matrizSemaforo = new int [] [] {{1,2,3},{VERDE.ordinal(),VERDE.ordinal(),VERDE.ordinal()}} ;
  }
  
  // Metodo auxiliar con los colores correctos
  private void coloresCorrectos () {
	// Implementacion colores correctos
	   if( trenes[1]>0 ) {
		   this.matrizSemaforo[1] [VERDE.ordinal()] = matrizSemaforo [1] [ROJO.ordinal()]; 
	   } else if( trenes[1] == 0 && ( trenes[2]>0 || presencia == true)) {
		   this.matrizSemaforo[1][VERDE.ordinal()] = matrizSemaforo [1] [AMARILLO.ordinal()];
	   } else if( trenes[1] == 0 && trenes[2] == 0 && presencia == false) {
		   this.matrizSemaforo[1][VERDE.ordinal()] = matrizSemaforo [1] [VERDE.ordinal()];
	   } else if( trenes[2]>0 || presencia == true) {
		   //coloresBaliza[2] = color.ROJO;
		   this.matrizSemaforo[2][VERDE.ordinal()] = matrizSemaforo [2] [ROJO.ordinal()];
	   } else if( trenes[2] == 0 && presencia == false) {
		   this.matrizSemaforo[2][VERDE.ordinal()] = matrizSemaforo [2] [VERDE.ordinal()];
	   } else {
		   this.matrizSemaforo[3][VERDE.ordinal()] = matrizSemaforo [3] [VERDE.ordinal()];
	   }
  }
  
  public void avisarPresencia(boolean presencia) {
	  // Modificar este metodo con presencia = false y || presencia = true
    mutex.enter();
    
   //------- PRE == CIERTO -------
    
  //------- POST -------
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
	    // Declaro variable color que devolvemremos 
	    Control.Color esperado = VERDE;
	    // si no se cumple la PRE --> EXCEPCION
	    if ( i == 0) {
	    	throw new PreconditionFailedException();
	    }  else {
	    /* CASO 1: semaforo: 1 y color actual: VERDE
	     * se cumple i != 1
	     */
	    // Si coinciden los colores del semaforo--> Se bloquea
	    if ( i == 1 && actual.equals(VERDE)) {
	    	condiciones[2].await();
	    // Si no coinciden los colores del semaforo--> Senaliza
	    } else if (i == 1 && !actual.equals(VERDE) && condiciones[2].waiting() > 0) {
	    	condiciones[2].signal();
	    	esperado = VERDE;
	    }
	    /* CASO 2: semaforo: 1 y color actual: AMARILLO
	     * se cumple i != 1
	     */
	    // Si coinciden los colores del semaforo--> Se bloquea
	    if ( i == 1 && actual.equals(AMARILLO)) {
	    	condiciones[3].await();
	    // Si no coinciden los colores del semaforo--> Senaliza
	    } else if (i == 1 && !actual.equals(AMARILLO) && condiciones[3].waiting() > 0) {
	    	condiciones[3].signal();
	    	esperado = AMARILLO;
	    }
	    /* CASO 3: semaforo: 1 y color actual: ROJO
	     * se cumple i != 1
	     */
	 // Si coinciden los colores del semaforo--> Se bloquea
	    if ( i == 1 && actual.equals(ROJO)) {
	    	condiciones[4].await();
	    // Si no coinciden los colores del semaforo--> Senaliza
	    } else if (i == 1 && !actual.equals(ROJO) && condiciones[4].waiting() > 0) {
	    	condiciones[4].signal();
	    	esperado = ROJO;
	    }
	    /* CASO 4: semaforo: 2 y color actual: VERDE
	     * se cumple i != 1
	     */
	    // Si coinciden los colores del semaforo--> Se bloquea
	    if ( i == 2 && actual.equals(VERDE)) {
	    	condiciones[5].await();
	    // Si no coinciden los colores del semaforo--> Senaliza
	    } else if (i == 2 && !actual.equals(VERDE) && condiciones[5].waiting() > 0) {
	    	condiciones[5].signal();
	    	esperado = VERDE;
	    }
	    /* CASO 5: semaforo: 2 y color actual: AMARILLO
	     * se cumple i != 1
	     */
	    // Si coinciden los colores del semaforo--> Se bloquea
	    if ( i == 2 && actual.equals(AMARILLO)) {
	    	condiciones[6].await();
	    // Si no coinciden los colores del semaforo--> Senaliza
	    } else if (i == 2 && !actual.equals(AMARILLO) && condiciones[6].waiting() > 0) {
	    	condiciones[6].signal();
	    	esperado = AMARILLO;
	    }
	    /* CASO 6: semaforo: 2 y color actual: ROJO
	     * se cumple i != 1
	     */
	    // Si coinciden los colores del semaforo--> Se bloquea
	    if ( i == 2 && actual.equals(ROJO)) {
	    	condiciones[7].await();
	    // Si no coinciden los colores del semaforo--> Senaliza
	    } else if (i == 2 && !actual.equals(ROJO) && condiciones[7].waiting() > 0) {
	    	condiciones[7].signal();
	    	esperado = ROJO;
	    }
	    /* CASO 7: semaforo: 3 y color actual: VERDE
	     * se cumple i != 1
	     */
	    // Si coinciden los colores del semaforo--> Se bloquea
	    if ( i == 3 && actual.equals(VERDE)) {
	    	condiciones[8].await();
	    // Si no coinciden los colores del semaforo--> Senaliza
	    } else if (i == 3 && !actual.equals(VERDE) && condiciones[8].waiting() > 0) {
	    	condiciones[8].signal();
	    	esperado = VERDE;
	    }
	    /* CASO 8: semaforo: 1 y color actual: AMARILLO
	     * se cumple i != 1
	     */
	    // Si coinciden los colores del semaforo--> Se bloquea
	    if ( i == 3 && actual.equals(AMARILLO)) {
	    	condiciones[9].await();
	    // Si no coinciden los colores del semaforo--> Senaliza
	    } else if (i == 3 && !actual.equals(AMARILLO) && condiciones[9].waiting() > 0) {
	    	condiciones[9].signal();
	    	esperado = AMARILLO;
	    }
	    /* CASO 9: semaforo: 3 y color actual: ROJO
	     * se cumple i != 1
	     */
	 // Si coinciden los colores del semaforo--> Se bloquea
	    if ( i == 3 && actual.equals(ROJO)) {
	    	condiciones[10].await();
	    // Si no coinciden los colores del semaforo--> Senaliza
	    } else if (i == 3 && !actual.equals(ROJO) && condiciones[10].waiting() > 0) {
	    	condiciones[10].signal();
	    	esperado = ROJO;
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
	    }
	    // 

	    mutex.leave();
	   
	  }
 
 
}
