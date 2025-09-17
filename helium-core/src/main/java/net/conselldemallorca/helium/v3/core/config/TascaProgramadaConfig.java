package net.conselldemallorca.helium.v3.core.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.service.MonitorTasquesService;
import net.conselldemallorca.helium.v3.core.api.service.TascaProgramadaService;

/** Configura les tasques en segon pla. */
@Configuration
@EnableScheduling
public class TascaProgramadaConfig implements SchedulingConfigurer {

	public static final String comprovarExecucionsMassives = "comprovarExecucionsMassives";
	public static final String comprovarReindexacioAsincrona = "comprovarReindexacioAsincrona";
	public static final String comprovarAnotacionsPendents = "comprovarAnotacionsPendents";
	public static final String processarAnotacionsAutomatiques = "processarAnotacionsAutomatiques";
	public static final String actualitzarUnitatsIProcediments = "actualitzarUnitatsIProcediments";
	public static final String updatePeticionsAsincronesPinbal = "updatePeticionsAsincronesPinbal";
	public static final String comprovarEmailAnotacionsNoAgrupats = "comprovarEmailAnotacionsNoAgrupats";
	public static final String comprovarEmailAnotacionsAgrupats = "comprovarEmailAnotacionsAgrupats";
	public static final String migrarExpedientsArxiu = "migrarExpedientsArxiu";

	@Autowired
    private TaskScheduler taskScheduler;
	@Autowired
	private ApplicationContext applicationContext;

    @Autowired
    private MonitorTasquesService monitorTasquesService;
    
    /** Per evitar referències circulars. */
    private TascaProgramadaService tascaProgramadaService = null;
    private TascaProgramadaService getTascaProgramadaService() {
    	if (tascaProgramadaService == null ) {
    		tascaProgramadaService = applicationContext.getBean(TascaProgramadaService.class);
    	}
    	return tascaProgramadaService;
    }
    
    private ScheduledTaskRegistrar taskRegistrar;
    
    // Map amb les tasques enregistrades i les planificacions
    private final Map<String, Runnable> tasks = new HashMap<String, Runnable>();
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new HashMap<String, ScheduledFuture<?>>();

    
    public void reiniciarTasquesSegonPla() {
    	if (taskRegistrar != null) {
    		taskRegistrar.destroy();
    		taskRegistrar.afterPropertiesSet();
    	}
    }


	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskScheduler);
    	this.taskRegistrar = taskRegistrar;

		
		/** Tasca programada per comprovar les execucions massives periòdicament 
		 * segons el paràmetre definit a les properties globals: "app.massiu.periode.noves"
		 */
    	addTask(
    		comprovarExecucionsMassives,
                new Runnable() {
                    @Override
                    public void run() {
                    	monitorTasquesService.inici(comprovarExecucionsMassives);
                        try{ 
                    	getTascaProgramadaService().comprovarExecucionsMassives();
                        	monitorTasquesService.fi(comprovarExecucionsMassives);
                        } catch(Throwable th) {
                        	tractarErrorTascaSegonPla(th, comprovarExecucionsMassives);
                        }
                    }
						}
        );
		
		/** Comprovació cada 10 segons si hi ha expedients pendents de reindexació asíncrona segons la taula
		 * hel_expedient_reindexacio. Cada cop que s'executa va consultant si en queden de pendents fins la 
		 * propera execució.
		 */
    	addTask(
    			comprovarReindexacioAsincrona,
                new Runnable() {
                    @Override
                    public void run() {
                    	monitorTasquesService.inici(comprovarReindexacioAsincrona);
                        try{ 
                        	getTascaProgramadaService().comprovarReindexacioAsincrona();
                        	monitorTasquesService.fi(comprovarReindexacioAsincrona);
                        } catch(Throwable th) {
                        	tractarErrorTascaSegonPla(th, comprovarReindexacioAsincrona);
                        }
                    }
                    }
        );
		
		/** Tasca programada per comprovar les anotacions pendents de consultar periòdicament 
		 * a DISTRIBUCIO. Entre comrpovació i comprovació hi ha un període de 10 segons. 
		 * Les anotacions es consultaran fins a un màxim de n reintents definits per la 
		 * propietat <i>app.anotacions.pendents.comprovar.intents</i> amb un valor per defecte
		 * de 5 reintents.
		 */
    	addTask(
    			comprovarAnotacionsPendents,
	                new Runnable() {
	                    @Override
	                    public void run() {
	                    	monitorTasquesService.inici(comprovarAnotacionsPendents);
	                        try{ 
                        	getTascaProgramadaService().comprovarAnotacionsPendents();
	                        	monitorTasquesService.fi(comprovarAnotacionsPendents);
	                        } catch(Throwable th) {
	                        	tractarErrorTascaSegonPla(th, comprovarAnotacionsPendents);
	                        }
	                    }
	                }
	        );
		
		/** Tasca programada per comprovar les anotacions que estan en estat de pendents de processament
		 * automàtic. Entre comrpovació i comprovació hi ha un període de 10 segons.
		 */
    	addTask(
    			processarAnotacionsAutomatiques,
	                new Runnable() {
	                    @Override
	                    public void run() {
	                    	monitorTasquesService.inici(processarAnotacionsAutomatiques);
	                        try{ 
                        	getTascaProgramadaService().processarAnotacionsAutomatiques();
	                        	monitorTasquesService.fi(processarAnotacionsAutomatiques);
	                        } catch(Throwable th) {
	                        	tractarErrorTascaSegonPla(th, processarAnotacionsAutomatiques);
	                        }
	                    }
	                    }
	        );
		
		/** Mètode periòdic per sincronitzar les taules internes d'unitats organitzatives i procediments
		 * segons la propietat app.unitats.procediments.sync.
		 */
    	addTask(
    			actualitzarUnitatsIProcediments,
	                new Runnable() {
	                    @Override
	                    public void run() {
	                    	monitorTasquesService.inici(actualitzarUnitatsIProcediments);
	                        try{ 
                        	getTascaProgramadaService().actualitzarUnitatsIProcediments();
	                        	monitorTasquesService.fi(actualitzarUnitatsIProcediments);
	                        } catch(Throwable th) {
	                        	tractarErrorTascaSegonPla(th, actualitzarUnitatsIProcediments);
	                        }
	                    }
	                    }
	        );
		
		/** Tasca programada per actualitzar les peticions asíncrones de PINBAL periòdicament.
		 *  Entre comrpovació i comprovació hi ha un període de 60 segons. 
		 */
    	addTask(
    			updatePeticionsAsincronesPinbal,
	                new Runnable() {
	                    @Override
	                    public void run() {
	                    	monitorTasquesService.inici(updatePeticionsAsincronesPinbal);
	                        try{ 
                        	getTascaProgramadaService().updatePeticionsAsincronesPinbal();
	                        	monitorTasquesService.fi(updatePeticionsAsincronesPinbal);
	                        } catch(Throwable th) {
	                        	tractarErrorTascaSegonPla(th, updatePeticionsAsincronesPinbal);
	                        }
	                    }
	                }
	        );
		
		/** Tasca programada per comprovar si hi ha enviment de correus no agrupats no pendents.
		 */
    	addTask(
    			comprovarEmailAnotacionsNoAgrupats,
	                new Runnable() {
	                    @Override
	                    public void run() {
	                    	monitorTasquesService.inici(comprovarEmailAnotacionsNoAgrupats);
	                        try{ 
                        	getTascaProgramadaService().comprovarEmailAnotacionsNoAgrupats();
	                        	monitorTasquesService.fi(comprovarEmailAnotacionsNoAgrupats);
	                        } catch(Throwable th) {
	                        	tractarErrorTascaSegonPla(th, comprovarEmailAnotacionsNoAgrupats);
	                        }
	                    }
	                    }
	        );
		
		/** Mètode periòdic per enviar correus agrupats de noves anotacions
		 * segons la propietat app.anotacions.emails.agrupats.cron , Per defecte a les 20h.
		 */
    	addTask(
    			comprovarEmailAnotacionsAgrupats,
	                new Runnable() {
	                    @Override
	                    public void run() {
	                    	monitorTasquesService.inici(comprovarEmailAnotacionsAgrupats);
	                        try{ 
                        	getTascaProgramadaService().comprovarEmailAnotacionsAgrupats();
	                        	monitorTasquesService.fi(comprovarEmailAnotacionsAgrupats);
	                        } catch(Throwable th) {
	                        	tractarErrorTascaSegonPla(th, comprovarEmailAnotacionsAgrupats);
	                        }
	                    }
                }
    	);
				
		/** Tasca programada per migrar expedients a Arxiu.
		 */
    	addTask(
    			migrarExpedientsArxiu,
                new Runnable() {
                    @Override
                    public void run() {
    					monitorTasquesService.inici(migrarExpedientsArxiu);
    					try{ 
    						getTascaProgramadaService().migrarExpedientsDocumentsArxiu();
    						monitorTasquesService.fi(migrarExpedientsArxiu);
    					} catch(Throwable th) {
    						tractarErrorTascaSegonPla(th, migrarExpedientsArxiu);
    					}
                    }
                }
    	);
	}

	/** Enregistre l'error als logs i marca la tasca amb error. */
	private void tractarErrorTascaSegonPla(Throwable th, String codiTasca) {
		String errMsg = th.getClass() + ": " + th.getMessage() + " (" + new Date().getTime() + ")";
		logger.error("Error no controlat a l'execució de la tasca en segon pla amb codi \"" + codiTasca + "\": " + errMsg, th);
		monitorTasquesService.error(codiTasca, errMsg);
	}
	
	public void addTask(String taskId, Runnable task) {
        monitorTasquesService.addTasca(taskId);
        tasks.put(taskId, task);
        ScheduledFuture<?> scheduledTask = taskRegistrar.getScheduler().schedule(task, getTrigger(taskId));
        scheduledTasks.put(taskId, scheduledTask);
    }
	
	/** Mètode privat per obtenir la següent data segons el codi de la tasca. */
	private Trigger getTrigger(String taskCodi) {

		if (taskCodi.equals(comprovarExecucionsMassives)) {
            return new Trigger() {
                @Override
                public Date nextExecutionTime(TriggerContext triggerContext) {
                	Long value = null;
                	try {
                		String strProperty = GlobalProperties.getInstance().getProperty("app.massiu.periode.noves"); 
                		value = new Long(strProperty);
                	} catch (Exception e) {
						logger.warn("Error consultant la propietat per la propera execució de comprovar execucions massives: " + e.getMessage());
					}
                	if (value == null) {
						value =new Long(10000);
                	}
            		PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
                    trigger.setInitialDelay(value);
                    Date nextExecution = trigger.nextExecutionTime(triggerContext);
                    Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
    				monitorTasquesService.updateProperaExecucio(comprovarExecucionsMassives, longNextExecution);
                    return nextExecution;
                }
            };
        } else if (taskCodi.equals(comprovarReindexacioAsincrona)) {
            return new Trigger() {
                @Override
                public Date nextExecutionTime(TriggerContext triggerContext) {
                	Long value = new Long("10000");
                    PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
                    trigger.setInitialDelay(value);
                    Date nextExecution = trigger.nextExecutionTime(triggerContext);

                    Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
    				monitorTasquesService.updateProperaExecucio(comprovarReindexacioAsincrona, longNextExecution);
                    return nextExecution;
                }
            };
        } else if (taskCodi.equals(comprovarAnotacionsPendents)) {
            return new Trigger() {
                @Override
                public Date nextExecutionTime(TriggerContext triggerContext) {
                	Long value = new Long("10000");
                    PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
                    trigger.setInitialDelay(value);
                    Date nextExecution = trigger.nextExecutionTime(triggerContext);
                    Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
    				monitorTasquesService.updateProperaExecucio(comprovarAnotacionsPendents, longNextExecution);
                    return nextExecution;
                }
            };
        } else if (taskCodi.equals(processarAnotacionsAutomatiques)) {
            return new Trigger() {
                @Override
                public Date nextExecutionTime(TriggerContext triggerContext) {
                	Long value = new Long("10000");
                    PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
                    trigger.setInitialDelay(value);
                    Date nextExecution = trigger.nextExecutionTime(triggerContext);
                    Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
    				monitorTasquesService.updateProperaExecucio(processarAnotacionsAutomatiques, longNextExecution);
                    return nextExecution;
                }
            };
        } else if (taskCodi.equals(actualitzarUnitatsIProcediments)) {
            return new Trigger() {
	                    @Override
	                    public Date nextExecutionTime(TriggerContext triggerContext) {
	                    	String value = null;
							try {
						value = GlobalProperties.getInstance().getProperty("app.unitats.procediments.sync");
							} catch (Exception e) {
						logger.warn("Error consultant la propietat per la propera execució d'actualitzar unitats i procediments: " + e.getMessage());
							}
	                    	if (value == null) {
						value = "0 29 15 * * *";
	                    	}
	                    	CronTrigger trigger = new CronTrigger(value);
	                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
	                        Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
    				monitorTasquesService.updateProperaExecucio(actualitzarUnitatsIProcediments, longNextExecution);
                    return nextExecution;
                }
            };
        } else if (taskCodi.equals(updatePeticionsAsincronesPinbal)) {
            return new Trigger() {
                @Override
                public Date nextExecutionTime(TriggerContext triggerContext) {
                	Long value = new Long("60000");
                    PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
                    trigger.setInitialDelay(value);
                    Date nextExecution = trigger.nextExecutionTime(triggerContext);
                    Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
    				monitorTasquesService.updateProperaExecucio(updatePeticionsAsincronesPinbal, longNextExecution);
	                        return nextExecution;
	                    }
            };
        } else if (taskCodi.equals(comprovarEmailAnotacionsNoAgrupats)) {
            return new Trigger() {
                @Override
                public Date nextExecutionTime(TriggerContext triggerContext) {
                	Long value = new Long("60000");
                    PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
                    trigger.setInitialDelay(value);
                    Date nextExecution = trigger.nextExecutionTime(triggerContext);
                    Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
    				monitorTasquesService.updateProperaExecucio(comprovarEmailAnotacionsNoAgrupats, longNextExecution);
                    return nextExecution;
	                }
            };
        } else if (taskCodi.equals(comprovarEmailAnotacionsAgrupats)) {
            return new Trigger() {
				@Override
                public Date nextExecutionTime(TriggerContext triggerContext) {
                	String value = null;
					try{ 
						value = GlobalProperties.getInstance().getProperty("app.anotacions.emails.agrupats.cron");
					} catch (Exception e) {
						logger.warn("Error consultant la propietat per la propera execució d'enviament de correus agrupats: " + e.getMessage());
					}
                	if (value == null) {
						value = "0 00 20 * * *";
					}
                	CronTrigger trigger = new CronTrigger(value);
                    Date nextExecution = trigger.nextExecutionTime(triggerContext);
                    Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
    				monitorTasquesService.updateProperaExecucio(comprovarEmailAnotacionsAgrupats, longNextExecution);
                    return nextExecution;
				}
            };
        } else if (taskCodi.equals(migrarExpedientsArxiu)) {
            return new Trigger() {
				@Override
				public Date nextExecutionTime(TriggerContext triggerContext) {
					
					Long value = 60L;
					PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MINUTES);
					trigger.setInitialDelay(value);
					Date nextExecution = trigger.nextExecutionTime(triggerContext);
					Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
					monitorTasquesService.updateProperaExecucio(migrarExpedientsArxiu, longNextExecution);
					return nextExecution;
				}
            };
        }
        return null;
    }
	
	public void restartSchedulledTasks(String taskCodi) {

		if (taskRegistrar != null) {
			if (comprovarExecucionsMassives.equals(taskCodi) || "totes".equals(taskCodi)) {
				rescheduleTask(comprovarExecucionsMassives);
			}
			if (comprovarReindexacioAsincrona.equals(taskCodi) || "totes".equals(taskCodi)) {
				rescheduleTask(comprovarReindexacioAsincrona);
			}
			if (comprovarAnotacionsPendents.equals(taskCodi) || "totes".equals(taskCodi)) {
				rescheduleTask(comprovarAnotacionsPendents);
			}
			if (processarAnotacionsAutomatiques.equals(taskCodi) || "totes".equals(taskCodi)) {
				rescheduleTask(processarAnotacionsAutomatiques);
			}
			if (actualitzarUnitatsIProcediments.equals(taskCodi) || "totes".equals(taskCodi)) {
				rescheduleTask(actualitzarUnitatsIProcediments);
			}
			if (updatePeticionsAsincronesPinbal.equals(taskCodi) || "totes".equals(taskCodi)) {
				rescheduleTask(updatePeticionsAsincronesPinbal);
			}
			if (comprovarEmailAnotacionsNoAgrupats.equals(taskCodi) || "totes".equals(taskCodi)) {
				rescheduleTask(comprovarEmailAnotacionsNoAgrupats);
			}
			if (comprovarEmailAnotacionsAgrupats.equals(taskCodi) || "totes".equals(taskCodi)) {
				rescheduleTask(comprovarEmailAnotacionsAgrupats);
			}
			if (migrarExpedientsArxiu.equals(taskCodi) || "totes".equals(taskCodi)) {
				rescheduleTask(migrarExpedientsArxiu);
			}
		}
	}

	public void rescheduleTask(String taskId) {
		Trigger newTrigger = getTrigger(comprovarExecucionsMassives);
		ScheduledFuture<?> scheduledTask = scheduledTasks.get(taskId);
		if (scheduledTask != null) {
			// Cancelar la tarea existente
			scheduledTask.cancel(true);
			// Añadir la tarea con el nuevo trigger
			Runnable task = tasks.get(taskId);
			if (task != null) {
				ScheduledFuture<?> newScheduledTask = taskRegistrar.getScheduler().schedule(task, newTrigger);
				scheduledTasks.put(taskId, newScheduledTask);
			}
		}
	}
	private static final Logger logger = LoggerFactory.getLogger(TascaProgramadaConfig.class);

}
