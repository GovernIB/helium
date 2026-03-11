package es.caib.helium.logic.intf.service;

import java.util.List;

import es.caib.comanda.model.v1.salut.ContextInfo;
import es.caib.comanda.model.v1.salut.IntegracioInfo;
import es.caib.comanda.model.v1.salut.SalutInfo;
import es.caib.comanda.model.v1.salut.SubsistemaInfo;

public interface SalutService {
	public List<IntegracioInfo> getIntegracions();
	public List<SubsistemaInfo> getSubsistemes();
	public List<ContextInfo> getContexts();
	public SalutInfo checkSalut(String versio, String performanceUrl);
}
