package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import es.caib.comanda.ms.log.model.FitxerContingut;
import es.caib.comanda.ms.log.model.FitxerInfo;
import es.caib.comanda.service.management.ApiException;


public interface LogService {
	public FitxerContingut getFitxerByNom(String nomFitxer) throws ApiException;
	public List<String> llegitUltimesLinies(String nomFitxer,Long nLinies) throws ApiException;
	public List<FitxerInfo> llistarFitxers() throws ApiException;
}
