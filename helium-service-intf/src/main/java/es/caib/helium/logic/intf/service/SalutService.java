package es.caib.helium.logic.intf.service;

import es.caib.comanda.model.server.monitoring.*;

import java.util.List;

public interface SalutService {
	public List<IntegracioInfo> getIntegracions();
	public List<SubsistemaInfo> getSubsistemes();
	public List<ContextInfo> getContexts();
	public SalutInfo checkSalut(String versio, String performanceUrl);
}
