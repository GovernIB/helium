package net.conselldemallorca.helium.jbpm3.spring;

import net.conselldemallorca.helium.core.model.dto.OperacioMassivaDto;
import net.conselldemallorca.helium.core.model.service.ExecucioMassivaService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SpringMassiuExecutorThread extends Thread {

	private static Log log = LogFactory.getLog(SpringMassiuExecutorThread.class);

	private ExecucioMassivaService execucioMassivaService;
	final SpringMassiuExecutor springMassiuExecutor;

	final int idleInterval;
	final int maxIdleInterval;
	final int waitTime;
	final int timeBetweenExecutions;

	int currentIdleInterval;
	int currentWaitInterval;
	volatile boolean isActive = true;
	volatile Long ultimaExecucioMassiva;

	public SpringMassiuExecutorThread(
			String name,
			SpringMassiuExecutor springMassiuExecutor,
			int idleInterval, 
			int maxIdleInterval, 
			int waitTime,
			int timeBetweenExecutions,
			ExecucioMassivaService execucioMassivaService) {
		super(name);
		this.springMassiuExecutor = springMassiuExecutor;
		this.idleInterval = idleInterval;
		this.maxIdleInterval = maxIdleInterval;
		this.waitTime = waitTime;
		this.execucioMassivaService = execucioMassivaService;
		this.timeBetweenExecutions = timeBetweenExecutions;
	}

	public void run() {
		currentIdleInterval = idleInterval;
		while (isActive) {
			try {
//				log.info("El thread de execucions massives '" + getName() + "' consulta les accions pendents...");
				OperacioMassivaDto operacioMassiva = execucioMassivaService.getExecucionsMassivesActiva(ultimaExecucioMassiva);
				if (operacioMassiva != null) {
//					log.info("El thread de execucions massives '" + getName() + "' ha obtingut la acció " + operacioMassiva.getId());
					try {
//						log.info("El thread de execucions massives '" + getName() + "' executa la acció " + operacioMassiva.getId());
						execucioMassivaService.executarExecucioMassiva(operacioMassiva);
						log.info("El thread de execucions massives '" + getName() + "' ha acabat d'executar la acció " + operacioMassiva.getId());
					}
					catch (Exception e) {
						// si s'ha produit una excepció, deseram l'error a la operació
						log.info("El thread de execucions massives '" + getName() + "' ha detectat un error en la execució de la acció " + operacioMassiva.getId() + ". Anem a generar l'error.");
						execucioMassivaService.generaInformeError(operacioMassiva, e);
//						log.info("El thread de execucions massives '" + getName() + "' ha generat l'error per a la acció " + operacioMassiva.getId());
					}
					ultimaExecucioMassiva = operacioMassiva.getExecucioMassivaId();
					actualitzaUltimaOperacio(operacioMassiva);
//					log.info("El thread de execucions massives '" + getName() + "' ha actualitzat la acció " + operacioMassiva.getId());
				} else {
//					log.info("El thread de execucions massives '" + getName() + "' no ha obtingut accions.");
					currentWaitInterval = waitTime;
				}
				if (isActive && currentWaitInterval > 0) {
					synchronized (springMassiuExecutor) {
//						log.info("El thread de execucions massives '" + getName() + "' esperarà " + currentWaitInterval + "ms abans de obtenir la seguent acció.");
						springMassiuExecutor.wait(currentWaitInterval);
					}
				}
				// Si no hi ha hagut excepció, resetejam els intervals
				currentIdleInterval = idleInterval;
				currentWaitInterval = timeBetweenExecutions;
			} catch (InterruptedException e) {
				log.info("El thread " + (isActive ? "actiu" : "inactiu") + " de execucions massives '" + getName() + "' ha estat interromput");
			} catch (Exception e) {
				log.error("Excepcio en el thread executor d'accions massives. Esperam " + currentIdleInterval + " milisegons", e);
				try {
					synchronized (springMassiuExecutor) {
						springMassiuExecutor.wait(currentIdleInterval);
					}
				} catch (InterruptedException e2) {
					log.debug("L'espera despres de l'excepcio ha estat interrompuda", e2);
				}
				// Després d'una excepcion, l'interval d'espera actual es
				// doblarà per a prevenir la generació de continues excepcions
				currentIdleInterval <<= 1;
				if (currentIdleInterval > maxIdleInterval || currentIdleInterval < 0) {
					currentIdleInterval = maxIdleInterval;
				}
			}
		}
		log.info(getName() + " leaves cyberspace");
	}
	
	private void actualitzaUltimaOperacio(OperacioMassivaDto dto) {
		if (dto.getUltimaOperacio()) {
			execucioMassivaService.actualitzaUltimaOperacio(dto);
		}
	}

	public void setActive(boolean isActive) {
		if (isActive == false)
			deactivate();
	}

	public void deactivate() {
		if (isActive) {
			isActive = false;
			interrupt();
		}
	}
}
