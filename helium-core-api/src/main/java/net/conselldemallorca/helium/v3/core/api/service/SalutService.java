package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.salut.AppInfo;
import net.conselldemallorca.helium.v3.core.api.dto.salut.ContextInfo;
import net.conselldemallorca.helium.v3.core.api.dto.salut.IntegracioInfo;
import net.conselldemallorca.helium.v3.core.api.dto.salut.SalutInfo;

public interface SalutService {
	public List<IntegracioInfo> getIntegracions();
	public List<AppInfo> getSubsistemes();
	public List<ContextInfo> getContexts(String baseUrl);
	public SalutInfo checkSalut(String versio, String performanceUrl);
}
