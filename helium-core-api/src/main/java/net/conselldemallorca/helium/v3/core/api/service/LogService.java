package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.comanda.ApiException;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.FitxerContingut;
import net.conselldemallorca.helium.v3.core.api.dto.comanda.FitxerInfo;

public interface LogService {
	public FitxerContingut getFitxerByNom(String nomFitxer) throws ApiException;
	public FitxerContingut llegitUltimesLinies(String nomFitxer,Long nLinies) throws ApiException;
	public List<FitxerInfo> llistarFitxers() throws ApiException;
}
