package net.conselldemallorca.helium.v3.core.api.dto;

/** Semàfor per evitar que mentre s'està creant una anotació de registre la tasca en 2n pla 
 * que les guarda a l'Arxiu les processi. És un comportament que només s'ha donat a l'entorn
 * de producció i que s'ha d'evitar. Conté un objecte estàtic amb el qual es podrà fer
 * <code>synchronize</code> en el WS d'alta de registres i en la tasca de 2n pla de processar
 * anotacions pendents de registre. 
 */
public class SemaphoreDto {

	public static Object semaphore = new Object();
	
	public static Object getSemaphore() {
		return semaphore;
	}
}
