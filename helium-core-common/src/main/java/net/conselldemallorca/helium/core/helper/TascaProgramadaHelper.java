package net.conselldemallorca.helium.core.helper;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.emiserv.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.service.TascaProgramadaService;

@Component
public class TascaProgramadaHelper {
	
	@Autowired
	private TascaProgramadaService tascaProgramadaService;
	
	public void reindexarExpedient (Long expedientId) throws NoTrobatException{
		tascaProgramadaService.reindexarExpedient(expedientId);
	}	
	
	public void actualitzarEstatNotificacions(Long notificacioId) throws NoTrobatException {
		tascaProgramadaService.actualitzarEstatNotificacions(notificacioId);
	}
	
	public void actualitzarExpedientReindexacioData(Long expedientId, Date dataReindexacioAsincrona) {
		tascaProgramadaService.actualitzarExpedientReindexacioData(expedientId, dataReindexacioAsincrona);
	}

}