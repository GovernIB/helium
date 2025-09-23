package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.comanda.AppInfo;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.ContextInfo;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.IntegracioInfo;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.SalutInfo;

public interface SalutService {
	public List<IntegracioInfo> getIntegracions();
	public List<AppInfo> getSubsistemes();
	public List<ContextInfo> getContexts(String baseUrl);
	public SalutInfo checkSalut(String versio, String performanceUrl);
}
