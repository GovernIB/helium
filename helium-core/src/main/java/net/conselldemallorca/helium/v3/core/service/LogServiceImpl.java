package net.conselldemallorca.helium.v3.core.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.stereotype.Service;

import es.caib.comanda.ms.log.helper.LogHelper;
import es.caib.comanda.ms.log.model.FitxerContingut;
import es.caib.comanda.ms.log.model.FitxerInfo;
import es.caib.comanda.service.management.ApiException;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.service.LogService;

@Service
public class LogServiceImpl implements LogService {
	
	private String LOGS_LOCATION = GlobalProperties.getInstance().getProperty("app.comanda.logs.location");
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	@Override
	public FitxerContingut getFitxerByNom(String nomFitxer) throws ApiException {
		LogHelper.setAppNom("helium");
		return LogHelper.getFitxerByNom(LOGS_LOCATION, nomFitxer);
	}

	@Override
	public List<String> llegitUltimesLinies(String nomFitxer, Long nLinies)
			throws ApiException {
		LogHelper.setAppNom("helium");
		return LogHelper.readLastNLines(LOGS_LOCATION, nomFitxer, nLinies);
	}

	@Override
	public List<FitxerInfo> llistarFitxers() throws ApiException {
		return LogHelper.llistarFitxers(LOGS_LOCATION, "helium");
	}

}
