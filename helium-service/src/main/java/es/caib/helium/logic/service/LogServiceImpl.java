package es.caib.helium.logic.service;

import java.util.List;

import es.caib.comanda.model.server.monitoring.FitxerContingut;
import es.caib.comanda.model.server.monitoring.FitxerInfo;
import es.caib.comanda.ms.log.helper.LogHelper;
import es.caib.helium.commons.config.PropertyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import es.caib.helium.logic.intf.service.LogService;

@Service
public class LogServiceImpl implements LogService {

	@Autowired
	private Environment env;

	private String getLogsLocation() {
		return env.getProperty(PropertyConfig.PROP_COMANDA_LOGS_LOCATION);
	}

	@Override
	public FitxerContingut getFitxerByNom(String nomFitxer) {
		return LogHelper.getFitxerByNom(getLogsLocation(), nomFitxer);
	}

	@Override
	public List<String> llegirDarreresLinies(String nomFitxer, Long nLinies) {
		return LogHelper.readLastNLines(getLogsLocation(), nomFitxer, nLinies);
	}

	@Override
	public List<FitxerInfo> llistarFitxers() {
		return LogHelper.llistarFitxers(getLogsLocation(), "");
	}

}
