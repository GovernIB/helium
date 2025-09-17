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
    
    /** Per evitar referÃ¨ncies circulars. */
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

		
		final String comprovarExecucionsMassives = "comprovarExecucionsMassives";
		/** Tasca programada per comprovar les execucions massives periòdicament 
		 * segons el paràmetre definit a les properties globals: "app.massiu.periode.noves"
		 */
		monitorTasquesService.addTasca(comprovarExecucionsMassives);
		taskRegistrar.addTriggerTask(
                new Runnable() {
                    @Override
                    public void run() {
                    	monitorTasquesService.inici(comprovarExecucionsMassives);
                        try{ 
                        	tascaProgramadaService.comprovarExecucionsMassives();
                        	monitorTasquesService.fi(comprovarExecucionsMassives);
                        } catch(Throwable th) {
                        	tractarErrorTascaSegonPla(th, comprovarExecucionsMassives);
                        }
                    }
                },
                new Trigger() {
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
                }
        );
		
		final String comprovarReindexacioAsincrona = "comprovarReindexacioAsincrona";
		/** Comprovació cada 10 segons si hi ha expedients pendents de reindexació asíncrona segons la taula
		 * hel_expedient_reindexacio. Cada cop que s'executa va consultant si en queden de pendents fins la 
		 * propera execució.
		 */
		monitorTasquesService.addTasca(comprovarReindexacioAsincrona);
		taskRegistrar.addTriggerTask(
                new Runnable() {
                    @Override
                    public void run() {
                    	monitorTasquesService.inici(comprovarReindexacioAsincrona);
                        try{ 
                        	tascaProgramadaService.comprovarReindexacioAsincrona();
                        	monitorTasquesService.fi(comprovarReindexacioAsincrona);
                        } catch(Throwable th) {
                        	tractarErrorTascaSegonPla(th, comprovarReindexacioAsincrona);
                        }
                    }
                },
                new Trigger() {
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
                }
        );
		
		
		
		final String comprovarAnotacionsPendents = "comprovarAnotacionsPendents";
		/** Tasca programada per comprovar les anotacions pendents de consultar periòdicament 
		 * a DISTRIBUCIO. Entre comrpovació i comprovació hi ha un període de 10 segons. 
		 * Les anotacions es consultaran fins a un màxim de n reintents definits per la 
		 * propietat <i>app.anotacions.pendents.comprovar.intents</i> amb un valor per defecte
		 * de 5 reintents.
		 */
		monitorTasquesService.addTasca(comprovarAnotacionsPendents);
		taskRegistrar.addTriggerTask(
	                new Runnable() {
	                    @Override
	                    public void run() {
	                    	monitorTasquesService.inici(comprovarAnotacionsPendents);
	                        try{ 
	                        	tascaProgramadaService.comprovarAnotacionsPendents();
	                        	monitorTasquesService.fi(comprovarAnotacionsPendents);
	                        } catch(Throwable th) {
	                        	tractarErrorTascaSegonPla(th, comprovarAnotacionsPendents);
	                        }
	                    }
	                },
	                new Trigger() {
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
	                }
	        );
		
		final String processarAnotacionsAutomatiques = "processarAnotacionsAutomatiques";
		/** Tasca programada per comprovar les anotacions que estan en estat de pendents de processament
		 * automàtic. Entre comrpovació i comprovació hi ha un període de 10 segons.
		 */
		monitorTasquesService.addTasca(processarAnotacionsAutomatiques);
		taskRegistrar.addTriggerTask(
	                new Runnable() {
	                    @Override
	                    public void run() {
	                    	monitorTasquesService.inici(processarAnotacionsAutomatiques);
	                        try{ 
	                        	tascaProgramadaService.processarAnotacionsAutomatiques();
	                        	monitorTasquesService.fi(processarAnotacionsAutomatiques);
	                        } catch(Throwable th) {
	                        	tractarErrorTascaSegonPla(th, processarAnotacionsAutomatiques);
	                        }
	                    }
	                },
	                new Trigger() {
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
	                }
	        );
		
		
		final String actualitzarUnitatsIProcediments = "actualitzarUnitatsIProcediments";
		/** Mètode periòdic per sincronitzar les taules internes d'unitats organitzatives i procediments
		 * segons la propietat app.unitats.procediments.sync.
		 */
		monitorTasquesService.addTasca(actualitzarUnitatsIProcediments);
		taskRegistrar.addTriggerTask(
	                new Runnable() {
	                    @Override
	                    public void run() {
	                    	monitorTasquesService.inici(actualitzarUnitatsIProcediments);
	                        try{ 
	                        	tascaProgramadaService.actualitzarUnitatsIProcediments();
	                        	monitorTasquesService.fi(actualitzarUnitatsIProcediments);
	                        } catch(Throwable th) {
	                        	tractarErrorTascaSegonPla(th, actualitzarUnitatsIProcediments);
	                        }
	                    }
	                },
	                new Trigger() {
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
	                }
	        );
		
		
		final String updatePeticionsAsincronesPinbal = "updatePeticionsAsincronesPinbal";
		/** Tasca programada per actualitzar les peticions asíncrones de PINBAL periòdicament.
		 *  Entre comrpovació i comprovació hi ha un període de 60 segons. 
		 */
		monitorTasquesService.addTasca(updatePeticionsAsincronesPinbal);
		taskRegistrar.addTriggerTask(
	                new Runnable() {
	                    @Override
	                    public void run() {
	                    	monitorTasquesService.inici(updatePeticionsAsincronesPinbal);
	                        try{ 
	                        	tascaProgramadaService.updatePeticionsAsincronesPinbal();
	                        	monitorTasquesService.fi(updatePeticionsAsincronesPinbal);
	                        } catch(Throwable th) {
	                        	tractarErrorTascaSegonPla(th, updatePeticionsAsincronesPinbal);
	                        }
	                    }
	                },
	                new Trigger() {
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
	                }
	        );
		
		final String comprovarEmailAnotacionsNoAgrupats = "comprovarEmailAnotacionsNoAgrupats";
		/** Tasca programada per comprovar si hi ha enviment de correus no agrupats no pendents.
		 */
		monitorTasquesService.addTasca(comprovarEmailAnotacionsNoAgrupats);
		taskRegistrar.addTriggerTask(
	                new Runnable() {
	                    @Override
	                    public void run() {
	                    	monitorTasquesService.inici(comprovarEmailAnotacionsNoAgrupats);
	                        try{ 
	                        	tascaProgramadaService.comprovarEmailAnotacionsNoAgrupats();
	                        	monitorTasquesService.fi(comprovarEmailAnotacionsNoAgrupats);
	                        } catch(Throwable th) {
	                        	tractarErrorTascaSegonPla(th, comprovarEmailAnotacionsNoAgrupats);
	                        }
	                    }
	                },
	                new Trigger() {
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
	                }
	        );
		
		
		final String comprovarEmailAnotacionsAgrupats = "comprovarEmailAnotacionsAgrupats";
		/** Mètode periòdic per enviar correus agrupats de noves anotacions
		 * segons la propietat app.anotacions.emails.agrupats.cron , Per defecte a les 20h.
		 */
		monitorTasquesService.addTasca(comprovarEmailAnotacionsAgrupats);
		taskRegistrar.addTriggerTask(
	                new Runnable() {
	                    @Override
	                    public void run() {
	                    	monitorTasquesService.inici(comprovarEmailAnotacionsAgrupats);
	                        try{ 
	                        	tascaProgramadaService.comprovarEmailAnotacionsAgrupats();
	                        	monitorTasquesService.fi(comprovarEmailAnotacionsAgrupats);
	                        } catch(Throwable th) {
	                        	tractarErrorTascaSegonPla(th, comprovarEmailAnotacionsAgrupats);
	                        }
	                    }
	                },
	                new Trigger() {
	                    @Override
	                    public Date nextExecutionTime(TriggerContext triggerContext) {
	                    	String value = null;
							try {
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
	                }
	        );
		
		
		final String migrarExpedientsArxiu = "migrarExpedientsArxiu";
		/** Tasca programada per migrar expedients a Arxiu.
		 */
		monitorTasquesService.addTasca(migrarExpedientsArxiu);
		taskRegistrar.addTriggerTask(
			new Runnable() {
				@Override
				public void run() {
					monitorTasquesService.inici(migrarExpedientsArxiu);
					try{ 
						tascaProgramadaService.migrarExpedientsDocumentsArxiu();
						monitorTasquesService.fi(migrarExpedientsArxiu);
					} catch(Throwable th) {
						tractarErrorTascaSegonPla(th, migrarExpedientsArxiu);
					}
				}
			},
			new Trigger() {
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
			}
		);
	}

	/** Enregistre l'error als logs i marca la tasca amb error. */
	private void tractarErrorTascaSegonPla(Throwable th, String codiTasca) {
		String errMsg = th.getClass() + ": " + th.getMessage() + " (" + new Date().getTime() + ")";
		logger.error("Error no controlat a l'execució de la tasca en segon pla amb codi \"" + codiTasca + "\": " + errMsg, th);
		monitorTasquesService.error(codiTasca, errMsg);
	}
	private static final Logger logger = LoggerFactory.getLogger(TascaProgramadaConfig.class);

}
