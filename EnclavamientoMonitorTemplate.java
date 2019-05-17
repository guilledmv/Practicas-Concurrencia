package Practica1;
import java.util.Arrays;
import es.upm.babel.cclib.Monitor;

//Grupo: Guillermo De Miguel Villanueva (160262) , Marcos Arevalo Salas (160266)

public class EnclavamientoMonitor implements Enclavamiento {
	

	//------------- VARIABLES GLOBALES -------------

  Monitor mutex = new Monitor();
  private Monitor.Cond [] condiciones = new Monitor.Cond [11]; // Declaro array de condiciones
  private boolean presencia;// Declaramos un tipo enumerado para los colores del semaforo
  private enum color {ROJO,AMARILLO,VERDE};
  private color ROJO = color.ROJO;
  private color AMARILLO = color.AMARILLO;
  private color VERDE = color.VERDE;
  private int [] trenes; 	// Declaramos un array de trenes para controlar el paso por las balizas
  //private color [] coloresBaliza; // Declaramos array del enumerado Color para los colores de los semaforos
  private int [] [] matrizSemaforo = new int [3] [3]; // Declaramos matriz para gestionar leerCambioSemaforo con las filas para el nº de semaforo
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
    if ( actual == false ) { //&& actual != ((this.trenes[1] > 1 || this.trenes[2] > 1 || this.trenes[2] == 1 && this.presencia == true ))) {
    	condiciones[1].await();
    	esperado = false;
    	throw new PreconditionFailedException();
    }
    /* Si actual == false --> Se cumple la CPRE --> Senaliza el hilo y esperado es verdadero
	   */
    if ( actual == true && condiciones[1].waiting() > 0) {//&& actual != ((this.trenes[1] > 1 || this.trenes[2] > 1 || this.trenes[2] == 1 && this.presencia == true ))) {
    	condiciones[1].signal();
    	esperado = true;
    	mutex.leave(); 
  }
   return esperado;
  }

  @Override
  public Control.Color leerCambioSemaforo(int i, Control.Color actual) {
	    mutex.enter();
	    Control.Color esperado = Control.Color.VERDE;
	    /* 1º CASO: semaforo: 1 y color actual: VERDE
	     * 
	     */
	    
	    
	   
	    mutex.leave();
	    return esperado;
	  }

  @Override
  public void avisarPasoPorBaliza(int i) {
	    mutex.enter();

	    mutex.leave();
	   
	  }
 
 
}
