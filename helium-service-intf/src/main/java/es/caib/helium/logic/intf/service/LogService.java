package es.caib.helium.logic.intf.service;

import java.util.List;

import es.caib.comanda.model.v1.log.FitxerContingut;
import es.caib.comanda.model.v1.log.FitxerInfo;
import es.caib.comanda.service.v1.avis.ApiException;

public interface LogService {
	public FitxerContingut getFitxerByNom(String nomFitxer) throws ApiException;
	public FitxerContingut llegitUltimesLinies(String nomFitxer,Long nLinies) throws ApiException;
	public List<FitxerInfo> llistarFitxers() throws ApiException;
}
