package es.caib.helium.logic.intf.service;

import es.caib.comanda.model.server.monitoring.FitxerContingut;
import es.caib.comanda.model.server.monitoring.FitxerInfo;

import java.util.List;

public interface LogService {
	public FitxerContingut getFitxerByNom(String nomFitxer);
	public List<String> llegirDarreresLinies(String nomFitxer,Long nLinies);
	public List<FitxerInfo> llistarFitxers();
}
