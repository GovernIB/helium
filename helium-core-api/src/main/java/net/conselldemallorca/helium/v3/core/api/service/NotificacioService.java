package net.conselldemallorca.helium.v3.core.api.service;

import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;

public interface NotificacioService {
	
	public void reenviarRemesa(
			Long remesaId, 
			Long expedientTipusId) throws NoTrobatException, Exception;
	
	public void refrescarRemesa(
			Long remesaId) throws NoTrobatException, Exception;
}
