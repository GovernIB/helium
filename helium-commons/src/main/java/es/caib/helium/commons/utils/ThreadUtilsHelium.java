package es.caib.helium.commons.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Classe amb mètodes comuns pels fils d'execucio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ThreadUtilsHelium {

	/** Mètode per establir un timeout en segons en el thread actual per a que es llenci
	 * una excepció InterruptedException en el cas que se sobrepassin el temps especificat.
	 * 
	 * @param timeout Valor en segons per establir el timout.
	 * @return Retorna el ScheduledExecutor per poder finalitzar el comptador i que ja no es llenci.
	 */
	public static ScheduledExecutorService setTimeout(int timeout) {
		// Programa l'interrupció del thrad actual
		final Thread currentThread = Thread.currentThread();
		ScheduledExecutorService scheduler =
		        Executors.newSingleThreadScheduledExecutor();
		
		scheduler.schedule(new Runnable() {
		    @Override
		    public void run() {
		        currentThread.interrupt();
		    }
		}, timeout, TimeUnit.SECONDS);
		return scheduler;
	}

}
