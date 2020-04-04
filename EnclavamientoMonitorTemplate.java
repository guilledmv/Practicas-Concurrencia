//package Practica1;
import es.upm.babel.cclib.Monitor;
import es.upm.babel.cclib.Monitor.Cond;
import es.upm.aedlib.fifo.FIFO;
import es.upm.aedlib.fifo.FIFOList;

// Algoritmo usado para la resolucion de metodos en los que la CPRE depende de parametros de entrada --> Indexacion por Cliente

public class EnclavamientoMonitor implements Enclavamiento {


	//------------- VARIABLES GLOBALES -------------

	Monitor mutex = new Monitor();
	private boolean presencia; // DECLARAMOS EL ATRIBUTO PRESENCIA, PARA DETECTAR PRESENCIA EN EL CRUCE
	private int [] trenes; 	// DECLARAMOS ARRAY DE TRENES PARA CONTROLAR EL PASO POR LAS BALIZAS
	private Control.Color [] coloresBaliza; // DECLARAMOS UN ARRAY DE COLORES PARA LOS SEMAFOROS
	private FIFO<PeticionBarrera> listaBarrera = new FIFOList<PeticionBarrera>(); // DECLARAMOS TRES COLAS PARA ALMACENAR LAS PETICIONES DE BARRERA, FRENO Y SEMAFORO
	private FIFO<PeticionFreno> listaFreno = new FIFOList<PeticionFreno>();
	private FIFO<PeticionSemaforo> listaSemaforo = new FIFOList<PeticionSemaforo>();

	public EnclavamientoMonitor() {

		// INICIALIZAMOS LOS ATRIBUTOS A LOS VALORES DEL CTAD, ES DECIR :
		// PRESENCIA--> FALSE
		// NUMERO DE TRENES DESPUES DE CADA BALIZA A 0
		// Y COLORES DE LOS 3 SEMAFOROS EN VERDE

		this.presencia = false; 
		this.trenes = new int [] {0,0,0,0}; 
		this.coloresBaliza = new Control.Color [] {Control.Color.VERDE, Control.Color.VERDE,Control.Color.VERDE,Control.Color.VERDE};

	}

	// METODO AUXILIAR QUE IMPLEMENTA LOS COLORES CORRECTOS
	private void coloresCorrectos () {

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
		//-----CPRE = CIERTO-----> POR TANTO NO TENEMOS QUE BLOQUEAR NINGUN PROCESO

		//-----POST--------
		// TOMAMOS EL VALOR DEL BOOLEANO PARAMETRO DE ENTRADA A NUESTRA VARIABLE GLOBAL, LLAMAMOS A ColoresCorrectos() y desbloqueamos
		this.presencia = presencia;
		coloresCorrectos();
		desbloquear();
		mutex.leave();

	}

	public boolean leerCambioBarrera(boolean actual) {
		// USAMOS EL mutex PARA ASEGURAR LA EXCLUSION MUTUA
		mutex.enter();
		//DECLARAMOS VARIABLE BOOLEANA QUE DEVOLVEREMOS
		boolean esperado = (this.trenes[1] + this.trenes[2] == 0);
		// SI NO SE CUMPLE CPRE--> CREA CONDICION ASOCIADA A BARRERA, INSERTA EN LA COLA Y PONE EN ESPERA EL PROCESO
		if ( actual == (this.trenes[1] + this.trenes[2] == 0) ) {
			// CREAMOS CONDICION ASOCIADA A BARRERA
			Monitor.Cond levantarBarrera = mutex.newCond();
			// ENCOLAMOS
			listaBarrera.enqueue(new PeticionBarrera(actual,levantarBarrera));
			// PONEMOS EN ESPERA
			levantarBarrera.await();	
		}
		// SI SE CUMPLE LA CPRE--> DESBLOQUEAMOS Y DEVOLVEMOS EL RESULTADO ESPERADO
		desbloquear();	
		mutex.leave();
		return esperado;

	}

	@Override
	public boolean leerCambioFreno(boolean actual) {
		// USAMOS EL mutex PARA ASEGURAR LA EXCLUSION MUTUA
		mutex.enter();
		//DECLARAMOS VARIABLE BOOLEANA QUE DEVOLVEREMOS
		boolean esperado = (this.trenes[1] > 1 || this.trenes[2] > 1 || this.trenes[2] == 1 && this.presencia == true);
		// SI NO SE CUMPLE CPRE--> CREA CONDICION ASOCIADA A FRENO, INSERTA EN LA COLA Y PONE EN ESPERA EL PROCESO
		if ( actual == (this.trenes[1] > 1 || this.trenes[2] > 1 || this.trenes[2] == 1 && this.presencia == true )) {
			// CREAMOS CONDICION ASOCIADA A FRENO
			Monitor.Cond activarFreno = mutex.newCond();
			// ENCOLAMOS
			listaFreno.enqueue(new PeticionFreno(actual,activarFreno));
			// PONEMOS EN ESPERA
			activarFreno.await();
		} 
		// SI SE CUMPLE LA CPRE--> DESBLOQUEAMOS Y DEVOLVEMOS EL RESULTADO ESPERADO
		desbloquear();	
		mutex.leave();
		return esperado;
	}

	@Override
	public Control.Color leerCambioSemaforo(int i, Control.Color actual) {
		// SI NO SE CUMPLE LA PRE --> LANZAMOS EXCEPCION
		if ( i == 0) {
			throw new PreconditionFailedException();
		}
		// USAMOS EL mutex PARA ASEGURAR LA EXCLUSION MUTUA
		mutex.enter();
		// DECLARAMOS COLOR QUE DEVOLVEREMOS
		Control.Color encendido = coloresBaliza[i];
		// SI NO SE CUMPLE CPRE--> CREA CONDICION ASOCIADA A SEMAFORO, INSERTA EN LA COLA Y PONE EN ESPERA EL PROCESO
		if ( actual.equals(coloresBaliza[i]) ) {
			// CREAMOS CONDICION PARA LOS SEMAFOROS
			Monitor.Cond condSemaforo = mutex.newCond();
			// ENCOLAMOS
			listaSemaforo.enqueue(new PeticionSemaforo(i,actual,condSemaforo));
			// PONEMOS EN ESPERA EL PROCESO
			condSemaforo.await();
		} 
		// SI SE CUMPLE LA CPRE--> DESBLOQUEAMOS Y DEVOLVEMOS EL RESULTADO ESPERADO
		desbloquear();
		mutex.leave();
		return encendido;
	}

	@Override
	public void avisarPasoPorBaliza(int i) {
		// SI NO SE CUMPLE LA PRE --> LANZAMOS EXCEPCION
		if ( i == 0) {
			throw new PreconditionFailedException();
		}
		// USAMOS EL mutex PARA ASEGURAR LA EXCLUSION MUTUA
		mutex.enter();
		//--- CPRE ---- = CIERTO--> POR TANTO, NO EXISTE NINGUN BLOQUEO

		//-----POST----
		this.trenes[i-1] = trenes [i-1]-1;
		this.trenes[i] = trenes[i]+1;
		coloresCorrectos();
		desbloquear();
		mutex.leave();
	}
	// CLASE AUXILIAR PARA LAS PETICIONES DE BARRERA, LA CUAL POSEE EL ATRIBUTO BOOLEANO QUE RECIBE EL METODO Y LA CONDICION ASOCIADA
	private class PeticionBarrera {

		private boolean activo;
		private Monitor.Cond condition;

		private PeticionBarrera(boolean esperado, Monitor.Cond cond) {
			this.activo = esperado;
			this.condition = cond;
		}
	}
	// CLASE AUXILIAR PARA LAS PETICIONES DE FRENO, LA CUAL POSEE EL ATRIBUTO BOOLEANO QUE RECIBE EL METODO Y LA CONDICION ASOCIADA
	private class PeticionFreno {

		private boolean activo;
		private Monitor.Cond condition;

		private PeticionFreno(boolean esperado, Monitor.Cond cond) {
			this.activo = esperado;
			this.condition = cond;
		}
	}
	// CLASE AUXILIAR PARA LAS PETICIONES DE SEMAFORO, LA CUAL POSEE EL ATRIBUTO ENTERO Y EL COLOR QUE RECIBE EL METODO Y LA CONDICION ASOCIADA
	private class PeticionSemaforo {

		private int numeroSemaforo;
		private Control.Color color;
		private Monitor.Cond condition;

		private PeticionSemaforo(int id , Control.Color actual, Cond cond) {
			this.numeroSemaforo = id;
			this.color = actual;
			this.condition = cond;
		}
	}
	// METODO AUXILIAR DE DESBLOQUEO PARA LOS PROCESOS EN ESPERA
	private void desbloquear() {
		// DECLARAMOS VARIABLE BOOLEANA PARA SABER SI ESTA SENALIZADO
		boolean senalizado = false;
		//DECLARAMOS TAMANO DE LAS COLAS
		int sizeBarrera = listaBarrera.size();
		int sizeFreno = listaFreno.size();
		int sizeSemaforo = listaSemaforo.size();
		// RECORRIDO DE LA COLA DE LAS PETICIONES DE BARRERA
		for (int i = 0; i < sizeBarrera && !senalizado; i++) {

			PeticionBarrera petBar = listaBarrera.first(); // OBTENEMOS PRIMER ELEMENTO DE LA COLA
			listaBarrera.dequeue(); // DESENCOLAMOS PRIMER ELEMENTO

			// SI SE CUMPLE LA CPRE DE BARRERA Y EXISTEN PETICIONES EN COLA --> SENALIZA
			if( petBar.activo != (trenes[1] + trenes[2] == 0) && petBar.condition.waiting() > 0) {
				petBar.condition.signal();
				senalizado = true;
				// SI NO SE CUMPLE LA CPRE DE BARRERA--> VUELVE A ENCOLAR EL ELEMENTO 
			} else {
				listaBarrera.enqueue(petBar);
			}
		} // CIERRE BUCLE COLA BARRERA
		// RECORRIDO DE LA COLA DE LAS PETICIONES DE FRENO
		for ( int j = 0 ; j < sizeFreno && !senalizado; j++) { 

			PeticionFreno petFren = listaFreno.first(); // OBTENEMOS PRIMER ELEMENTO DE LA COLA
			listaFreno.dequeue(); // DESENCOLAMOS PRIMER ELEMENTO

			// SI SE CUMPLE LA CPRE DE FRENO Y EXISTEN PETICIONES EN COLA --> SENALIZA
			if ( petFren.activo != ((trenes[1] > 1) || (trenes[2] > 1) || (trenes[2] == 1) && (presencia == true)) && petFren.condition.waiting() > 0) {
				petFren.condition.signal();
				senalizado = true;
				// SI NO SE CUMPLE LA CPRE DE FRENO--> VUELVE A ENCOLAR EL ELEMENTO
			} else {
				listaFreno.enqueue(petFren);
			}
		}// CIERRE BUCLE COLA DE FRENO
		// RECORRIDO DE LA COLA DE LAS PETICIONES DE SEMAFORO
		for( int z = 0; z < sizeSemaforo && !senalizado; z++) {

			PeticionSemaforo petSem = listaSemaforo.first(); // OBTENEMOS PRIMER ELEMENTO DE LA COLA
			listaSemaforo.dequeue(); // DESENCOLAMOS PRIMER ELEMENTO

			// SI SE CUMPLE LA CPRE PARA SEMAFORO 1 Y EXISTEN PETICIONES EN COLA --> SENALIZA
			if ( petSem.numeroSemaforo == 1 && (petSem.color != (coloresBaliza[1])) && petSem.condition.waiting() > 0 ) {
				petSem.condition.signal();
				senalizado = true;
			} else {
				// SI NO SE CUMPLE LA CRE DE SEMAFORO 1--> VUELVE A ENCOLAR EL ELEMENTO
				listaSemaforo.enqueue(petSem);
			}
			// SI SE CUMPLE LA CPRE PARA SEMAFORO 2 Y EXISTEN PETICIONES EN COLA --> SENALIZA
			if ( (petSem.numeroSemaforo == 2) && (petSem.color != (coloresBaliza[2])) && petSem.condition.waiting() > 0 ) {
				petSem.condition.signal();
				senalizado = true;
			} else {
				// SI NO SE CUMPLE LA CRE DE SEMAFORO 2--> VUELVE A ENCOLAR EL ELEMENTO
				listaSemaforo.enqueue(petSem);
			}

		} // Cierre BUCLE COLA SEMAFORO
	} // CIERRE METODO AUXILIAR desbloquear
}
