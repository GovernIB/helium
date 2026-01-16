package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import es.caib.comanda.ms.salut.model.ContextInfo;
import es.caib.comanda.ms.salut.model.IntegracioInfo;
import es.caib.comanda.ms.salut.model.SalutInfo;
import es.caib.comanda.ms.salut.model.SubsistemaInfo;

public interface SalutService {
	public List<IntegracioInfo> getIntegracions();
	public List<SubsistemaInfo> getSubsistemes();
	public List<ContextInfo> getContexts();
	public SalutInfo checkSalut(String versio, String performanceUrl);
}
