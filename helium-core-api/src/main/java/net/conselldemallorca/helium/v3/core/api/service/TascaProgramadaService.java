package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;

import net.conselldemallorca.helium.v3.core.api.exception.ExecucioMassivaException;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;

/** Servei amb els mètodes per executar tasques progamades en segon pla i gestionar-les.
 * 
 */
public interface TascaProgramadaService {
	
	public void comprovarExecucionsMassives() throws NoTrobatException, ExecucioMassivaException;
	
	public void comprovarReindexacioAsincrona() throws NoTrobatException;
	public void setReindexarAsincronament(boolean reindexar);
	public boolean isReindexarAsincronament();
	public void reindexarExpedient (Long expedientId) throws NoTrobatException;
	public void actualitzarExpedientReindexacioData(Long expedientId, Date dataReindexacio);
	
	public void comprovarEstatNotificacions() throws NoTrobatException;
	
	public void actualitzarEstatNotificacions(Long notificacioId) throws NoTrobatException;
}
