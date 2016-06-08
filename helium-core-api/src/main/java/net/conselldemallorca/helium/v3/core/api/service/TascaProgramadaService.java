package net.conselldemallorca.helium.v3.core.api.service;

import net.conselldemallorca.helium.v3.core.api.exception.ExecucioMassivaException;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;


public interface TascaProgramadaService {
	public void comprovarExecucionsMassives() throws NoTrobatException, ExecucioMassivaException;
}
